import BaseTask from "./BaseTask";

export default class SQLTask extends BaseTask {
    sql = {
        script: '',
        url: '',
        username: '',
        password: '',
    }
    constructor(taskType) {
        super();
        this.taskType = taskType
    }

    static convert(baseTask) {
        let obj = new SQLTask();
        Object.assign(obj, baseTask);
        return obj;
    }
}