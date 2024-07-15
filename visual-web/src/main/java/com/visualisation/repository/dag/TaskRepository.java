package com.visualisation.repository.dag;

import com.visualisation.model.dag.Task;
import com.visualisation.model.dag.TaskId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, TaskId> {

    @Modifying
    @Query(value = "update t_task set status = :status where instance_id = :instanceId and task_id = :taskId", nativeQuery = true)
    void updateStatusByInstanceIdAndTaskId(@Param("instanceId") Long instanceId, @Param("taskId") Long taskId, @Param("status") Integer status);

}
