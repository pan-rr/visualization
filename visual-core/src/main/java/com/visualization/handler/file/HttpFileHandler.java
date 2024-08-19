package com.visualization.handler.file;

import com.visualization.constant.LocalFileConstant;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Component(value = "httpFileHandler")
public class HttpFileHandler implements FileHandler {
    @Override
    public File download(String sourcePath, Map<?, ?> uploadParam) {
        String fileName = Paths.get(sourcePath).getFileName().toString();
        String filePath = LocalFileConstant.getRandomHttpTempFilePath(fileName);
        File file;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 这里可以根据client 处理 timeout等配置
        OkHttpClient client = builder.build();
        Request request = new Request.Builder().url(sourcePath).build();
        try {
            InputStream inputStream = client.newCall(request).execute().body().byteStream();
            file = new File(filePath);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] arr = new byte[1024];
            while (inputStream.read(arr) != -1) {
                outputStream.write(arr);
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    @Override
    public void upload(File file, String targetPath, Map<?, ?> uploadParam) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.build();
        MediaType mediaType = MediaType.parse("multipart/form-data; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, file);
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), requestBody)
                .build();
        Request request = new Request.Builder()
                .url(targetPath)
                .post(body)
                .build();
        try {
            client.newCall(request).execute().body();
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void main(String[] args) {
//        HttpFileHandler httpFileHandler = new HttpFileHandler();
////        File file = httpFileHandler.download("http://localhost:8377/file/download/hobby.csv", null);
////        System.err.println(file.getAbsolutePath());
//        File file = new File("/Users/fenda/visual/hobby.csv");
//        httpFileHandler.upload(file,"http://localhost:8377/file/upload",null);
//    }


}
