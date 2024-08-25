const tenantUtil = (store)=>{
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

export default tenantUtil