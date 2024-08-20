package com.visualization.manager;

import com.visualization.log.logger.VisualLogService;
import com.visualization.log.model.VisualStageWrapper;
import com.visualization.model.dag.db.DAGPointer;
import com.visualization.service.PointerDispatchService;
import com.visualization.thread.Worker;
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

    @Resource
    private VisualLogService visualLogService;

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
            t = Worker.createDeamonWorker(() -> {
                while (true) {
                    try {
                        DAGPointer pointer = pointerQ.take();
                        String key = pointer.computeLockKey();
                        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, key, 1, TimeUnit.HOURS);
                        // 拿不到锁就放弃任务
                        if (Boolean.FALSE.equals(flag)) continue;
                        map.put(Thread.currentThread(), pointer);
                        visualLogService.accept(VisualStageWrapper.start(pointer));
                        dagManager.executeTask(pointer);
                        redisTemplate.delete(key);
                        visualLogService.accept(VisualStageWrapper.success(pointer));
                    } catch (Exception e) {
                        DAGPointer pointer = map.get(Thread.currentThread());
                        visualLogService.accept(VisualStageWrapper.fail(pointer, e));
                        failTaskQ.offer(pointer);
                    }
                }
            }, "dag-worker-" + i);
            t.start();
            workers.add(t);
        }

        keeper = Worker.createDeamonWorker(() -> {
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
        }, "dag-keeper");
        keeper.start();

        failTaskWorker = Worker.createDeamonWorker(() -> {
            while (true) {
                try {
                    DAGPointer pointer = failTaskQ.take();
                    dagManager.updateTaskInfo(pointer);
                } catch (InterruptedException e) {
                    log.error("failTaskWorker error : {} , {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
                }
            }
        }, "dag-failTaskWorker");
        failTaskWorker.start();

    }

    @Scheduled(fixedDelay = 30000L)
    public void offerSignal() {
        signalQ.offer(1);
    }


}
