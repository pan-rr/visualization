<template>
  <div>
    <el-form ref="createForm" :model="createForm">
      <el-form-item label="权限归属者名称：">
        {{ createForm.tenantName }}
      </el-form-item>
      <el-form-item label="权限名称：">
        <el-input v-model="createForm.permissionName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-transfer :titles="['可选资源', '已选资源']" v-model="createForm.chossenOptions"
          :data="resourceOptions"></el-transfer>
      </el-form-item>
      <el-form-item style="margin: auto;">
        <el-button type="primary" @click="submitCreatePermission">立即创建</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getCurrentTenant } from '../../../utils/tenantUtil';
import { getResourceOption } from '../../../api/resource';
import { createPermission } from '../../../api/permission';

export default {
  data() {
    return {
      tenant:{},
      resourceOptions: [],
      createForm: {
        tenantId: '',
        tenantName: '',
        permissionName: '',
        resourceList: '',
        chossenOptions: [],
      },
      searchForm: {
        tenantId: '',
        tenantName: '',
        permissionName: '',
      }
    }
  },
  methods: {
    submitCreatePermission() {
      this.createForm.resourceList = this.createForm.chossenOptions.join(",")
      createPermission(this.createForm).then(res => {
        this.finish()
      })
    },
    init() {
      this.tenant = getCurrentTenant();
      this.createForm = this.$options.data().createForm
      this.createForm.tenantId = this.tenant.id;
      this.createForm.tenantName = this.tenant.name;
      this.searchForm = this.$options.data().searchForm
      this.searchForm.tenantId = this.tenant.id;
      this.searchForm.tenantName = this.tenant.name;
      getResourceOption(this.tenant.id).then(res => {
        this.resourceOptions = res.data.result;
      })
    },
    finish() {
      this.$emit('finish', true)
    },
  },
  mounted() {
    this.init();
  }
};
</script>