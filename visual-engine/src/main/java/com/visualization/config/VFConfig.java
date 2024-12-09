package com.visualization.config;

import com.visualization.runtime.VFunctions;
import com.visualization.stage.VLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Configuration
public class VFConfig {

    @Resource
    private VLogger vLogger;

    @PostConstruct
    public void init() {
        log();
    }

    private void log() {
        VFunctions.LOG.recoverFunction((vlog -> {
            vLogger.accept(vlog);
        }));
    }
}
