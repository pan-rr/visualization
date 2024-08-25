package com.visualization.service.impl;

import com.visualization.mapper.ResourceMapper;
import com.visualization.model.api.Option;
import com.visualization.model.api.PortalResource;
import com.visualization.model.api.ResourceOption;
import com.visualization.model.db.SystemResource;
import com.visualization.service.ResourceService;
import com.visualization.utils.SnowIdUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Resource
    private ResourceMapper resourceMapper;

    @Override
    public void createResource(PortalResource portalResource) {
        SystemResource systemResource = portalResource.convert();
        systemResource.setResourceId(SnowIdUtil.generateId());
        resourceMapper.insert(systemResource);
    }

    @Override
    public List<PortalResource> getResourceList(PortalResource portalResource) {
        List<SystemResource> res = resourceMapper.selectList(portalResource.buildWrapper());
        return PortalResource.convert(res);
    }

    @Override
    public List<ResourceOption> getResourceOptions(String tenantId) {
        List<Option> resourceOptions = resourceMapper.getResourceOptions(Long.valueOf(tenantId));
        return Option.convertResourceOption(resourceOptions);
    }
}
