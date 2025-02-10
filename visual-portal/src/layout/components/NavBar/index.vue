<template>
  <div>
    <div class="navbar flex flex-align-center flex-justify-between border-b">
      <div class="flex flex-align-center">
        <Collapse />
        <Breadcrumb />
      </div>

      <div
        style="margin-left: auto;color: #909399;border: 1px solid #DCDFE6;background-color: #F5F7FA;display: table-cell;vertical-align: middle;white-space: nowrap;">
        <span style="margin-left:20px;margin-right:20px;"><el-icon
            class="el-icon-office-building"></el-icon>企业/组织/团队</span>
        <el-select v-model="chosenTenant" slot="append" placeholder="请选择租户" @change="changeTenant">
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
import { getCurrentTenantName } from "../../../utils/tenantUtil";
import { resetSpaceOption } from "../../../utils/spaceUtil";
export default {
  name: "",
  components: { Collapse, Breadcrumb, AvatarDropDown, VisitedViews },
  data() {
    return {
      currentTenant: getCurrentTenantName(),
      tenantOptions: this.$store.getters.userInfo.tenantOptions,
      chosenTenant: this.$store.getters.userInfo.chosenTenant,
    };
  },
  mounted() {
    this.currentTenant = getCurrentTenantName();
  },
  methods: {
    changeTenant(value) {
      this.$store.getters.userInfo.chosenTenant = value;
      this.currentTenant = getCurrentTenantName();
      this.$store.dispatch("permission/userPermissionResource", value).then(() => {
        this.$store.commit("user/SET_TENANT", value)
        this.$store.commit("tagsView/CLEAR_CACHE_VIEW");
        this.$store.commit("tagsView/CLEAR_VISITED_VIEW");
        if (this.$route.path != '/home') {
          this.$router.replace('/home')
        }
      });
      resetSpaceOption();
    },
  },
  created() {
    resetSpaceOption();
  }
};
</script>

<style lang="scss" scoped>
.navbar {
  >div:first-child {
    height: 50px;
  }
}
</style>
