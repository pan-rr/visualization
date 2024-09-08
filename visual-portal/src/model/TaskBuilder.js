import SQLTask from "./SQLTask";
import VisualTask from "./VisualTask";

const TaskBuilder = (taskType) => {
    if (taskType === 'SQL') return new SQLTask(taskType);
    else return new VisualTask(taskType);
}

export default TaskBuilder;