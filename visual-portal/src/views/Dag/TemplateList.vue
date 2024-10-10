<template>
  <div>

    <div>
      <space-selector :space-ref="spaceRef"></space-selector>
    </div>
    <div>
      <el-table :data="tableData" style="width: 100%" max-height="100%" border stripe @filter-change="filterChange">
        <el-table-column type="expand">
          <template #default="props">
            <div>
              <CanvasReadonly :templateId="props.row.templateId" ></CanvasReadonly>
              <!-- <LFCanvas :templateId="props.row.templateId" ></LFCanvas> -->
            </div>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="templateId" label="流程模版ID">
        </el-table-column>
        <el-table-column align="center" prop="name" label="流程模版名称">
        </el-table-column>
        <el-table-column align="center" prop="version" label="发布时间">
        </el-table-column>
        <el-table-column align="center" label="流程模版状态" :filters="statusOptions" column-key="status">
          <template slot-scope="scope">
            <el-tag size="medium">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" fixed="right" label="操作">
          <template slot-scope="scope">
            <el-button @click.native.prevent="createInstance(scope.row.templateId)" type="text" size="mini">
              运行实例
            </el-button>
            <el-button @click.native.prevent="disableTemplate(scope.row.status, scope.row.templateId)" type="text"
              size="mini" :disabled="scope.row.status === '禁用'">
              停用
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


import { createInstanceById, getTemplateList, disableTemplateById } from '../../api/dag';
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
      statusOptions: [{ text: '正常', value: '0' }, { text: '完成', value: '1' }, { text: '待执行', value: '-4' }
        , { text: '终止', value: '-2' }]
    }
  },
  computed: {
    space() {
      return this.spaceRef.data;
    }
  },
  methods: {
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
    deleteRow(index, rows) {
      rows.splice(index, 1);
    },
    disableTemplate(status, templateId) {
      disableTemplateById(templateId).then(res => {
        this.getList()
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
      getTemplateList(pageable).then(res => {
        let _this = this
        _this.tableData = res.data.result
        _this.total = res.data.total
      })

    },

  },
  computed: {
    space() {
      return this.spaceRef.data;
    }
  },
  mounted() {
    this.choosenStatus = this.statusOptions.map(o => parseInt(o.value))
    this.getList();
  },
  watch: {
    currentPage: {
      // immediate: true,
      deep: true,
      handler(newVal, oldVal) {
        this.getList()
      }
    },
    pageSize: {
      // immediate: true,
      deep: true,
      handler(newVal, oldVal) {
        this.getList()
      }
    },
    choosenStatus: {
      // immediate: true,
      deep: true,
      handler(newVal, oldVal) {
        this.getList()
      }
    },
    space: {
      // immediate: true,
      deep: true,
      handler(newVal, oldVal) {
        this.getList()
      }
    }
  }
}
</script>