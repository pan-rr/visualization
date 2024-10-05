package com.visualization.service.impl;

import com.visualization.manager.DAGManager;
import com.visualization.manager.PointerQueueManager;
import com.visualization.model.dag.db.DAGPointer;
import com.visualization.service.PointerDispatchService;
import com.visualization.service.WorkerStatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;


@Service
@Slf4j
public class PointerDispatchServiceImpl implements PointerDispatchService {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @Value("${visual.pointer.scanLimit:10}")
    private Integer scanLimit;

    @Value("${visual.pointer.queueName:visual_pointer}")
    private String queueName;

    @Resource
    private PointerQueueManager pointerQueueManager;

    @Resource
    private DAGManager dagManager;

    @Resource
    private RedisTemplate<String,DAGPointer> redisTemplate;

    @Resource
    private WorkerStatService workerStatService;

    private final ArrayBlockingQueue<Object> signalQ = new ArrayBlockingQueue<>(2);

    @Scheduled(fixedDelay = 30000L)
    public void offerSignal() {
        signalQ.offer(BigInteger.ZERO);
    }


    private void takeSignal() throws InterruptedException {
        this.signalQ.take();
    }

    private void clearSignal() {
        this.signalQ.clear();
    }

    private boolean allowDispatch() {
        return workerStatService.hungry(pointerQueueManager.getQSize());
    }

    private void loadRemoteQueue(){
        ZSetOperations<String, DAGPointer> zSet = redisTemplate.opsForZSet();
        Long size = zSet.size(queueName);
        if (Objects.isNull(size) || size < workerStatService.getWorkerCount()){
            List<DAGPointer> pointers = dagManager.getPointers(scanLimit * 2);
            Set<ZSetOperations.TypedTuple<DAGPointer>> collect = pointers.stream().map(i -> ZSetOperations.TypedTuple.of(i, i.getPriority())).collect(Collectors.toSet());
            zSet.add(queueName,collect);
        }
    }

    private void loadLocalQueue(){
        Integer workerCount = workerStatService.getWorkerCount();
        ZSetOperations<String, DAGPointer> zSet = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<DAGPointer>> set = zSet.popMax(queueName, workerCount);
        if (!CollectionUtils.isEmpty(set)){
            this.pointerQueueManager.offerPointer(set.stream().map(ZSetOperations.TypedTuple::getValue).collect(Collectors.toList()));
        }else {
            this.pointerQueueManager.offerPointer(dagManager.getPointers(workerCount));
        }
    }

    @Override
    public void dispatchPointer() throws InterruptedException {
        if (!this.allowDispatch()) {
            return;
        }
        this.takeSignal();
        loadRemoteQueue();
        loadLocalQueue();
        this.clearSignal();
    }
}
