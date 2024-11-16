package com.visualization.service;

import com.visualization.model.PageParam;
import com.visualization.model.dag.db.*;
import com.visualization.model.dag.logicflow.LogicFlowPack;
import com.visualization.model.portal.Option;
import com.visualization.model.portal.PortalDataSource;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DAGService {

    Page<DAGTemplate> getTemplateList(PageParam parameter);

    Page<DAGInstance> getInstanceList(PageParam parameter);

    void saveTemplateByPack(LogicFlowPack pack);

    List<Edge> findNextEdges(DAGPointer pointer);

    void deletePointer(DAGPointer pointer);

    void makeSurePointerConfigMatchTemplate(DAGPointer pointer, DAGTemplate template);

    List<Long> saveReadyPointers(List<DAGPointer> pointers, Long instanceId);

    void tryFinishInstance(Long instanceId);

    void updateInstanceStatus(Long instanceId, Integer status);

    void updatePointer(DAGPointer pointer);

    List<DAGPointer> getExecutablePointers(int limit);

    DAGInstance createInstanceByTemplateId(Long templateId);

    DAGInstance getInstance(Long instanceId);

    void updateTemplateStatus(Long templateId, int status);

    void deleteEdges(List<EdgeId> ids);

    void saveDAGDataSource(PortalDataSource portalDataSource);

    void deleteDataSource(String id);

    List<PortalDataSource> getDataSourceList(PortalDataSource portalDataSource);

    List<DAGDataSource> getListByIds(List<Long> ids);

    List<Option> getDataSourceOptions(String space);

    DAGTemplate getTemplateByPointer(DAGPointer pointer);

    void changeTemplatePriority(Long templateId, Double priority);

    void changeTemplateRetryCount(Long templateId, Integer retryCount);

    DAGTemplate getExecutableTemplate(Long templateId);

    String getTemplateStr(Long templateId);

    String getContext(Long instanceId);

}
