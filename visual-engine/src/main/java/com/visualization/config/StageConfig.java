package com.visualization.config;

import com.visualization.enums.Status;
import com.visualization.manager.DAGManager;
import com.visualization.model.dag.db.DAGPointer;
import com.visualization.runtime.VLogTheme;
import com.visualization.runtime.VLogTheme;
import com.visualization.stage.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Configuration
public class StageConfig {

    @Resource
    private DAGManager dagManager;

    @Resource
    private VisualJobHandler visualJobHandler;

    @Resource
    private VisualStageDispatchHandler visualStageDispatchHandler;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private VisualEngineService visualEngineService;

    @Resource
    private VLogger vLogger;


    @Order(-9999999)
    @Bean
    public Function<VisualStageContext, Boolean> takePointer() {
        return ctx -> {
            try {
                ctx.setPointer(visualJobHandler.takePointer());
                ctx.computeStageName();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        };
    }

    @Order(-99999)
    @Bean
    public Function<VisualStageContext, Boolean> occupy() {
        return ctx -> {
            visualEngineService.decreaseIdle();
            ctx.setOccupy(true);
            return true;
        };
    }

    @Order(-9999)
    @Bean
    public Function<VisualStageContext, Boolean> registerJob() {
        return ctx -> {
            String key = ctx.getStageName();
            return redisTemplate.opsForValue().setIfAbsent(key, Status.NEW.getStatusName(), 1, TimeUnit.HOURS);
        };
    }

    @Order(-999)
    @Bean
    public Function<VisualStageContext, Boolean> startLog() {
        return ctx -> {
            DAGPointer pointer = ctx.getPointer();
            vLogger.accept(VLogPoint.builder()
                    .templateId(pointer.getTemplateId().toString())
                    .instanceId(pointer.getInstanceId().toString())
                    .taskId(pointer.getTaskId().toString())
                    .message(VLogTheme.START.getMessage())
                    .theme(VLogTheme.START.getCode())
                    .time(Instant.now())
                    .build());
            return true;
        };
    }

    @Order(-99)
    @Bean
    public Function<VisualStageContext, Boolean> executeJob() {
        return ctx -> {
            dagManager.executeStage(ctx.getPointer());
            redisTemplate.opsForValue().set(ctx.getStageName(), Status.FINISHED.getStatusName(), 1, TimeUnit.HOURS);
            return true;
        };
    }

    @Order(-9)
    @Bean
    public Function<VisualStageContext, Boolean> successLog() {
        return ctx -> {
            DAGPointer pointer = ctx.getPointer();
            vLogger.accept(VLogPoint.builder()
                    .templateId(pointer.getTemplateId().toString())
                    .instanceId(pointer.getInstanceId().toString())
                    .taskId(pointer.getTaskId().toString())
                    .message(VLogTheme.FINISH.getMessage())
                    .theme(VLogTheme.FINISH.getCode())
                    .time(Instant.now())
                    .build());
            return true;
        };
    }
}
