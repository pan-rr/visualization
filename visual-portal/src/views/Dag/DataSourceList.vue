<template>
  <div>
    <SpaceSelector :spaceRef="spaceRef"></SpaceSelector>
    <div>
      <el-table :data="list" style="width: 100%" max-height="100%" border stripe>
        <el-table-column align="center" prop="name" label="数据源名称">
        </el-table-column>
        <el-table-column align="center" prop="config.url" label="数据源url">
        </el-table-column>
        <el-table-column align="center" prop="config.username" label="数据源账号">
        </el-table-column>
        <el-table-column align="center" prop="config.password" label="数据源密码 ">
        </el-table-column>
        <el-table-column align="center" fixed="right" label="操作">
          <template slot-scope="scope">
            <el-button type="text" @click.native.prevent="deleteDataSource(scope.row.id)" size="mini">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>


import { deleteDataSource, getDataSourceList } from '../../api/dag';
import SpaceSelector from '../../layout/components/Visual/SpaceSelector.vue';

export default {
  components: {
    SpaceSelector
  },
  data() {
    return {
      list: [],
      spaceRef: {
        data: ''
      }
    }
  },
  methods: {
    deleteDataSource(id){
      deleteDataSource(id).then(res=>{
        this.getList();
      })
    },
    getList() {
      let param = {
        space: this.spaceRef.data
      }
      getDataSourceList(param).then(res => {
        this.list = res.data.result
      })
    },

  },
  mounted() {
    this.getList();
  },
  watch: {
    spaceRef: {
      immediate: true,
      deep: true,
      handler(newVal, oldVal) {
        this.getList()
      }
    }
  }
}
</script>