
import { userLogin, userLogout } from '../../api/auth'

export default {
  namespaced: true,
  state: {
    userInfo: {
      userId: '',
      name: '',
      avatar: '',
      spaceOptions: [],
      tenantOptions: [],
      chosenTenant: ''
    },
  },
  mutations: {
    SET_USER_INFO(state, data) {
      state.userInfo.userId = data.userId
      state.userInfo.name = data.name
      state.userInfo.avatar = ''
      state.userInfo.spaceOptions = data.spaceOptions
      state.userInfo.tenantOptions = data.tenantOptions
      state.userInfo.chosenTenant = data.tenantOptions ? data.tenantOptions[0].value : ''
      localStorage.setItem('visual', data.token)
    },
    SET_TENANT(state, data) {
      state.userInfo.chosenTenant = data
    },
  },
  actions: {
    login({ commit, dispatch, state }, data) {
      return new Promise((resolve) => {
        userLogin(data).then(async (res) => {
          commit('SET_USER_INFO', res.data.result)
          await dispatch('space/initHolder', res.data.result.spaceOptions,{root:true})
          await dispatch('permission/userPermissionResource', state.userInfo.chosenTenant,{root:true})
          // await dispatch('permission/handleRoutes', null, { root: true })
          resolve('success')
        })
      })
    },
    logout({ commit, dispatch }) {
      return new Promise((resolve) => {
        userLogout().then(async () => {
          commit('SET_USER_INFO', {
            name: '',
            avatar: '',
          })
          await dispatch('permission/resetRoute', null, { root: true })
          commit('tagsView/CLEAR_CACHE_VIEW', null, {
            root: true,
          })
          commit('tagsView/CLEAR_VISITED_VIEW', null, {
            root: true,
          })
          commit('tagsView/CLEAR_FIXED_VISITED_VIEW', null, {
            root: true,
          })
          resolve('success')
        })
      })
    },
  },
  modules: {},
}
