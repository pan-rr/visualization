package com.visualization.controller;

import com.visualization.manager.DAGManager;
import com.visualization.manager.LogicFlowManager;
import com.visualization.manager.TimeLineManager;
import com.visualization.model.PageParameter;
import com.visualization.model.PageResponse;
import com.visualization.model.Response;
import com.visualization.model.dag.db.DAGInstance;
import com.visualization.model.dag.db.DAGTemplate;
import com.visualization.model.portal.PortalDAGInstance;
import com.visualization.model.dag.logicflow.LogicGraph;
import com.visualization.model.portal.PortalDAGTemplate;
import com.visualization.service.DAGService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private TimeLineManager timeLineManager;

    @PostMapping("/createTemplate")
    public Response<Object> createTemplate(@RequestBody LogicGraph logicGraph) {
        logicFlowManager.saveTemplate(logicGraph);
        return Response.success("流程配置成功！");
    }

    @PostMapping("/getTemplateList")
    public PageResponse getTemplateList(@RequestBody PageParameter parameter) {
        PageRequest request = PageRequest.of(parameter.getPage() - 1, parameter.getSize());
        Page<DAGTemplate> templateList = dagService.getTemplateList(request);
        List<PortalDAGTemplate> collect = templateList.getContent().stream().map(DAGTemplate::convert).collect(Collectors.toList());
        return PageResponse.success(collect, templateList.getNumberOfElements());

    }

    @PostMapping("/getInstanceList")
    public PageResponse getInstanceList(@RequestBody PageParameter parameter) {
        PageRequest request = PageRequest.of(parameter.getPage() - 1, parameter.getSize());
        Page<DAGInstance> instanceList = dagService.getInstanceList(request);
        List<PortalDAGInstance> collect = instanceList.getContent().stream().map(DAGInstance::convert).collect(Collectors.toList());
        return PageResponse.success(collect, instanceList.getNumberOfElements());

    }


    @GetMapping("/createInstance")
    public Response<Object> createInstance(@RequestParam("templateId") String templateId) {
        Long id = Long.valueOf(templateId);
        DAGInstance instance = dagManager.createLogicFlowInstanceByTemplateId(id);
        return Response.success(instance.getInstanceId().toString());
    }

    @GetMapping("/disableTemplateById")
    public Response<Object> disableTemplateById(@RequestParam("templateId") String templateId) {
        Long id = Long.valueOf(templateId);
        dagManager.disableTemplateById(id);
        return Response.success("禁用模版成功");
    }

    @GetMapping("/getLogTimeLine")
    public Response<Object> getLogTimeLineByInstanceId(@RequestParam("instanceId") String instanceId) {
        return Response.success(timeLineManager.getTimeLineByInstanceId(instanceId));
    }

}