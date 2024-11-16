import BaseTask from "./BaseTask";

export default class HttpTask extends BaseTask {
    http = {
        header: {},
        url: '',
        method:'GET',
        body: {},
        timeout: 30
    }
    constructor(taskType) {
        super();
        this.taskType = taskType
    }

    static convert(baseTask) {
        let obj = new HttpTask();
        Object.assign(obj, baseTask);
        return obj;
    }
}