<template>
  <div style="line-height: 200%;">
    <h1>数据源创建</h1>
    <space-selector :spaceRef="spaceRef"></space-selector>
    <el-form :model="dataSource" label-position="left">
      <el-form-item prop="name" label="数据源名称：">
        <el-input v-model="dataSource.name" placeholder="数据源名称" prefix-icon="el-icon-user" />
      </el-form-item>
      <el-form-item prop="url" label="数据源URL：">
        <el-input v-model="dataSource.config.url" placeholder="数据源url" prefix-icon="el-icon-user" />
      </el-form-item>
      <el-form-item prop="url" label="数据源账号：">
        <el-input v-model="dataSource.config.username" placeholder="数据源账号" prefix-icon="el-icon-user" />
      </el-form-item>
      <el-form-item prop="url" label="数据源密码：">
        <el-input v-model="dataSource.config.password" placeholder="数据源密码" prefix-icon="el-icon-user" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">创建数据源</el-button>
        <el-button @click="initForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>


</template>

<script>

import { Message } from 'element-ui';
import SpaceSelector from '../../layout/components/Visual/SpaceSelector.vue';
import { saveDataSource } from '../../api/dag';

export default {
  components: {
    SpaceSelector
  },
  data() {
    return {
      dataSource: {
        space: '',
        name: '',
        config: {
          username: '',
          password: '',
          url: '',
        }
      },
      spaceRef: {
        data: '',
      },
    }
  },
  methods: {
    onSubmit() {
      this.dataSource.space = this.spaceRef.data;
      saveDataSource(this.dataSource).then(res => {
        this.initForm()
        Message({
          message: res.data.result,
          type: 'success',
          duration: 5 * 1000,
        })
      })
    },
    initForm() {
      this.dataSource = this.$options.data().dataSource
      this.dataSource.space = this.$store.getters
    }
  },
  mounted() {

  }
};
</script>