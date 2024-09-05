<template>
    <div>
        <el-form-item label="csv可选参数">
            <el-input placeholder="请输入csv可选参数key" v-model="paramKey">
                <template slot="prepend">key</template>
            </el-input>
            <el-input placeholder="请输入csv可选参数value" v-model="paramVal">
                <template slot="prepend"> value</template>
                <template slot="append">
                    <el-button @click="addParam()">新增参数</el-button>
                </template>
            </el-input>
            <el-table :data="getList()" stripe border v-if="time">
                <el-table-column prop="key" label="键">
                </el-table-column>
                <el-table-column prop="value" label="值">
                </el-table-column>
                <el-table-column fixed="right" label="操作">
                    <template slot-scope="scope">
                        <el-button @click="delKey(scope.row.key)">删除此项</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-form-item>
    </div>
</template>

<script>




export default {
    props: {
        view: {
            type: Object
        },
    },
    data() {
        return {
            paramKey: '',
            paramVal: '',
            time : Date.now()
        }
    },
    mounted() {

    },
    methods: {
        delKey(key){
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