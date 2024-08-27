package com.visualization.service.impl;

import com.visualization.exeception.AuthException;
import com.visualization.mapper.ResourceMapper;
import com.visualization.model.api.Option;
import com.visualization.model.api.PortalResource;
import com.visualization.model.api.ResourceOption;
import com.visualization.model.db.SystemResource;
import com.visualization.service.ResourceService;
import com.visualization.utils.SnowIdUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Resource
    private ResourceMapper resourceMapper;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createResource(PortalResource portalResource) {
        SystemResource systemResource = portalResource.convert();
        systemResource.setResourceId(SnowIdUtil.generateId());
        int cnt = resourceMapper.checkResourceName(portalResource.getTenantId(), portalResource.getResourceName());
        if (cnt > 0) {
            throw new AuthException("资源名重复！");
        }
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
