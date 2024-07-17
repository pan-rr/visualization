package com.visualisation.manager;

import com.visualisation.model.dag.DAGPointer;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

@Component
public class PointerQueueManager {

    private LinkedBlockingQueue<DAGPointer> q = new LinkedBlockingQueue<>();

    public LinkedBlockingQueue<DAGPointer> getQueue(){
        return q;
    }
}
