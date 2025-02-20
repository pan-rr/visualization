package com.visualization.stage;

import com.visualization.enums.WorkerGroup;
import com.visualization.manager.DAGManager;
import com.visualization.model.dag.db.DAGPointer;
import com.visualization.model.dag.db.DAGTemplate;
import com.visualization.runtime.VLogTheme;
import com.visualization.service.DAGService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Instant;
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
    private VisualEngineService visualEngineService;


    @Value("${visual.error.printStackTrace:false}")
    private Boolean printStackTrace;

    @Resource
    private VLogger vLogger;


    private final LinkedBlockingQueue<VisualStageContext> failQ = new LinkedBlockingQueue<>();


    private void log(DAGPointer pointer, Throwable e) {
        vLogger.accept(VLogPoint.builder()
                .templateId(pointer.getTemplateId().toString())
                .instanceId(pointer.getInstanceId().toString())
                .taskId(pointer.getTaskId().toString())
                .message(Arrays.toString(e.getStackTrace()))
                .theme(VLogTheme.FAIL.getCode())
                .space(pointer.getSpace())
                .time(Instant.now())
                .build());
    }

    @PostConstruct
    public void init() {
        visualEngineService.recruitWorker(() -> {
            for (; ; ) {
                try {
                    VisualStageContext context = failQ.take();
                    printStackTrace(context.getThrowable());
                    DAGPointer pointer = context.getPointer();
                    log(pointer, context.getThrowable());
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

    private void printStackTrace(Throwable e) {
        if (Boolean.TRUE.equals(printStackTrace)) {
            log.error("visual stage error {}", e.getStackTrace());
        }
    }

    public void offerFail(VisualStageContext context) {
        this.failQ.offer(context);
    }
}
