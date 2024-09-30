<template>
  <div>

    <div>
      <space-selector :space-ref="spaceRef"></space-selector>
      <el-input v-model="templateName" clearable>
        <template slot="prepend">
          <el-icon class="el-icon-collection-tag"></el-icon>
          流程模版名称:
        </template>
      </el-input>
    </div>
    <div>
      <el-button-group>
        <el-button type="plain" size="small" @click="$_zoomIn">放大</el-button>
        <el-button type="plain" size="small" @click="$_zoomOut">缩小</el-button>
        <el-button type="plain" size="small" @click="$_zoomReset">大小适应</el-button>
        <el-button type="plain" size="small" @click="$_translateRest">定位还原</el-button>
        <el-button type="plain" size="small" @click="$_reset">还原(大小&定位)</el-button>
        <el-button type="plain" size="small" @click="$_undo" :disabled="undoDisable">上一步(ctrl+z)</el-button>
        <el-button type="plain" size="small" @click="$_redo" :disabled="redoDisable">下一步(ctrl+y)</el-button>
        <el-button type="plain" size="small" @click="$_download">下载图片</el-button>
        <el-button type="plain" size="small" @click="$_catData">查看数据</el-button>
        <el-button v-if="catTurboData" type="plain" size="small" @click="$_catTurboData">查看turbo数据</el-button>
        <el-button type="plain" size="small" @click="$_showMiniMap">查看缩略图</el-button>
        <el-button type="plain" size="small" @click="$_createProcess">发布流程</el-button>
      </el-button-group>
    </div>
  </div>
</template>
<script>
import { createTemplate } from '../../../api/dag';
import { Message } from 'element-ui'
import SpaceSelector from '../Visual/SpaceSelector.vue';



export default {
  name: 'Control',
  components: { SpaceSelector },
  props: {
    lf: Object || String,
    catTurboData: Boolean
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
      // this.$emit('createProcess');
      const { lf } = this.$props;
      const data = lf.getGraphData()
      createTemplate({
        name: this.$data.templateName,
        logicFlow: data,
        space: this.spaceRef.data
      }).then(response => {
        if (response?.data?.code === 0) {
          this.$emit('refreash')
          this.templateName = ''
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
