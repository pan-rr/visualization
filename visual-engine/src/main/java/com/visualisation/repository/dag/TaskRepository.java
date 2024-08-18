package com.visualisation.repository.dag;

import com.visualisation.model.dag.db.TaskProject;
import com.visualisation.model.dag.db.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(value = "select task_id , name from t_task where task_id in :ids", nativeQuery = true)
    List<TaskProject> getTasks(@Param("ids") List<Long> taskId);

}
