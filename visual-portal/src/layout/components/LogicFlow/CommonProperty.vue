<template>
  <div>
    <el-form :model="visualTask">
      <el-form-item label="节点名称">
        <el-input v-model="visualTask.text"></el-input>
      </el-form-item>
      <div v-if="taskType === 'SQL'">
        <el-form-item label="需执行的SQL">
          <el-input v-model="visualTask.script"></el-input>
        </el-form-item>
        <el-form-item label="数据库URL">
          <el-input v-model="visualTask.url"></el-input>
        </el-form-item>
        <el-form-item label="数据库账号">
          <el-input v-model="visualTask.username"></el-input>
        </el-form-item>
        <el-form-item label="数据库密码">
          <el-input v-model="visualTask.password"></el-input>
        </el-form-item>
      </div>
      <div v-else-if="taskType === 'visual'">
        <div>
          <div>
            Visual任务配置进度
            <el-button-group>
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
          <el-divider></el-divider>
        </div>
        <div v-if="visualTask.step === 0">
          <div>
            输入视图配置:
            <el-divider direction="vertical"></el-divider>
            <el-select v-model="inputView" placeholder="请选择视图类型" size="mini">
              <el-option v-for="item in [{ label: 'csv', value: 'csv' }, { label: 'jdbc', value: 'jdbc' }]"
                :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
            <el-button @click="addIntputView" plain size="mini">新增输入视图</el-button>
          </div>
          <div v-for="(view, index) in visualTask.input ">
            <div> 序号-{{ index }}<el-divider direction="vertical"></el-divider>{{ view.viewType }}视图
              <el-divider direction="vertical"></el-divider>
              <el-button @click="deleteInputView(index)" plain size="mini">删除该输入视图</el-button>
            </div>
            <el-form-item label="进入engine使用的表名">
              <el-input v-model="visualTask.input[index].tableName"></el-input>
            </el-form-item>
            <div v-if="visualTask.input[index].viewType === 'csv'">
              <el-form-item label="csv存放路径">
                <el-input v-model="visualTask.input[index].filePath"></el-input>
              </el-form-item>
              <KVTable :view="visualTask.input[index]"></KVTable>
            </div>
            <div v-else-if="visualTask.input[index].viewType === 'jdbc'">
              <el-form-item label="需执行的SQL">
                <el-input v-model="visualTask.input[index].script"></el-input>
              </el-form-item>
              <el-form-item label="数据库URL">
                <el-input v-model="visualTask.input[index].param.url"></el-input>
              </el-form-item>
              <el-form-item label="数据库账号">
                <el-input v-model="visualTask.input[index].param.username"></el-input>
              </el-form-item>
              <el-form-item label="数据库密码">
                <el-input v-model="visualTask.input[index].param.password"></el-input>
              </el-form-item>
            </div>
          </div>
        </div>
        <div v-else-if="visualTask.step === 1">
          <div>
            输出视图配置:
            <el-divider direction="vertical"></el-divider>
            <el-select v-model="outputView" placeholder="请选择视图类型" size="mini" @change="changeOutput">
              <el-option
                v-for="item in [{ label: 'csv', value: 'csv' }, { label: 'jdbc', value: 'jdbc' }, { label: 'console', value: 'console' }, { label: 'excel', value: 'excel' }]"
                :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </div>

          <el-form-item label="查询SQL">
            <el-input v-model="visualTask.output.script"></el-input>
          </el-form-item>

          <div v-if="visualTask.output.viewType === 'csv'">
            <el-form-item label="csv存放路径">
              <el-input v-model="visualTask.output.filePath"></el-input>
            </el-form-item>
            <KVTable :view="visualTask.output" ></KVTable>


          </div>
          <div v-if="visualTask.output.viewType === 'excel'">
            <el-form-item label="excel存放路径">
              <el-input v-model="visualTask.output.filePath"></el-input>
            </el-form-item>
            <TabTable :param="visualTask.output.param"></TabTable>
          </div>
          <div v-else-if="visualTask.output.viewType === 'jdbc'">
            <el-form-item label="数据库URL">
              <el-input v-model="visualTask.output.param.url"></el-input>
            </el-form-item>
            <el-form-item label="数据库账号">
              <el-input v-model="visualTask.output.param.username"></el-input>
            </el-form-item>
            <el-form-item label="数据库密码">
              <el-input v-model="visualTask.output.param.password"></el-input>
            </el-form-item>
          </div>
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
import TaskBuilder from '../../../model/TaskBuilder';
import TabTable from '../Visual/TabTable.vue';
import KVTable from '../Visual/KVTable.vue';


export default {
  components: { vueJsonEditor, TabTable, KVTable },
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
      if(properties.output){
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
      this.visualTask.switchOutputView(this.outputView);
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
