import request from '@/utils/request'

const curryRequest = (url, method, data) => {
  return request.post(`/user/${url}`,data)
  // return request(`/user/${url}`, method, data)
}

export function apiLogin(data) {
  return request.post('/user/login',data)
  // return curryRequest('login', 'post', data)
}

export function apiLogout() {
  return curryRequest('logout', 'get')
}

export function apiChangePassword(data) {
  return curryRequest('changePassword', 'post', data)
}
