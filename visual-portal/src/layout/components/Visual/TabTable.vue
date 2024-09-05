<template>
    <div>

        <el-button size="small" @click="addTab">
            新增一列
        </el-button>

        <el-tabs type="card" closable @tab-remove="removeTab">
            <el-tab-pane v-for="(item, col) in twoDArr" :key="`第${col + 1}列`" :label="`第${col + 1}列`" :name="`${col}`">
                <el-input placeholder="请输入表头" v-model="headerStr">
                    <el-button slot="append" @click="appendHeader(col)">添加表头</el-button>
                </el-input>
                <el-table :data="getList(col)" stripe border>
                    <el-table-column prop="row" label="行号">
                    </el-table-column>
                    <el-table-column prop="name" label="表头名">
                    </el-table-column>
                    <el-table-column fixed="right" label="操作">
                        <template slot-scope="scope">
                            <el-button @click="delHeader(scope.row.name, col)">删除该表头 </el-button>
                        </template>
                    </el-table-column>
                </el-table>

            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<script>




export default {
    props: {
        param: {
            type: Object
        },
    },
    data() {
        return {
            twoDArr: [],
            headerStr: '',
            time: Date.now(),
        }
    },
    mounted() {
        this.twoDArr = this.param['headers']
    },
    methods: {
        delHeader(header, col) {
            this.twoDArr[col] = this.twoDArr[col].filter((item, index) => { return item != header });
            this.time = Date.now()
        },
        addTab() {
            this.twoDArr.push([])
        },
        removeTab(tabName) {
            this.param.set('headers', this.twoDArr.filter((item, index) => { return index != tabName }))
            this.twoDArr = this.param.get('headers')
        },
        appendHeader(col) {
            this.twoDArr.at(col).push(this.headerStr)
            this.headerStr = ''
        },
        getList(col) {
            return this.twoDArr[col].map((item, index) => {
                return {
                    row: index,
                    name: item
                }
            })
        }
    },

};
</script>