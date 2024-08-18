<template>
  <div>
    <el-table :data="tableData" style="width: 100%" max-height="100%">

      <el-table-column prop="templateId" label="流程模版ID">
      </el-table-column>
      <el-table-column prop="templateName" label="流程模版名称">
      </el-table-column>
      <el-table-column prop="instanceId" label="流程实例ID">
      </el-table-column>
      <el-table-column prop="createTime" label="流程实例创建时间">
      </el-table-column>
      <el-table-column prop="finishTime" label="流程实例完成时间">
      </el-table-column>
      <el-table-column label="流程实例状态">
        <template slot-scope="scope">
          <el-tag size="medium">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作">
        <template slot-scope="scope">
          <el-button type="text" @click.native.prevent="loadTimeLine(scope.row.instanceId)" size="mini">
            查看执行日志
          </el-button>
        </template>
      </el-table-column>
    </el-table>


    <el-pagination @size-change="getList" @current-change="getList" :current-page=currentPage
      :page-sizes="[100, 200, 300]" :page-size=pageSize layout="total, sizes, prev, pager, next, jumper" :total=total>
    </el-pagination>


    <el-dialog title="实例执行日志时间线" :visible.sync="openTimeLine" center>
      <div class="block">
        <el-timeline :reverse="true">
            <el-timeline-item v-for="(item,index) in timeLine" :key="index"  :timestamp="item.time" placement="top" >
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

</template>

<script>
import { getInstanceList, getLogTimeLine } from '../../api/dag';

export default {
  name: 'VisualInstanceList',
  data() {
    return {
      tableData: [],
      total: 10,
      pageSize: 10,
      currentPage: 1,
      timeLine: [],
      openTimeLine: false,
    }
  },
  methods: {
    getList() {
      let pageable = {
        page: this.currentPage,
        size: this.pageSize
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
    this.getList();
  },

}
</script>