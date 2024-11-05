package com.visualization.service;

import com.visualization.model.api.PortalResource;
import com.visualization.model.api.PortalResources;
import com.visualization.model.api.ResourceOption;

import java.util.List;

public interface ResourceService {

    void createResources(PortalResources portalResources);

    List<PortalResource> getResourceList(PortalResource portalResource);

    List<ResourceOption> getResourceOptions(String tenantId);

}
