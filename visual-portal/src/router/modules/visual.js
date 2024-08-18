import Layout from '@/layout/index.vue'
export default {
  path: '/visual',
  name: 'Visual',
  // redirect: '/visual/define',
  meta: { title: '流程管理', icon: 'el-icon-s-data', needCache: true },
  component: Layout,
  children: [
    {
      path: 'templateDefine',
      name: 'VisualTemplateDefine',
      meta: { title: '流程定义', needCache: true },
      component: () => import('@/layout/components/LogicFlow/LF.vue')
    },
    {
      path: 'templateList',
      name: 'VisualTemplateList',
      meta: { title: '已定义流程列表', needCache: true },
      component: () => import('@/views/Dag/TemplateList.vue')
    },
    {
      path: 'instanceList',
      name: 'VisualInstanceList',
      meta: { title: '流程实例列表', needCache: false },
      component: () => import('@/views/Dag/InstanceList.vue')
    },
    {
      path: 'fileManage',
      name: 'FileManage',
      meta: { title: '文件管理', needCache: false },
      component: () => import('@/views/File/index.vue')
    },
  ]
}
