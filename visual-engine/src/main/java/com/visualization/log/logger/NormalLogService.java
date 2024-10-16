package com.visualization.log.logger;

import com.visualization.exception.StageLogException;
import com.visualization.log.model.StageLogPoint;
import com.visualization.log.model.VisualStageWrapper;
import com.visualization.model.dag.db.DAGPointer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class NormalLogService extends VisualLogService {

    @Override
    protected void handleLog(List<VisualStageWrapper> list) {
        for (VisualStageWrapper visualStageWrapper : list) {
            DAGPointer pointer = visualStageWrapper.getPointer();
            log.error("实例ID:{},任务ID:{},结果:{},", pointer.getInstanceId(), pointer.getTaskId(), visualStageWrapper.getStageExecuteMessage());
        }
    }

    @Override
    public List<StageLogPoint> getLogPointsByInstanceId(String instanceId) {
        throw new StageLogException("本地logger不支持查询操作");
    }
}
