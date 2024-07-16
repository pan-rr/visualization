package com.visualisation.repository.dag;

import com.visualisation.model.dag.DAGInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DAGInstanceRepository extends JpaRepository<DAGInstance, Long> {

    @Modifying
    @Query(value = "update t_dag_instance set status = :status where instance_id = :instanceId", nativeQuery = true)
    void updateStatus(@Param("instanceId") Long instanceId, @Param("status") Integer status);
}
