import service from "../utils/request";
import axiosExtra from 'axios-extra'

const httpExtra = axiosExtra.create({
    maxConcurrent: 5, //并发为1
    queueOptions: {
        retry: 3, //请求失败时,最多会重试3次
        retryIsJump: false //是否立即重试, 否则将在请求队列尾部插入重试请求
    }
})

/**
 * 根据文件的md5获取未上传完的任务
 * @param md5 文件md5
 * @returns {Promise<AxiosResponse<any>>}
 */
const taskInfo = (md5) => {
    return service.get(`/file/fileChunk/checkFileChunkTask/${md5}`)
}

/**
 * 初始化一个分片上传任务
 * @param md5 文件md5
 * @param fileName 文件名称
 * @param totalSize 文件大小
 * @param chunkSize 分块大小
 * @returns {Promise<AxiosResponse<any>>}
 */
const initTask = ({ md5, fileName, totalSize, chunkSize }) => {
    return service.post('/file/fileChunk/initFileChunk', { md5, fileName, totalSize, chunkSize })
}

/**
 * 获取预签名分片上传地址
 * @param md5 文件md5
 * @param partNumber 分片编号
 * @returns {Promise<AxiosResponse<any>>}
 */
const preSignUrl = ({ md5, partNumber }) => {
    return service.get(`/file/fileChunk/generatePreSignedUrl/${md5}/${partNumber}`)
}

/**
 * 合并分片
 * @param md5
 * @returns {Promise<AxiosResponse<any>>}
 */
const merge = (md5) => {
    return service.post(`/file/fileChunk/merge/${md5}`)
}

export {
    taskInfo,
    initTask,
    preSignUrl,
    merge,
    httpExtra
}