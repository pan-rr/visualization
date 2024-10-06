package com.visualization.repository.dag;

import com.visualization.model.dag.db.DAGInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface DAGInstanceRepository extends JpaRepository<DAGInstance, Long>, JpaSpecificationExecutor<DAGInstance> {

    @Modifying
    @Query(value = "update t_dag_instance set status = :status where instance_id = :instanceId", nativeQuery = true)
    void updateInstanceStatus(@Param("instanceId") Long instanceId, @Param("status") Integer status);

    @Modifying
    @Query(value = "update t_dag_instance set unfinished_task_count = unfinished_task_count - 1 where instance_id = :instanceId", nativeQuery = true)
    void decreaseUnfinishedTaskCount(@Param("instanceId") Long instanceId);

    @Modifying
    @Query(value = "update t_dag_instance set status = :status, finish_time = :finishTime where instance_id = :instanceId and unfinished_task_count < 1", nativeQuery = true)
    void tryFinishInstance(@Param("instanceId") Long instanceId, @Param("finishTime") Date finishTime, @Param("status") Integer status);

}
