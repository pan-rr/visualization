import BaseView from "./BaseView";

export default class JDBC extends BaseView {
    filePath;
    viewType = 'jdbc';
    param = {
        url: '',
        username: '',
        password: '',
    }
}