import BaseTask from "./BaseTask";

export default class FlinkTask extends BaseTask {
    flink = {
        site:'',
        jarId:'',
        options:'',
        waitUntilFinish:true,
        waitInterval:30
    }
    constructor(taskType) {
        super();
        this.taskType = taskType
    }

    static convert(baseTask) {
        let obj = new FlinkTask();
        Object.assign(obj, baseTask);
        return obj;
    }
}