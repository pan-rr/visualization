import store from '@/store'

const getCurrentTenant = () => {
  let tenant = {}
  let tenantOptions = store.getters.userInfo.tenantOptions;
  let chosenTenant = getCurrentTenantId()
  for (let item of tenantOptions) {
    if (item.value === chosenTenant) {
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
  return store.getters.userInfo.chosenTenant;
}

export { getCurrentTenant, getCurrentTenantName, getCurrentTenantId }