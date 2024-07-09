package com.visualisation;

import com.visualisation.handler.SQLHandler;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class H2Test {

    @Resource
    private Commander commander;

    @Test
    public void test() throws IOException {
        long l = System.currentTimeMillis();
        commander.commandWithLocalFile("json/example10.json");
        System.err.println("jvm参数：-XX=+UseG1GC;-Xms=1g;-Xmx=8g");
        System.err.println("耗时：" + String.valueOf(System.currentTimeMillis() - l) + "ms");
    }


    public static void main(String[] args) throws JSQLParserException, SQLException, ParseException {
        String sql = "select t_user.id , t_user.name , b.* from t_user inner join t_lesson b on t_user.id = b.id";
        Map<String,Object> map = new HashMap<>();
        map.put("t_user","t_dasd");
        String selectSQL = SQLHandler.getSelectSQL(sql, map);
        System.err.println(selectSQL);
    }
}
