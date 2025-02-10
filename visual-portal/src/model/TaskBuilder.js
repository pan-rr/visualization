import ContextTask from "./ContextTask";
import FlinkTask from "./FlinkTask";
import HttpTask from "./HttpTask";
import SQLTask from "./SQLTask";
import TableTask from "./TableTask";
import VisualTask from "./VisualTask";

const TaskBuilder = (taskType) => {
    switch (taskType) {
        case 'SQL': return new SQLTask(taskType);
        case 'CONTEXT_INJECT': return new ContextTask(taskType);
        case 'HTTP': return new HttpTask(taskType);
        case 'FLINK': return new FlinkTask(taskType);
        case 'TABLE' : return new TableTask(taskType);
        default: return new VisualTask(taskType);
    }
}

export default TaskBuilder;