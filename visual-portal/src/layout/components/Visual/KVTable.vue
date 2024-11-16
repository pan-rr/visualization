<template>
    <div class="container">
        <div class="title">
            <i :class="iconStyle" @click="show = !show">
                {{ title }} <Tips v-if="tips" :message="tips"></Tips>
            </i>
        </div>
        <div v-if="show">
            <div class="input"><el-form :inline="true">
                    <el-form-item label="参数键Key：">
                        <el-input v-model="paramKey" placeholder="请输入可选参数键"></el-input>
                    </el-form-item>
                    <el-form-item label="参数值Value：">
                        <el-input v-model="paramVal" placeholder="请输入可选参数值"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button @click="addParam()">新增参数</el-button>
                    </el-form-item>
                </el-form>
            </div>
            <div class="value">
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
            </div>
        </div>
    </div>
</template>

<script>

    import Tips from './Tips.vue';
    import '@/assets/css/visual/kv-table.scss';

    export default {
        components: {
            Tips
        },
        props: {
            targetWrapper: {
                type: Object
            },
            target: {
                type: String
            },
            title: {
                type: String
            },
            tips: {
                type: String
            },
            expand: {
                type: Boolean,
                default: true,
            }
        },
        data() {
            return {
                paramKey: '',
                paramVal: '',
                time: Date.now(),
                show: false
            }
        },
        mounted() {
            this.show = this.expand || this.show;
        },
        methods: {
            delKey(key) {
                Reflect.deleteProperty(this.targetWrapper[this.target], key);
                this.time = Date.now();
            },
            addParam() {
                if (this.paramKey === '' && this.paramVal === '') return;
                this.targetWrapper[this.target][this.paramKey] = this.paramVal
                this.empty();
            },
            empty() {
                this.paramKey = '';
                this.paramVal = '';
            },
            getList() {
                this.refresh();
                return Object.keys(this.targetWrapper[this.target]).map(item => {
                    return {
                        key: item,
                        value: this.targetWrapper[this.target][item]
                    }
                });
            },
            refresh() {
                this.$emit('refresh', this.targetWrapper[this.target])
            }
        },
        computed: {
            iconStyle() {
                return this.show ? 'el-icon-arrow-down' : 'el-icon-arrow-right'
            }
        }
    };
</script>