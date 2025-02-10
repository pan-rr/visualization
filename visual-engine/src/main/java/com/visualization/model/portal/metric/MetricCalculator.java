package com.visualization.model.portal.metric;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.visualization.enums.Status;
import com.visualization.model.dag.db.DAGTemplate;
import com.visualization.repository.dag.DAGTemplateRepository;
import com.visualization.runtime.VLogTheme;
import com.visualization.service.MinIOService;
import com.visualization.utils.Base62Util;
import com.visualization.utils.MapToBeanUtil;
import com.visualization.utils.SPELUtil;
import lombok.Builder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Builder
public class MetricCalculator {

    private InfluxDBClient influxDBClient;

    private NamedParameterJdbcTemplate jdbcTemplate;

    private DAGTemplateRepository dagTemplateRepository;

    private MinIOService minIOService;

    private Metric res;

    private MetricKey mode;

    private List<TaskRecord> taskRecords;

    private TemplateSummary summary;

    private Map<String, Object> params;

    private List<Runnable> functions;

    public Metric calculate() {
        init();
        execute();
        return res;
    }

    private void execute() {
        for (Runnable function : functions) {
            function.run();
        }
        if (!MetricKey.space.equals(mode)) {
            res.setId(Base62Util.zip(Long.parseLong(res.getId())));
        }
    }

    private void computeResult() {
        for (TaskRecord record : taskRecords) {
            int code = record.getTheme();
            VLogTheme theme = VLogTheme.codeToTheme(code);
            res.accumulate(theme);
        }
    }

    private void init() {
        res.init();
        params = new ConcurrentHashMap<>();
        functions = new LinkedList<>();
        functions.add(this::taskRecords);
        functions.add(this::computeResult);
        functions.add(this::template);
        functions.add(this::unfinishedTask);
        functions.add(this::taskTimeCost);
        functions.add(this::instanceTimeCost);
        functions.add(this::taskResultDistribution);
        functions.add(this::taskTotalCount);
        functions.add(this::templateError);
        functions.add(this::instanceError);
        this.mode = MetricKey.getMode(res.getMode());
        params.put(MetricKey.mode.name(), mode.getAlias());
        res.setId(res.getId());
        res.setSpace(res.getSpace());
        params.put("id", res.getId());
        params.put(MetricKey.space.name(), res.getSpace());
        String start = res.getTime();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate time = LocalDate.parse(start, pattern);
        params.put("start", start);
        params.put("stop", pattern.format(time.plusDays(1)));
    }


