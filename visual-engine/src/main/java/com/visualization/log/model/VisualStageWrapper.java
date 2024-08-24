package com.visualization.log.model;

import com.visualization.enums.StageEnum;
import com.visualization.model.dag.db.DAGPointer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VisualStageWrapper {


    private DAGPointer pointer;

    private Throwable exception;

    private Instant time;

    private StageEnum stageEnum;

    private static final String FAIL_MESSAGE_EXPRESSION = "执行失败，原因：{0}";

    public static VisualStageWrapper success(DAGPointer pointer) {
        return new VisualStageWrapper(pointer, null, Instant.now(), StageEnum.STAGE_FINISHED);
    }

    public static VisualStageWrapper start(DAGPointer pointer) {
        return new VisualStageWrapper(pointer, null, Instant.now(), StageEnum.STAGE_START);
    }

    public static VisualStageWrapper fail(DAGPointer pointer, Throwable e) {
        return new VisualStageWrapper(pointer, e, Instant.now(), StageEnum.STAGE_FAIL);
    }


    public String getStageExecuteMessage() {
        if (Objects.requireNonNull(stageEnum) == StageEnum.STAGE_FAIL) {
            return MessageFormat.format(FAIL_MESSAGE_EXPRESSION, exception.getMessage());
        }
        return stageEnum.getMessage();

    }


}
