<template>
  <div>
    <el-form label-width="80px" :model="formData">
      <el-form-item label="节点名称">
        <el-input v-model="formData.text"></el-input>
      </el-form-item>
      <!-- <el-form-item label="名称">
        <el-input v-model="formData.name"></el-input>
      </el-form-item>
      <el-form-item label="活动区域">
        <el-input v-model="formData.region"></el-input>
      </el-form-item>
      <el-form-item label="活动形式">
        <el-input v-model="formData.type"></el-input>
      </el-form-item> -->
      <el-form-item label="节点JSON">
        <!-- <el-input v-model="formData.json"></el-input> -->
        <!-- <el-input type="textarea" v-model="formData.json"></el-input> -->
        <vue-json-editor v-model="formData.json" :showBtns="false"  :mode="'code'"></vue-json-editor>
      </el-form-item>
       <el-form-item>
        <el-button :plain="true"  type="primary" @click="onSubmit">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>

// import {getId}  from "@/api/dag";
import vueJsonEditor from 'vue-json-editor'
import { saveTask } from '../../../api/dag';

export default {
  components:{vueJsonEditor},
  name: '',
  props: {
    nodeData: Object,
    lf: Object || String,
  },
  mounted() {
    const { properties, text } = this.$props.nodeData
    if (properties) {
      this.$data.formData = Object.assign({}, this.$data.formData, properties)
    }
    if (text && text.value) {
      this.$data.formData.text = text.value
    }
    if (text && text.value) {
      this.$data.text = text.value
    }
  },
  data () {
    return {
      text: '',
      formData: {
          text: '',
          json:'',
        }
    }
  }, 
  methods: {
    onSubmit() {
      const { id } = this.$props.nodeData
      this.$props.lf.setProperties(id, {
        ...this.$data.formData
      });
      let task = {
        id : id,
        name : this.$data.formData.text,
        json : JSON.stringify(this.$data.formData.json),
      }
      saveTask(task)
      this.$props.lf.updateText(id, this.$data.formData.text);
      this.$emit('onClose')
    },
  }
}
</script>
<style scoped>
</style>
