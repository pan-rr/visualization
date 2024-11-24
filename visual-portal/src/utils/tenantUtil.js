import store from '@/store'


const getCurrentTenant = () => {
  let tenant = {}
  let tenantOptions = store.getters.userInfo.tenantOptions;
  let choosenTenant = store.getters.userInfo.choosenTenant
  for (let item of tenantOptions) {
    if (item.value === choosenTenant) {
      tenant['id'] = item.value;
      tenant['name'] = item.label
      break;
    }
  }
  return tenant;
}

const getCurrentTenantName = () => {
  let tenant = getCurrentTenant();
  return tenant.name;
}

const getCurrentTenantId = () => {
  return store.getters.userInfo.choosenTenant;
}

export { getCurrentTenant, getCurrentTenantName, getCurrentTenantId }