package com.visualization.repository.dag;

import com.visualization.model.dag.db.DAGInstance;
import com.visualization.model.dag.db.InstanceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DAGInstanceContextRepository extends JpaRepository<InstanceContext, Long>, JpaSpecificationExecutor<DAGInstance> {

    @Modifying
    @Query(value = "update t_dag_instance_ctx set context = :ctx , version = :newVersion where instance_id = :id and version = :oldVersion", nativeQuery = true)
    int saveContext(@Param("ctx") String context, @Param("newVersion") Long newVersion, @Param("oldVersion") Long oldVersion, @Param("id") Long id);
}
