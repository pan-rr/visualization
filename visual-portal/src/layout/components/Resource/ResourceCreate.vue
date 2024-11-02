<template>
  <div>
    <el-form ref="form" :model="form">
      <el-form-item label="资源归属者名称：">
        <el-input v-model="form.tenantName" readonly></el-input>
      </el-form-item>
      <el-form-item label="资源归属者ID：">
        <el-input v-model="form.tenantId" readonly></el-input>
      </el-form-item>
      <!-- <el-form-item label="资源名称：">
        <el-input v-model="form.resourceName"></el-input>
      </el-form-item> -->
      <el-form-item label="资源名称：">
        <el-autocomplete class="inline-input" v-model="form.resourceName"
          :fetch-suggestions="queryResource"></el-autocomplete>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">立即创建</el-button>
        <el-button type="primary" @click="finish">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { createResource } from '../../../api/resource';
import { Message } from 'element-ui';
import { getCurrentTenant } from '../../../utils/tenantUtil';
import { searchResource } from '../../../config/authConfig';

export default {
  name: "ResourceCreate",
  data() {
    return {
      form: {
        tenantId: '',
        tenantName: '',
        resourceName: '',
      }
    }
  },
  methods: {
    onSubmit() {
      createResource(this.form).then(res => {
        this.initForm()
        Message({
          message: res.data.result,
          type: 'success',
          duration: 5 * 1000,
        })
        this.finish(true);
      })
    },
    finish() {
      this.$emit('finish', true)
    },
    initForm() {
      this.form = this.$options.data().form;
      let tenantRes = getCurrentTenant();
      this.form.tenantId = tenantRes['id'];
      this.form.tenantName = tenantRes['name'];
    },
    queryResource(query, cb) {
      cb(searchResource(query))
    },
  },
  mounted() {
    this.initForm()
  }
};
</script>