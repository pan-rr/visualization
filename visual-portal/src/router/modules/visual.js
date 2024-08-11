import Layout from '@/layout/index.vue'
export default {
  path: '/visual',
  name: 'Visual',
  // redirect: '/visual/define',
  meta: { title: '流程管理', icon: 'el-icon-s-data', needCache: true },
  component: Layout,
  children: [
    {
      path: 'define',
      name: 'VisualTemplateDefine',
      meta: { title: '流程定义', needCache: true },
      component: () => import('@/layout/components/LogicFlow/LF.vue')
    },
    {
      path: 'templateList',
      name: 'VisualTemplateList',
      meta: { title: '已定义流程列表', needCache: true },
      component: () => import('@/layout/components/LogicFlow/TemplateList.vue')
    },
    {
      path: 'instanceList',
      name: 'VisualInstanceList',
      meta: { title: '流程实例列表', needCache: true },
      component: () => import('@/layout/components/LogicFlow/InstanceList.vue')
    },
    {
      path: 'uploadPage',
      name: 'UploadPage',
      meta: { title: '文件上传', needCache: false },
      component: () => import('@/layout/components/File/UploadPage.vue')
    },
  ]
}
