package com.visualization;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.visualization.manager.FileManager;
import com.visualization.view.base.VisualStage;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Component
public class Commander {


    public void executeStage(VisualStage stage) {
        stage.execute();
    }


    public void commandWithJsonString(String json) {
        Gson gson = new Gson();
        VisualStage visualStage = gson.fromJson(json, VisualStage.class);
        visualStage.execute();
    }

    public void commandWithLocalFile(String path) throws IOException {
        File file = FileManager.getFileByPath(path);
        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        JsonReader jsonReader = new JsonReader(br);
        Gson gson = new Gson();
        VisualStage visualStage = gson.fromJson(jsonReader, VisualStage.class);
        visualStage.execute();
        br.close();
        jsonReader.close();
    }

}
