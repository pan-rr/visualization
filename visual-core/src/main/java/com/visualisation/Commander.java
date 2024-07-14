package com.visualisation;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.visualisation.handler.FileHandler;
import com.visualisation.manager.ViewManager;
import com.visualisation.view.Graph;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.*;

@Component
public class Commander {

    @Resource(name = "db")
    private DataSource dataSource;

    public void commandWithJsonString(String json) {
        Gson gson = new Gson();
        Graph graph = gson.fromJson(json, Graph.class);
        ViewManager viewManager = new ViewManager(graph.getInputView(), graph.getOutputView(), dataSource);
        viewManager.execute();
    }

    public void commandWithLocalFile(String path) throws IOException {
        File file = FileHandler.getFileByPath(path);
        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        JsonReader jsonReader = new JsonReader(br);
        Gson gson = new Gson();
        Graph graph = gson.fromJson(jsonReader, Graph.class);
        ViewManager viewManager = new ViewManager(graph.getInputView(), graph.getOutputView(), dataSource);
        viewManager.execute();
        br.close();
        jsonReader.close();
    }

}
