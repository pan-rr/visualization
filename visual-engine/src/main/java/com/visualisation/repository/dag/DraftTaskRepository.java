package com.visualisation.repository.dag;

import com.visualisation.model.dag.logicflow.DraftTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DraftTaskRepository extends JpaRepository<DraftTask,String> {
}
