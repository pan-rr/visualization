package com.visualization.controller;


import com.visualization.model.Response;
import com.visualization.model.file.FileChunkParam;
import com.visualization.model.file.FileChunkRecord;
import com.visualization.model.file.FileChunkTask;
import com.visualization.service.FileChunkService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/portal/file/fileChunk")
public class FileChunkController {

    @Resource
    private FileChunkService fileChunkService;


    /**
     * 创建一个上传任务
     *
     * @return 结果
     */
    @PostMapping("/initFileChunk")
    public Response<FileChunkTask> initFileChunk(@Valid @RequestBody FileChunkParam param, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Response.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return Response.success(fileChunkService.initFileChunkTask(param));
    }

    /**
     * 尝试秒传
     *
     * @return 结果
     */
    @PostMapping("/tryQuickUpload")
    public Response<Object> tryQuickUpload(@Valid @RequestBody FileChunkParam param, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Response.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return Response.success(fileChunkService.tryQuickUpload(param));
    }


    /**
     * 获取上传进度
     *
     * @param md5 文件md5
     * @return 结果
     */
    @GetMapping("/checkFileChunkTask/{md5}")
    public Response<FileChunkTask> checkFileChunkTask(@PathVariable("md5") String md5) {
        return Response.success(fileChunkService.checkFileChunkTask(md5));
    }


    /**
     * 获取每个分片的预签名上传地址
     *
     * @param md5        md5
     * @param partNumber 分片
     * @return 结果
     */
    @GetMapping("/generatePreSignedUrl/{md5}/{partNumber}")
    public Response<Object> generatePreSignedUrl(@PathVariable("md5") String md5, @PathVariable("partNumber") Integer partNumber) {
        FileChunkRecord record = fileChunkService.getFileChunkRecordByMD5(md5);
        Map<String, String> params = new HashMap<>();
        params.put("partNumber", partNumber.toString());
        params.put("uploadId", record.getUploadId());
        return Response.success(fileChunkService.generatePreSignedUrl(record.getOssKey(), params));
    }

    /**
     * 合并分片
     *
     * @param md5
     * @return 结果
     */
    @PostMapping("/merge/{md5}")
    public Response merge(@PathVariable("md5") String md5) {
        fileChunkService.merge(md5);
        return Response.success("合并成功！");
    }

}
