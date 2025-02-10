package com.visualization.stage;

import com.visualization.exception.DAGException;
import com.visualization.model.dag.db.InstanceContext;
import com.visualization.repository.dag.DAGInstanceContextRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class VisualStageContextService {

    @Resource
    private DAGInstanceContextRepository dagInstanceContextRepository;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public InstanceContext getById(Long instanceId) {
        InstanceContext context = dagInstanceContextRepository.getById(instanceId);
        context.toMap();
        return context;
    }

    public void createContext(InstanceContext context) {
        dagInstanceContextRepository.save(context);
    }

    private boolean queryLock(String key) {
        return redisTemplate.opsForValue().setIfAbsent(key, 1, 1, TimeUnit.MINUTES);
    }

    public void updateContext(InstanceContext context) {
        InstanceContext dbLatest, latest;
        int cnt = 5, affect = 0;
        String key = context.getKey();
        try {
            while (cnt-- > 0) {
                if (Boolean.TRUE.equals(queryLock(key))) {
                    dbLatest = dagInstanceContextRepository.getById(context.getInstanceId());
                    Long oldVersion = dbLatest.getVersion();
                    latest = InstanceContext.inject(dbLatest, context);
                    affect = dagInstanceContextRepository.saveContext(latest.getContext(), latest.getVersion(), oldVersion, latest.getInstanceId());
                    if (affect > 0) {
                        redisTemplate.delete(key);
                        break;
                    }
                } else {
                    TimeUnit.SECONDS.sleep(1);
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        if (affect < 1) throw new DAGException("尝试保存context失败达到阈值");
    }
}
