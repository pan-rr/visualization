<template>
  <div>
    <div style="display: flex; justify-items:flex-start;">
      <el-button plain type='info' icon='el-icon-refresh' @click="getList">刷新</el-button>
      <div style="width: 100%">
        <space-selector :space-ref="spaceRef"></space-selector>
      </div>
    </div>
    <div style="max-height: fit-content;">
      <el-table :data="tableData" style="width: 100%" max-height="100%" border stripe @filter-change="filterChange"
        :default-sort="{ prop: 'templateId', order: 'descending' }" @sort-change="changeSort">
        <el-table-column type="expand">
          <template #default="props">
            <div>
              <el-select v-model="meunOption">
                <el-option key="templateConfig" value="templateGraph" label="查看模版"></el-option>
                <el-option key="metric" value="metric" label="执行监控"></el-option>
              </el-select>
            </div>
            <div>
              <CanvasReadonly v-if="meunOption === 'templateGraph'" :templateId="props.row.templateId">
              </CanvasReadonly>
              <Metric v-else-if="meunOption === 'metric'"
                :options="{ id: props.row.templateId, mode: 'templateId', groupName: props.row.name }">
              </Metric>
            </div>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="name" label="流程模版名称">
        </el-table-column>
        <el-table-column align="center" prop="version" label="发布时间" sortable>
        </el-table-column>
        <el-table-column align="center" label="执行优先级" prop="priority" sortable>
          <template slot-scope="scope">
            <el-input-number @change="changePriority(scope.row.templateId, scope.row.priority)"
              v-model="scope.row.priority" controls-position="right" size="mini" :min="1" :max="10"></el-input-number>
          </template>
        </el-table-column>
        <el-table-column align="center" label="失败重试次数">
          <template slot-scope="scope">
            <el-input-number @change="changeRetryCount(scope.row.templateId, scope.row.retryCount)"
              v-model="scope.row.retryCount" controls-position="right" size="mini" :min="3"></el-input-number>
          </template>
        </el-table-column>
        <el-table-column align="center" label="上下文初始值">
          <template slot-scope="scope">
            {{ JSON.stringify(JSON.parse(scope.row.context), null, '\t') }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="流程模版状态" :filters="statusOptions" column-key="status">
          <template slot-scope="scope">
            <el-select v-model="scope.row.status" size="mini" @change="changeStatus(scope)">
              <el-option v-for="item in statusOptions" :key="item.value" :label="item.text" :value="item.value">
              </el-option>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column align="center" fixed="right" label="操作">
          <template slot-scope="scope">
            <div>
              <el-button :disabled="scope.row.status === '-3'"
                @click.native.prevent="createInstance(scope.row.templateId)" type="text" size="mini">
                运行实例
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination @size-change="changeSize" @current-change="changePage" @prev-click="changePage"
        @next-click="changePage" :current-page=currentPage :page-sizes="[10, 20, 50, 100]" :page-size=pageSize
        layout="total, sizes, prev, pager, next, jumper" :total=total>
      </el-pagination>
    </div>
  </div>
</template>

<script>


import { createInstanceById, getTemplateList, getStatusOptions, changeTemplatePriority, changeTemplateStatus, changeTemplateRetryCount } from '../../api/dag';
import { Message } from 'element-ui'
import SpaceSelector from '../../layout/components/Visual/SpaceSelector.vue';
import CanvasReadonly from './CanvasReadonly.vue';
import Metric from '../Metric/metric.vue';



export default {
  name: 'VisualTemplateList',
  components: {
    SpaceSelector, CanvasReadonly, Metric
  },
  data() {
    return {
      spaceRef: {
        data: ''
      },
      tableData: [],
      total: 10,
      pageSize: 10,
      currentPage: 1,
      opchoosenStatus: [],
      statusOptions: [],
      sort: [{
        field: 'templateId',
        direction: -1
      }],
      meunOption: 'templateGraph'
    }
  },
  computed: {
    space() {
      return this.spaceRef.data;
    }
  },
  methods: {
    changeSort(rule) {
      let arr = this.sort.filter(i => i.field !== rule.prop);
      if (rule.order === 'descending') {
        arr.push({ field: rule.prop, direction: -1 })
      } else if (rule.order === 'ascending') {
        arr.push({ field: rule.prop, direction: 1 })
      }
      this.sort = arr;
      this.getList();
    },
    changeStatus(scope) {
      changeTemplateStatus(scope.row.templateId, scope.row.status).then(() => {
        this.$set(this.tableData[scope.$index], 'status', scope.row.status);
      })
    },
    changePriority(templateId, priority) {
      changeTemplatePriority(templateId, priority);
    },
    changeRetryCount(templateId, retryCount) {
      changeTemplateRetryCount(templateId, retryCount);
    },
    filterChange(filter) {
      if (filter['status']) {
        this.choosenStatus = Object.values(filter['status']).map(i => parseInt(i))
        this.getList();
      }
    },
    changePage(val) {
      this.currentPage = val
    },
    changeSize(val) {
      this.pageSize = val
    },
    createInstance(templateId) {
      createInstanceById(templateId).then(res => {
        let data = res.data
        let msg = `创建实例成功，实例id：${data.result}`
        Message({
          message: msg,
          type: data.message,
          duration: 5 * 1000,
        })
      })
    },
    getList() {
      let pageable = {
        page: this.currentPage,
        size: this.pageSize,
        conditions: {
          space: {
            conditionType: 'eq',
            param: [this.space]
          },
          status: {
            conditionType: 'in',
            param: this.choosenStatus.length > 0 ? this.choosenStatus : [0]
          }
        },
        sort: this.sort
      };
      getTemplateList(pageable).then(res => {
        let _this = this
        _this.tableData = res.data.result
        _this.total = res.data.total
      })

    },
    getStatusOptions() {
      getStatusOptions(1).then(res => {
        this.statusOptions = res.data.result.map(i => { return { "text": i.label, "value": i.value } });
      })
    },
  },
  mounted() {
    this.getStatusOptions();
    this.choosenStatus = this.statusOptions.map(o => parseInt(o.value))
    this.getList(); console.log(this.context)
  },
  watch: {
    currentPage: {
      immediate: false,
      deep: true,
      handler(newVal, oldVal) {
        this.getList()
      }
    },
    pageSize: {
      immediate: false,
      deep: true,
      handler(newVal, oldVal) {
        this.getList()
      }
    },
    space: {
      immediate: false,
      deep: true,
      handler(newVal, oldVal) {
        this.getList()
      }
    }
  }
}
</script>