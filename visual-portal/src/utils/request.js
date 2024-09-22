import axios from 'axios'
import store from '@/store'
import { Message } from 'element-ui'

import { validateResponse } from './responseValid'

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'

const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API,
  timeout: 10000,
})

service.interceptors.request.use(
  (config) => {
    let token = localStorage.getItem('visual');
    let tenantId = store.getters.userInfo.choosenTenant;
    config.headers['visual_tenant'] = tenantId;
    if (token != 'undefined') {
      config.headers['visual'] = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

service.interceptors.response.use(
  (response) => {
    let contentType = response?.headers['content-type']
    if (contentType?.includes('application/octet-stream')) {
      // 二进制文件直接返回
      return response
    }
    const res = response.data

    // if (res?.header?.code === 0)return response
    let check = validateResponse(res)
    if (check == false) {
      Message({
        message: res.result || 'Error',
        type: 'error',
        duration: 5 * 1000,
      })




      // if (res?.code === -999) {
      //   if (route.path != '/login') {
      //     router.replace('/login')
      //   }
      // }
      // if (res.code === 5000) {
      //   MessageBox.confirm('您已经退出登录状态，您可以点击取消留在当前页面或者重新登录', {
      //     confirmButtonText: '重新登录',
      //     cancelButtonText: '取消',
      //     type: 'warning',
      //   }).then(() => {
      //     store.dispatch('user/resetToken').then(() => {
      //       location.reload()
      //     })
      //   })
      // }

      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return response
    }
  },
  (error) => {
    Message({
      message: error.header ? error.header.msg : '请求超时，请稍后重试',
      type: 'error',
      duration: 5 * 1000,
    })

    return Promise.reject(error)
  },
)
// /**
//  * 封装接口请求方法
//  * @param url 域名后需补齐的接口地址
//  * @param method 接口请求方式
//  * @param data data下的其他数据体
//  */
// const request = (url, method, data) => {
//   return service({
//     url,
//     method,
//     data: {
//       body: data,
//     },
//   })
// }

// export default request

export default service
