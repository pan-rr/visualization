package com.visualization.stage;

import com.visualization.enums.WorkerGroup;
import com.visualization.thread.Worker;

public class VisualWorkerFactory {

    public static VisualWorker newWorker(Runnable task, WorkerGroup group) {
        Worker worker = new Worker(task);
        worker.setName(group.name());
        worker.setDaemon(true);
        worker.start();
        return VisualWorker.builder().worker(worker).group(group).build();
    }

    public static VisualWorker newWorker(Runnable task, WorkerGroup group, int index) {
        Worker worker = new Worker(task);
        worker.setName(group.name() + "-" + index);
        worker.setDaemon(true);
        worker.start();
        return VisualWorker.builder().worker(worker).group(group).build();
    }

}
