<template>
  <div>
    <div v-if="submitable">
      <space-selector :space-ref="spaceRef"></space-selector>
      <el-input v-model="form.name" clearable>
        <template slot="prepend">
          <el-icon class="el-icon-collection-tag"></el-icon>
          流程模版名称:
        </template>
      </el-input>
    </div>
    <div >
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