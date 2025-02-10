<template>
    <div>
        <el-form-item label="">
            <!-- <Tips message="该配置会按键值对插入或覆盖上下文对应配置"></Tips> -->
            <KVTable v-show="submitable" title="需配置或覆盖的上下文配置" :target-wrapper="fatherRef" target="contextInject"
                :expand=true @refresh="refresh"></KVTable>
            <Editor title="配置项展示" ref="editor"
                :editor-options="{ mode: 'yaml', theme: 'neo', readOnly: true, collaspsable: false }" target="value"
                :target-ref="conf">
            </Editor>
        </el-form-item>
    </div>
</template>

<script>

    import Editor from '../Editor.vue';
    import KVTable from '../KVTable.vue';
    import Tips from '../Tips.vue';




    export default {
        components: {
            Tips, Editor, KVTable
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
                }
            }
        },
        methods: {
            refresh(obj) {
                this.conf.value = JSON.stringify(obj, null, '\t');
                // s = JSON.stringify(JSON.parse(s), null, '\t');
                this.$refs.editor.changeValue(this.conf.value);
            }
        },



    };
</script>