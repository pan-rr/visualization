package com.visualization.controller;

import com.visualization.enums.Status;
import com.visualization.manager.DAGDataSourceManager;
import com.visualization.manager.DAGManager;
import com.visualization.manager.LogicFlowManager;
import com.visualization.manager.TimeLineManager;
import com.visualization.model.PageParam;
import com.visualization.model.PageResponse;
import com.visualization.model.Response;
import com.visualization.model.dag.db.DAGInstance;
import com.visualization.model.dag.db.DAGTemplate;
import com.visualization.model.dag.logicflow.LogicGraph;
import com.visualization.model.portal.PortalDAGInstance;
import com.visualization.model.portal.PortalDAGTemplate;
import com.visualization.model.portal.PortalDataSource;
import com.visualization.service.DAGService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/portal")
public class PortalController {

    @Resource
    private LogicFlowManager logicFlowManager;

    @Resource
    private DAGService dagService;

    @Resource
    private DAGManager dagManager;

    @Resource
    private DAGDataSourceManager dagDataSourceManager;

    @Resource
    private TimeLineManager timeLineManager;

    @PostMapping("/createTemplate")
    public Response<Object> createTemplate(@RequestBody LogicGraph logicGraph) {
        logicFlowManager.saveTemplate(logicGraph);
        return Response.success("流程配置成功！");
    }

    @PostMapping("/getTemplateList")
    public PageResponse getTemplateList(@RequestBody PageParam parameter) {
        Page<DAGTemplate> templateList = dagService.getTemplateList(parameter);
        List<PortalDAGTemplate> collect = templateList.getContent().stream().map(DAGTemplate::convert).collect(Collectors.toList());
        return PageResponse.success(collect, (int) templateList.getTotalElements());
    }

    @GetMapping("/getTemplateStr")
    public Response<Object> getTemplateStr(@RequestParam("templateId") String templateId) {
        Long id = Long.valueOf(templateId);
        return Response.success(dagService.getTemplateStr(id));
    }

    @PostMapping("/getInstanceList")
    public PageResponse getInstanceList(@RequestBody PageParam parameter) {
        Page<DAGInstance> instanceList = dagService.getInstanceList(parameter);
        List<PortalDAGInstance> collect = instanceList.getContent().stream().map(DAGInstance::convert).collect(Collectors.toList());
        return PageResponse.success(collect, (int) instanceList.getTotalElements());
    }


    @GetMapping("/createInstance")
    public Response<Object> createInstance(@RequestParam("templateId") String templateId) {
        Long id = Long.valueOf(templateId);
        DAGInstance instance = dagManager.createInstanceByTemplateId(id);
        return Response.success(instance.getInstanceId().toString());
    }

    @GetMapping("/terminateInstance")
    public Response<Object> terminateInstance(@RequestParam("instanceId") String instanceId) {
        Long id = Long.valueOf(instanceId);
        dagManager.terminateInstance(id);
        return Response.success("中止实例成功！");
    }

    @GetMapping("/getContext")
    public Response<Object> getContext(@RequestParam("instanceId") String instanceId) {
        Long id = Long.valueOf(instanceId);
        return Response.success(dagService.getContext(id));
    }

    @GetMapping("/disableTemplateById")
    public Response<Object> disableTemplateById(@RequestParam("templateId") String templateId) {
        Long id = Long.valueOf(templateId);
        dagManager.disableTemplateById(id);
        return Response.success("禁用模版成功");
    }

    @GetMapping("/changeTemplateStatus")
    public Response<Object> changeTemplateStatus(@RequestParam("templateId") String templateId, @RequestParam("status") int status) {
        Long id = Long.valueOf(templateId);
        dagManager.changeTemplateStatus(id, status);
        return Response.success("修改模版状态成功");
    }

    @GetMapping("/changeTemplatePriority")
    public Response<Object> changeTemplatePriority(@RequestParam("templateId") String templateId, @RequestParam("priority") Double priority) {
        Long id = Long.valueOf(templateId);
        dagService.changeTemplatePriority(id, priority);
        return Response.success("修改模版优先级成功");
    }

    @GetMapping("/changeTemplateRetryCount")
    public Response<Object> changeTemplateRetryCount(@RequestParam("templateId") String templateId, @RequestParam("retryCount") Integer retryCount) {
        Long id = Long.valueOf(templateId);
        dagService.changeTemplateRetryCount(id, retryCount);
        return Response.success("修改模版优先级成功");
    }

    @GetMapping("/getLogTimeLine")
    public Response<Object> getLogTimeLineByInstanceId(@RequestParam("instanceId") String instanceId) {
        return Response.success(timeLineManager.getTimeLine(instanceId));
    }

    @PostMapping("/saveDataSource")
    public Response<Object> saveDataSource(@RequestBody PortalDataSource portalDataSource) {
        dagDataSourceManager.saveDAGDataSource(portalDataSource);
        return Response.success("保存数据源成功！");
    }

    @PostMapping("/getDataSourceList")
    public Response<Object> getDataSourceList(@RequestBody PortalDataSource portalDataSource) {
        return Response.success(dagDataSourceManager.getDataSourceList(portalDataSource));
    }

    @GetMapping("/deleteDataSource")
    public Response<Object> deleteDataSource(@RequestParam("id") String id) {
        dagDataSourceManager.deleteDataSource(id);
        return Response.success("删除数据源成功！");
    }

    @GetMapping("/getDataSourceOptions")
    public Response<Object> getDataSourceList(@RequestParam("space") String space) {
        return Response.success(dagDataSourceManager.getDataSourceOptions(space));
    }

    @GetMapping("/getStatusOptions")
    public Response<Object> getStatusOptions(@RequestParam(value = "type", required = false) Integer type) {
        return Response.success(Status.getOptions(type));
    }
}
