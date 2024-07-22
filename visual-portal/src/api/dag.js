import request from "../utils/request";
import { Message } from 'element-ui'


export async function createTemplate(graph) {
    let resp = (await request.post('/createTemplate', graph)).data
    sendSuccessMessage(resp)
}

export async function getTemplateList(pageable) {
    return await request.post('/getTemplateList', pageable)  
}

export async function getInstanceList(pageable) {
    return await request.post('/getInstanceList', pageable)  
}

export async function createInstanceById(templateId) {
    await request.get('/createInstance', { params: { templateId } }).then(res=>{
        sendSuccessMessage(res.data)
    })
}

export async function disableTemplateById(templateId) {
    await request.get('/disableTemplateById', { params: { templateId } })
}

export async function saveTask(task) {
    let resp = (await request.post('/saveTask', task)).data
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