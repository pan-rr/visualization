import service from "../utils/request";

export async function userLogin(data) {
    return await service.post('auth/user/login', data)  
}

export async function userRegister(data) {
    return await service.post('auth/user/register', data)  
}