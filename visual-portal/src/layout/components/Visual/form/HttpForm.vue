<template>
    <div>
        <el-form-item label="请求url">
            <el-input v-model="fatherRef.http.url" ></el-input>
        </el-form-item>
        <el-form-item label="请求类型">
            <el-select v-model="fatherRef.http.method" placeholder="请选择请求类型" size="mini">
                <el-option v-for="item in [{ label: 'GET', value: 'GET' }, { label: 'POST', value: 'POST' }]"
                    :key="item.value" :label="item.label" :value="item.value">
                </el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="请求timeout，单位:秒">
            <el-input-number v-model="fatherRef.http.timeout" controls-position="right" :min="1"
                :max="30" :precision="0" :step="1"></el-input-number>
        </el-form-item>
        <el-form-item label="">
            <KVTable title="header配置" :target-wrapper="fatherRef.http" target="header" :expand=true></KVTable>
        </el-form-item>
        <el-form-item label="">
            <KVTable title="请求体body配置" :target-wrapper="fatherRef.http" target="body" :expand=true></KVTable>
        </el-form-item>
        <Editor title="预览HTTP配置" ref="editor"
            :editor-options="{ mode: 'yaml', theme: 'neo', readOnly: true, collaspsable: false }" target="value"
            :target-ref="conf">
        </Editor>
    </div>
</template>

<script>

    import Editor from '../Editor.vue';
    import KVTable from '../KVTable.vue';

    export default {
        components: {
            KVTable, Editor
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
                conf: {
                    value: '',
                },
                timmer: null
            }
        },
        mounted() {
            this.timmer = setInterval(() => {
                this.setValue();
            }, 800)
        },
        methods: {
            setValue() {
                this.conf.value = JSON.stringify(this.fatherRef.http, null, '\t');
                this.$refs.editor.changeValue(this.conf.value);
            }
        },
        destroyed() {
            clearInterval(this.timmer);
        }
    };
</script>