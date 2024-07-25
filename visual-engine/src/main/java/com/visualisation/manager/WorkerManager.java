package com.visualisation.manager;

import com.visualisation.model.dag.DAGPointer;
import com.visualisation.service.PointerDispatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class WorkerManager {
    @Value("${visual.dag.worker.count}")
    private Integer workerCount = 8;

    @Value("${visual.pointer.scanLimit}")
    private Integer scanLimit = 10;

    @Value("${visual.pointer.queueName}")
    private String queueName;

    @Resource
    private DAGManager dagManager;

    @Resource
    private PointerQueueManager pointerQueueManager;

    @Resource
    private PointerDispatchService pointerDispatchService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private int watermark;

    private LinkedBlockingQueue<DAGPointer> pointerQ;

    private LinkedBlockingQueue<Object> signalQ = new LinkedBlockingQueue<>();

    private LinkedBlockingQueue<DAGPointer> failTaskQ = new LinkedBlockingQueue<>();
    private List<Worker> workers;

    private Worker keeper;

    private Worker failTaskWorker;

    private ConcurrentHashMap<Thread, DAGPointer> map = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        pointerQ = pointerQueueManager.getQueue();
        workers = new ArrayList<>(workerCount);
        watermark = Math.max(workerCount / 2, 5);
        Worker t;
        for (int i = 0; i < workerCount; i++) {
            t = new Worker(() -> {
                while (true) {
                    try {
                        DAGPointer pointer = pointerQ.take();
                        String key = "visual_pointer_" + pointer.getTaskId();
                        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, key, 1, TimeUnit.HOURS);
                        // 拿不到锁就放弃任务
                        if (Boolean.FALSE.equals(flag)) continue;
                        map.put(Thread.currentThread(), pointer);
                        dagManager.executeTask(pointer);
                        redisTemplate.delete(key);
                    } catch (Exception e) {
                        log.error("处理失败，原因：{} , {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
                        DAGPointer pointer = map.get(Thread.currentThread());
                        failTaskQ.offer(pointer);
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
                    signalQ.take();
                    if (pointerQ.size() <= watermark) {
                        pointerDispatchService.dispatchPointer(scanLimit);
                        signalQ.clear();
                    }
                } catch (Exception e) {
                    log.error("keeper error : {} , {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
                }
            }
        });
        keeper.setDaemon(true);
        keeper.setName("dag-keeper");
        keeper.start();

        failTaskWorker = new Worker(() -> {
            while (true) {
                try {
                    DAGPointer pointer = failTaskQ.take();
                    dagManager.updateTaskInfo(pointer);
                } catch (InterruptedException e) {
                    log.error("failTaskWorker error : {} , {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
                }
            }
        });
        failTaskWorker.setDaemon(true);
        failTaskWorker.setName("dag-failTaskWorker");
        failTaskWorker.start();

    }

    @Scheduled(fixedDelay = 30000L)
    public void offerSignal() {
        signalQ.offer(1);
    }

    private static class Worker extends Thread {
        public Worker(Runnable r) {
            super(r);
        }
    }

}
