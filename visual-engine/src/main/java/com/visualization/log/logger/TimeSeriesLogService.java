package com.visualization.log.logger;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.visualization.log.model.StageLogPoint;
import com.visualization.log.model.VisualStageWrapper;
import com.visualization.utils.SPELUtil;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSeriesLogService extends VisualLogService {

    private static final String QUERY_BY_INSTANCE_ID_EXP = "from(bucket: \"#{[bucketName]}\")\n" +
            "  |> range(start: -15d, stop: now())\n" +
            "  |> filter(fn: (r) => r._measurement == \"#{[measurement]}\" )  \n" +
            "  |> filter(fn: (r) => r.instance_id == \"#{[instanceId]}\" )  \n" +
            "  |> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")";

    @Resource(name = "visualInfluxDBClient")
    private InfluxDBClient influxDBClient;

    @Override
    protected void handleLog(List<VisualStageWrapper> list) {
        List<StageLogPoint> points = StageLogPoint.covert(list);
        if (!CollectionUtils.isEmpty(points)) {
            WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
            // 秒级
            writeApiBlocking.writeMeasurements(WritePrecision.S, points);
        }
    }

    @Override
    public List<StageLogPoint> getLogPointsByInstanceId(String instanceId) {
        Map<String, Object> map = new HashMap<>();
        map.put("bucketName", "visual_log");
        map.put("measurement", "visual_stage_log");
        map.put("instanceId", instanceId);
        String q = SPELUtil.parseExpression(QUERY_BY_INSTANCE_ID_EXP, map);
        QueryApi api = influxDBClient.getQueryApi();
        return api.query(q, StageLogPoint.class);
    }
}
