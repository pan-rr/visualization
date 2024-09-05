import BaseView from "./BaseView";

export default class Excel extends BaseView {
    filePath;
    viewType = 'excel';

    constructor() {
        super();
        this.param.headers = []
    }

    setHeaders(headers) {
        this.param.headers = headers
    }
}