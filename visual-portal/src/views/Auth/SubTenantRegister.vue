<template>
  <div>
    <h1>子租户创建</h1>
    <el-form :model="registerForm" label-position="left">
      <el-form-item prop="fatherId" label="父账号名称：">
        <el-input v-model="registerForm.fatherName" placeholder="父账号名称" prefix-icon="el-icon-user" readonly />
      </el-form-item>
      <el-form-item prop="fatherName" label="父账号ID：">
        <el-input v-model="registerForm.fatherId" placeholder="父账号ID" prefix-icon="el-icon-user" readonly />
      </el-form-item>
      <el-form-item prop="oa" label="子账号oa：">
        <el-input v-model="registerForm.oa" placeholder="子账号oa" prefix-icon="el-icon-user" />
      </el-form-item>
      <el-form-item prop="username" label="子账号名称：">
        <el-input v-model="registerForm.username" placeholder="子账号名称" prefix-icon="el-icon-user" />
      </el-form-item>
      <el-form-item prop="password" label="子账号密码：">
        <el-input type="password" prefix-icon="el-icon-lock" v-model="registerForm.password" placeholder="子账号密码"
          show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">注册子账号</el-button>
        <el-button @click="initForm">重置</el-button>
      </el-form-item>
    </el-form>


  </div>

</template>

<script>

import { Message } from 'element-ui';

import { registerSubTenant } from '../../api/auth';
import { getCurrentTenant } from '../../utils/tenantUtil';

export default {
  name: "SubTenantRegister",
  data() {
    return {
      registerForm: {
        fatherId: '',
        fatherName: '',
        oa: "",
        password: "",
        username: '',
      },
    }
  },
  methods: {
    onSubmit() {
      registerSubTenant(this.registerForm).then(res => {
        this.initForm()
        Message({
          message: res.data.result,
          type: 'success',
          duration: 5 * 1000,
        })
      })
    },
    initForm() {
      this.registerForm = this.$options.data().registerForm
      let tenantRes = getCurrentTenant();
      this.registerForm.fatherId = tenantRes['id'];
      this.registerForm.fatherName = tenantRes['name'];
    }
  },
  mounted() {
    this.initForm()
  }
};
</script>