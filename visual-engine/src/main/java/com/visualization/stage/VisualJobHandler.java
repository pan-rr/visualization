package com.visualization.stage;

import com.visualization.model.dag.db.DAGPointer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class VisualJobHandler {

    @Resource
    private VisualEngineService visualEngineService;

    private final PriorityBlockingQueue<DAGPointer> pointerQ = new PriorityBlockingQueue<>();


    public DAGPointer takePointer() throws InterruptedException {
        DAGPointer take = pointerQ.take();
        visualEngineService.decreaseJobCount();
        return take;
    }

    public void offerPointer(List<DAGPointer> list) {
        if (!CollectionUtils.isEmpty(list)) {
            pointerQ.addAll(list);
            visualEngineService.increaseJobCount(list.size());
        }
    }

}
