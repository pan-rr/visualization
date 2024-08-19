package com.visualization.repository.dag;

import com.visualization.model.dag.db.DAGInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface DAGInstanceRepository extends JpaRepository<DAGInstance, Long> {

    @Modifying
    @Query(value = "update t_dag_instance set status = :status where instance_id = :instanceId", nativeQuery = true)
    void updateInstanceStatus(@Param("instanceId") Long instanceId, @Param("status") Integer status);

    @Modifying
    @Query(value = "update t_dag_instance set status = :status, finish_time = :finishTime where instance_id = :instanceId", nativeQuery = true)
    void finishInstance(@Param("instanceId") Long instanceId, @Param("finishTime") Date finishTime, @Param("status") Integer status);
}
