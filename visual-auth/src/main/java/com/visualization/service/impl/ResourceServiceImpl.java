package com.visualization.service.impl;

import com.visualization.mapper.ResourceMapper;
import com.visualization.model.api.Option;
import com.visualization.model.api.PortalResource;
import com.visualization.model.api.PortalResources;
import com.visualization.model.api.ResourceOption;
import com.visualization.model.db.SystemResource;
import com.visualization.service.ResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Resource
    private ResourceMapper resourceMapper;


    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void createResources(PortalResources portalResources) {
        List<SystemResource> list = portalResources.convert();
        if (!CollectionUtils.isEmpty(list)) {
            resourceMapper.saveResources(list, portalResources.getTenantId());
        }
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
