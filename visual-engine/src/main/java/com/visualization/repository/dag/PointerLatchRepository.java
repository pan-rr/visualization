package com.visualization.repository.dag;

import com.visualization.model.dag.db.PointerLatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface PointerLatchRepository extends JpaRepository<PointerLatch, Long> {

    @Modifying
    @Query(value = "update t_pointer_latch set count = count - 1 where instance_id = :instanceId and task_id in :ids", nativeQuery = true)
    void decreaseCount(@Param("ids") List<Long> ids, @Param("instanceId") Long instanceId);

    @Query(value = "select task_id from t_pointer_latch where instance_id = :instanceId and count < 1", nativeQuery = true)
    Set<Long> getReadyTask(@Param("instanceId") Long instanceId);

    @Modifying
    @Query(value = "delete from t_pointer_latch where instance_id = :instanceId and task_id in :ids and count < 1", nativeQuery = true)
    void removeReadyLatch(@Param("ids") Collection<Long> ids, @Param("instanceId") Long instanceId);

    @Modifying
    @Query(value = "delete from t_pointer_latch where instance_id = :instanceId", nativeQuery = true)
    void deleteLatchByInstanceId(@Param("instanceId") Long instanceId);

}
