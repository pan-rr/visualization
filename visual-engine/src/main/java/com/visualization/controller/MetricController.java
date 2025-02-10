package com.visualization.controller;

import com.visualization.model.Response;
import com.visualization.model.portal.metric.Metric;
import com.visualization.service.MetricService;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/portal/metric")
public class MetricController {

    @Resource
    private MetricService metricService;

    @SneakyThrows
    @PostMapping("/taskMetrics")
    public Response<Object> metrics(@RequestBody Metric metric) {
        return Response.success(metricService.getMetrics(metric));
    }

}
