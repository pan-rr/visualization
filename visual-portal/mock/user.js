const Mock = require('mockjs')

Mock.mock('/api/user/login', 'post', (req) => {
  const data = JSON.parse(req.body)
  return {
    header: {
      code: 0,
    },
    body: {
      userId: '001',
      name: data.name,
      space: ['public',data.name+':oa'],
      // avatar: 'http://114.115.235.59/avatar.png',
    },
  }
})

Mock.mock('/api/user/logout', 'get', () => {
  return {
    header: {
      code: 0,
    },
    body: {},
  }
})

Mock.mock('/api/user/changePassword', 'post', () => {
  return {
    header: {
      code: 0,
    },
    body: {},
  }
})
