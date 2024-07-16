package com.visualisation;

import com.visualisation.jpa.SnowIdWorker;
import com.visualisation.manager.DAGManager;
import com.visualisation.model.dag.*;
import com.visualisation.service.DAGService;
import com.visualisation.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
public class ServiceTest {


    @Resource
    TaskService taskService;

    @Resource
    DAGService dagService;

    @Resource
    DAGManager manager;

    @Test
    public void testSave() {
        DAGTemplate build = DAGTemplate.builder()
                .build();
        List<Node> list = new LinkedList<>();
        Node root1 = Node.builder()
                .name("root1")
                .build();
        Node root2 = Node.builder()
                .name("root2")
                .build();
        list.add(root1);
        list.add(root2);
        root1.setNextList(Arrays.asList(Node.builder()
                .name("t1")
                .build()));
        build.setList(list);
        dagService.saveTemplate(build);
    }

    @Test
    void testDag() {
        dagService.createInstanceByTemplateId(1262743053198688256L);
    }

    @Test
    void saveTask() {
        DAGTemplate build = DAGTemplate.builder()
                .name("single")
                .build();
        build.setList(Arrays.asList(Node.builder()
                .name("single-root")
                .build()));
        dagService.saveTemplate(build);
        List<Node> list = build.getList();
        int i = 0;
        for (Node node : list) {
            i++;
            Task task = Task.builder()
                    .taskId(node.getId())
                    .name("test-->" + i)
                    .json("{\n" +
                            "  \"input\": [\n" +
                            "    {\n" +
                            "      \"viewType\": \"csv\",\n" +
                            "      \"filePath\": \"csv/student2.csv\",\n" +
                            "      \"tableName\": \"student\",\n" +
                            "      \"param\": {\n" +
                            "        \"headers\": \"no|name|sex\",\n" +
                            "        \"fieldSeparator\": \"|\",\n" +
                            "        \"caseSensitiveColumnNames\": \"true\",\n" +
                            "        \"charset\": \"utf-8\",\n" +
                            "        \"writeColumnHeader\": \"false\"\n" +
                            "      }\n" +
                            "    },\n" +
                            "    {\n" +
                            "      \"viewType\": \"csv\",\n" +
                            "      \"filePath\": \"csv/hobby.csv\",\n" +
                            "      \"tableName\": \"hobby\"\n" +
                            "    }\n" +
                            "  ],\n" +
                            "  \"output\": {\n" +
                            "    \"viewType\": \"console\",\n" +
                            "    \"script\": \"select a.no,a.name,b.type from student a inner join hobby b on a.no = b.no\"\n" +
                            "  }\n" +
                            "}").build();
            taskService.saveTask(task);

        }

    }

    @Test
    void testCreateInstance() {
        dagService.createInstanceByTemplateId(1262745914414465024L);
    }

    @Test
    void saveTask2() {
        List<Long> taskIds = Arrays.asList(1262745914351550464L, 1262745914351681536L, 1262745914351550465L);
        long instanceId = new SnowIdWorker(0, 0).nextId();
        int i = 0;
        for (Long id : taskIds) {
            i++;
            Task task = Task.builder()
                    .taskId(id)
                    .name("test-->" + i)
                    .json("{\n" +
                            "  \"input\": [\n" +
                            "    {\n" +
                            "      \"viewType\": \"csv\",\n" +
                            "      \"filePath\": \"csv/student2.csv\",\n" +
                            "      \"tableName\": \"student\",\n" +
                            "      \"param\": {\n" +
                            "        \"headers\": \"no|name|sex\",\n" +
                            "        \"fieldSeparator\": \"|\",\n" +
                            "        \"caseSensitiveColumnNames\": \"true\",\n" +
                            "        \"charset\": \"utf-8\",\n" +
                            "        \"writeColumnHeader\": \"false\"\n" +
                            "      }\n" +
                            "    },\n" +
                            "    {\n" +
                            "      \"viewType\": \"csv\",\n" +
                            "      \"filePath\": \"csv/hobby.csv\",\n" +
                            "      \"tableName\": \"hobby\"\n" +
                            "    }\n" +
                            "  ],\n" +
                            "  \"output\": {\n" +
                            "    \"viewType\": \"console\",\n" +
                            "    \"script\": \"select a.no,a.name,b.type from student a inner join hobby b on a.no = b.no\"\n" +
                            "  }\n" +
                            "}").build();
            taskService.saveTask(task);

        }

    }

    @Test
    void testExecuteTask() {
        DAGPointer p = DAGPointer
                .builder()
                .instanceId(1262746701970538496L)
                .taskId(1262745914351550464L)
                .build();

        manager.executeTask(p);
    }
}
