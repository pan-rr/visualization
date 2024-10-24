package com.visualization.repository.dag;

import com.visualization.model.dag.db.DAGTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DAGTemplateRepository extends JpaRepository<DAGTemplate, Long>, JpaSpecificationExecutor<DAGTemplate> {

    @Modifying
    @Query(value = "update t_dag_template set status = :status where template_id = :templateId", nativeQuery = true)
    void updateTemplateStatus(@Param("templateId") Long templateId, @Param("status") int status);


    @Modifying
    @Query(value = "update t_dag_template set priority = :priority where template_id = :templateId", nativeQuery = true)
    void changeTemplatePriority(@Param("templateId") Long templateId, @Param("priority") Double priority);

    @Modifying
    @Query(value = "update t_dag_template set retry_count = :retryCount where template_id = :templateId", nativeQuery = true)
    void changeTemplateRetryCount(@Param("templateId") Long templateId, @Param("retryCount") Integer retryCount);

}
