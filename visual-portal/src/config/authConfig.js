import { permissionRoutes } from "../router";

const authConfig = new Map();
const authPatternConfig = new Map();

authConfig.set("auth/tenant/registerSubTenant", "注册子账号");
authConfig.set("engine/portal/createTemplate", "发布流程");
authConfig.set("engine/portal/getTemplateStr", "流程模版查看");
authConfig.set("engine/portal/changeTemplatePriority", "修改流程优先级");
authConfig.set("engine/portal/changeTemplateRetryCount", "修改流程失败重试次数");
authConfig.set("engine/portal/changeTemplateStatus", "修改流程模版状态");
authConfig.set("engine/portal/getTemplateList", "查看流程模版列表");
authConfig.set("engine/portal/getInstanceList", "查看流程实例列表");
authConfig.set("engine/portal/getLogTimeLine", "查看实例执行日志");
authConfig.set("engine/portal/createInstance", "运行实例");
authConfig.set("engine/portal/terminateInstance", "终止实例");
authConfig.set("engine/portal/getDataSourceList", "数据源列表");
authConfig.set("engine/portal/saveDataSource", "创建数据源");
authConfig.set("engine/portal/deleteDataSource", "删除数据源");
authConfig.set("engine/portal/file/fileManage/mkdir", "文件管理");
authConfig.set("engine/portal/file/fileManage/listDir", "文件管理");
authConfig.set("engine/portal/file/fileManage/download", "文件管理");
authConfig.set("auth/permission/getPermissionList", "授权管理");
authConfig.set("auth/permission/createPermission", "新建权限");
authConfig.set("auth/permission/grantPermission", "人员赋权");
authConfig.set("auth/permission/getGrantViewList", "赋权人员管理");
authConfig.set("auth/permission/retractPermission", "赋权人员管理");
authConfig.set("auth/resource/createResource", "新建资源");
authConfig.set("auth/resource/getResourceList", "资源管理");
authConfig.set("auth/resource/getResourceOption", "资源管理");


authPatternConfig.set(new RegExp("engine/portal/file/fileChunk\S*"), "上传文件");

const resourceName = new Set();

const loadResource = () => {
    authPatternConfig.values().forEach(i => resourceName.add(i));
    authConfig.values().forEach(i => resourceName.add(i));
    handleRouterResource(permissionRoutes);
}

const handleRouterResource = (router) => {
    router.forEach(r => {
        if (r.meta?.needAuthFilter) {
            resourceName.add(r.meta.title);
        }
        if (r.children && r.children.length > 0) {
            handleRouterResource(r.children);
        }
    });
}


const getResourceCode = (url) => {
    return authConfig.get(url) || getResourceCodeByPattern(url);
}

const getResourceCodeByPattern = (url) => {
    for (let [pattern, code] of authPatternConfig) {
        if (pattern.test(url)) return code;
    }
    return null;
}


const searchResource = (str) => {
    if (!str) return [];
    if (resourceName.size === 0) loadResource();
    let res = [];
    resourceName.forEach(i => {
        if (i.indexOf(str) >= 0) res.push({ value: i });
    })
    return res;
}

export { getResourceCode, searchResource }