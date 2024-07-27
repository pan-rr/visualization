package com.visualisation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
public class H2Test {

    @Resource
    private Commander commander;

    @Test
    public void test() throws IOException {
        long l = System.currentTimeMillis();

        commander.commandWithLocalFile("json/example.json");
        System.err.println("jvm参数：-XX=+UseG1GC;-Xms=2g;-Xmx=8g");
        System.err.println("耗时：" + String.valueOf(System.currentTimeMillis() - l) + "ms");
    }

}
