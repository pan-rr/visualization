package com.visualization.service.impl;

import com.visualization.manager.DAGManager;
import com.visualization.manager.PointerQueueManager;
import com.visualization.model.dag.db.DAGPointer;
import com.visualization.service.PointerDispatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class PointerDispatchServiceImpl implements PointerDispatchService {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${server.port}")
    private String port;

    @Resource
    private PointerQueueManager pointerQueueManager;

    @Resource
    private DAGManager dagManager;

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private RestTemplate restTemplate;

    private String ip;

    private final String engineSite = "http://{0}:{1}/{2}/dag/acceptPointers";

    private String getIp() {
        if (ip == null) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
        return ip;
    }

    @Override
    public void dispatchPointer(int limit) {
        List<DAGPointer> pointers = dagManager.getPointers(limit);
        if (!CollectionUtils.isEmpty(pointers)) {
            List<ServiceInstance> engines = discoveryClient.getInstances(applicationName);
            if (CollectionUtils.isEmpty(engines)){
                return;
            }
            int engineSize = engines.size();
            Map<Integer, List<DAGPointer>> map = pointers
                    .stream()
                    .collect(Collectors.groupingBy(p -> (int) (p.getTaskId() % engineSize)));
            map.forEach((k, v) -> {
                ServiceInstance targetEngine = engines.get(k);
                if (getIp().equals(targetEngine.getHost())) {
                    pointerQueueManager.getQueue().addAll(v);
                } else {
                    String url = MessageFormat.format(engineSite, targetEngine.getHost(), String.valueOf(targetEngine.getPort()), contextPath);
                    restTemplate.postForEntity(url, v, Object.class);
                }
            });

        }
    }
}
