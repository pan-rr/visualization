import FlinkTask from "./FlinkTask";
import HttpTask from "./HttpTask";
import SQLTask from "./SQLTask";
import VisualTask from "./VisualTask";

const TaskBuilder = (taskType) => {
    switch (taskType) {
        case 'SQL': return new SQLTask(taskType);
        case 'HTTP': return new HttpTask(taskType);
        case 'FLINK': return new FlinkTask(taskType);
        default: return new VisualTask(taskType);
    }
}

export default TaskBuilder;