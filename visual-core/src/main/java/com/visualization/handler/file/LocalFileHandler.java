package com.visualization.handler.file;

import com.visualization.constant.LocalFileConstant;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Component(value = "localFileHandler")
public class LocalFileHandler implements FileHandler {

    @Override
    public int getPriority() {
        return 0;
    }

    private String getPath(String s) {
        return LocalFileConstant.getTempFilePath(s);
    }

    @Override
    public File download(String sourcePath, Map<?, ?> uploadParam) {
        File file;
        String path = getPath(sourcePath);
        file = new FileSystemResource(path).getFile();
        return file;
    }

    @Override
    public void upload(File file, String targetPath, Map<?, ?> uploadParam) {
        Path target = Paths.get(getPath(targetPath));
        Path filePath = file.toPath();
        try {
            Files.move(filePath, target);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
