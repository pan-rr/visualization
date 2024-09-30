<template>
    <div>
        <el-input v-model="spaceRef.data" :readonly="true" placeholder="请选择空间">
            <template slot="prepend">存储空间:</template>
            <el-select v-model="spaceRef.data" slot="append" placeholder="请选择空间" @change="changeSpace">
                <el-option v-for="item in this.$store.getters.spaceHolder.spaceOptions" :key="item.value"
                    :label="item.label" :value="item.value">
                </el-option>
            </el-select>
        </el-input>
    </div>
</template>

<script>
import { changeChoosenSpace } from '../../../utils/spaceUtil';




export default {

    props: {
        spaceRef: {
            type: Object
        },
    },
    data() {
        return {

        }
    },
    mounted() {
        this.spaceRef.data = this.$store.getters.spaceHolder.chosenSpaceLabel
    },
    methods: {
        changeSpace(value) {
            let label = changeChoosenSpace(value)
            this.spaceRef.data = label
        },
    }

};
</script>