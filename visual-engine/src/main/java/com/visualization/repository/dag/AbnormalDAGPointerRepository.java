package com.visualization.repository.dag;

import com.visualization.model.dag.db.AbnormalDAGPointer;
import com.visualization.model.dag.db.PointerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbnormalDAGPointerRepository extends JpaRepository<AbnormalDAGPointer, PointerId> {

}
