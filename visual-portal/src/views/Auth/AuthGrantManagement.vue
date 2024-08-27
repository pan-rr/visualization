<template>
    <div>
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="权限归属者ID：">
                <span>{{ searchForm.tenantId }}</span>
                <!-- <el-input v-model="searchForm.tenantId" placeholder="权限归属者ID" readonly></el-input> -->
            </el-form-item>
            <el-form-item label="已赋权人员OA：">
                <el-input v-model="searchForm.oa" placeholder="已赋权人员OA"></el-input>
            </el-form-item>
            <el-button-group>
                <el-button type="primary" @click="getList">查询</el-button>
            </el-button-group>
        </el-form>
        <el-table :data="grantViewList" border stripe>
            <el-table-column align="center" prop="tenantName" label="权限归属者名称">
            </el-table-column>
            <el-table-column align="center" prop="username" label="用户名">
            </el-table-column>
            <el-table-column align="center" prop="oa" label="用户OA">
            </el-table-column>
            <el-table-column align="center" prop="permissionName" label="权限名称">
            </el-table-column>
            <el-table-column align="center" fixed="right" label="操作">
                <template slot-scope="scope">
                    <el-button @click="retract(scope.row)" type="warning" plain>撤销权限</el-button>
                </template>
            </el-table-column>
        </el-table>





    </div>
</template>

<script>
import { Message } from 'element-ui';
import { getGrantViewList, retractPermission } from '../../api/permission';
import ResourceCreate from '../../layout/components/Resource/ResourceCreate.vue';
import tenantUtil from '../../utils/tenantUtil';

export default {
    name: "AuthGrantManagement",
    components: { ResourceCreate },
    data() {
        return {
            dialogVisible: false,
            tenant: {},
            grantViewList: [],
            createForm: {
                tenantId: '',
                tenantName: '',
                permissionName: '',
                resourceList: '',
                chossenOptions: [],
            },
            searchForm: {
                tenantId: '',
                tenantName: '',
                oa: '',
            }
        }
    },
    methods: {
        init() {
            this.tenant = tenantUtil(this.$store)
            this.searchForm = this.$options.data().searchForm
            this.searchForm.tenantId = this.tenant.id;
            this.searchForm.tenantName = this.tenant.name;
            this.getList();
        },

        getList() {
            getGrantViewList(this.searchForm).then(res => {
                this.grantViewList = res.data.result
                for (let item of this.grantViewList) {
                    item.tenantName = this.tenant.name
                }
            })
        },
        closeDialog(flag) {
            this.dialogVisible = false
            if (flag) {
                this.init();
            }
        },
        retract(row){
            retractPermission({
                oa : row.oa,
                permissionId : row.permissionId
            }).then(res=>{
                this.getList();
                Message({
                    message: res.data.result,
                    type: 'success',
                    duration: 5 * 1000,
                })
            })
        }


    },
    mounted() {
        this.init();
    }
};
</script>