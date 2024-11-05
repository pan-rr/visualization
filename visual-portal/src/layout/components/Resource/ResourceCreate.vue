<template>
  <div>
    <el-form ref="form" :model="form">
      <el-form-item label="资源归属者名称：">
        {{ form.tenantName }}
      </el-form-item>

      <el-form-item label="待创建资源：">
        <el-select  v-model="form.resourceNames" filterable multiple remote :remote-method="getResourceOption" :loading="loading" placeholder="资源名称">
            <el-option v-for="item in options" :key="item.label" :label="item.label" :value="item.label">
            </el-option>
        </el-select>
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
import { getResourceOptions } from '../../../config/authConfig';

export default {
  name: "ResourceCreate",
  data() {
    return {
      options:[],
      loading : false,
      form: {
        tenantId: '',
        tenantName: '',
        resourceNames:[],
      }
    }
  },
  methods: {
    onSubmit() {
      console.log(this.form);
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
    getResourceOption(query){
        if(query !== ''){
          this.loading = true;
          setTimeout(()=>{
              this.options = getResourceOptions(query);
              this.loading = false;
          })
        }else{
          this.options = getResourceOptions();
        }
    },
  },
  mounted() {
    this.initForm()
    this.options = getResourceOptions();
  }
};
</script>