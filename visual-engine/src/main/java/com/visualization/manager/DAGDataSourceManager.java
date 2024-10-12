package com.visualization.manager;

import com.visualization.constants.VisualDataSourceConstants;
import com.visualization.exception.DAGException;
import com.visualization.model.dag.db.DAGDataSource;
import com.visualization.model.portal.Option;
import com.visualization.model.portal.PortalDataSource;
import com.visualization.service.DAGService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DAGDataSourceManager {

    @Resource
    private DAGService dagService;

    public void deleteDataSource(String id){
        dagService.deleteDataSource(id);
    }

    public void saveDAGDataSource(PortalDataSource portalDataSource) {
        dagService.saveDAGDataSource(portalDataSource);
    }

    public List<PortalDataSource> getDataSourceList(PortalDataSource portalDataSource) {
        return dagService.getDataSourceList(portalDataSource);
    }

    public List<Option> getDataSourceOptions(String space) {
        return dagService.getDataSourceOptions(space);
    }

    public void rewriteDataSource(List<Map> configs) {
        List<Map> list = configs.stream().filter(i -> {
                    Object o = i.get(VisualDataSourceConstants.DATA_SOURCE_ID);
                    if (o == null) return false;
                    return StringUtils.isNotBlank(String.valueOf(o));
                }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<Long> ids = list.stream().map(i -> Long.valueOf((String) i.get(VisualDataSourceConstants.DATA_SOURCE_ID))).collect(Collectors.toList());
        List<DAGDataSource> dataSources = dagService.getListByIds(ids);
        if (CollectionUtils.isEmpty(dataSources) || ids.size() != dataSources.size()) {
            throw new DAGException("配置文件存在缺失的数据源配置，请检查！");
        }
        Map<Long, Map<?, ?>> convertMap = DAGDataSource.convertMap(dataSources);
        list.forEach(i -> {
            Object id = i.get(VisualDataSourceConstants.DATA_SOURCE_ID);
            String idStr = String.valueOf(id);
            Map<?, ?> map = convertMap.get(Long.valueOf(idStr));
            i.putAll(map);
            i.remove(VisualDataSourceConstants.DATA_SOURCE_ID);
        });
    }
}
