package com.visualization.repository.dag;

import com.visualization.model.dag.db.DAGDataSource;
import com.visualization.model.dag.db.DAGDataSourceProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DAGDataSourceRepository extends JpaRepository<DAGDataSource, Long> {

    @Query(value = "select count(1) from t_dag_datasource where hash = :hash and space = :space", nativeQuery = true)
    Integer getSpaceHashCount(@Param("hash") Integer hash, @Param("space") String space);


    @Query(value = "select data_source_id , name from t_dag_datasource where space = :space", nativeQuery = true)
    List<DAGDataSourceProject> getProject(@Param("space") String space);
}
