export default class BaseView {

    viewType;
    tableName;
    script;
    // param = new Map();
    param = {};

    appendParam(key,val) {    
        this.param[key] = val
    }

    deleteParam(key) {
        Reflect.deleteProperty(this.param,key);
    }

}