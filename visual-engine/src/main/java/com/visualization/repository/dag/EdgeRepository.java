package com.visualization.repository.dag;

import com.visualization.model.dag.db.Edge;
import com.visualization.model.dag.db.EdgeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EdgeRepository extends JpaRepository<Edge, EdgeId> {

    List<Edge> findByInstanceIdAndFromTaskId(@Param("instanceId") Long instanceId, @Param("fromTaskId") Long taskId);

}
