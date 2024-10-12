package com.visualization.model.portal;

import com.google.gson.Gson;
import com.visualization.exception.DAGException;
import com.visualization.model.dag.db.DAGDataSource;
import com.visualization.utils.ShortLinkUtil;
import com.visualization.utils.SnowIdUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalDataSource {

    private String id;
    private String name;
    private String space;
    private Map<String, String> config;

    public void validate() {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(space) || CollectionUtils.isEmpty(config)) {
            throw new DAGException("数据源配置有空值！");
        }
    }

    public Example<DAGDataSource> buildExample() {
        DAGDataSource source = new DAGDataSource();
        Example<DAGDataSource> res = Example.of(source);
        if (StringUtils.isNotBlank(name)) {
            source.setName(name);
        }
        if (StringUtils.isNotBlank(space)) {
            source.setSpace(space);
        }
        return res;
    }

    public DAGDataSource convert() {
        validate();
        Gson gson = new Gson();
        String json = gson.toJson(config);
        return DAGDataSource.builder()
                .dataSourceId(SnowIdUtil.nextId())
                .space(space)
                .config(json)
                .name(name)
                .configHash(ShortLinkUtil.zipToInt(json))
                .build();
    }
}
