package com.visualization.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WorkerStatService {

    @Value("${visual.dag.worker.count:8}")
    private Integer workerCount;

    private AtomicInteger idleWorkerCount;


    @PostConstruct
    public void init(){
        this.idleWorkerCount = new AtomicInteger(workerCount);
    }
    public Integer getWorkerCount(){
        return this.workerCount;
    }

    public Boolean hungry(int jobCount){
        int max = Math.max(idleWorkerCount.get(), 1);
        return max >= jobCount;
    }

    public void decreaseIdle(){
        this.idleWorkerCount.decrementAndGet();
    }

    public void increaseIdle(){
        this.idleWorkerCount.incrementAndGet();
    }
}
