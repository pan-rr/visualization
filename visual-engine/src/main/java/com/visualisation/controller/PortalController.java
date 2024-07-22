package com.visualisation.controller;

import com.visualisation.manager.DAGManager;
import com.visualisation.manager.LogicFlowManager;
import com.visualisation.model.PageParameter;
import com.visualisation.model.PageResponse;
import com.visualisation.model.Response;
import com.visualisation.model.dag.DAGInstance;
import com.visualisation.model.dag.DAGTemplate;
import com.visualisation.model.dag.PortalDAGInstance;
import com.visualisation.model.dag.logicflow.DraftTask;
import com.visualisation.model.dag.logicflow.LogicGraph;
import com.visualisation.model.portal.PortalDAGTemplate;
import com.visualisation.service.DAGService;
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
        Page<DAGInstance> templateList = dagService.getInstanceList(request);
        List<PortalDAGInstance> collect = templateList.getContent().stream().map(DAGInstance::convert).collect(Collectors.toList());
        return PageResponse.success(collect, templateList.getNumberOfElements());

    }

    @PostMapping("/saveTask")
    public Response<Object> saveDraftTask(@RequestBody DraftTask task) {
        logicFlowManager.saveDraftTask(task);
        return Response.success("任务保存成功！");
    }


    @GetMapping("/createInstance")
    public Response<Object> createInstance(@RequestParam("templateId") String templateId) {
        Long id = Long.valueOf(templateId);
        dagManager.createLogicFlowInstanceByTemplateId(id);
        return Response.success("新建实例成功");
    }

    @GetMapping("/disableTemplateById")
    public Response<Object> disableTemplateById(@RequestParam("templateId") String templateId) {
        Long id = Long.valueOf(templateId);
        dagManager.disableTemplateById(id);
        return Response.success("禁用模版成功");
    }


}
