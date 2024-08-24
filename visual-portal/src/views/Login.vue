<template>
  <div class="login h_100 flex flex-justify-center">
    <div class="inner_box">
      <div v-if="needRegister">
        <div class="title c_fff bold t-center">注册账号</div>
        <!-- <el-form ref="form" :model="register" :rules="rules">
          <el-form-item prop="oa">
            <el-input v-model="register.oa" placeholder="oa账号" prefix-icon="el-icon-user" />
          </el-form-item>
          <el-form-item prop="username">
            <el-input v-model="register.username" placeholder="用户名" prefix-icon="el-icon-user" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input type="password" prefix-icon="el-icon-lock" v-model="register.password" placeholder="密码"
              show-password />
          </el-form-item>
          <el-form-item label="账号属性" prop="userType">
            <el-radio-group v-model="register.userType">
              <el-radio label="0">个人账号</el-radio>
              <el-radio label="1">企业/组织/团队账号</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-link type="primary" @click="disableRegister">返回登录</el-link>
          <el-button class="w_100" type="primary" :loading="loginLoading" @click="login('form')">登录</el-button>
        </el-form> -->
        <el-form   :model="registerForm">
          <el-form-item prop="oa">
            <el-input v-model="registerForm.oa" placeholder="oa账号" prefix-icon="el-icon-user" />
          </el-form-item>
          <el-form-item prop="username">
            <el-input v-model="registerForm.username" placeholder="用户名" prefix-icon="el-icon-user" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input type="password" prefix-icon="el-icon-lock" v-model="registerForm.password" placeholder="密码"
              show-password />
          </el-form-item>
          <el-form-item label="账号属性" prop="userType">
            <el-radio-group v-model="registerForm.userType">
              <el-radio label="0">个人账号</el-radio>
              <el-radio label="1">企业/组织/团队账号</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-link type="primary" @click="disableRegister">返回登录</el-link>
          <el-button class="w_100" type="primary" :loading="loginLoading" @click="reisterUser">登录</el-button>
        
        </el-form>
      </div>

      <div v-else>
        <div class="title c_fff bold t-center">系统登录</div>
        <el-form ref="form" :model="param" :rules="rules">
          <el-form-item prop="oa">
            <el-input v-model="param.oa" placeholder="用户名" prefix-icon="el-icon-user" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input type="password" prefix-icon="el-icon-lock" v-model="param.password" placeholder="密码"
              show-password />
          </el-form-item>

          <el-link type="primary" @click="showRegister">注册账号</el-link>
          <el-button class="w_100" type="primary" :loading="loginLoading" @click="login('form')">登录</el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { userRegister } from '../api/auth';
import { Message } from 'element-ui'


export default {
  name: "Login",
  data() {
    return {
      needRegister: false,
      param: {
        oa: "",
        password: ""
      },
      registerForm: {
        oa: "",
        password: "",
        username: '',
        userType: '',
      },
      rules: {
        oa: [{ required: true, message: "请输入用户名", trigger: blur }],
        password: [{ required: true, message: "请输入密码", trigger: blur }]
      },
      loginLoading: false
    };
  },
  created() { },
  methods: {
    showRegister() {
      this.needRegister = true;
    },
    disableRegister() {
      this.needRegister = false;
    },
    reisterUser(){
      this.loginLoading = true;
      this.disableRegister()
      userRegister(this.registerForm).then(res=>{
        this.registerForm = this.$options.data().registerForm 
        Message({
            message: res.data.result,
            type: 'success',
            duration: 5 * 1000,
        })
      }).finally(res=>{
        this.loginLoading = false;
      })
    },
    login(formName) {
      this.loginLoading = true;
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.$store.dispatch("user/login", this.param).then(() => {
            this.loginLoading = false;
            this.$router.push({ name: "Home" });
          });
        } else {
          return false;
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.login {
  background-color: #2d3a4b;

  .inner_box {
    width: 40%;
    margin-top: 10%;

    .title {
      padding-bottom: 40px;
      font-size: 30px;
    }

    ::v-deep .el-form-item {
      background-color: #283443;
      border: 1px solid #434c58;
      border-radius: 4px;

      .el-input {
        input {
          height: 50px;
          padding-right: 30px;
          font-size: 16px;
          background-color: transparent;
          border: none;
          color: #fff;
        }
      }
    }
  }
}
</style>
