package com.visualisation;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.visualisation.filter.ViewFilter;
import com.visualisation.manager.FileManager;
import com.visualisation.manager.ViewManager;
import com.visualisation.view.Graph;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Component
public class Commander {

    @Resource(name = "db")
    private DataSource dataSource;

    @Resource
    private List<ViewFilter> viewFilters;

    public void commandWithJsonString(String json) {
        Gson gson = new Gson();
        Graph graph = gson.fromJson(json, Graph.class);
        graph.filter(viewFilters);
        ViewManager viewManager = new ViewManager(graph.getInputView(), graph.getOutputView(), dataSource);
        viewManager.execute();
    }

    public void commandWithLocalFile(String path) throws IOException {
        File file = FileManager.getFileByPath(path);
        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        JsonReader jsonReader = new JsonReader(br);
        Gson gson = new Gson();
        Graph graph = gson.fromJson(jsonReader, Graph.class);
        graph.filter(viewFilters);
        ViewManager viewManager = new ViewManager(graph.getInputView(), graph.getOutputView(), dataSource);
        viewManager.execute();
        br.close();
        jsonReader.close();
    }

}
