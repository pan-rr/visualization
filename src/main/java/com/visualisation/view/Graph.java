package com.visualisation.view;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Graph {

    private List<Map<String,Object>> input;
    private Map<String,Object> output;

    public List<Map<String, Object>> getInput() {
        return input;
    }

    public void setInput(List<Map<String, Object>> input) {
        this.input = input;
    }

    public Map<String, Object> getOutput() {
        return output;
    }

    public void setOutput(Map<String, Object> output) {
        this.output = output;
    }

    public List<ViewConf> getInputView(){
        return input.stream().map(ViewConf::new).collect(Collectors.toList());
    }

    public Graph() {

    }

    public ViewConf getOutputView(){
        return new ViewConf(output);
    }
}
