<template>
    <div>
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="权限归属者ID：">
                <el-input v-model="searchForm.tenantId" placeholder="权限归属者ID" readonly></el-input>
            </el-form-item>
            <el-form-item label="权限名称：">
                <el-input v-model="searchForm.permissionName" placeholder="权限名称"></el-input>
            </el-form-item>
            <el-button-group>
                <el-button type="primary" @click="getList">查询</el-button>
                <el-button type="primary" @click="dialogVisible = true">新建权限</el-button>
            </el-button-group>
        </el-form>
        <el-table :data="permissionList" border stripe>
            <!-- <el-table-column align="center" prop="tenantId" label="权限归属者ID">
            </el-table-column> -->
            <el-table-column align="center" prop="tenantName" label="权限归属者名称">
            </el-table-column>
            <!-- <el-table-column align="center" prop="permissionId" label="权限ID">
            </el-table-column> -->
            <el-table-column align="center" prop="permissionName" label="权限名称">
            </el-table-column>
            <el-table-column align="center" prop="resourceList" label="权限资源">
                <template slot-scope="scope">
                    <p v-for="item in scope.row.resourceList">{{ item }}</p>
                </template>
            </el-table-column>
            <el-table-column align="center" fixed="right" label="操作">
                <template slot-scope="scope">
                    <el-button @click="selectRow(scope.row)" plain>赋权</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="新建权限" :visible.sync="dialogVisible" width="53%">
            <div>
                <el-form ref="createForm" :model="createForm">
                    <el-form-item label="权限归属者名称：">
                        <el-input v-model="createForm.tenantName" readonly></el-input>
                    </el-form-item>

                    <el-form-item label="权限归属者ID：">
                        <el-input v-model="createForm.tenantId" readonly></el-input>
                    </el-form-item>

                    <el-form-item label="权限名称：">
                        <el-input v-model="createForm.permissionName"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-transfer :titles="['可选资源', '已选资源']" v-model="createForm.chossenOptions"
                            :data="resourceOptions"></el-transfer>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="submitCreatePermission">立即创建</el-button>
                        <el-button type="primary" @click="closeDialog">取消</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </el-dialog>

        <el-dialog title="人员赋权" :visible.sync="drawerVisible" width="60%" center>
            <el-descriptions border>
                <el-descriptions-item>
                    <template slot="label">
                        权限名称
                    </template>
                    {{ drawerData.permissionName }}
                </el-descriptions-item>
                <el-descriptions-item>
                    <template slot="label">
                        权限ID
                    </template>
                    {{ drawerData.permissionId }}
                </el-descriptions-item>
            </el-descriptions>
            <el-input placeholder="请输入被赋权对象OA" v-model="drawerData.oa">
                <template slot="prepend">被赋权对象OA</template>
                <el-button slot="append" icon="el-icon-close" @click="drawerVisible = false">取消</el-button>
                <el-button slot="append" icon="el-icon-user" @click="grant">确认赋权</el-button>
            </el-input>

        </el-dialog>

    </div>
</template>

<script>
import { Message } from 'element-ui';
import { getPermissionList, createPermission, grantPermission } from '../../api/permission';
import { getResourceOption } from '../../api/resource';
import ResourceCreate from '../../layout/components/Resource/ResourceCreate.vue';
import tenantUtil from '../../utils/tenantUtil';

export default {
    name: "PermissionManagement",
    components: { ResourceCreate },
    data() {
        return {
            dialogVisible: false,
            drawerVisible: false,
            drawerData: {},
            tenant: {},
            resourceMap: new Map(),
            permissionList: [],
            resourceOptions: [],
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
                permissionName: '',
            }
        }
    },
    methods: {
        init() {
            this.tenant = tenantUtil(this.$store)
            this.createForm = this.$options.data().createForm
            this.createForm.tenantId = this.tenant.id;
            this.createForm.tenantName = this.tenant.name;
            this.searchForm = this.$options.data().searchForm
            this.searchForm.tenantId = this.tenant.id;
            this.searchForm.tenantName = this.tenant.name;
            this.getOption();
            this.getList();
        },
        getOption() {
            getResourceOption(this.tenant.id).then(res => {
                this.resourceOptions = res.data.result;
                for (let item of this.resourceOptions) {
                    this.resourceMap.set(item.key, item.label)
                }
            })
        },
        getList() {
            getPermissionList(this.searchForm).then(res => {
                this.permissionList = res.data.result
                for (let item of this.permissionList) {
                    item.tenantName = this.tenant.name
                    item.resourceList = item.resourceList.map(i => { return this.resourceMap.get(i) })
                }
            })
        },
        closeDialog(flag) {
            this.dialogVisible = false
            if (flag) {
                this.init();
            }
        },
        submitCreatePermission() {
            this.createForm.resourceList = this.createForm.chossenOptions.join(",")
            createPermission(this.createForm).then(res => {
                this.closeDialog(true)
            })
        },
        selectRow(row) {

            this.drawerVisible = true
            this.drawerData = row
        },
        grant() {
            console.log(this.drawerData)
            grantPermission(this.drawerData).then(res => {
                Message({
                    message: res.data.result,
                    type: 'success',
                    duration: 5 * 1000,
                })
                this.drawerVisible = false
            })
        }
    },
    mounted() {
        this.init();
    }
};
</script>