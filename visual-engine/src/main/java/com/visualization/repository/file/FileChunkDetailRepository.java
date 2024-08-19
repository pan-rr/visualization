package com.visualization.repository.file;

import com.visualization.model.file.FileChunkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileChunkDetailRepository extends JpaRepository<FileChunkRecord, Long> {

    @Query(value = "select * from t_file_task where md5 = :md5 limit 1", nativeQuery = true)
    FileChunkRecord findByMD5(@Param("md5") String md5);
}
