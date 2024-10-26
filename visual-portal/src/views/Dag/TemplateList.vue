<template>
  <div>

    <div>
      <space-selector :space-ref="spaceRef"></space-selector>
    </div>
    <div>
      <el-table :data="tableData" style="width: 100%" max-height="100%" border stripe @filter-change="filterChange">
        <el-table-column  type="expand">
          <template #default="props">
            <div>
              <CanvasReadonly :templateId="props.row.templateId"></CanvasReadonly>
            </div>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="templateId" label="流程模版ID">
        </el-table-column>
        <el-table-column align="center" prop="name" label="流程模版名称">
        </el-table-column>
        <el-table-column align="center" prop="version" label="发布时间">
        </el-table-column>
        <el-table-column align="center" label="执行优先级">
          <template slot-scope="scope">
            <el-input-number @change="changePriority(scope.row.templateId, scope.row.priority)"
              v-model="scope.row.priority" controls-position="right" size="mini" :min="1" :max="10"></el-input-number>
          </template>
        </el-table-column>
        <el-table-column align="center" label="重试次数">
          <template slot-scope="scope">
            <el-input-number @change="changeRetryCount(scope.row.templateId, scope.row.retryCount)"
              v-model="scope.row.retryCount" controls-position="right" size="mini" :min="3"></el-input-number>
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
            <el-button @click.native.prevent="createInstance(scope.row.templateId)" type="text" size="mini">
              运行实例
            </el-button>
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


export default {
  name: 'VisualTemplateList',
  components: {
    SpaceSelector, CanvasReadonly
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
      choosenStatus: [],
      statusOptions: [],
      lastParam:'',
    }
  },
  computed: {
    space() {
      return this.spaceRef.data;
    }
  },
  methods: {
    changeStatus(scope) {
      changeTemplateStatus(scope.row.templateId, scope.row.status)
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
          space: this.space,
          status: this.choosenStatus
        }
      }
      let str = JSON.stringify(pageable);
      if(this.lastParam === str){
        return;
      }else{
        this.lastParam = str;
      }
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
    this.getList();
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
    choosenStatus: {
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