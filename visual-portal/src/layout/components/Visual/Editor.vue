<template>
    <div style="border:1px solid #DCDFE6;width: 100%;">
        <h1 style="width: 100%;border:1% solid #DCDFE6;color: #909399;background-color: #F5F7FA;">
            <p :class="iconStyle" @click="collaspse" v-if="options.collaspsable != false"></p>
            {{ title }}
        </h1>
        <div v-if="show" id="editor">
            <codemirror ref="cm" @ready="onReady" :options="options" @input="inputChange" v-if="targetRef">
            </codemirror>
        </div>

    </div>
</template>

<script>
    import { codemirror } from 'vue-codemirror';
    import "codemirror/lib/codemirror.css";
    import 'codemirror/addon/lint/lint.css'
    // 引入主题 可多个
    import "codemirror/theme/idea.css";
    import "codemirror/theme/neo.css";
    // 引入语言模式 可多个
    import "codemirror/mode/sql/sql";
    import "codemirror/mode/yaml/yaml";
    import "codemirror/mode/javascript/javascript";
    import "codemirror/addon/hint/show-hint";
    import "codemirror/addon/hint/show-hint.css";
    import "codemirror/addon/hint/sql-hint";
    import 'codemirror/addon/hint/javascript-hint';


    export default {
        components: { codemirror },
        props: {
            targetRef: {
                type: Object,
            },
            target: {
                type: String,
            },
            title: {
                type: String,
            },
            editorOptions: {
                type: Object
            },
        },
        data() {
            return {
                show: true,
            }
        },
        mounted(){
            let str = this.targetRef[this.target];
            this.$refs?.cm?.handerCodeChange(str);
        },
        computed: {
            options() {
                let propsOptions = this.editorOptions ? this.editorOptions : {};
                let realOptions = {
                    line: true,
                    lineNumbers: true,
                    // 软换行
                    lineWrapping: true,
                    // tab宽度
                    tabSize: 4,
                    matchBrackets: true,
                    theme: 'idea',
                    mode: 'sql',
                    collaspsable : true,
                    hintOptions: {
                        hint: this.handleShowHint,
                        completeSingle: false,
                    }
                };
                realOptions = { ...realOptions, ...propsOptions }
                return realOptions;
            },
            iconStyle() {
                return this.show ? 'el-icon-arrow-down' : 'el-icon-arrow-right'
            }
        },
        methods: {
            collaspse() {
                this.show = !this.show;
            },
            inputChange(content) {
                this.$nextTick(() => {
                    this.targetRef[this.target] = content
                });
            },
            onReady(cm) {
                cm.on('inputRead', function () {
                    cm.showHint({ completeSingle: false });
                })
            },
            changeValue(str) {
                this.$refs?.cm?.handerCodeChange(str);
            },
        },
        watch: {
            targetRef: {
                immediate: true,
                deep: true,
                handler() {
                    if (this?.targetRef[this.target] === undefined || this?.targetRef[this.target] === '') {
                        this.changeValue('');
                    }
                }
            }

        }
    };
</script>
<style lang="scss">
    .CodeMirror-hints {
        z-index: 30000 !important;
        height: auto !important;
    }

    #editor {
        .CodeMirror {
            height: auto !important;
            overflow-y: scroll !important;
        }
    }
</style>