package com.visualization.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

@RestController
@Slf4j
public class FileController {

    private static final String DIR = System.getProperty("user.home") + "/visual/visualFile/";

    @GetMapping("/download/{fileName}")
    public void fileDownLoad(HttpServletResponse response, @PathVariable("fileName") String fileName) {
        File file = new File(DIR + fileName);
        System.err.println(DIR + fileName);
        if (!file.exists()) {
            throw new RuntimeException("文件不存在！");
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()));) {
            byte[] buff = new byte[1024];
            OutputStream os = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            log.error("{}", e);
        }
    }


//    @GetMapping("/upload")
//    public void httpUpload(@RequestParam("files") MultipartFile files[]) throws IOException {
//        for (MultipartFile file : files) {
//            String fileName = file.getOriginalFilename();  // 文件名
//            File dest = new File(DIR + fileName);
//            if (!dest.getParentFile().exists()) {
//                dest.getParentFile().mkdirs();
//            }
//            try {
//                file.transferTo(dest);
//            } catch (Exception e) {
//                throw e;
//            }
//        }
//
//    }


    @PostMapping("/upload/{fileName}")
    public void fileUpload(@RequestParam("file") MultipartFile file, @PathVariable("fileName") String fileName) throws IOException {
        if (fileName == null) fileName = file.getOriginalFilename();  // 文件名
        File dest = new File(DIR + fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
    }

}
