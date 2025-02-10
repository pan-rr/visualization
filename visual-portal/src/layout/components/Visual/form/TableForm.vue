<template>
    <div>
        <el-form-item label="">
            <el-tabs type="border-card">
                <el-tab-pane label="Table SQL配置">
                    <Editor title="SQL语句间请用';'隔开'" :editor-options="{ mode: 'sql' }" target="sql"
                        :target-ref="fatherRef.table"></Editor>
                    <el-card class="box-card">
                        <div slot="header" class="clearfix">
                            <span>语法检验</span>
                            <el-button style="float: right;padding: 3px 0;" size="midumn"
                                @click="validate">查看检验结果</el-button>
                        </div>
                        <div v-if="errors?.length === 0">
                            <el-empty description="无语法错误或未进行校验"></el-empty>
                        </div>
                        <div v-else v-for="e in errors" :key="e" class="text item">
                            {{ e }}
                        </div>
                    </el-card>
                </el-tab-pane>
                <el-tab-pane label="Table SQL使用说明">
                    <vue_markdown>{{ this.md }}</vue_markdown>
                </el-tab-pane>
            </el-tabs>
        </el-form-item>
    </div>
</template>

<script>

import { Base64 } from 'js-base64';
import Editor from '../Editor.vue';
import vue_markdown from 'vue-markdown';
import { introduction } from '../../../../assets/text/table'
import { validate } from '../../../../api/table';



export default {
    components: {
        Editor, vue_markdown
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
            md: Base64.decode(introduction),
            errors: []
        }
    },
    methods: {
        validate() {
            validate(this.fatherRef.table.sql).then(res => {
                this.errors = res?.data?.result[0] ? res.data.result : [];
            })
        }
    },
    mounted() {

    }
};
</script>