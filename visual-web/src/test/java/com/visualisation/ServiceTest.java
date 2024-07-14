package com.visualisation;

import com.visualisation.model.dag.Task;
import com.visualisation.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ServiceTest {


    @Resource
    TaskService taskService;

    @Autowired
    JpaProperties jpaProperties;

    @Test
    public void testSave(){
        System.err.println("--->>>");
        System.err.println(jpaProperties.getProperties());
        Task testSingle = Task
                .builder()
//                .id(UUID.randomUUID().toString())
                .name("testSingle")
                .json("{\n" +
                        "  \"input\": [\n" +
                        "    {\n" +
                        "      \"viewType\": \"jdbc\",\n" +
                        "      \"tableName\": \"person\",\n" +
                        "      \"indexOn\": \"no,sex\",\n" +
                        "      \"script\": \"select * from person limit 10000\",\n" +
                        "      \"param\": {\n" +
                        "        \"appDataSourceName\": \"db2\"\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"viewType\": \"jdbc\",\n" +
                        "      \"tableName\": \"prov\",\n" +
                        "      \"indexOn\": \"no\",\n" +
                        "      \"script\": \"select * from prov limit 10000\",\n" +
                        "      \"param\": {\n" +
                        "        \"appDataSourceName\": \"db2\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"output\": {\n" +
                        "    \"viewType\": \"csv\",\n" +
                        "    \"filePath\": \"csv/test_jdbc_csv.csv\",\n" +
                        "    \"param\": {\n" +
                        "      \"charset\": \"utf-8\",\n" +
                        "      \"fieldDelimiter\": \"\",\n" +
                        "      \"writeColumnHeader\": \"true\"\n" +
                        "    },\n" +
                        "    \"script\": \"select a.*,b.ancetral_place from person a inner join prov b on a.no = b.no\"\n" +
                        "  }\n" +
                        "}")
                .build();
        taskService.saveTask(testSingle);
    }
}
