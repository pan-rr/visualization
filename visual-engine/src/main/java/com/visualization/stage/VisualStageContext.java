package com.visualization.stage;

import com.visualization.model.dag.db.DAGPointer;
import com.visualization.model.dag.db.DAGTemplate;
import lombok.Data;

@Data
public class VisualStageContext {

    private DAGPointer pointer;

    private DAGTemplate template;

    private String stageName;

    private Throwable throwable;

    private boolean occupy = false;

    public VisualStageContext computeStageName() {
        this.stageName = pointer.computeStageName();
        return this;
    }

    public VisualStageContext setPointer(DAGPointer pointer) {
        this.pointer = pointer;
        return this;
    }

    public VisualStageContext setTemplate(DAGTemplate template) {
        this.template = template;
        return this;
    }


}
