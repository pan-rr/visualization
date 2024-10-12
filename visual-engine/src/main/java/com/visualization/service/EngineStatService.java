package com.visualization.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class EngineStatService {

    @Value("${visual.dag.worker.count:8}")
    private Integer totalWorkerCount;

    private AtomicInteger idleWorkerCount;


    @PostConstruct
    public void init(){
        this.idleWorkerCount = new AtomicInteger(totalWorkerCount);
    }
    public Integer getTotalWorkerCount(){
        return this.totalWorkerCount;
    }

    public Boolean lessThanJobCount(int jobCount){
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
