import service from "../utils/request";

export async function getGraph(pre, level) {
    return await service.get('auth/graph/getGraph', { params: { pre, level } })
}

export async function getTree(pre, level) {
    return await service.get('auth/graph/getTree', { params: { pre, level } })
}