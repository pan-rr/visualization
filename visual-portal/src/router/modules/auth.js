import Layout from '@/layout/index.vue'
export default {
  path: '/auth',
  name: 'Auth',
  meta: { title: '权限管理', icon: 'el-icon-s-data', needCache: false },
  component: Layout,
  children: [
    {
      path: 'resourceManagement',
      name: 'ResourceManagement',
      meta: { title: '资源管理', needCache: false },
      component: () => import('@/views/Auth/ResourceManagement.vue')
    },
    {
      path: 'permissionManagement',
      name: 'PermissionManagement',
      meta: { title: '权限管理', needCache: false },
      component: () => import('@/views/Auth/PermissionManagement.vue')
    },
    {
      path: 'authGrantManagement',
      name: 'AuthGrantManagement',
      meta: { title: '人员赋权', needCache: false },
      component: () => import('@/views/Auth/AuthGrantManagement.vue')
    },
  ]
}
