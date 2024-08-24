<template>
  <div>
    <div class="navbar flex flex-align-center flex-justify-between border-b">
      <div class="flex flex-align-center">
        <Collapse />
        <Breadcrumb />
      </div>
      <div style="margin-left: auto;">
       <el-icon class="el-icon-office-building"></el-icon><span>企业/组织/团队</span>
      <el-select v-model="choosenTenant" slot="append" placeholder="请选择空间" @change="changeTenant">
        <el-option v-for="item in tenantOptions" :key="item.value" :label="item.label" :value="item.value">
        </el-option>
      </el-select>
    </div>
      <AvatarDropDown />
    </div>
    <VisitedViews />
  </div>
</template>

<script>
import Collapse from "./Collapse.vue";
import Breadcrumb from "./Breadcrumb.vue";
import AvatarDropDown from "./AvatarDropDown.vue";
import VisitedViews from "./VisitedViews.vue";
export default {
  name: "",
  components: { Collapse, Breadcrumb, AvatarDropDown, VisitedViews },
  data() {
    return {
      tenantOptions: this.$store.getters.userInfo.tenantOptions,
      choosenTenant: this.$store.getters.userInfo.choosenTenant,
    };
  },
  methods:{
    changeTenant(value) {
      this.$store.getters.userInfo.choosenTenant = value;
    },
  },
  watch: {
    choosenTenant: {
      immediate: true,
      deep: true,
      handler(newVal, oldVal) {
        console.log(this.choosenTenant)
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.navbar {
  > div:first-child {
    height: 50px;
  }
}
</style>
