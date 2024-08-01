package com.visualisation.utils;

import com.visualisation.jpa.SnowIdWorker;

public class SnowIdUtil {

    private static final SnowIdWorker worker = new SnowIdWorker(0,0);

    public static Long nextId(){
        return worker.nextId();
    }
}
