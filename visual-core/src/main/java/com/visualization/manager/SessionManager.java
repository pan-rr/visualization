package com.visualization.manager;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class SessionManager {

    private static final Random random = new Random();

    private static final Set<Integer> ids = new ConcurrentSkipListSet<>();

    public static int getId(){
        int id;
        do {
            id = Math.abs(random.nextInt());
        } while (!ids.add(id));
        return id;
    }

    public static void returnId(int id){
        ids.remove(id);
    }
}
