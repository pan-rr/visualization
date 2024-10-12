import service from "../utils/request";
import { Message } from 'element-ui'


export async function createTemplate(graph) {
    return await service.post('engine/portal/createTemplate', graph)
}

export async function getTemplateJSON(templateId) {
    return await service.get('engine/portal/getTemplateStr', { params: { templateId } })
}

export async function changeTemplatePriority(templateId, priority) {
    return await service.get('engine/portal/changeTemplatePriority', { params: { templateId, priority } })
}

export async function changeTemplateRetryCount(templateId, retryCount) {
    return await service.get('engine/portal/changeTemplateRetryCount', { params: { templateId, retryCount } })
}

export async function changeTemplateStatus(templateId, status) {
    return await service.get('engine/portal/changeTemplateStatus', { params: { templateId, status } })
}

export async function getTemplateList(pageable) {
    return await service.post('engine/portal/getTemplateList', pageable)
}

export async function getInstanceList(pageable) {
    return await service.post('engine/portal/getInstanceList', pageable)
}

export async function getStatusOptions(type) {
    return await service.get('engine/portal/getStatusOptions', { params: { type } })
}

export async function getLogTimeLine(instanceId) {
    return await service.get('engine/portal/getLogTimeLine', { params: { instanceId } })
}

export async function createInstanceById(templateId) {
    return await service.get('engine/portal/createInstance', { params: { templateId } })
}

export async function disableTemplateById(templateId) {
    return await service.get('engine/portal/disableTemplateById', { params: { templateId } })
}

export async function saveTask(task) {
    let resp = (await service.post('engine/portal/saveTask', task)).data
    sendSuccessMessage(resp)
    return resp
}

export async function getDataSourceList(param) {
    return await service.post('engine/portal/getDataSourceList', param)
}

export async function saveDataSource(param) {
    return await service.post('engine/portal/saveDataSource', param)
}

export async function deleteDataSource(id) {
    return await service.get('engine/portal/deleteDataSource', { params: { id } })
}

export async function getDataSourceOptions(space) {
    return await service.get('engine/portal/getDataSourceOptions', { params: { space } })
}

function sendSuccessMessage(resp) {
    if (resp.code === 0) {
        Message({
            message: resp.result,
            type: 'success',
            duration: 5 * 1000,
        })
    }

}