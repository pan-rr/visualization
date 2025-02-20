import download from "downloadjs";
import service from "../utils/request";





export async function mkdir(path) {
    return await service.get('engine/portal/file/fileManage/mkdir', { params: { path: path } })
}


export async function listDir(path) {
    return await service.get('engine/portal/file/fileManage/listDir', { params: { path: path } })
}

export function downloadFile(folder, fileName) {
    let path = folder + fileName
    service.get('engine/portal/file/fileManage/download', {
        params: { path: path },
        responseType: "blob"
    }).then(res => {
        download(res.data, fileName)
    })
}
