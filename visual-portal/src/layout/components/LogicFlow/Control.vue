<template>
  <div>
    <div v-if="submitable">
     
      <!-- <div class="templatePage">
        <div class="templateContext">
          <KVTable title="上下文模版配置" :targetWrapper="contextRef" target="context"></KVTable>
        </div>
        <div class="templateConfig">
          <div class="configRow">
            <div class="configKey">流程模版名称</div>
            <div><el-input v-model="form.name" clearable prefix-icon="el-icon-collection-tag"></el-input></div>
          </div>
          <div class="configRow">
            <div class="configKey">失败重试次数</div>
            <div><el-input-number v-model="form.retryCount" controls-position="right" :min="2"
                :max="10"></el-input-number></div>
          </div>
          <div class="configRow">
            <div class="configKey">执行优先级</div>
            <div><el-input-number v-model="form.priority" controls-position="right" :min="1"
                :max="10"></el-input-number>
            </div>
          </div>
          <div class="configRow">
            <el-button style="width: 100%; background-color: aliceblue;" v-if="submitable" type="plain"
              @click="$_createProcess">发布流程</el-button>
          </div>
        </div>

      </div> -->

      <el-tabs type="card">
        
        <el-tab-pane label="模版属性配置">
          <div class="templateConfig" >
            
            <div class="configRow">
              <div class="configKey">流程模版名称</div>
              <div><el-input v-model="form.name" clearable prefix-icon="el-icon-collection-tag"></el-input></div>
            </div>
            <div class="configRow">
              <div class="configKey">失败重试次数</div>
              <div><el-input-number v-model="form.retryCount" controls-position="right" :min="2"
                  :max="10"></el-input-number></div>
            </div>
            <div class="configRow">
              <div class="configKey">执行优先级</div>
              <div><el-input-number v-model="form.priority" controls-position="right" :min="1"
                  :max="10"></el-input-number>
              </div>
            </div>
            <space-selector :space-ref="spaceRef"></space-selector>
            <div class="configRow">
              <el-button style="width: 100%; " v-if="submitable" type="info" plain
                @click="$_createProcess">发布流程</el-button>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="模版上下文配置">
          <div class="templateContext">
            <KVTable :targetWrapper="contextRef" target="context"></KVTable>
          </div>
        </el-tab-pane>
        
      </el-tabs>
    </div>
    <div>
      <el-button-group style="display: flex; justify-content:left;">
        <el-button type="plain" size="medium" @click="$_zoomIn">放大</el-button>
        <el-button type="plain" size="medium" @click="$_zoomOut">缩小</el-button>
        <el-button type="plain" size="medium" @click="$_zoomReset">大小适应</el-button>
        <el-button type="plain" size="medium" @click="$_translateRest">定位还原</el-button>
        <el-button type="plain" size="medium" @click="$_reset">还原(大小&定位)</el-button>
        <el-button type="plain" size="medium" @click="$_undo" :disabled="undoDisable">上一步(ctrl+z)</el-button>
        <el-button type="plain" size="medium" @click="$_redo" :disabled="redoDisable">下一步(ctrl+y)</el-button>
        <el-button type="plain" size="medium" @click="$_download">下载图片</el-button>
        <el-button type="plain" size="medium" @click="$_catData">查看数据</el-button>
        <el-button v-if="catTurboData" type="plain" size="medium" @click="$_catTurboData">查看turbo数据</el-button>
        <el-button type="plain" size="medium" @click="$_showMiniMap">查看缩略图</el-button>
        <el-button v-if="submitable" type="plain" size="medium" @click="$_createProcess">发布流程</el-button>
      </el-button-group>
    </div>
  </div>
</template>
<script>
import { createTemplate } from '../../../api/dag';
import { Message } from 'element-ui'
import SpaceSelector from '../Visual/SpaceSelector.vue';
import KVTable from '../Visual/KVTable.vue';
import '@/assets/css/visual/canvas.scss';


export default {
  name: 'Control',
  components: { SpaceSelector, KVTable },
  props: {
    lf: Object || String,
    catTurboData: Boolean,
    submitable: Boolean,
  },
  data() {
    return {
      undoDisable: true,
      redoDisable: true,
      graphData: null,
      dataVisible: false,
      templateName: '',
      spaceRef: {
        data: ''
      },
      contextRef: {
        context: {}
      },
      form: {
        name: '',
        logicFlow: {},
        space: '',
        context: '',
        retryCount: 2,
        priority: 1,
      }
    }
  },
  mounted() {
    this.$props.lf.on('history:change', ({ data: { undoAble, redoAble } }) => {
      this.$data.undoDisable = !undoAble
      this.$data.redoDisable = !redoAble
    });
  },
  methods: {
    $_zoomIn() {
      this.$props.lf.zoom(true);
    },
    $_zoomOut() {
      this.$props.lf.zoom(false);
    },
    $_zoomReset() {
      this.$props.lf.resetZoom();
    },
    $_translateRest() {
      this.$props.lf.resetTranslate();
    },
    $_reset() {
      this.$props.lf.resetZoom();
      this.$props.lf.resetTranslate();
    },
    $_undo() {
      this.$props.lf.undo();
    },
    $_redo() {
      this.$props.lf.redo();
    },
    $_download() {
      this.$props.lf.getSnapshot();
    },
    $_catData() {
      this.$emit('catData');
    },
    $_catTurboData() {
      this.$emit('catTurboData');
    },
    $_showMiniMap() {
      const { lf } = this.$props;
      lf.extension.miniMap.show(lf.graphModel.width - 150, 40)
    },
    $_createProcess() {
      const { lf } = this.$props;
      const data = lf.getGraphData()
      this.form.space = this.spaceRef.data;
      this.form.logicFlow = data;
      this.form.context = JSON.stringify(this.contextRef.context);
      createTemplate(this.form).then(response => {
        if (response?.data?.code === 0) {
          this.$emit('refreash')
          this.form = this.$options.data().form;
          this.contextRef = this.$options.data().contextRef;
          Message({
            message: response?.data?.result,
            type: 'success',
            duration: 5 * 1000,
          })
        }
      });
    },
  },
  computed: {

  }
}
</script>
<style scoped></style>
<style src="@a"