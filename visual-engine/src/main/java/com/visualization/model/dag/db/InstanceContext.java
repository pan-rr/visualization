package com.visualization.model.dag.db;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_dag_instance_ctx")
public class InstanceContext {
    @Id
    private Long instanceId;
    @Column(columnDefinition = "text")
    private String context;
    private Long version;

    @Transient
    private Map<String, Object> ctx;

    public void toMap() {
        Gson gson = new Gson();
        this.ctx = gson.fromJson(context, Map.class);
    }


    public static InstanceContext inject(InstanceContext db , InstanceContext current){
        Gson gson = new Gson();
        Map<String,Object> old = gson.fromJson(db.getContext(), Map.class);
        old.putAll(current.getCtx());
        return InstanceContext.builder().instanceId(db.getInstanceId()).context(gson.toJson(old)).version(System.currentTimeMillis()).build();
    }

    public static InstanceContext initContext(DAGInstance instance, DAGTemplate template) {
        InstanceContext init = InstanceContext.builder()
                .instanceId(instance.getInstanceId())
                .version(System.currentTimeMillis())
                .build();
        Gson gson = new Gson();
        String templateContext = template.getContext();
        init.ctx = StringUtils.isNotBlank(templateContext) ? gson.fromJson(templateContext, Map.class) : new HashMap<>();
        init.ctx.put("space", instance.getSpace());
        init.ctx.put("templateId", instance.getTemplateId().toString());
        init.context = new Gson().toJson(init.ctx);
        return init;
    }
}
