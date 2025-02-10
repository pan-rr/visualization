import BaseTask from "./BaseTask";

export default class ContextTask extends BaseTask {
    contextInject = {
    }
    constructor(taskType) {
        super();
        this.taskType = taskType
    }

}