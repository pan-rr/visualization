import { da } from "element-plus/es/locales.mjs";
import service from "../utils/request";



export async function getPermissionList(data) {
    return await service.post('auth/permission/getPermissionList', data)
}


export async function createPermission(data) {
    return await service.post('auth/permission/createPermission', data)
}

export async function grantPermission(data) {
    return await service.post('auth/permission/grantPermission', data)
}

export async function getGrantViewList(data) {
    return await service.post('auth/permission/getGrantViewList', data)
}

export async function retractPermission(data) {
    return await service.post('auth/permission/retractPermission', data)
}

