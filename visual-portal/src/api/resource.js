import service from "../utils/request";

export async function createResource(data) {
    return await service.post('auth/resource/createResource', data)
}


export async function getResourceList(data) {
    return await service.post('auth/resource/getResourceList', data)
}

export async function getResourceOption(tenantId) {
    return await service.get('auth/resource/getResourceOption', { params: { tenantId } })
}