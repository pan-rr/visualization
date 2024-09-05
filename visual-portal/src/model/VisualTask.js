import BaseTask from "./BaseTask";
import CSV from "./visualView/CSV";
import JDBC from "./visualView/JDBC";
import Console from "./visualView/Console";
import Excel from "./visualView/Excel";

export default class VisualTask extends BaseTask {
    input = [];
    output = {};
    step = 0;

    constructor(taskType) {
        super();
        this.taskType = taskType
    }
    static convert(baseTask) {
        let obj = new VisualTask();
        Object.assign(obj, baseTask);
        return obj;
    }

    deleteInputView(index) {
        this.input = this.input.filter((item, i) => { return i != index });
    }

    addInputView(type) {
        switch (type) {
            case 'csv': this.input.push(new CSV()); break;
            case 'jdbc': this.input.push(new JDBC()); break;
        }
    }

    switchOutputView(type) {
        switch (type) {
            case 'csv': this.output = new CSV(); break;
            case 'jdbc': this.output = new JDBC(); break;
            case 'console': this.output = new Console(); break;
            case 'excel': this.output = new Excel(); break;
        }
    }
}