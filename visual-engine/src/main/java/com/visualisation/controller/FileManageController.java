package com.visualisation.controller;


import com.visualisation.model.Response;
import com.visualisation.model.file.FileDetail;
import com.visualisation.service.FileManageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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


}
