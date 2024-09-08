export const nodeList = [
  // {
  //   text: '任务节点',
  //   type: 'rect',
  //   class: 'node-rect'
  // },
  {
    text: 'Visual任务节点',
    type: 'rect',
    class: 'node-v',
    taskType: 'VISUAL'
  },
  {
    text: 'SQL任务节点',
    type: 'rect',
    class: 'node-SQL',
    taskType: 'SQL'
  },
];

export const BpmnNode = [
  {
    type: 'bpmn:startEvent',
    text: '开始',
    class: 'bpmn-start'
  },
  {
    type: 'bpmn:endEvent',
    text: '结束',
    class: 'bpmn-end'
  },
  {
    type: 'bpmn:exclusiveGateway',
    text: '网关',
    class: 'bpmn-exclusiveGateway'
  },
  {
    type: 'bpmn:userTask',
    text: '用户',
    class: 'bpmn-user'
  },
]
