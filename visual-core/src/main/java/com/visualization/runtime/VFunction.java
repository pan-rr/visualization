package com.visualization.runtime;


import java.util.function.Consumer;

public class VFunction<D> {

    protected Consumer<D> handler = (d) -> {
        // do nothing
    };

    public void recoverFunction(Consumer<D> function) {
        this.handler = function;
    }

    public void handle(D data) {
        this.handler.accept(data);
    }

}
