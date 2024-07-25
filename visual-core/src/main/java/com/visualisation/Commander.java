package com.visualisation;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.visualisation.filter.ViewFilter;
import com.visualisation.manager.FileManager;
import com.visualisation.view.VisualGraph;
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
        VisualGraph visualGraph = gson.fromJson(json, VisualGraph.class);
        visualGraph.execute();
    }

    public void commandWithLocalFile(String path) throws IOException {
        File file = FileManager.getFileByPath(path);
        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        JsonReader jsonReader = new JsonReader(br);
        Gson gson = new Gson();
        VisualGraph visualGraph = gson.fromJson(jsonReader, VisualGraph.class);
        visualGraph.execute();
        br.close();
        jsonReader.close();
    }

}
