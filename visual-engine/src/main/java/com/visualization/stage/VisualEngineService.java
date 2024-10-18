package com.visualization.stage;

import com.visualization.enums.Action;
import com.visualization.enums.WorkerGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class VisualEngineService {

    @Resource
    private ApplicationContext context;

    @Value("${visual.dag.worker.count:8}")
    private Integer totalWorkerCount;

    @Value("${visual.dag.worker.count/2+1:1}")
    private Integer idleThreshold;

    private AtomicInteger idleWorkerCount;

    private final AtomicInteger jobCount = new AtomicInteger(0);

    private final List<VisualWorker> workers = new ArrayList<>();


    @PostConstruct
    public void init() {
        this.idleWorkerCount = new AtomicInteger(totalWorkerCount);
    }

    public Integer getTotalWorkerCount() {
        return this.totalWorkerCount;
    }

    public Boolean allowLoadLocalJob() {
        return idleWorkerCount.get() >= idleThreshold;
    }

    public void decreaseIdle() {
        this.idleWorkerCount.decrementAndGet();
    }

    public void increaseIdle() {
        this.idleWorkerCount.incrementAndGet();
    }

    public void decreaseJobCount() {
        this.jobCount.decrementAndGet();
        if (allowLoadLocalJob()) {
            context.publishEvent(Action.LOAD_LOCAL_JOB);
        }
    }

    public void increaseJobCount(int delta) {
        this.jobCount.addAndGet(delta);
    }

    public void recruitWorker(Runnable task, WorkerGroup group, int workerNum) {
        workers.add(VisualWorkerFactory.newWorker(task, group, workerNum));
    }

    public void recruitWorker(Runnable task, WorkerGroup group) {
        workers.add(VisualWorkerFactory.newWorker(task, group));
    }

}
