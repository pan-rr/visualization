
import { md5 } from "js-md5";
import { getResourceCode } from "../config/authConfig";



const tagPermission = (config) => {
    let url = config.url;
    let code = getResourceCode(url);
    let token = localStorage.getItem('visual');
    code = encodeURIComponent(code);
    let res = md5.hex(md5.hex(code +"/"+ url + config.headers['visual_tenant']) + token)
    config.headers['visual_resource'] = res;
}

export { tagPermission }