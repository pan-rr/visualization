package com.visualization.thread;


public class Worker extends Thread{

    public Worker(Runnable r) {
        super(r);
    }

    public static Worker createDeamonWorker(Runnable r,String workerName){
        Worker worker = new Worker(r);
        worker.setName(workerName);
        worker.setDaemon(true);
        worker.start();
        return worker;
    }

}
