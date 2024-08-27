import router, { permissionRoutes, resetRouter } from '@/router'
import { getUserTenantPermission } from '../../api/auth';
import permissionResourceSet from '../../utils/pemissionResource';
import getPermitRoutes from '../../utils/routePermit';

export default {
  namespaced: true,
  state: {
    routes: [],
    auth: {},
  },
  mutations: {
    setRoutes(state, permissionRoutes) {
      state.routes = permissionRoutes
    },
    SET_USER_AUTH(state, data) {
      state.auth = data;
    },
  },
  actions: {
    handleRoutes(context) {
      // permissionRoutes.forEach((item) => {
      //   router.addRoute(item)
      // })
      // context.commit('setRoutes', permissionRoutes)

      let auth = context.state.auth
      let remainRoutes = permissionRoutes;
      if (!auth.permitAll) {
        let set = permissionResourceSet(auth)
        remainRoutes = getPermitRoutes(permissionRoutes, set)
      }
      resetRouter()
      remainRoutes.forEach((item) => {
        router.addRoute(item)
      })
      context.commit('setRoutes', remainRoutes)
    },
    resetRoute(context) {
      resetRouter();
      context.commit('setRoutes', [])
    },
    userPermissionResource({ commit, dispatch }, tenantId) {
      return new Promise((resolve) => {
        getUserTenantPermission(tenantId).then(async (res) => {
          commit('SET_USER_AUTH', res.data.result)
          await dispatch('resetRoute')
          await dispatch('handleRoutes', null)
          resolve('success')
        })
      })
    }
  },
}
