package com.visualisation.repository.dag;

import com.visualisation.model.dag.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,String> {
}
