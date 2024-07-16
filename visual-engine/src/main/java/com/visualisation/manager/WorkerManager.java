package com.visualisation.manager;

import com.visualisation.model.dag.DAGPointer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class WorkerManager {
    @Value("${visual.dag.worker.count}")
    private Integer workerCount = 8;

    @Value("${visual.pointer.scanLimit}")
    private Integer scanLimit = 10;

    @Value("${visual.pointer.queueName}")
    private String queueName;

    @Resource
    private DAGManager dagManager;

    private int watermark;

    private LinkedBlockingQueue<DAGPointer> q = new LinkedBlockingQueue<>();

    private LinkedBlockingQueue<Object> signal = new LinkedBlockingQueue<>();

    private LinkedBlockingQueue<DAGPointer> increaseFailCountQ = new LinkedBlockingQueue<>();
    private List<Worker> workers;

    private Worker keeper;

    private Worker increaseWorker;

    private ConcurrentHashMap<Thread, DAGPointer> map = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        workers = new ArrayList<>(workerCount);
        watermark = Math.max(workerCount / 2, 5);
        Worker t;
        for (int i = 0; i < workerCount; i++) {
            t = new Worker(() -> {
                while (true) {
                    try {
                        DAGPointer pointer = q.take();
                        map.put(Thread.currentThread(), pointer);
                        dagManager.executeTask(pointer);
                        map.remove(Thread.currentThread());
                    } catch (Exception e) {
                        e.printStackTrace();
                        DAGPointer pointer = map.get(Thread.currentThread());
                        increaseFailCountQ.offer(pointer);
                    }
                }
            });
            t.setDaemon(true);
            t.setName("dag-worker-" + i);
            t.start();
            workers.add(t);
        }

        keeper = new Worker(() -> {
            while (true) {
                try {
                    signal.take();
                    if (q.size() <= watermark) {
                        List<DAGPointer> pointers = dagManager.getPointers(scanLimit);
                        System.err.println(pointers);
                        if (!CollectionUtils.isEmpty(pointers)) {
                            q.addAll(pointers);
                        }
                        // 只提取一次
                        signal.clear();

                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        keeper.setDaemon(true);
        keeper.setName("dag-keeper");
        keeper.start();

        increaseWorker = new Worker(() -> {
            while (true) {
                try {
                    DAGPointer pointer = increaseFailCountQ.take();
                    dagManager.updateCount(pointer);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        increaseWorker.setDaemon(true);
        increaseWorker.setName("dag-increaseWorker");
        increaseWorker.start();

    }

    @Scheduled(fixedDelay = 10000L)
    public void offerSignal() {
        signal.offer(1);
    }

    private static class Worker extends Thread {
        public Worker(Runnable r) {
            super(r);
        }
    }

}
