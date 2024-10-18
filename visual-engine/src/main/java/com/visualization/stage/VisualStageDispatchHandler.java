package com.visualization.stage;

import com.visualization.enums.Action;
import com.visualization.enums.WorkerGroup;
import com.visualization.manager.DAGManager;
import com.visualization.model.dag.db.DAGPointer;
import com.visualization.thread.Worker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;


@Service
@Slf4j
public class VisualStageDispatchHandler {

    @Value("${visual.dag.stage.dispatch-batch-size:10}")
    private Integer batchSize;

    @Value("${visual.dag.stage.queue-name}")
    private String queueName;

    @Resource
    private VisualJobHandler visualJobHandler;

    @Resource
    private DAGManager dagManager;

    @Resource
    private RedisTemplate<String, DAGPointer> redisTemplate;

    @Resource
    private VisualEngineService visualEngineService;

    private final ArrayBlockingQueue<Action> actionQ = new ArrayBlockingQueue<>(5);

    private Worker keeper;

    @PostConstruct
    public void init() {
        visualEngineService.recruitWorker(() -> {
            while (true) {
                try {
                    Action action = actionQ.take();
                    switch (action) {
                        case LOAD_LOCAL_JOB:
                            loadLocalQueue();
                            break;
                        case LOAD_REMOTE_JOB:
                            loadRemoteQueue();
                            break;
                        default:
                    }
                } catch (Throwable e) {
                    log.error("dispatcher error : {} , {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
                }
            }
        }, WorkerGroup.DAG_DISPATCH);
    }

    @Scheduled(fixedDelay = 60000L)
    public void offerSignal() {
        actionQ.offer(Action.LOAD_REMOTE_JOB);
        actionQ.offer(Action.LOAD_LOCAL_JOB);
    }

    @EventListener
    public void actionHandler(Action action) {
        actionQ.offer(action);
    }

    private boolean allowDispatch() {
        return visualEngineService.allowLoadLocalJob();
    }

    private void loadRemoteQueue() {
        ZSetOperations<String, DAGPointer> zSet = redisTemplate.opsForZSet();
        Long size = zSet.size(queueName);
        if (Objects.isNull(size) || size <= batchSize) {
            List<DAGPointer> pointers = dagManager.getExecutablePointers(batchSize);
            Set<ZSetOperations.TypedTuple<DAGPointer>> collect = pointers.stream().map(i -> ZSetOperations.TypedTuple.of(i, i.getPriority())).collect(Collectors.toSet());
            zSet.add(queueName, collect);
        }
    }

    private void loadLocalQueue() {
        if (!this.allowDispatch()) {
            return;
        }
        Integer workerCount = visualEngineService.getTotalWorkerCount();
        ZSetOperations<String, DAGPointer> zSet = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<DAGPointer>> set = zSet.popMax(queueName, workerCount);
        if (!CollectionUtils.isEmpty(set)) {
            this.visualJobHandler.offerPointer(set.stream().map(ZSetOperations.TypedTuple::getValue).collect(Collectors.toList()));
        } else {
            this.visualJobHandler.offerPointer(dagManager.getExecutablePointers(workerCount));
        }
    }


}
