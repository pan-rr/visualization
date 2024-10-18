package com.visualization.stage;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.function.Function;

@Component
public class VisualStageProcessorFactory {

    @Resource
    private List<Function<VisualStageContext, Boolean>> handleChain;

    @Resource
    private VisualStageFailHandler failStageHandler;

    @Resource
    private VisualEngineService visualEngineService;

    public VisualStageProcessor buildProcessor(){
        VisualStageProcessor processor = new VisualStageProcessor();
        processor.setContext(new VisualStageContext());
        processor.setHandleChain(handleChain);
        processor.setErrorHandler(failStageHandler::offerFail);
        processor.setFinalHandler(context -> {
            if (context.isOccupy()){
                visualEngineService.increaseIdle();
            }
        });
        return processor;
    }
}
