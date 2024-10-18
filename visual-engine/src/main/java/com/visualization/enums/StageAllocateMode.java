package com.visualization.enums;

import lombok.Getter;

@Getter
public enum StageAllocateMode {

    RANDOM(1),
    APPOINTED_MACHINE(2);


    private final int mode;

    StageAllocateMode(int mode){
        this.mode = mode;
    }
}
