package com.visualization.log.logger;

import com.visualization.exception.StageLogException;
import com.visualization.log.model.StageLogPoint;
import com.visualization.log.model.VisualStageWrapper;
import com.visualization.thread.Worker;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public abstract class VisualLogService {

    private final LinkedBlockingQueue<VisualStageWrapper> Q = new LinkedBlockingQueue<>();

    @PostConstruct
    public void init() {
        Worker logWorker = Worker.createDeamonWorker(() -> {
            while (true) {
                try {
                    handleLog(take());
                } catch (Throwable e) {
                    log.error("visual-log 异常:", e);
                }
            }
        }, "visual-logWorker");
        logWorker.start();
    }

    public void accept(VisualStageWrapper log) {
        Q.offer(log);
    }

    protected void handleLog(List<VisualStageWrapper> list) {

    }

    @SneakyThrows
    private List<VisualStageWrapper> take() {
        VisualStageWrapper one = Q.take();
        int size = Q.size();
        List<VisualStageWrapper> res = new ArrayList<>(size + 1);
        if (size > 0) {
            Q.drainTo(res, size);
        }
        res.add(one);
        return res;
    }

    public List<StageLogPoint> getLogPointsByInstanceId(String instanceId) {
        throw new StageLogException("未实现日志查询操作");
    }
}
