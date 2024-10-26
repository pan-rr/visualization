<template>
    <div>
        <el-form-item label="需执行的SQL" v-if="viewType === 'in'">
            <Tips message="查询语句"></Tips>
            <el-input v-model="fatherRef.script"></el-input>
        </el-form-item>
        <div style="line-height: 150%">
            <el-switch v-model="useDataSourceConfig" active-text="使用配置数据源" inactive-text="使用临时数据源"
                @change="changeSwitch"></el-switch>
        </div>
        <div v-if="useDataSourceConfig">
            <el-form-item label="已配置的数据源">
                <el-select v-model="fatherRef.param.dataSourceId" placeholder="请选择">
                    <el-option v-for="item in dataSourceOptions" :key="item.value" :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
            </el-form-item>
        </div>
        <div v-else>
            <el-form-item label="数据库URL">
                <el-input v-model="fatherRef.param.url"></el-input>
            </el-form-item>
            <el-form-item label="数据库账号">
                <el-input v-model="fatherRef.param.username"></el-input>
            </el-form-item>
            <el-form-item label="数据库密码">
                <el-input v-model="fatherRef.param.password"></el-input>
            </el-form-item>
        </div>
    </div>
</template>

<script>


import { getDataSourceOptions } from '../../../../api/dag';
import Tips from '../Tips.vue';




export default {
    components: {
        Tips
    },
    props: {
        fatherRef: {
            type: Object
        },
        viewType: {
            type: String
        }
    },
    data() {
        return {
            useDataSourceConfig: false,
            dataSourceOptions: [],
        }
    },
    mounted() {

    },
    methods: {
        changeSwitch(value) {
            this.useDataSourceConfig = value;
            if (value) {
                this.getDataSources()
            }
        },
        getDataSources() {
            getDataSourceOptions(this.$store.getters.spaceHolder.chosenSpaceLabel).then(res => {
                this.dataSourceOptions = res.data.result;
            })
        }
    },

};
</script>