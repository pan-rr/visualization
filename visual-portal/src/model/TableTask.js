import BaseTask from "./BaseTask";

export default class TableTask extends BaseTask {
    table = {
        sql: '',
    }
    constructor(taskType) {
        super();
        this.taskType = taskType
    }

    static convert(baseTask) {
        let obj = new TableTask();
        Object.assign(obj, baseTask);
        return obj;
    }
}