package com.visualisation.repository.dag;

import com.visualisation.model.dag.DAGPointer;
import com.visualisation.model.dag.PointerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DAGPointerRepository extends JpaRepository<DAGPointer, PointerId> {

    @Modifying
    @Query(value = "delete from t_dag_pointer where instance_id = :instanceId and task_id = :taskId", nativeQuery = true)
    void deleteByInstanceIdAndTaskId(@Param("instanceId") Long instanceId, @Param("taskId") Long taskId);


    @Query(value = "select count(1) from t_dag_pointer where instance_id = :instanceId",nativeQuery = true)
    Integer getInstancePointCount(@Param("instanceId") Long instanceId);


    @Modifying
    @Transactional
    @Query(value = "update t_dag_pointer set count = count + 1 where instance_id = :instanceId and task_id = :taskId", nativeQuery = true)
    void updateCount(@Param("instanceId") Long instanceId, @Param("taskId") Long taskId);


    @Query(value = "select * from t_dag_pointer where status = 0 and count < retry_max_count limit :size",nativeQuery = true)
    List<DAGPointer> getPointers(@Param("size") int limit);
}
