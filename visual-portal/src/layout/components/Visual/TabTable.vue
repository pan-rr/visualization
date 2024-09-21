<template>
    <div>

        <div style="line-height: 4em">
            <el-switch v-model="needHeader" active-text="使用自定义表头" inactive-text="使用默认表头"></el-switch>
            <el-button v-if="needHeader" size="mini" @click="addTab">新增一列表头</el-button>
        </div>

        <div v-if="needHeader">

            <el-tabs type="card" closable @tab-remove="removeTab" v-if="time">
                <el-tab-pane v-for="(item, col) in twoDArr" :key="`第${col}列`" :label="`第${col}列`" :name="`${col}`">
                    <el-input placeholder="请输入表头" v-model="headerStr">
                        <el-button slot="append" @click="appendHeader(col)">添加表头</el-button>
                    </el-input>
                    <el-table :data="getList(col)" stripe border>
                        <el-table-column align="center" prop="row" label="行号">
                        </el-table-column>
                        <el-table-column align="center" prop="name" label="表头名">
                        </el-table-column>
                        <el-table-column align="center" fixed="right" label="操作">
                            <template slot-scope="scope">
                                <el-button @click="delHeader(scope.row.name, col)">删除该表头 </el-button>
                            </template>
                        </el-table-column>
                    </el-table>

                </el-tab-pane>
            </el-tabs>
        </div>
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
            needHeader: false,
            twoDArr: [],
            headerStr: '',
            time: Date.now(),
        }
    },
    mounted() {
        this.twoDArr = this.param['headers']
        if(this.twoDArr.length > 0){
            this.needHeader = true
        }
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
            this.param['headers'] = this.twoDArr.filter((item, index) => { return index != tabName })
            this.twoDArr = this.param['headers']
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
    watch: {
        needHeader: {
            handler(oldV, newV) {
                if (!newV) {
                    this.param['headers'] = []
                    this.twoDArr = []
                    this.twoDArr.push([])
                }
            }
        }
    }
};
</script>