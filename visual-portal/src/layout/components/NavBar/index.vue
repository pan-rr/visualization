<template>
  <div>
    <div class="navbar flex flex-align-center flex-justify-between border-b">
      <div class="flex flex-align-center">
        <Collapse />
        <Breadcrumb />
      </div>
      <div style="margin-left: auto;">


        <el-input v-model="currentTenant" class="select" :readonly="true" placeholder="请选择租户">
          <template slot="prepend"><el-icon class="el-icon-office-building"></el-icon><span>企业/组织/团队</span></template>
          <el-select v-model="choosenTenant" slot="append" placeholder="请选择租户" @change="changeTenant">
            <el-option v-for="item in tenantOptions" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-input>


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
import tenantUtil from "../../../utils/tenantUtil";
export default {
  name: "",
  components: { Collapse, Breadcrumb, AvatarDropDown, VisitedViews },
  data() {
    return {
      currentTenant: tenantUtil(this.$store).name,
      tenantOptions: this.$store.getters.userInfo.tenantOptions,
      choosenTenant: this.$store.getters.userInfo.choosenTenant,
    };
  },
  methods: {
    changeTenant(value) {
      this.$store.getters.userInfo.choosenTenant = value;
      this.currentTenant = tenantUtil(this.$store).name;
      this.$store.dispatch("permission/userPermissionResource", value).then(() => {
        this.$store.commit("user/SET_TENANT", value)
        this.$store.commit("tagsView/CLEAR_CACHE_VIEW");
        this.$store.commit("tagsView/CLEAR_VISITED_VIEW");
        if (this.$route.path != '/home') {
          this.$router.replace('/home')
        }
      });
    },
  },

};
</script>

<style lang="scss" scoped>
.navbar {
  >div:first-child {
    height: 50px;
  }
}
</style>
