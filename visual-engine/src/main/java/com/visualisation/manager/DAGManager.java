package com.visualisation.manager;

import com.visualisation.Commander;
import com.visualisation.DAGException;
import com.visualisation.model.dag.*;
import com.visualisation.service.DAGService;
import com.visualisation.service.TaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class DAGManager {

    @Value("${visual.dag.edge.retryMaxCount}")
    private Integer retryMaxCount;

    @Resource
    private DAGService dagService;

    @Resource
    private TaskService taskService;

    @Resource
    private Commander commander;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Transactional(transactionManager = "transactionManagerDAG")
    public void executeTask(DAGPointer pointer) {
        TaskKey taskKey = pointer.getTaskKey();
        String key = "visual_pointer_" + pointer.getTaskId();
        // redisson版本有点问题，先用这个
        boolean flag = redisTemplate.opsForValue().setIfAbsent(key, key, 1, TimeUnit.HOURS);
        // 拿不到锁就放弃任务
        if (!flag) return;
        Task task = taskService.getTaskById(taskKey.getTaskId());
        if (Objects.isNull(task)) {
            throw new DAGException("任务为空，id:" + taskKey);
        }
        commander.commandWithJsonString(task.getJson());
        dagService.deletePointer(taskKey);
        List<Edge> nextEdges = dagService.findNextEdges(taskKey);
        if (!CollectionUtils.isEmpty(nextEdges)) {
            List<DAGPointer> pointers = nextEdges
                    .stream()
                    .filter(edge -> !Objects.equals(edge.getToTaskId(), edge.getFromTaskId()))
                    .map(edge -> DAGPointer.builder()
                            .instanceId(edge.getInstanceId())
                            .taskId(edge.getToTaskId())
                            .count(0)
                            .retryMaxCount(retryMaxCount)
                            .build()).collect(Collectors.toList());
            dagService.saveReadyPointers(pointers);
        } else {
            dagService.tryFinishInstance(taskKey.getInstanceId());
        }
        redisTemplate.delete(key);
    }

    @Transactional(transactionManager = "transactionManagerDAG")
    public DAGInstance createInstanceByTemplateId(Long templateId) {
        return dagService.createInstanceByTemplateId(templateId);
    }

    public void saveTemplate(DAGTemplate dagTemplate) {
        dagService.saveTemplate(dagTemplate);
    }

    public void saveTask(Task task) {
        taskService.saveTask(task);
    }

    public void updateCount(DAGPointer pointer) {
        dagService.updateCount(pointer);
    }

    public List<DAGPointer> getPointers(int limit) {
        return dagService.getPointers(limit);
    }
}
