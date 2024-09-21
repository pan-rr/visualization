package com.visualization.handler;

import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicReference;

public class SpringContextHandler {

    private static final AtomicReference<ApplicationContext> ctxR = new AtomicReference<>(null);

    public static void setCtx(ApplicationContext ctx) {
        ctxR.set(ctx);
    }

    public static ApplicationContext getCtx() {
        return ctxR.get();
    }
}
