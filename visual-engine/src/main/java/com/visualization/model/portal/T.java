package com.visualization.model.portal;

import com.visualization.utils.SPELUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class T {
    public static void main(String[] args) {


        String s = Base64.getEncoder().encodeToString("测试-1z5cd9D0bSM".getBytes());
        System.out.println(s);

        List<String> list = Arrays.asList("22", "3213", "asdas");
        System.out.println(list);
        String join = StringUtils.join(list, ',');
        System.out.println(join);
        Map<String,Object> map = new HashMap<>();
        String ss = "import \"date\"\n" +
                "from(bucket: \"visual_log\")\n" +
                "|> range(start: -15d, stop: now())\n" +
                "|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")\n" +
                "|> filter(fn: (r) => r.theme !=  1 )\n" +
                "|> filter(fn: (r) => contains(value: r.template_id, set: #{[list]})  )\n" +
                "|> group(columns: [ \"task_id\"], mode:\"by\") \n" +
                "|> filter(fn: (r) =>  contains(value: r.theme, set: [0,2]))\n" +
                "|> map(fn: (r) => ({ r with _time: int(v: r._time) / 1000000000 }))\n" +
                "|> spread(column: \"_time\")";
        map.put("list",list);
        String res = SPELUtil.parseExpression(ss, map);
        System.out.println(res);
    }
}
