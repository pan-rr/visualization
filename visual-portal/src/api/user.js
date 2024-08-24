import request from '@/utils/request'


const curryRequest = (url, method, data) => {
  return request.post(`/user/${url}`, data)
  // return request(`/user/${url}`, method, data)
}

export function apiLogin(data) {
  return new Promise(resolve => {
    resolve(
      {
        code: 0,
        data: {
          body:{
            userId: '001',
            name: data.name,
            space: ['public', data.name + ':oa'],
          }
        },
      }
    )
  });
  // return request.post('/user/login',data)
  // return curryRequest('login', 'post', data)
}

export function apiLogout() {
  return curryRequest('logout', 'get')
}

export function apiChangePassword(data) {
  return curryRequest('changePassword', 'post', data)
}
