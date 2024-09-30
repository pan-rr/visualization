package com.visualization.controller;

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

    @GetMapping("/getLogTimeLine")
    public Response<Object> getLogTimeLineByInstanceId(@RequestParam("instanceId") String instanceId) {
        return Response.success(timeLineManager.getTimeLineByInstanceId(instanceId));
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

    @GetMapping("/getDataSourceOptions")
    public Response<Object> getDataSourceList(@RequestParam("space") String space) {
        return Response.success(dagDataSourceManager.getDataSourceOptions(space));
    }
}
