package com.visualisation.repository.dag;

import com.visualisation.model.dag.Edge;
import com.visualisation.model.dag.EdgeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EdgeRepository extends JpaRepository<Edge, EdgeId> {

    List<Edge> findByInstanceIdAndFromTaskId(@Param("instanceId") Long instanceId, @Param("fromTaskId") Long taskId);

}
