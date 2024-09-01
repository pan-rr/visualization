import router from '@/router'

const needRedirectCode = [-999, -998];

const redirectLogin = () => {
    let path = router.history.current.path;
    if (path != '/login') {
        router.replace('/login')
    }
}

const checkAccess = (res) => {
    if (needRedirectCode.includes(res?.code)) {
        redirectLogin()
        return false;
    }
    return true
}


const checkCode = (res) => {
    return res?.code >= 0;
}

const validateResponse = (res) => {
    return checkAccess(res)
        && checkCode(res)
}

export {
    checkAccess,
    checkCode,
    validateResponse
}