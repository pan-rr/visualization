package com.visualization.manager;

import com.visualization.model.dag.db.DAGPointer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class PointerQueueManager {

    private final PriorityBlockingQueue<DAGPointer> pointerQ = new PriorityBlockingQueue<>();


    public PriorityBlockingQueue<DAGPointer> getQueue() {
        return pointerQ;
    }

    public DAGPointer takeFromPointerQ() throws InterruptedException {
        return pointerQ.take();
    }

    public int getQSize(){
        return pointerQ.size();
    }

    public void offerPointer(List<DAGPointer> list) {
        pointerQ.addAll(list);
    }
}
