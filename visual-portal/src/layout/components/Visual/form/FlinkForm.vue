<template>
    <div>
        <el-form-item label="Flink集群地址">
            <span>例:http://localhost:8081</span>
            <el-input v-model="fatherRef.flink.site"></el-input>
        </el-form-item>
        <el-form-item label="Flink JarId">
            <el-input v-model="fatherRef.flink.jarId"></el-input>
        </el-form-item>
        <el-form-item>
            <KVTable title="启动参数(可选)" :target-wrapper="jobOptionsRef" target="options" :expand=true @refresh="handleOptions"></KVTable>
        </el-form-item>
        <el-form-item label="是否等待任务完成">
            <el-select v-model="fatherRef.flink.waitUntilFinish">
                <el-option v-for="item in [{ label: '是', value: true }, { label: '否', value: false }]" :key="item.value"
                    :label="item.label" :value="item.value">
                </el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="检查任务状态间隔，单位:秒">
            <el-input-number v-model="fatherRef.flink.waitInterval" controls-position="right" :min="1" :max="30"
                :precision="0" :step="1"></el-input-number>
        </el-form-item>

    </div>
</template>

<script>

import KVTable from '../KVTable.vue';

export default {
    components: {
        KVTable
    },
    props: {
        fatherRef: {
            type: Object
        },
        submitable: {
            type: Boolean
        }
    },
    data() {
        return {
            jobOptionsRef: {
                options: {

                }
            }
        }
    },
    methods: {
        handleOptions(){
            this.fatherRef.flink.options = JSON.stringify(this.jobOptionsRef.options);
        }
    },
    
};
</script>