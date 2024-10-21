package com.visualization.stage;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Data
public class VisualStageProcessor {

    private VisualStageContext context;

    private List<Function<VisualStageContext, Boolean>> handleChain;

    private Consumer<VisualStageContext> errorHandler;

    private Consumer<VisualStageContext> finalHandler;

    public void proceed() {
        try {
            boolean flag;
            for (Function<VisualStageContext, Boolean> handler : handleChain) {
                flag = handler.apply(context);
                if (!Boolean.TRUE.equals(flag)) break;
            }
        } catch (Throwable e) {
            context.setThrowable(e);
            errorHandler.accept(context);
        } finally {
            finalHandler.accept(context);
        }
    }

}
