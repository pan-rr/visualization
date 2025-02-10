package com.visualization.model.portal.metric;

import com.visualization.runtime.VLogTheme;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Metric {

    private String mode;
    private String id;
    private String space;
    private String time;

    private int totalCount;

    private int finishedCount;

    private int errorCount;

    private List<Map<MetricKey, Object>> unfinishedTasks;

    private Map<NameValueMetricType, List<NameValueMetric>> nameValueMetric;

    private void validate() {
        boolean flag = StringUtils.isNoneBlank(mode, id, space, time);
        if (!flag) throw new NullPointerException("mode, id, space, time不能为空");
    }

    public void init() {
        validate();
        totalCount = 0;
        finishedCount = 0;
        errorCount = 0;
        unfinishedTasks = new ArrayList<>(0);
        nameValueMetric = new HashMap<>();
    }

    public void accumulate(VLogTheme theme) {
        switch (theme) {
            case FINISH:
                finishedCount++;
                break;
            case FAIL:
                errorCount++;
                break;
        }
    }
}
