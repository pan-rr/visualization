<template>
  <div>
    <div>
      <space-selector :space-ref="spaceRef"></space-selector>
    </div>
    <div>
      <el-table :data="tableData" style="width: 100%" max-height="100%" border stripe @filter-change="filterChange">
        <el-table-column align="center" prop="templateId" label="流程模版ID">
        </el-table-column>
        <el-table-column align="center" prop="templateName" label="流程模版名称">
        </el-table-column>
        <el-table-column align="center" prop="instanceId" label="实例ID">
        </el-table-column>
        <el-table-column align="center" label="实例状态" :filters="statusOptions" column-key="status">
          <template slot-scope="scope">
            <el-tag size="mini">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="unfinishedTaskCount" label="待完成任务数">
        </el-table-column>
        <el-table-column align="center" prop="createTime" label="创建时间">
        </el-table-column>
        <el-table-column align="center" prop="finishTime" label="完成时间">
        </el-table-column>
        <el-table-column align="center" fixed="right" label="操作">
          <template slot-scope="scope">
            <el-button type="text" @click.native.prevent="loadTimeLine(scope.row.instanceId)" size="mini">
              查看实例执行日志
            </el-button>
            <el-button v-if="scope.row.status == '正常'" type="text" @click.native.prevent="terminateInstance(scope.row)"
              size="mini">
              终止实例
            </el-button>
          </template>
        </el-table-column>
      </el-table>


      <el-pagination @size-change="changeSize" @current-change="changePage" @prev-click="changePage"
        @next-click="changePage" :current-page=currentPage :page-sizes="[10, 20, 50, 100]" :page-size=pageSize
        layout="total, sizes, prev, pager, next, jumper" :total=total>
      </el-pagination>


      <el-dialog title="实例执行日志时间线" :visible.sync="openTimeLine" center>
        <div style="margin: 2%;">
          <span v-if="timeLine.length < 1">暂无时间日志或日志已过期</span>
          <el-timeline :reverse="true" v-else>
            <el-timeline-item v-for="(item, index) in timeLine" :key="index" :timestamp="item.time" placement="top">
              <el-card>
                <h4>任务名称：{{ item.taskName }}</h4>
                <p>任务ID：{{ item.taskId }}</p>
                <p>执行结果：{{ item.message }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="resetTimeLine">取 消</el-button>
          <el-button type="primary" @click="resetTimeLine">确 定</el-button>
        </span>
      </el-dialog>

    </div>
  </div>
</template>

<script>

import { getInstanceList, getLogTimeLine, getStatusOptions, terminateInstance } from '../../api/dag';
import SpaceSelector from '../../layout/components/Visual/SpaceSelector.vue';


export default {
  name: 'VisualInstanceList',
  components: {
    SpaceSelector
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
      timeLine: [],
      openTimeLine: false,
      choosenStatus: [],
      statusOptions: [],
      lastParam:'',
    }
  },
  methods: {
    getStatusOptions() {
      getStatusOptions(2).then(res => {
        this.statusOptions = res.data.result.map(i => { return { "text": i.label, "value": i.value } });
      })
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
      getInstanceList(pageable).then(res => {
        let _this = this
        _this.tableData = res.data.result
        _this.total = res.data.total
      })
    },
    terminateInstance(row) {
      terminateInstance(row.instanceId).then(() => {
        this.getList();
      });
    },
    resetTimeLine() {
      this.openTimeLine = false;
      this.timeLine = [];
    },
    loadTimeLine(instanceId) {
      this.openTimeLine = true;
      getLogTimeLine(instanceId).then(res => {
        this.timeLine = res.data.result
      })
    }
  },
  mounted() {
    this.getStatusOptions();
    this.choosenStatus = this.statusOptions.map(o => parseInt(o.value))
    this.getList();
  },
  computed: {
    space() {
      return this.spaceRef.data;
    }
  },
  watch: {
    currentPage: {
      immediate: true,
      deep: true,
      handler(newVal, oldVal) {
        this.getList()
      }
    },
    pageSize: {
      immediate: true,
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