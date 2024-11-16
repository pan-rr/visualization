package com.visualization.stage;

import com.visualization.exception.DAGException;
import com.visualization.model.dag.db.InstanceContext;
import com.visualization.repository.dag.DAGInstanceContextRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class VisualStageContextService {

    @Resource
    private DAGInstanceContextRepository dagInstanceContextRepository;

    public InstanceContext getById(Long instanceId) {
        InstanceContext context = dagInstanceContextRepository.getById(instanceId);
        context.toMap();
        return context;
    }

    public void createContext(InstanceContext context){
        dagInstanceContextRepository.save(context);
    }

    public void updateContext(InstanceContext context) {
        InstanceContext dbLatest,latest;
        int cnt = 5, affect = 0;
        while (cnt-- > 0) {
            dbLatest = dagInstanceContextRepository.getById(context.getInstanceId());
            Long oldVersion = dbLatest.getVersion();
            latest = InstanceContext.inject(dbLatest, context);
            affect = dagInstanceContextRepository.saveContext(latest.getContext(), latest.getVersion(), oldVersion, latest.getInstanceId());
            if (affect > 0) break;
        }
        if (affect <= 0) {
            throw new DAGException("尝试保存context失败达到阈值");
        }
    }
}
