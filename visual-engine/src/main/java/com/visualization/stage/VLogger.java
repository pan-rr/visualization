package com.visualization.stage;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.visualization.enums.WorkerGroup;
import com.visualization.runtime.VLog;
import com.visualization.utils.SPELUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Slf4j
public class VLogger {

    @Resource
    private VisualEngineService visualEngineService;

    @Resource(name = "visualInfluxDBClient")
    private InfluxDBClient influxDBClient;

    private final LinkedBlockingQueue<VLogPoint> Q = new LinkedBlockingQueue<>();

    @PostConstruct
    public void init() {
        visualEngineService.recruitWorker(
                () -> {
                    while (true) {
                        try {
                            handleVLog(take());
                        } catch (Throwable e) {
                            log.error("visual-log 异常:", e);
                        }
                    }
                },
                WorkerGroup.DAG_LOG
        );
    }

    public void accept(VLog log) {
        Q.offer(VLogPoint.convert(log));
    }

    public void accept(VLogPoint log) {
        Q.offer(log);
    }

    @SneakyThrows
    private List<VLogPoint> take() {
        VLogPoint one = Q.take();
        int size = Q.size();
        List<VLogPoint> res = new ArrayList<>(size + 1);
        if (size > 0) {
            Q.drainTo(res, size);
        }
        res.add(one);
        return res;
    }

    private void handleVLog(List<VLogPoint> list) {
        if (!CollectionUtils.isEmpty(list)) {
            WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
            writeApiBlocking.writeMeasurements(WritePrecision.MS, list);
        }
    }


    public List<VLogPoint> getLogPointsByInstanceId(String instanceId) {
        Map<String, Object> map = new HashMap<>();
        map.put("bucketName", "visual_log");
        map.put("measurement", "v_stage_log");
        map.put("instanceId", instanceId);
        String q = SPELUtil.parseExpression("from(bucket: \"#{[bucketName]}\")\n" +
                "  |> range(start: -15d, stop: now())\n" +
                "  |> filter(fn: (r) => r._measurement == \"#{[measurement]}\" )  \n" +
                "  |> filter(fn: (r) => r.instance_id == \"#{[instanceId]}\" )  \n" +
                "  |> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")", map);
        QueryApi api = influxDBClient.getQueryApi();
        return api.query(q, VLogPoint.class);
    }
}
