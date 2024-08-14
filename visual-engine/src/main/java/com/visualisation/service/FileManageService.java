package com.visualisation.service;

import com.visualisation.model.file.FileDetail;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface FileManageService {

    void mkdir(String folderPath, boolean needRewrite);

    List<FileDetail> listFolder(String folderPath);

    void download(String path, HttpServletResponse response);
}
