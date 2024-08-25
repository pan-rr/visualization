package com.visualization.service;

import com.visualization.model.api.PortalResource;
import com.visualization.model.api.ResourceOption;

import java.util.List;

public interface ResourceService {

    void createResource(PortalResource portalResource);

    List<PortalResource> getResourceList(PortalResource portalResource);

    List<ResourceOption> getResourceOptions(String tenantId);

}
