import Layout from '@/layout/index.vue'
export default {
  path: '/datasource',
  name: 'Datasource',
  meta: { title: '数据源管理', icon: 'el-icon-s-data', needCache: false, needAuthFilter: true },
  component: Layout,
  children: [
    {
      path: 'dataSourceCreate',
      name: 'DataSourceCreate',
      meta: { title: '创建数据源', needCache: false, needAuthFilter: true },
      component: () => import('@/views/Dag/DataSourceCreate.vue')
    },
    {
      path: 'dataSourceList',
      name: 'DataSourceList',
      meta: { title: '数据源列表', needCache: false, needAuthFilter: true },
      component: () => import('@/views/Dag/DataSourceList.vue')
    },
  ]
}
