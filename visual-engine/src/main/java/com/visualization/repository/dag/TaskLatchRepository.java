package com.visualization.repository.dag;

import com.visualization.model.dag.db.TaskLatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TaskLatchRepository extends JpaRepository<TaskLatch, Long> {

    @Modifying
    @Query(value = "update t_task_latch set count = count - 1 where task_id in :ids", nativeQuery = true)
    void decreaseCount(@Param("ids") List<Long> ids);

    @Query(value = "select task_id from t_task_latch where task_id in :ids and count > 0", nativeQuery = true)
    Set<Long> getUnreadyTask(@Param("ids") List<Long> ids);

    @Modifying
    @Query(value = "delete from t_task_latch where task_id in :ids and count < 1", nativeQuery = true)
    void deleteLatchByIds(@Param("ids") List<Long> ids);

}
