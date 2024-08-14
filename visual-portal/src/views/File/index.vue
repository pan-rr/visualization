<template>
    <div>
        <el-card class="box-card" shadow="hover">
            <div class="text item">
                <el-input v-model="space" class="input-with-select" :readonly="true" placeholder="请选择空间">
                    <template slot="prepend">存储空间:</template>
                    <el-select v-model="space" slot="append" placeholder="请选择空间" @change="changeSpace">
                        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
                        </el-option>
                    </el-select>
                </el-input>
            </div>
        </el-card>

        <div>
            <el-card class="box-card" shadow="hover">
                <div slot="header" style="display: flex;">
                    <el-button type="primary" plain icon="el-icon-back" @click="exitDir"></el-button>
                    <el-input v-model="path" :readonly="true">
                        <template slot="prepend">空间Path:</template>
                    </el-input>
                    <el-button type="success" plain icon="el-icon-folder-add" @click="createDir">新建文件夹</el-button>
                </div>
                <el-container v-for="item in files" :key="item.name" type="flex" justify style="">
                    <el-main>
                        <el-link @click="enterDir(item.name)" :disabled="!item.isFolder">
                            <i v-if="item.isFolder" class="el-icon-folder"></i>
                            <i v-else class="el-icon-tickets"></i>
                            {{ item.name }}
                        </el-link>
                    </el-main>
                    <el-button v-if="!item.isFolder" round><i class="el-icon-download"></i>下载</el-button>
                </el-container>
            </el-card>
        </div>

        <div>
            <!-- <el-card  class="box-card" shadow="hover">
                <el-upload  drag action="/" multiple :http-request="handleHttpRequest"
                    :on-remove="handleRemoveFile" >
                    <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                    <div class="el-upload__text">
                        请拖拽文件到此处或 <em>点击此处上传</em>
                    </div>
                </el-upload>
            </el-card> -->
           
        </div>

    </div>
</template>

<script>

import { listDir, mkdir } from '../../api/fileManage';





export default {
    data() {
        return {
            options: [],
            space: '',
            path: '',
            pathList: [],
            files: [],
       
        }
    },
    methods: {
        initData() {
            this.getSpace();
            this.intiDir()
        },
        intiDir() {
            this.changeSpace(this.options[0].value)
        },
        getSpace() {
            let arr = this.$store.getters.userInfo.space
            this.options = arr.map((i, idx) => { return { value: i, label: i } })
        },
        changeSpace(value) {
            this.space = value
            this.pathList = []
            this.pathList.push(value)
        },
        enterDir(path) {
            this.pathList.push(path)
        },
        exitDir() {
            if (this.pathList.length > 1) {
                this.pathList.pop()
            }
        },
        loadDir() {
            listDir(this.path).then(res => {
                this.files = res.data.result
            })
        },
        createDir() {
            this.$prompt('请输入文件夹名称', '创建文件夹', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
            }).then(({ value }) => {
                this.files.push({ name: value, isFolder: true })
                value = value + '/'
                let folder = this.path + value
                mkdir(folder).then(res => {
                    this.$message({
                        type: 'success',
                        message: '创建成功 ' + res.data.result
                    });
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '取消输入'
                });
            });
        },




    },
    beforeMount() {
        this.initData()
    },
    watch: {
        pathList: {
            immediate: true,
            deep: true,
            handler(newVal, oldVal) {
                this.path = this.pathList.join('/') + "/"
                if (this.path && this.path.length > 1) {
                    this.loadDir()
                }

            }
        }
    }
}
</script>
