<template>
    <div>
        <el-form-item label="csv可选参数">
            <Tips
                message="样例，括号包裹键值对。（headers:no|name|sex）   （fieldSeparator:|）  （caseSensitiveColumnNames:true） （charset:utf-8） （writeColumnHeader: false）">
            </Tips>
            <!-- <el-input placeholder="请输入csv可选参数key" v-model="paramKey">
                <template slot="prepend">key</template>
</el-input>
<el-input placeholder="请输入csv可选参数value" v-model="paramVal">
    <template slot="prepend"> value</template>
    <template slot="append">
                    <el-button @click="addParam()">新增参数</el-button>
                </template>
</el-input> -->

            <el-container>
                <el-header>
                    <el-form :inline="true">
                        <el-form-item label="CSV参数键：">
                            <el-input v-model="paramKey" placeholder="请输入CSV可选参数键"></el-input>
                        </el-form-item>
                        <el-form-item label="CSV参数值：">
                            <el-input v-model="paramVal" placeholder="请输入CSV可选参数值"></el-input>
                        </el-form-item>
                        <el-form-item>
                            <el-button @click="addParam()">新增参数</el-button>
                        </el-form-item>
                    </el-form>
                </el-header>
                <el-main>
                    <el-table :data="getList()" stripe border v-if="time">
                        <el-table-column align="center" prop="key" label="键">
                        </el-table-column>
                        <el-table-column align="center" prop="value" label="值">
                        </el-table-column>
                        <el-table-column align="center" fixed="right" label="操作">
                            <template slot-scope="scope">
                                <el-button @click="delKey(scope.row.key)">删除此项</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-main>
            </el-container>



        </el-form-item>
    </div>
</template>

<script>

import Tips from './Tips.vue';




export default {
    components: {
        Tips
    },
    props: {
        view: {
            type: Object
        },
    },
    data() {
        return {
            paramKey: '',
            paramVal: '',
            time: Date.now()
        }
    },
    mounted() {

    },
    methods: {
        delKey(key) {
            this.view.deleteParam(key);
            this.time = Date.now();
        },
        addParam() {
            this.view.appendParam(this.paramKey, this.paramVal)
            this.empty();
        },
        empty() {
            this.paramKey = '';
            this.paramVal = '';
        },
        getList() {
            return Object.keys(this.view.param).map(item => {
                return {
                    key: item,
                    value: this.view.param[item]
                }
            });
        }
    },

};
</script>