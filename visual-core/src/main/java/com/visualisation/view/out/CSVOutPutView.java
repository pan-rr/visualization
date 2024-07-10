package com.visualisation.view.out;

import com.visualisation.handler.FileHandler;
import com.visualisation.handler.SQLHandler;
import com.visualisation.manager.ViewManager;
import com.visualisation.view.BaseView;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.ParseException;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;

public class CSVOutPutView extends BaseView implements OutputView {

    private static final String OUTPUT_SQL = "CALL CSVWRITE({0} , {1} {2});";


    public static OutputView produce(Pair<Map<String, Object>, ViewManager> p) {
        CSVOutPutView view = new CSVOutPutView();
        ViewManager viewManager = p.getSecond();
        view.id = viewManager.getId();
        view.properties = p.getFirst();
        view.federationDataSource = viewManager.getFederationDataSource();
        view.jdbcTemplate = new JdbcTemplate(view.federationDataSource);
        view.param = (Map<String, String>) view.properties.get("param");
        return view;
    }

    private String prepareSQL(String selectSQL, String filePath) throws IOException, SQLException, JSQLParserException {
        selectSQL = new StringBuilder().append('\'').append(selectSQL).append('\'').toString();
        StringBuilder options = new StringBuilder();
        if (!CollectionUtils.isEmpty(param)) {
            param.forEach((k, v) -> options.append(' ').append(k).append('=').append(v));
            options.replace(0,1, ",'");
            options.append('\'');
        }
        StringBuilder path = new StringBuilder();
        path.append('\'').append(filePath).append('\'');
        String sql = MessageFormat
                .format(OUTPUT_SQL, path, selectSQL, options.toString());
        return sql;
    }


    @Override
    public void generate() {
        execute();
    }

    @Override
    public void execute() {
        output();
    }

    @Override
    public void output() {
        try {
            String select = SQLHandler.getSelectSQL(getScript(), tableNames);
            StringBuilder pathBuilder = new StringBuilder(this.getClass().getClassLoader().getResource("").getPath())
                    .append('/')
                    .append(properties.get("filePath"));
            String path = pathBuilder.toString();
            String absolutePath = FileHandler.getFileByPath(path).getAbsolutePath();
            String outSQL = prepareSQL(select, absolutePath);
            jdbcTemplate.execute(outSQL);
        } catch (SQLException | JSQLParserException | ParseException | IOException e) {
            throw new RuntimeException(e);
        }


    }
}
