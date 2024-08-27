import Layout from '@/layout/index.vue'
export default {
  path: '/auth',
  name: 'Auth',
  meta: { title: '权限管理', icon: 'el-icon-s-data', needCache: false, needAuthFilter: true },
  component: Layout,
  children: [
    {
      path: 'resourceManagement',
      name: 'ResourceManagement',
      meta: { title: '资源管理', needCache: false, needAuthFilter: true },
      component: () => import('@/views/Auth/ResourceManagement.vue')
    },
    {
      path: 'permissionManagement',
      name: 'PermissionManagement',
      meta: { title: '权限管理', needCache: false, needAuthFilter: true },
      component: () => import('@/views/Auth/PermissionManagement.vue')
    },
    {
      path: 'authGrantManagement',
      name: 'AuthGrantManagement',
      meta: { title: '人员赋权', needCache: false, needAuthFilter: true },
      component: () => import('@/views/Auth/AuthGrantManagement.vue')
    },
    {
      path: 'subTenantRegister',
      name: 'SubTenantRegister',
      meta: { title: '子租户创建', needCache: false, needAuthFilter: true },
      component: () => import('@/views/Auth/SubTenantRegister.vue')
    },
  ]
}
