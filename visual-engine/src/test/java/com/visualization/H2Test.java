package com.visualization;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.visualization.log.model.StageLogPoint;
import com.visualization.utils.SPELUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@SpringBootTest
public class H2Test {

    @Resource
    private Commander commander;
    @Resource(name = "visualInfluxDBClient")
    private InfluxDBClient influxDBClient;

    @Test
    public void test() throws IOException {
        long l = System.currentTimeMillis();

//        WriteApiBlocking api = influxDBClient.getWriteApiBlocking();
//        Point point = Point.measurement("visual_log");
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("instance_id",1274418687830392832L);
//        map.put("task_id",1274418687830392832L);
//        point.time(Instant.now(),WritePrecision.MS);
//        point.addFields(map);
//        api.writePoint(point);

        Random random = new Random();


        WriteApiBlocking write = influxDBClient.getWriteApiBlocking();
        write.writeMeasurement(WritePrecision.MS, StageLogPoint.builder()
                .time(Instant.now())
                .taskId(String.valueOf(random.nextLong()))
                        .instanceId(String.valueOf(random.nextLong()))
                .message("success")
                .build()
        );



//        QueryApi api = influxDBClient.getQueryApi();
//        List<StageLogPoint> list = api.query("from(bucket: \"visual_log\")\n" +
//                "  |> range(start: -15d, stop: now())\n" +
//                "  |> filter(fn: (r) => r._measurement == \"visual_stage_log\" )  \n" +
//                "  |> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")", StageLogPoint.class);
//        System.err.println(list);

        Map<String,Object> map = new HashMap<>();
        map.put("bucketName","visual_log");
        map.put("measurement","visual_stage_log");
        map.put("instanceId","1274418687830392832");

        String s = "from(bucket: \"#{[bucketName]}\")\n" +
                "  |> range(start: -15d, stop: now())\n" +
                "  |> filter(fn: (r) => r._measurement == \"#{[measurement]}\" )  \n" +
                "  |> filter(fn: (r) => r.instance_id == \"#{[instanceId]}\" )  \n" +
                "  |> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")";

        String q = SPELUtils.parseExpression(s, map);
        System.err.println(q);
        QueryApi api = influxDBClient.getQueryApi();
        List<StageLogPoint> list = api.query(q, StageLogPoint.class);
        System.err.println(list);

//        commander.commandWithLocalFile("example-data/json/example11.json");
//        System.err.println("jvm参数：-XX=+UseG1GC;-Xms=2g;-Xmx=8g");
//        System.err.println("耗时：" + String.valueOf(System.currentTimeMillis() - l) + "ms");
    }

}
