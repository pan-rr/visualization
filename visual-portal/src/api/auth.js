import service from "../utils/request";

export async function userLogin(data) {
    return await service.post('auth/user/login', data)
}

export async function userLogout() {
    return await service.get('auth/user/logout')
}

export async function userRegister(data) {
    return await service.post('auth/user/register', data)
}

export async function changePassword(data) {
    return await service.post('auth/user/changePassword', data)
}

export async function registerSubTenant(data) {
    return await service.post('auth/user/registerSubTenant', data)
}

export async function getUserTenantPermission(tenantId) {
    return await service.get('auth/user/getUserTenantPermission', { params: { tenantId } })
}