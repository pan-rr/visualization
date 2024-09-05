package com.visualization.manager;

import com.visualization.model.dag.db.DAGPointer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class PointerQueueManager {

    private final LinkedBlockingQueue<DAGPointer> Q = new LinkedBlockingQueue<>();

    public LinkedBlockingQueue<DAGPointer> getQueue() {
        return Q;
    }

    public void addAll(List<DAGPointer> list) {
        Q.addAll(list);
    }
}
