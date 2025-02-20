package com.visualization.stage;

import com.google.gson.Gson;
import com.visualization.constant.TaskTypeConstant;
import com.visualization.constant.ViewConstant;
import com.visualization.enums.VisualContextKey;
import com.visualization.exception.DAGException;
import com.visualization.manager.DAGDataSourceManager;
import com.visualization.model.dag.db.DAGPointer;
import com.visualization.model.dag.db.Task;
import com.visualization.model.file.FilePathMapping;
import com.visualization.repository.file.FilePathMappingRepository;
import com.visualization.runtime.VContextManager;
import com.visualization.runtime.VStageContext;
import com.visualization.service.TaskService;
import com.visualization.utils.FilePathUtil;
import com.visualization.utils.ShortLinkUtil;
import com.visualization.runtime.VisualStage;
import lombok.Builder;

import java.util.*;

@Builder
public class VisualStageBuilder {

    private DAGPointer dagPointer;

    private TaskService taskService;

    private FilePathMappingRepository filePathMappingRepository;

    private DAGDataSourceManager dagDataSourceManager;

    private VisualStage visualStage;

    private Map<String, Object> context;


    public VisualStage buildStage() {
        createStage();
        rewriteFilePath();
        rewriteDatasource();
        return visualStage;
    }

    private void createStage() {
        Long taskId = dagPointer.getTaskId();
        Task task = taskService.getTaskById(taskId);
        if (Objects.isNull(task)) {
            throw new DAGException("任务为空，id:" + taskId);
        }
        Gson gson = new Gson();
        String json = task.getJson();
        String taskJson = VisualStageExpressionRewriter.rewriteExpression(json, this.context);
        this.visualStage = gson.fromJson(taskJson, VisualStage.class);
        this.visualStage.setVisualContext(this.context);
        VETContext vetContext = VETContext.builder()
                .stageContext(new VStageContext(this.context))
                .taskId(dagPointer.getTaskId()).build();
        VContextManager.register(vetContext);
        this.visualStage.setRuntimeContext(vetContext);
    }

    private void rewriteDatasource() {
        List<Map> configs = new ArrayList<>();
        switch (visualStage.getTaskType()) {
            case TaskTypeConstant.VISUAL:
                List<Map<String, Object>> input = visualStage.getInput();
                input.forEach(i -> configs.add((Map) i.get(ViewConstant.PARAM)));
                configs.add((Map) visualStage.getOutput().get(ViewConstant.PARAM));
                break;
            case TaskTypeConstant.SQL:
                configs.add(visualStage.getSql());
                break;
            default:
        }
        dagDataSourceManager.rewriteDataSource(configs);
    }


    private void rewriteFilePath() {
        if (TaskTypeConstant.VISUAL.equals(visualStage.getTaskType())) {
            List<Map<String, Object>> input = visualStage.getInput();
            for (Map<String, Object> conf : input) {
                conf.compute(ViewConstant.FILE_PATH, (k, v) -> getRealPath(v));
            }
            Map<String, Object> output = visualStage.getOutput();
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
        params.put(VisualContextKey.space.name(), space);
        params.put(VisualContextKey.instanceId.name(), dagPointer.getInstanceId());
        params.put(VisualContextKey.templateId.name(), dagPointer.getTemplateId());
        params.put("filePath", s);
        return FilePathUtil.getStoragePath(params);
    }
}
