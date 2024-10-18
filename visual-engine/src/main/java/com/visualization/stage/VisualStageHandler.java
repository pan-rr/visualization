package com.visualization.stage;

import com.visualization.enums.WorkerGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;

@Component
@Slf4j
public class VisualStageHandler {

    @Resource
    private VisualStageProcessorFactory factory;

    @Resource
    private VisualEngineService visualEngineService;

    @PostConstruct
    void init() {
        Integer workerCount = visualEngineService.getTotalWorkerCount();
        for (int i = 0; i < workerCount; i++) {
            visualEngineService.recruitWorker(() -> {
                for (; ; ) {
                    try {
                        factory.buildProcessor().proceed();
                    } catch (Throwable e) {
                        log.error("stage handler error : {} , {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
                    }
                }
            }, WorkerGroup.DAG_EXECUTOR, i);
        }
    }
}
