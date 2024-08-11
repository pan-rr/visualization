package com.visualisation.controller;


import com.visualisation.model.Response;
import com.visualisation.model.file.FileChunkParam;
import com.visualisation.model.file.FileChunkRecord;
import com.visualisation.model.file.FileChunkTask;
import com.visualisation.service.FileChunkService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


/**
 * 分片上传-分片任务记录(SysUploadTask)表控制层
 *
 * @since 2022-08-22 17:47:31
 */
@RestController
@RequestMapping("/portal/fileChunk")
public class FileChunkController {
    /**
     * 服务对象
     */
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
     * 获取上传进度
     *
     * @param md5 文件md5
     * @return 结果
     */
    @GetMapping("/{md5}")
    public Response<FileChunkTask> taskInfo(@PathVariable("md5") String md5) {
        return Response.success(fileChunkService.checkFileChunkTask(md5));
    }


    /**
     * 获取每个分片的预签名上传地址
     *
     * @param md5        md5
     * @param partNumber 分片
     * @return 结果
     */
    @GetMapping("/{md5}/{partNumber}")
    public Response<Object> preSignUploadUrl(@PathVariable("md5") String md5, @PathVariable("partNumber") Integer partNumber) {
        FileChunkRecord record = fileChunkService.getFileChunkRecordByMD5(md5);
        Map<String, String> params = new HashMap<>();
        params.put("partNumber", partNumber.toString());
        params.put("uploadId", record.getUploadId());
        return Response.success(fileChunkService.genPreSignUploadUrl(record.getOssKey(), params));
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
