package com.visualisation.log.model;

import com.visualisation.model.dag.db.DAGPointer;
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

    private static final String SUCCESS_MESSAGE_EXPRESSION = "任务ID：{0}，执行成功";

    private static final String FAIL_MESSAGE_EXPRESSION = "任务ID：{0}执行失败，原因：{1}";

    public static VisualStageWrapper success(DAGPointer pointer) {
        return new VisualStageWrapper(pointer, null, Instant.now());
    }

    public static VisualStageWrapper fail(DAGPointer pointer, Throwable e) {
        return new VisualStageWrapper(pointer, e, Instant.now());
    }


    public String getStageExecuteMessage() {
        return Objects.isNull(exception) ? MessageFormat.format(SUCCESS_MESSAGE_EXPRESSION, pointer.getTaskId().toString())
                : MessageFormat.format(FAIL_MESSAGE_EXPRESSION, pointer.getTaskId(), exception.getMessage());

    }


}
