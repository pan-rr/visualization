<template>
    <div>
        <el-form-item label="需执行的SQL">
            <Tips message="查询语句"></Tips>
            <Editor title="SQL" :editor-options="{ mode: 'sql' }" target="script" :target-ref="fatherRef.sql"></Editor>
        </el-form-item>
        <div style="line-height: 150%">
            <el-switch v-model="useDataSourceConfig" active-text="使用配置数据源" inactive-text="使用临时数据源"
                @change="changeSwitch"></el-switch>
        </div>
        <div v-if="useDataSourceConfig">
            <el-form-item label="已配置的数据源">
                <el-select v-model="fatherRef.sql.dataSourceId" placeholder="请选择">
                    <el-option v-for="item in dataSourceOptions" :key="item.value" :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
            </el-form-item>
        </div>
        <div v-else>
            <el-form-item label="临时数据源URL">
                <el-input v-model="fatherRef.sql.url"></el-input>
            </el-form-item>
            <el-form-item label="临时数据源账号">
                <el-input v-model="fatherRef.sql.username"></el-input>
            </el-form-item>
            <el-form-item label="临时数据源密码">
                <el-input v-model="fatherRef.sql.password"></el-input>
            </el-form-item>
        </div>
    </div>
</template>

<script>

import { getDataSourceOptions } from '../../../../api/dag';
import Editor from '../Editor.vue';
import Tips from '../Tips.vue';




export default {
    components: {
        Tips,Editor
    },
    props: {
        fatherRef: {
            type: Object
        },
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