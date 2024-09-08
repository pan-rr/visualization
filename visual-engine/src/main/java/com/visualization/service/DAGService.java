package com.visualization.service;

import com.visualization.model.dag.db.*;
import com.visualization.model.dag.logicflow.LogicFlowPack;
import com.visualization.model.param.NormalParam;
import com.visualization.model.param.PageParameter;
import com.visualization.model.portal.Option;
import com.visualization.model.portal.PortalDataSource;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DAGService {

    Page<DAGTemplate> getTemplateList(PageParameter<NormalParam> parameter);

    Page<DAGInstance> getInstanceList(PageParameter<NormalParam> parameter);

    void saveTemplateByPack(LogicFlowPack pack);

    List<Edge> findNextEdges(TaskKey taskKey);

    void deletePointer(TaskKey taskKey);

    List<Long> saveReadyPointers(List<DAGPointer> pointers);

    void tryFinishInstance(Long instanceId);

    void updateInstanceStatus(Long instanceId, Integer status);

    void updatePointer(DAGPointer pointer);

    List<DAGPointer> getPointers(int limit);

    DAGInstance createInstanceByTemplateId(Long templateId);

    void updateTemplateStatus(Long templateId, int status);

    void deleteEdges(List<EdgeId> ids);

    void saveDAGDataSource(PortalDataSource portalDataSource);

    List<PortalDataSource> getDataSourceList(PortalDataSource portalDataSource);

    List<DAGDataSource> getListByIds(List<Long> ids);

    List<Option> getDataSourceOptions(String space);
}
