```java

package com.test.visual;


import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.Arrays;

import java.util.LinkedList;
import java.util.List;

public class FlinkTest {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        EnvironmentSettings envSettings = EnvironmentSettings.newInstance()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env, envSettings);

        List<Row> list = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            list.add(Row.of(i, "name_" + i, 1));
        }
        Table temp = tableEnvironment.fromValues(list);


        tableEnvironment.createTemporaryView("t_data", temp);
        String[] strings = tableEnvironment.listTables();
        System.err.println(Arrays.toString(strings));


//        System.err.println("-------------");
        String ddl = "CREATE TABLE t_student (\n" +
                "  id INT,\n" +
                "  name STRING,\n" +
                "  sex INT,\n" +
                "  create_time TIMESTAMP\n" +
                ") WITH (\n" +
                "   'connector' = 'jdbc',\n" +
                "   'url' = 'jdbc:mysql://localhost:3306/test',\n" +
                "   'username' = 'user'," +
                "   'password' = 'user'," +
                "   'table-name' = 't_student',\n" +
                "   'driver' = 'com.mysql.cj.jdbc.Driver',\n" +
                "   'lookup.cache.max-rows' = '500', \n" +
                "   'lookup.cache.ttl' = '10s',\n" +
                "   'lookup.max-retries' = '3'" +
                ")";
        System.out.println(ddl);
        tableEnvironment.executeSql(ddl);

        TableResult tableResult = tableEnvironment.executeSql("insert into t_student(id,name,sex,create_time) select f0 id,f1 name,f2 sex,CURRENT_TIMESTAMP create_time from t_data");
//        tableResult.print();

    }
}


```

```xml

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>flink-test</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <flink.version>1.20.0</flink.version>
        <scala.binary.version>2.12</scala.binary.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>


    <dependencies>
        <!-- Table ecosystem -->
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-table-api-java-bridge</artifactId>
            <version>${flink.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-table-api-scala-bridge_${scala.binary.version}</artifactId>
            <version>${flink.version}</version>

        </dependency>


        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-table-api-java</artifactId>
            <version>${flink.version}</version>

        </dependency>

<!--        &lt;!&ndash; https://mvnrepository.com/artifact/org.apache.flink/flink-table-planner &ndash;&gt;-->
<!--        <dependency>-->
<!--            <groupId>org.apache.flink</groupId>-->
<!--            <artifactId>flink-table-planner_2.12</artifactId>-->
<!--            <version>${flink.version}</version>-->
<!--        </dependency>-->
<!--        &lt;!&ndash; https://mvnrepository.com/artifact/org.apache.flink/flink-table-planner-blink &ndash;&gt;-->
<!--        <dependency>-->
<!--            <groupId>org.apache.flink</groupId>-->
<!--            <artifactId>flink-table-planner-blink_2.12</artifactId>-->
<!--            <version>1.13.6</version>-->
<!--        </dependency>-->


        <!--        &lt;!&ndash; https://mvnrepository.com/artifact/org.apache.flink/flink-table-planner-loader &ndash;&gt;-->
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-table-planner-loader</artifactId>
            <version>${flink.version}</version>
        </dependency>


        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-clients</artifactId>
            <version>${flink.version}</version>

        </dependency>

        <!-- https://mvnrepository.com/artifact/org.apache.flink/flink-connector-jdbc -->
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-jdbc</artifactId>
            <version>3.1.2-1.18</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.29</version>
        </dependency>
    </dependencies>


        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-shade-plugin</artifactId>
                    <version>3.1.1</version>
                    <executions>
                        <execution>
                            <phase>package</phase>
                            <goals>
                                <goal>shade</goal>
                            </goals>
                            <configuration>
                                <artifactSet>
                                    <excludes>
                                        <exclude>com.google.code.findbugs:jsr305</exclude>
                                    </excludes>
                                </artifactSet>
                                <filters>
                                    <filter>
                                        <!-- Do not copy the signatures in the META-INF folder.
                                        Otherwise, this might cause SecurityExceptions when using the JAR. -->
                                        <artifact>*:*</artifact>
                                        <excludes>
                                            <exclude>META-INF/*.SF</exclude>
                                            <exclude>META-INF/*.DSA</exclude>
                                            <exclude>META-INF/*.RSA</exclude>
                                        </excludes>
                                    </filter>
                                </filters>
                                <transformers>
                                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                        <!-- Replace this with the main class of your job -->
                                        <mainClass>com.test.visual.FlinkTest</mainClass>
                                    </transformer>
                                    <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                                </transformers>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
</project>

```