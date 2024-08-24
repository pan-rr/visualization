import { apiLogout } from '@/api/user'
import { userLogin } from '../../api/auth'

export default {
  namespaced: true,
  state: {
    userInfo: {
      userId: '',
      name: '',
      avatar: '',
      spaceOptions:[],
      tenantOptions :[],
      choosenTenant:''
    },
    token: '',
  },
  mutations: {
    SET_USER_INFO(state, data) {
      state.userInfo.userId = data.userId
      state.userInfo.name = data.name
      state.userInfo.avatar = ''
      state.userInfo.spaceOptions = data.spaceOptions
      state.userInfo.tenantOptions = data.tenantOptions
      state.userInfo.choosenTenant = data.tenantOptions[0]
      localStorage.setItem('visual',data.token)
    },
  },
  actions: {
    login({ commit, dispatch }, data) {
      return new Promise((resolve) => {
        userLogin(data).then(async (res) => {
          commit('SET_USER_INFO', res.data.result)
          
          await dispatch('permission/handleRoutes', null, { root: true })
          resolve('success')
        })
      })
    },
    logout({ commit, dispatch }) {
      return new Promise((resolve) => {
        apiLogout().then(async () => {
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
