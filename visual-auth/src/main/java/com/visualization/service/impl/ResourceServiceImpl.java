package com.visualization.service.impl;

import com.visualization.mapper.ResourceMapper;
import com.visualization.model.db.SystemResource;
import com.visualization.service.ResourceService;
import com.visualization.utils.SnowIdUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Resource
    private ResourceMapper resourceMapper;

    @Override
    public void createResource(SystemResource systemResource) {
        systemResource.setResourceId(SnowIdUtil.generateId());
        resourceMapper.insert(systemResource);
    }

}
