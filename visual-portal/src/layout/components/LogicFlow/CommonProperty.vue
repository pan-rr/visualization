<template>
  <div>
    <el-form :model="visualTask" style="margin: auto;">
      <el-card shadow="never">
        <el-form-item label="节点名称">
          <el-input v-model="visualTask.text"></el-input>
        </el-form-item>
      </el-card>
      <div v-if="taskType === 'SQL'">
        <el-card shadow="never">
          <SQLForm :fatherRef="visualTask"></SQLForm>
          <el-form-item>
            <el-button plain type="primary" @click="onSubmit">保存</el-button>
          </el-form-item>
        </el-card>
      </div>
      <div v-else-if="taskType === 'VISUAL'">

        <el-card shadow="never">

          <div slot="header" class="clearfix">
            <el-tag>任务配置进度</el-tag>
            <el-button-group style="float: right; ">
              <el-button plain size="mini"
                @click="visualTask.step = visualTask.step > 0 ? visualTask.step - 1 : 0">上一进度</el-button>
              <el-button plain size="mini"
                @click="visualTask.step = visualTask.step == 2 ? 2 : visualTask.step + 1">下一进度</el-button>
            </el-button-group>
          </div>
          <el-steps simple :active="visualTask.step" finish-status="success">
            <el-step title="配置输入视图"></el-step>
            <el-step title="配置输出视图"></el-step>
            <el-step title="提交表单"></el-step>
          </el-steps>

        </el-card>

        <div v-if="visualTask.step === 0">
          <el-card shadow="never">
            <div>
              <el-tag>输入视图</el-tag>
              <div style="float: right;">
                <el-select v-model="inputView" placeholder="请选择视图类型" size="mini">
                  <el-option v-for="item in [{ label: 'csv', value: 'csv' }, { label: 'jdbc', value: 'jdbc' }]"
                    :key="item.value" :label="item.label" :value="item.value">
                  </el-option>
                </el-select>
                <el-button @click="addIntputView" plain size="mini">新增输入视图</el-button>
              </div>
            </div>
          </el-card>
          <div v-for="(view, index) in visualTask.input">
            <el-card shadow="never">
              <div>

                <el-tag size="mini">视图序号：{{ index }}</el-tag>
                <el-divider direction="vertical"></el-divider>
                <el-tag size="mini">{{ view.viewType }}视图</el-tag>
                <el-divider direction="vertical"></el-divider>
                <el-button @click="deleteInputView(index)" plain size="mini">删除该输入视图</el-button>
              </div>
              <el-form-item label="进入engine使用的表名">
                <Tips message="数据加载进engine后使用的表名"></Tips>
                <el-input v-model="visualTask.input[index].tableName"></el-input>
              </el-form-item>
              <div v-if="visualTask.input[index].viewType === 'csv'">
                <el-form-item label="csv存放路径">
                  <Tips message="文件在存储空间的相对路径，若果是模版共享的，请放在spaceShare文件夹下"></Tips>
                  <el-input v-model="visualTask.input[index].filePath"></el-input>
                </el-form-item>
                <KVTable :view="visualTask.input[index]"></KVTable>
              </div>
              <div v-else-if="visualTask.input[index].viewType === 'jdbc'">
                <JDBCForm :fatherRef="visualTask.input[index]" :view-type="'in'"></JDBCForm>
              </div>

            </el-card>
          </div>
        </div>
        <div v-else-if="visualTask.step === 1">
          <el-card shadow="never">
            <div>
              输出视图配置:
              <el-divider direction="vertical"></el-divider>
              <el-select v-model="outputView" placeholder="请选择视图类型" size="mini" @change="changeOutput">
                <el-option
                  v-for="item in [{ label: 'csv', value: 'csv' }, { label: 'jdbc', value: 'jdbc' }, { label: 'console(本地调试用)', value: 'console' }, { label: 'excel', value: 'excel' }]"
                  :key="item.value" :label="item.label" :value="item.value">
                </el-option>
              </el-select>
            </div>
          </el-card>
          <el-card shadow="never">
            <el-form-item label="查询SQL">
              <el-input v-model="visualTask.output.script"></el-input>
            </el-form-item>

            <div v-if="visualTask.output.viewType === 'csv'">
              <el-form-item label="csv存放路径">
                <Tips message="文件在存储空间的相对路径，若果是模版共享的，请放在spaceShare文件夹下"></Tips>
                <el-input v-model="visualTask.output.filePath"></el-input>
              </el-form-item>
              <KVTable :view="visualTask.output"></KVTable>
            </div>
            <div v-if="visualTask.output.viewType === 'excel'">
              <el-form-item label="excel存放路径">
                <Tips message="文件在存储空间的相对路径，若果是模版共享的，请放在spaceShare文件夹下"></Tips>
                <el-input v-model="visualTask.output.filePath"></el-input>
              </el-form-item>
              <TabTable :param="visualTask.output.param" excludeSQL="true"></TabTable>
            </div>
            <div v-else-if="visualTask.output.viewType === 'jdbc'">
              <JDBCForm :fatherRef="visualTask.output" :view-type="'out'"></JDBCForm>
            </div>
          </el-card>
        </div>

        <div v-if="visualTask.step === 2">
          <el-result icon="info" title="进度完成" subTitle="请点击确定保存作，请提交前检查配置项，配置项只有运行时才检测配置的正确性">
            <template slot="extra">
              <el-form-item>
                <el-button plain type="primary" @click="onSubmit">保存</el-button>
              </el-form-item>
            </template>
          </el-result>
        </div>

      </div>

    </el-form>

  </div>
</template>
<script>

import vueJsonEditor from 'vue-json-editor'
import TaskBuilder from '../../../model/TaskBuilder.js';
import TabTable from '../Visual/TabTable.vue';
import KVTable from '../Visual/KVTable.vue';
import Tips from '../Visual/Tips.vue';
import SQLForm from '../Visual/form/SQLForm.vue';
import JDBCForm from '../Visual/form/JDBCForm.vue';


export default {
  components: { vueJsonEditor, TabTable, KVTable, Tips, SQLForm, JDBCForm },
  name: '',
  props: {
    nodeData: Object,
    lf: Object || String,
  },
  mounted() {
    const { properties, text } = this.$props.nodeData
    if (properties) {
      this.taskType = properties.taskType
      let template = TaskBuilder(this.taskType)
      Object.assign(template, this.$data.visualTask, properties)
      this.visualTask = template
      this.visualTask.output
      this.changeOutput()
      if (properties.output) {
        Object.assign(this.visualTask.output, properties.output)
      }
    }
    if (text && text.value) {
      this.$data.text = text.value
    }
  },
  data() {
    return {
      text: '',
      taskType: '',
      paramKey: '',
      paramVal: '',
      visualTask: {},
      inputView: 'csv',
      outputView: 'csv',
    }
  },
  methods: {
    onSubmit() {
      const { id } = this.$props.nodeData
      let submitData;
      submitData = { ...this.visualTask, taskType: this.taskType };
      this.$props.lf.setProperties(id, submitData);
      this.$props.lf.updateText(id, submitData.text);
      this.$emit('onClose')
    },
    changeOutput() {
      if (this.visualTask.switchOutputView) {
        this.visualTask.switchOutputView(this.outputView);
      }
    },
    addIntputView() {
      this.visualTask.addInputView(this.inputView)
    },
    deleteInputView(index) {
      this.visualTask.deleteInputView(index);
    },
  },
}
</script>
<style scoped></style>
