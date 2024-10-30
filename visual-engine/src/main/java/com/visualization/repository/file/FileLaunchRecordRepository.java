package com.visualization.repository.file;

import com.visualization.model.file.FileLaunchRecord;
import com.visualization.model.file.FilePathMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileLaunchRecordRepository extends JpaRepository<FileLaunchRecord,String> {

}
