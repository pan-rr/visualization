<template>
    <div>
        <h1>资源列表</h1>
        <el-form :inline="true" :model="form" class="demo-form-inline">
            <el-form-item label="资源归属者ID：">
                <span>{{ form.tenantId }}</span>
                <!-- <el-input v-model="form.tenantId" placeholder="资源归属者ID" readonly></el-input> -->
            </el-form-item>
            <el-divider direction="vertical"></el-divider>
            <el-form-item label="资源名称：">
                <el-input v-model="form.resourceName" placeholder="资源名称"></el-input>
            </el-form-item>
            <el-button-group>
                <el-button type="primary" @click="getList">查询</el-button>
                <el-button type="primary" @click="dialogVisible = true">新建资源</el-button>
            </el-button-group>
        </el-form>
        <el-table :data="resourceList" border stripe>
            <!-- <el-table-column align="center" prop="tenantId" label="资源归属者ID">
            </el-table-column> -->
            <el-table-column align="center" prop="tenantName" label="资源归属者ID">
            </el-table-column>
            <!-- <el-table-column align="center" prop="resourceId" label="资源ID">
            </el-table-column> -->
            <el-table-column align="center" prop="resourceName" label="资源名称">
            </el-table-column>
        </el-table>
        <el-dialog title="新建资源" :visible.sync="dialogVisible">
            <ResourceCreate @finish="closeDialog"></ResourceCreate>
        </el-dialog>
    </div>
</template>

<script>
import { getResourceList } from '../../api/resource';
import ResourceCreate from '../../layout/components/Resource/ResourceCreate.vue';
import tenantUtil from '../../utils/tenantUtil';

export default {
    name: "ResourceManagement",
    components: { ResourceCreate },
    data() {
        return {
            dialogVisible: false,
            tenant: {},
            resourceList: [],
            form: {
                tenantId: '',
                resourceName: '',
            }
        }
    },
    methods: {
        init() {
            this.tenant = tenantUtil(this.$store)
            this.form = this.$options.data().form
            this.form.tenantId = this.tenant.id;

        },
        getList() {
            getResourceList(this.form).then(res => {
                this.resourceList = res.data.result
                for (let item of this.resourceList) {
                    item.tenantName = this.tenant.name
                }
            })
        },
        closeDialog(flag) {
            this.dialogVisible = false
            if (flag) this.getList()
        }
    },
    mounted() {
        this.init();
        this.getList();
    }
};
</script>