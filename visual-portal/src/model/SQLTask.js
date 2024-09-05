import BaseTask from "./BaseTask";

export default class SQLTask extends BaseTask {
    script;
    param;
    constructor(taskType) {
        super();
        this.taskType = taskType
        this.param = {
            url: '',
            username: '',
            password: '',
        }
    }

    static convert(baseTask) {
        let obj = new SQLTask();
        Object.assign(obj, baseTask);
        return obj;
    }
}