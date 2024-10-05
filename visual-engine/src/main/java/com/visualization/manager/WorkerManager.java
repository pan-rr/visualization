package com.visualization.manager;

import com.visualization.log.logger.VisualLogService;
import com.visualization.log.model.VisualStageWrapper;
import com.visualization.model.dag.db.DAGPointer;
import com.visualization.service.PointerDispatchService;
import com.visualization.service.WorkerStatService;
import com.visualization.thread.Worker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

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

    @Resource
    private WorkerStatService workerStatService;


    private LinkedBlockingQueue<Tuple2<DAGPointer, Throwable>> failTaskQ = new LinkedBlockingQueue<>();
    private List<Worker> workers;

    private Worker keeper;

    private Worker failTaskWorker;

    private final ConcurrentHashMap<Thread, DAGPointer> map = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        Integer workerCount = workerStatService.getWorkerCount();
        workers = new ArrayList<>(workerCount);
        Worker t;
        for (int i = 0; i < workerCount; i++) {
            t = Worker.createDeamonWorker(() -> {
                while (true) {
                    try {
                        workerStatService.decreaseIdle();
                        DAGPointer pointer = pointerQueueManager.takeFromPointerQ();
                        String key = pointer.computeLockKey();
                        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, key, 1, TimeUnit.HOURS);
                        // 拿不到锁就放弃任务
                        if (Boolean.FALSE.equals(flag)) continue;
                        map.put(Thread.currentThread(), pointer);
                        visualLogService.accept(VisualStageWrapper.start(pointer));
                        dagManager.executeStage(pointer);
                        visualLogService.accept(VisualStageWrapper.success(pointer));
                    } catch (Throwable e) {
                        failTaskQ.offer(Tuples.of(map.get(Thread.currentThread()), e));
                    } finally {
                        workerStatService.increaseIdle();
                    }
                }
            }, "dag-worker-" + i);
            workers.add(t);
        }

        keeper = Worker.createDeamonWorker(() -> {
            while (true) {
                try {
                    pointerDispatchService.dispatchPointer();
                } catch (Throwable e) {
                    log.error("keeper error : {} , {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
                }
            }
        }, "dag-keeper");

        failTaskWorker = Worker.createDeamonWorker(() -> {
            while (true) {
                try {
                    Tuple2<DAGPointer, Throwable> tuple = failTaskQ.take();
                    DAGPointer pointer = tuple.getT1();
                    visualLogService.accept(VisualStageWrapper.fail(pointer, tuple.getT2()));
                    pointer.fail();
                    dagManager.updateTaskInfo(pointer);
                    redisTemplate.delete(pointer.computeLockKey());
                } catch (Throwable e) {
                    log.error("failTaskWorker error : {} , {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
                }
            }
        }, "dag-failTaskWorker");

    }


}
