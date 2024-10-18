package com.visualization.stage;

import com.visualization.enums.WorkerGroup;
import com.visualization.log.logger.VisualLogService;
import com.visualization.log.model.VisualStageWrapper;
import com.visualization.manager.DAGManager;
import com.visualization.model.dag.db.DAGPointer;
import com.visualization.model.dag.db.DAGTemplate;
import com.visualization.service.DAGService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Slf4j
public class VisualStageFailHandler {

    @Resource
    private DAGManager dagManager;

    @Resource
    private DAGService dagService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private VisualLogService visualLogService;

    @Resource
    private VisualEngineService visualEngineService;


    private final LinkedBlockingQueue<VisualStageContext> failQ = new LinkedBlockingQueue<>();


    @PostConstruct
    public void init() {
        visualEngineService.recruitWorker(() -> {
            for (; ; ) {
                try {
                    VisualStageContext context = failQ.take();
                    DAGPointer pointer = context.getPointer();
                    visualLogService.accept(VisualStageWrapper.fail(pointer, context.getThrowable()));
                    DAGTemplate template = dagService.getTemplateByPointer(pointer);
                    dagService.makeSurePointerConfigMatchTemplate(pointer, template);
                    pointer.fail();
                    dagManager.handleFailStage(pointer);
                    redisTemplate.delete(context.getStageName());
                } catch (Throwable e) {
                    log.error("failTaskWorker error : {} , {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
                }
            }
        }, WorkerGroup.DAG_FAIL_HANDLER);
    }

    public void offerFail(VisualStageContext context) {
        this.failQ.offer(context);
    }
}
