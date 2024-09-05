package com.visualization.builder;

import com.google.gson.Gson;
import com.visualization.constant.ViewConstant;
import com.visualization.exception.DAGException;
import com.visualization.model.dag.db.DAGPointer;
import com.visualization.model.dag.db.Task;
import com.visualization.model.file.FilePathMapping;
import com.visualization.repository.file.FilePathMappingRepository;
import com.visualization.service.TaskService;
import com.visualization.utils.FilePathUtil;
import com.visualization.utils.ShortLinkUtil;
import com.visualization.view.base.VisualStage;
import lombok.Builder;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Builder
public class VisualStageBuilder {

    private DAGPointer dagPointer;

    private TaskService taskService;

    private FilePathMappingRepository filePathMappingRepository;

    private VisualStage visualStage;

    public VisualStage buildStage() {
        createStage();
        rewriteFilePath();
        return visualStage;
    }

    private void createStage() {
        Long taskId = dagPointer.getTaskId();
        Task task = taskService.getTaskById(taskId);
        if (Objects.isNull(task)) {
            throw new DAGException("任务为空，id:" + taskId);
        }
        Gson gson = new Gson();
        this.visualStage = gson.fromJson(task.getJson(), VisualStage.class);
    }


    private void rewriteFilePath() {
        List<Map<String, Object>> input = visualStage.getInput();
        if (!CollectionUtils.isEmpty(input)) {
            for (Map<String, Object> conf : input) {
                conf.compute(ViewConstant.FILE_PATH, (k, v) -> getRealPath(v));
            }
        }
        Map<String, Object> output = visualStage.getOutput();
        if (!CollectionUtils.isEmpty(output)) {
            output.compute(ViewConstant.FILE_PATH, (k, v) -> getRealPath(v));
        }
    }

    private String getRealPath(Object suffix) {
        if (suffix == null) return null;
        String logicPath = computeOSSPath(suffix);
        Optional<FilePathMapping> optional = filePathMappingRepository.findById(ShortLinkUtil.zipToInt(logicPath));
        if (optional.isPresent()) {
            return optional.get().getSourcePath();
        }
        return logicPath;
    }

    private String computeOSSPath(Object suffix) {
        String space = dagPointer.getSpace();
        String s = String.valueOf(suffix);
        if (s.contains("://")) return s;
        Map<String, Object> params = new HashMap<>();
        if (s.startsWith(FilePathUtil.SPACE_SHARE)) {
            params.put(FilePathUtil.SPACE_SHARE, true);
        }
        params.put("space", space);
        params.put("instanceId", dagPointer.getInstanceId());
        params.put("templateId", dagPointer.getTemplateId());
        params.put("filePath", s);
        return FilePathUtil.getStoragePath(params);
    }
}
