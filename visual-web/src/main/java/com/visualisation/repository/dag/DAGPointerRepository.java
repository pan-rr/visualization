package com.visualisation.repository.dag;

import com.visualisation.model.dag.DAGPointer;
import com.visualisation.model.dag.PointerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DAGPointerRepository extends JpaRepository<DAGPointer, PointerId> {

    void deleteByInstanceIdAndTaskId(Long instanceId, Long taskId);
}
