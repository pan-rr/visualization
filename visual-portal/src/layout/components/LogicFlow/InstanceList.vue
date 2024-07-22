<template>
    <div>
        <el-table
      :data="tableData"
      style="width: 100%"
      max-height="100%">

      <el-table-column
        prop="templateId"
        label="流程模版ID"
        width="300">
      </el-table-column>
      <el-table-column
        prop="name"
        label="流程模版名称"
        width="120">
      </el-table-column>
      <el-table-column
        prop="templateId"
        label="流程实例ID"
        width="300">
      </el-table-column>
      <el-table-column
      label="流程模版状态"
      width="180">
      <template slot-scope="scope">
        <el-tag size="medium">{{ scope.row.status }}</el-tag>
      </template>
    </el-table-column>
    </el-table>

    <el-pagination
      @size-change="getList"
      @current-change="getList"
      :current-page=currentPage
      :page-sizes="[100, 200, 300]"
      :page-size=pageSize
      layout="total, sizes, prev, pager, next, jumper"
      :total=total>
    </el-pagination>
    </div>
    
  </template>
  
  <script>
import { getInstanceList  } from '../../../api/dag';

    export default {
      name: 'VisualInstanceList',
      data() {
        return {
            tableData: [],
            total: 10,
            pageSize : 10,
            currentPage:1,
        }
      },
      methods: {
        getList(){
         
            let pageable = {
                page : this.currentPage,
                size : this.pageSize
            }
            getInstanceList(pageable).then(res =>{
              let _this = this
              _this.tableData = res.data.result
              _this.total = res.data.total
            })
            
        }
      },
      mounted (){
        this.getList();
      },
      
    }
  </script>