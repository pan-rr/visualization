import service from "../utils/request";
import { Message } from 'element-ui'


export async function createTemplate(graph) {
    return await service.post('/createTemplate', graph)
}

export async function getTemplateList(pageable) {
    return await service.post('/getTemplateList', pageable)  
}

export async function getInstanceList(pageable) {
    return await service.post('/getInstanceList', pageable)  
}

export async function getLogTimeLine(instanceId) {
    return await service.get('/getLogTimeLine', { params: { instanceId } })
}

export async function createInstanceById(templateId) {
    return await service.get('/createInstance', { params: { templateId } })
}

export async function disableTemplateById(templateId) {
    await service.get('/disableTemplateById', { params: { templateId } })
}

export async function saveTask(task) {
    let resp = (await service.post('/saveTask', task)).data
    sendSuccessMessage(resp)
    return resp
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