    private void taskRecords() {
        QueryApi api = influxDBClient.getQueryApi();
        String s = "from(bucket: \"visual_log\")\n" +
                "|> range(start: time(v:\"#{[start]}\"), stop: time(v:\"#{[stop]}\"))\n" +
                "|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")" +
                "|> filter(fn: (r) => r.space ==  \"#{[space]}\")" +
                "|> filter(fn: (r) => r.theme !=  1)" +
                "|> filter(fn: (r) => r.#{[mode]} ==  \"#{[id]}\")" +
                "|> group(columns: [ \"instance_id\",\"task_id\"], mode:\"by\") \n" +
                "|> max(column: \"_time\")\n" +
                "|> map(fn: (r) => ({ r with _time: int(v: r._time) / 1000000000 }))";
        String str = SPELUtil.parseExpression(s, params);
        this.taskRecords = api.query(str, TaskRecord.class);
        if (CollectionUtils.isEmpty(this.taskRecords)) return;
        Map<String, Object> query = new HashMap<>();
        query.put("ids", this.taskRecords.stream().map(TaskRecord::getInstanceId).collect(Collectors.toList()));
        List<String> terminateInstances = jdbcTemplate.queryForList("select instance_id from t_dag_instance where instance_id in (:ids) and status in (-2,-3,-6)", query, String.class);
        if (CollectionUtils.isEmpty(terminateInstances)) return;
        taskRecords.forEach(i -> {
            if (terminateInstances.contains(i.getInstanceId())) {
                i.setTheme(VLogTheme.TERMINATE.getCode());
            }
        });
    }

    private void template() {
        if (CollectionUtils.isEmpty(taskRecords)) {
            summary = TemplateSummary.getInstance();
            return;
        }
        String space = taskRecords.get(0).getSpace();
        Set<String> includes = this.taskRecords.stream().map(TaskRecord::getTemplateId).collect(Collectors.toSet());
        List<DAGTemplate> templates = dagTemplateRepository.findAllById(includes.stream().map(Long::parseLong).collect(Collectors.toList()));
        Map<String, String> map = minIOService.getTemplateStrBySpace(space, includes);
        summary = TemplateSummary.getInstance(templates, map);
    }


    private void unfinishedTask() {
        if (CollectionUtils.isEmpty(taskRecords)) return;
        List<TaskRecord> list = this.taskRecords.stream().filter(r -> VLogTheme.FINISH.getCode() != r.getTheme() && r.getTheme() != VLogTheme.TERMINATE.getCode()).collect(Collectors.toList());
        res.setUnfinishedTasks(summary.taskMetrics(list));
    }

    private void taskTimeCost() {
        if (CollectionUtils.isEmpty(taskRecords)) {
            res.getNameValueMetric().put(NameValueMetricType.task_time_cost, new LinkedList<>());
            return;
        }
        List<String> list = taskRecords.stream().map(TaskRecord::getTemplateId).distinct().collect(Collectors.toList());
        String join = StringUtils.join(list, ',');
        Map<String, Object> map = new HashMap<>(params);
        map.put("template_ids", join);
        String s = "import \"date\"\n" +
                "import \"strings\"" +
                "from(bucket: \"visual_log\")\n" +
                "|> range(start: time(v:\"#{[start]}\"), stop: time(v:\"#{[stop]}\"))\n" +
                "|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")\n" +
                "|> filter(fn: (r) => contains(value: r.template_id, set: strings.split(v: \"#{[template_ids]}\", t: \",\"))  )\n" +
                "|> filter(fn: (r) =>  contains(value: r.theme, set: [0,2]))\n" +
                "|> group(columns: [ \"task_id\",\"instance_id\"], mode:\"by\") \n" +
                "|> map(fn: (r) => ({ r with _time: int(v: r._time) / 1000000000 }))\n" +
                "|> spread(column: \"_time\")\n" +
                "|> group(columns: [ \"task_id\"], mode:\"by\") \n" +
                "|> max(column: \"_time\")" +
                "|> rename(columns: {_time: \"value\", task_id: \"name\"})\n" +
                "|> map(fn: (r) => ({ r with value: string(v: r.value)}))";
        String str = SPELUtil.parseExpression(s, map);
        QueryApi api = influxDBClient.getQueryApi();
        List<NameValueMetric> query = api.query(str, NameValueMetric.class);
        query.forEach(i -> i.setName(summary.getTaskName(i.getName(), i.getName())));
        res.getNameValueMetric().put(NameValueMetricType.task_time_cost, query);
    }

    private void instanceTimeCost() {
        if (CollectionUtils.isEmpty(taskRecords)) {
            res.getNameValueMetric().put(NameValueMetricType.instance_time_cost, new LinkedList<>());
            return;
        }
        List<String> list = taskRecords.stream().map(TaskRecord::getInstanceId).collect(Collectors.toList());
        Map<String, Object> param = new HashMap<>();
        param.put("ids", list);
        param.put("status", Status.FINISHED.getStatus());
        List<Map<String, Object>> query = jdbcTemplate.queryForList("SELECT instance_id name, TIME_TO_SEC(TIMEDIFF(finish_time , create_time)) value FROM t_dag_instance tdi WHERE instance_id in (:ids) and  status = 1  ORDER BY value DESC LIMIT 10", param);
        List<NameValueMetric> metrics = MapToBeanUtil.mapListToBeanList(query, NameValueMetric.class);
        metrics.forEach(i->i.setName(Base62Util.zip(Long.parseLong(i.getName()))));
        res.getNameValueMetric().put(NameValueMetricType.instance_time_cost, metrics);
    }

    private void taskResultDistribution() {
        Map<Integer, List<TaskRecord>> collect = this.taskRecords.stream().collect(Collectors.groupingBy(TaskRecord::getTheme));
        List<NameValueMetric> list = new ArrayList<>(collect.size());
        Set<Integer> set = collect.keySet();
        for (Integer i : set) {
            NameValueMetric metric = new NameValueMetric();
            metric.setName(VLogTheme.codeToTheme(i).getMessage());
            metric.setValue(collect.get(i).size() + "");
            list.add(metric);
        }
        res.getNameValueMetric().put(NameValueMetricType.task_result_distribution, list);
    }

    private void taskTotalCount() {
        QueryApi api = influxDBClient.getQueryApi();
        String s = "from(bucket: \"visual_log\")\n" +
                "|> range(start: time(v:\"#{[start]}\"), stop: time(v:\"#{[stop]}\"))\n" +
                "|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")" +
                "|> filter(fn: (r) => r.space ==  \"#{[space]}\")" +
                "|> filter(fn: (r) => r.theme ==  0)" +
                "|> filter(fn: (r) => r.#{[mode]} ==  \"#{[id]}\")" +
                "|> group(columns: [\"space\"], mode:\"by\")\n" +
                "|> count(column: \"theme\")";
        String str = SPELUtil.parseExpression(s, params);
        List<TaskRecord> query = api.query(str, TaskRecord.class);
        if (CollectionUtils.isEmpty(query)) {
            res.setTotalCount(0);
        } else {
            res.setTotalCount(query.get(0).getTheme());
        }
    }

    private void instanceError() {
        String s = "import \"date\"\n" +
                "from(bucket: \"visual_log\")\n" +
                "|> range(start: time(v:\"#{[start]}\"), stop: time(v:\"#{[stop]}\"))\n" +
                "|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")\n" +
                "|> filter(fn: (r) => r.space ==  \"#{[space]}\")" +
                "|> filter(fn: (r) => r.theme ==  3)\n" +
                "|> filter(fn: (r) => r.#{[mode]} ==  \"#{[id]}\")\n" +
                "|> group(columns: [ \"instance_id\"], mode:\"by\") \n" +
                "|> count(column: \"_measurement\")\n" +
                "|> rename(columns: { _measurement: \"value\" ,instance_id:\"name\"})" +
                "|> map(fn: (r) => ({ r with name: string(v: r.name),value: string(v: r.value) }))";
        String str = SPELUtil.parseExpression(s, params);
        QueryApi api = influxDBClient.getQueryApi();
        List<NameValueMetric> query = api.query(str, NameValueMetric.class);
        query.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        query.forEach(i->i.setName(Base62Util.zip(Long.parseLong(i.getName()))));
        res.getNameValueMetric().put(NameValueMetricType.instance_error_rank, query);
    }

    private void templateError() {
        String s = "import \"date\"\n" +
                "from(bucket: \"visual_log\")\n" +
                "|> range(start: time(v:\"#{[start]}\"), stop: time(v:\"#{[stop]}\"))\n" +
                "|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")\n" +
                "|> filter(fn: (r) => r.space ==  \"#{[space]}\")" +
                "|> filter(fn: (r) => r.theme ==  3)\n" +
                "|> filter(fn: (r) => r.#{[mode]} ==  \"#{[id]}\")\n" +
                "|> group(columns: [ \"template_id\"], mode:\"by\") \n" +
                "|> count(column: \"_measurement\")\n" +
                "|> rename(columns: { _measurement: \"value\" ,template_id:\"name\"})" +
                "|> map(fn: (r) => ({ r with name: string(v: r.name),value: string(v: r.value) }))";
        String str = SPELUtil.parseExpression(s, params);
        QueryApi api = influxDBClient.getQueryApi();
        List<NameValueMetric> query = api.query(str, NameValueMetric.class);
        query.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        summary.templateName(query);
        res.getNameValueMetric().put(NameValueMetricType.template_error_rank, query);
    }


}
