<template>
    <div>
       <SpaceSelector :spaceRef="spaceRef"></SpaceSelector>

        <div>
            <el-card class="box-card" shadow="hover">
                <div slot="header" style="display: flex;">
                    <el-button plain icon="el-icon-back" @click="exitDir"></el-button>
                    <el-button style="margin: 0%;" plain icon="el-icon-refresh" @click="loadDir"></el-button>
                    <el-input v-model="path" :readonly="true">
                        <template slot="prepend">
                            <el-icon class="el-icon-collection-tag"></el-icon>
                            空间路径:
                        </template>
                    </el-input>
                    <el-button type="info" plain icon="el-icon-folder-add" @click="createDir">新建文件夹</el-button>

                    <UploadFile :buttonStyle="true" :folder="this.path" @reloadDir="loadDir"></UploadFile>

                </div>
                <div v-if="files.length === 0">
                    <h3><el-icon class="el-icon-search"></el-icon>当前文件夹下暂无文件</h3>
                    <el-skeleton />
                </div>
                <div v-else>

                    <el-table :data="files" style="width: 100%">
                        <el-table-column label="名称">
                            <template slot-scope="scope">
                                <el-link v-if="scope.row.isFolder" @click="enterDir(scope.row.name)">
                                    <i class="el-icon-folder"></i>
                                    {{ scope.row.name }}
                                </el-link>
                                <div v-else>
                                    <i class="el-icon-tickets"></i>
                                    {{ scope.row.name }}
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" fixed="right">
                            <template slot-scope="scope">
                                <el-button v-if="!scope.row.isFolder" plain size="small"
                                    @click="downloadFile(path, scope.row.name)">
                                    <el-icon class="el-icon-download"></el-icon>下载
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>



                    <!-- <el-container v-for="item in files" :key="item.name" type="flex" justify style="">
                        <el-main>
                            <el-link @click="enterDir(item.name)" v-if="item.isFolder">
                                <i class="el-icon-folder"></i>
                                {{ item.name }}
                            </el-link>
                            <el-link v-else type="primary">
                                <i class="el-icon-tickets"></i>
                                {{ item.name }}
                            </el-link>
                        </el-main>
                        <el-button v-if="!item.isFolder" plain size="small" style="margin: 1%;"
                            @click="downloadFile(path, item.name)">
                            <el-icon class="el-icon-download"></el-icon>下载
                        </el-button>
                    </el-container> -->
                </div>
            </el-card>
        </div>

        <div>
            <UploadFile :folder="this.path" @reloadDir="loadDir"></UploadFile>
        </div>

    </div>
</template>

<script>

import { downloadFile, listDir, mkdir } from '../../api/fileManage';
import UploadFile from '../../layout/components/File/UploadFile.vue';
import SpaceSelector from '../../layout/components/Visual/SpaceSelector.vue';






export default {
    name: 'FileManage',
    components: { UploadFile ,SpaceSelector},
    data() {
        return {
           
            path: '',
            pathList: [],
            files: [],
            spaceRef: {
                data: ''
            }
        }
    },
    methods: {
        initData() {
            this.intiDir()
        },
        intiDir() {
            this.changeSpace(this.space)
        },
        changeSpace(value) {
            console.log(value)
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
            // this.files = [{name:"dsadsa",isFolder:true},{name:"dasas.csv",isFolder:false}]
            listDir(this.path).then(res => {
                this.files = res.data.result
            })
        },
        createDir() {
            this.$prompt('请输入文件夹名称', '创建文件夹', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
            }).then(({ value }) => {

                let folder = this.path + value + '/'
                mkdir(folder).then(res => {
                    this.$message({
                        type: 'success',
                        message: res.data.result + '创建成功'
                    });
                }).then(res => {
                    let newFolder = value.split('/')[0]
                    this.files.push({ name: newFolder, isFolder: true })
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '取消创建文件夹'
                });
            });
        },
        downloadFile(folder, name) {
            downloadFile(folder, name)
        }

    },
    beforeMount() {
        this.initData()
    },
    computed: {
        space(){
            return this.spaceRef.data
        }
    },
    watch: {
        space:{
            immediate: true,
            deep: true,
            handler(newVal, oldVal) {
                this.intiDir()
            }
        },
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
