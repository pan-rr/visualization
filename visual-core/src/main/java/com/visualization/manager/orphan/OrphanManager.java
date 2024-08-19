package com.visualization.manager.orphan;

import java.util.Map;

public abstract class OrphanManager {

    public static void execute(Map<String,String> properties){
        throw new RuntimeException("无法执行非法配置！");
    }
}
