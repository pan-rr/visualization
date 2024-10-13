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

    
    let check = validateResponse(res)
    if (check == false) {
      Message({
        message: res.result || 'Error',
        type: 'error',
        duration: 5 * 1000,
      })


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


export default service
