<template>
  <div class="templateContainer">
    <h2 class="templateTitle" :submitable="submitable"> Visualization流程绘制</h2>
    <!-- 辅助工具栏 -->
    <Control :submitable="submitable" v-if="lf" :lf="lf" @catData="$_catData" @refreash="$_refreash"></Control>
    <el-container style="height: 90vh;">
      <el-aside width="8%" v-if="submitable">
        <!-- 节点面板 -->
        <NodePanel :lf="lf" :nodeList="nodeList"></NodePanel>
      </el-aside>
      <el-main style="padding: 0%;">
        <!-- 画布 -->
        <div id="LF-view" style="height: 100%; width: 100%; margin: 0%; border: 1px solid #DCDFE6;" ref="container">
        </div>
      </el-main>
    </el-container>



    <!-- 属性面板 -->
    <el-drawer title="节点任务配置页" :visible.sync="dialogVisible" direction="rtl" size="60%" :before-close="closeDialog">
      <PropertyDialog :submitable="submitable" v-if="dialogVisible" :nodeData="clickNode" :lf="lf"
        @setPropertiesFinish="closeDialog">
      </PropertyDialog>
    </el-drawer>
    <!-- 数据查看面板 -->
    <el-dialog title="数据" :visible.sync="dataVisible" width="50%">
      <DataDialog :graphData="graphData"></DataDialog>
    </el-dialog>
  </div>
</template>
<script>
  import LogicFlow from '@logicflow/core'
  import { Menu, Snapshot, MiniMap } from '@logicflow/extension'
  import '@logicflow/core/dist/style/index.css'
  import '@logicflow/extension/lib/style/index.css'
  import NodePanel from './NodePanel'
  import AddPanel from './AddPanel'
  import Control from './Control.vue'
  import PropertyDialog from './PropertyDialog.vue'
  import DataDialog from './DataDialog'
  import { nodeList } from './config'

  import {
    registerVisual,
    registerSQL,
    registerHttp,
    registerFlink
  } from './registerNode'
  import { getTemplateJSON } from '../../../api/dag'
  import SpaceSelector from '../Visual/SpaceSelector.vue'
  import Editor from '../Visual/Editor.vue'
  import '@/assets/css/visual/canvas.scss';

  let canvasData = {};


  export default {
    name: "DAGCanvas",
    props: {
      templateId: String,
      submitable: Boolean,
    },
    components: { NodePanel, AddPanel, Control, PropertyDialog, DataDialog, SpaceSelector, Editor },
    data() {
      return {
        lf: null,
        showAddPanel: false,
        addPanelStyle: {
          top: 0,
          left: 0
        },
        nodeData: null,
        addClickNode: null,
        clickNode: null,
        dialogVisible: false,
        graphData: null,
        dataVisible: false,
        config: {
          background: {
            backgroundColor: '#f7f9ff',
          },
          grid: {
            // size: 10,
            // visible: true
          },
          keyboard: {
            enabled: true
          },
          edgeTextDraggable: true,
        },
        moveData: {},
        nodeList,
      }
    },
    mounted() {
      this.$_initLf();
      if (this.templateId != undefined) {
        this.getTemplate();
      } else {
        canvasData = {}
      }
      this.$_render();
    },
    methods: {
      $_initLf() {
        // 画布配置
        const lf = new LogicFlow({
          ...this.config,
          grid: true,
          plugins: [
            Menu,
            MiniMap,
            Snapshot
          ],
          edgeType: 'bezier',
          container: this.$refs.container,
        })
        this.lf = lf
        this.$_registerNode()
      },
      // 自定义
      $_registerNode() {
        registerVisual(this.lf)
        registerSQL(this.lf)
        registerHttp(this.lf)
        registerFlink(this.lf)
        this.$_render()
      },
      $_render() {
        this.lf.render(canvasData)
        this.$_LfEvent()
      },
      $_refreash() {
        this.lf.render({})
        this.$_LfEvent()
      },
      $_getData() {
        const data = this.lf.getGraphData()
        
      },
      $_LfEvent() {
        this.lf.on('node:click', ({ data }) => {
          this.$data.clickNode = data
          this.$data.dialogVisible = true
        })
        this.lf.on('edge:click', ({ data }) => {
          this.$data.clickNode = data
          this.$data.dialogVisible = true
        })
        this.lf.on('element:click', () => {
          this.hideAddPanel()
        })
        this.lf.on('edge:add', ({ data }) => {
          
        })
        this.lf.on('node:mousemove', ({ data }) => {
          this.moveData = data
        })
        this.lf.on('blank:click', () => {
          this.hideAddPanel()
        })
        this.lf.on('connection:not-allowed', (data) => {
          this.$message({
            type: 'error',
            message: data.msg
          })
        })
        this.lf.on('node:mousemove', () => {
          
        })
      },
      clickPlus(e, attributes) {
        e.stopPropagation()

        const { clientX, clientY } = e
        this.$data.addPanelStyle.top = (clientY - 40) + 'px'
        this.$data.addPanelStyle.left = clientX + 'px'
        this.$data.showAddPanel = true
        this.$data.addClickNode = attributes
      },
      mouseDownPlus(e, attributes) {
        e.stopPropagation()
        
      },
      hideAddPanel() {
        this.$data.showAddPanel = false
        this.$data.addPanelStyle.top = 0
        this.$data.addPanelStyle.left = 0
        this.$data.addClickNode = null
      },
      closeDialog() {
        this.$data.dialogVisible = false
      },
      $_catData() {
        this.$data.graphData = this.$data.lf.getGraphData();
        this.$data.dataVisible = true;
      },
      getTemplate() {
        getTemplateJSON(this.templateId).then(res => {
          canvasData = JSON.parse(res.data.result);
          this.$_render();
        }).catch(() => {
          canvasData = {};
          this.$_render();
        })
      }
    },
    computed: {

    }
  }
</script>
<style>
  /* .logic-flow-view {
    height: 100vh;
    position: relative;
  } */

  /* .demo-title {
    text-align: center;
    margin: 1%;
  } */

  /* .demo-control {
    position: absolute;
    top: 2%;
    right: 2%;
    z-index: 2;
  } */

  /* #LF-view {
    width: calc(100% - 100px);
    height: 80%;
    outline: none;
    margin-left: 50px;
  } */



  /* .add-panel {
    position: absolute;
    z-index: 11;
    background-color: white;
    padding: 10px 5px;
  } */

  .el-drawer__body {
    height: 80%;
    overflow: auto;
    margin-top: -30px;
    z-index: 3;
  }

  @keyframes lf_animate_dash {
    to {
      stroke-dashoffset: 0;
    }
  }
</style>
