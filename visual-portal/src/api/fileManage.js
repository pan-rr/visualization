import service from "../utils/request";




export async function mkdir(path) {
    return await service.get('/file/fileManage/mkdir',{ params: { path:path } })
}


export async function listDir(path) {
    return await service.get('/file/fileManage/listDir',{ params: { path:path } })
}

