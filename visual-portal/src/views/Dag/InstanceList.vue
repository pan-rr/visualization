<template>
  <div>
    <div>
      <div class="text item">
        <el-input v-model="space" class="input-with-select" :readonly="true" placeholder="请选择空间">
          <template slot="prepend">存储空间:</template>
          <el-select v-model="space" slot="append" placeholder="请选择空间" @change="changeSpace">
            <el-option v-for="item in spaceOptions" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-input>
      </div>
    </div>
    <div>
      <el-table :data="tableData" style="width: 100%" max-height="100%" border stripe @filter-change="filterChange">
        <el-table-column align="center" prop="templateId" label="流程模版ID">
        </el-table-column>
        <el-table-column align="center" prop="templateName" label="流程模版名称">
        </el-table-column>
        <el-table-column align="center" prop="instanceId" label="流程实例ID">
        </el-table-column>
        <el-table-column align="center" prop="createTime" label="流程实例创建时间">
        </el-table-column>
        <el-table-column align="center" prop="finishTime" label="流程实例完成时间">
        </el-table-column>
        <el-table-column align="center" label="流程实例状态" :filters="statusOptions" column-key="status">

          <template slot-scope="scope">
            <el-tag size="medium">{{ scope.row.status }}</el-tag>
          </template>

        </el-table-column>
        <el-table-column align="center" fixed="right" label="操作">
          <template slot-scope="scope">
            <el-button type="text" @click.native.prevent="loadTimeLine(scope.row.instanceId)" size="mini">
              查看执行日志
            </el-button>
          </template>
        </el-table-column>
      </el-table>


      <el-pagination @size-change="changeSize" @current-change="changePage" @prev-click="changePage"
        @next-click="changePage" :current-page=currentPage :page-sizes="[10, 20, 50, 100]" :page-size=pageSize
        layout="total, sizes, prev, pager, next, jumper" :total=total>
      </el-pagination>


      <el-dialog title="实例执行日志时间线" :visible.sync="openTimeLine" center>
        <div class="block">
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

import { getInstanceList, getLogTimeLine } from '../../api/dag';

export default {
  name: 'VisualInstanceList',
  data() {
    return {
      space: '',
      spaceOptions: [],
      tableData: [],
      total: 10,
      pageSize: 10,
      currentPage: 1,
      timeLine: [],
      openTimeLine: false,
      choosenStatus: [],
      statusOptions: [{ text: '完成', value: '1' }, { text: '待执行', value: '-4' }
        , { text: '终止', value: '-2' }]
    }
  },
  methods: {
    filterChange(filter) {
      if (filter['status']) {
        this.choosenStatus = Object.values(filter['status']).map(i => parseInt(i))

      }
    },
    getSpace() {
      let arr = this.$store.getters.userInfo.space
      this.spaceOptions = arr.map((i, idx) => { return { value: i, label: i } })
    },
    changeSpace(value) {
      this.space = value
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
        param: {
          space: this.space,
          status: this.choosenStatus
        }
      }
      getInstanceList(pageable).then(res => {
        let _this = this
        _this.tableData = res.data.result
        _this.total = res.data.total
      })
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
    this.getSpace();
    this.changeSpace(this.spaceOptions[0].value)
    this.choosenStatus = this.statusOptions.map(o => parseInt(o.value))
    this.getList();
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
      immediate: true,
      deep: true,
      handler(newVal, oldVal) {
        this.getList()
      }
    },
    space:{
      immediate: true,
      deep: true,
      handler(newVal, oldVal) {
        this.getList()
      }
    }
  }
}
</script>