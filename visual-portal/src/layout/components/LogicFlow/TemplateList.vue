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
        prop="version"
        label="发布时间"
        width="120">
      </el-table-column>
      <el-table-column
      label="流程模版状态"
      width="180">
      <template slot-scope="scope">
        <el-tag size="medium">{{ scope.row.status }}</el-tag>
      </template>
    </el-table-column>
      <el-table-column
        fixed="right"
        label="操作"
        width="120">
        <template slot-scope="scope">
          <el-button
            @click.native.prevent="createInstance(scope.row.templateId)"
            type="text"
            size="mini">
            运行实例
          </el-button>
          <el-button
            @click.native.prevent="disableTemplate(scope.row.status,scope.row.templateId)"
            type="text"
            size="mini"
            :disabled = "scope.row.status === '禁用'"
            >
            停用
          </el-button>
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
import { createInstanceById, getTemplateList,disableTemplateById  } from '../../../api/dag';

    export default {
      name: 'VisualTemplateList',
      data() {
        return {
            tableData: [],
            total: 10,
            pageSize : 10,
            currentPage:1,
        }
      },
      methods: {
        createInstance(templateId) {
          createInstanceById(templateId)
        },
        deleteRow(index, rows) {
          rows.splice(index, 1);
        },
        disableTemplate(status,templateId){
          disableTemplateById(templateId)
          this.$router.go(0)
        },
        getList(){
            let pageable = {
                page : this.currentPage,
                size : this.pageSize
            }
            getTemplateList(pageable).then(res =>{
              let _this = this
              _this.tableData = res.data.result
              _this.total = res.data.total
              // console.log(_this.tableData)
            })
            
        }
      },
      mounted (){
        this.getList();
      },
      
    }
  </script>