package com.visualization.controller;


import com.visualization.model.Response;
import com.visualization.model.file.FileDetail;
import com.visualization.service.FileManageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/portal/file/fileManage")
public class FileManageController {

    @Resource
    private FileManageService fileManageService;

    @GetMapping("/mkdir")
    public Response<String> mkdir(@RequestParam("path") String path) {
        fileManageService.mkdir(path,true);
        return Response.success(path);
    }

    @GetMapping("/listDir")
    public Response<List<FileDetail>> listDir(@RequestParam("path") String path) {
        List<FileDetail> res = fileManageService.listFolder(path);
        return Response.success(res);
    }

    @GetMapping("/download")
    public void download(@RequestParam("path") String path, HttpServletResponse response) {
       fileManageService.download(path,response);
    }

}
