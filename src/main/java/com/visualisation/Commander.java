package com.visualisation;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.visualisation.manager.ViewManager;
import com.visualisation.view.Graph;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Paths;

@Component
public class Commander {

    @Resource
    private DataSource dataSource;

    public void commandWithJsonString(String json) {
        Gson gson = new Gson();
        Graph graph = gson.fromJson(json, Graph.class);
        ViewManager viewManager = new ViewManager(graph.getInputView(), graph.getOutputView(), dataSource);
        viewManager.execute();
    }

    public void commandWithLocalFile(String path) throws IOException {

        JsonReader jsonReader;
        if (path.startsWith("/")) {
            File file = Paths.get(path).toFile();
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            jsonReader = new JsonReader(br);
        } else {
            ClassPathResource resource = new ClassPathResource(path);
            InputStream inputStream = resource.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            jsonReader = new JsonReader(br);
        }
        Gson gson = new Gson();
        Graph graph = gson.fromJson(jsonReader, Graph.class);
        ViewManager viewManager = new ViewManager(graph.getInputView(), graph.getOutputView(), dataSource);
        viewManager.execute();
    }

}
