package com.visualisation.view.out;

import com.visualisation.constant.LocalFileConstant;
import com.visualisation.constant.ViewConstant;
import com.visualisation.handler.SQLHandler;
import com.visualisation.manager.FileManager;
import com.visualisation.manager.ViewManager;
import com.visualisation.view.BaseView;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.ParseException;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;

public class CSVOutPutView extends BaseView implements OutputView {

    private static final String OUTPUT_SQL = "CALL CSVWRITE({0} , {1} {2});";

    private File csv;


    public static OutputView produce(Pair<Map<String, Object>, ViewManager> p) {
        CSVOutPutView view = new CSVOutPutView();
        ViewManager viewManager = p.getSecond();
        view.id = viewManager.getId();
        view.properties = p.getFirst();
        view.federationDataSource = viewManager.getFederationDataSource();
        view.jdbcTemplate = new JdbcTemplate(view.federationDataSource);
        view.param = (Map<String, Object>) view.properties.get(ViewConstant.PARAM);
        return view;
    }

    private String prepareSQL(String selectSQL, String filePath) throws IOException, SQLException, JSQLParserException {
        selectSQL = new StringBuilder().append('\'').append(selectSQL).append('\'').toString();
        StringBuilder options = new StringBuilder();
        if (!CollectionUtils.isEmpty(param)) {
            param.forEach((k, v) -> options.append(' ').append(k).append('=').append(v));
            options.replace(0, 1, ",'");
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
            String targetPath = String.valueOf(properties.get("filePath"));
            String[] split = targetPath.split("/");
            String tempFilePath = LocalFileConstant.getRandomTempFilePath(split[split.length - 1]);
            String outSQL = prepareSQL(select, tempFilePath);
            jdbcTemplate.execute(outSQL);
            csv = new File(tempFilePath);
            Object o = properties.get("fileHandlerId");
            String fileHandlerId = o == null ? "" : String.valueOf(o);
            FileManager.outputFile(csv, targetPath, fileHandlerId, (Map<?, ?>) properties.get("fileParam"));
        } catch (SQLException | JSQLParserException | ParseException | IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void destroy() {
        super.destroy();
        Object o = properties.get("destroyFileAfterFinish");
        if (o != null && Boolean.TRUE.equals(Boolean.valueOf(o.toString()))) {
            try {
                Files.deleteIfExists(Paths.get(csv.getPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
