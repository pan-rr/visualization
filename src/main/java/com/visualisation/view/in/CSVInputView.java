package com.visualisation.view.in;

import com.visualisation.handler.FileHandler;
import com.visualisation.handler.SQLHandler;
import com.visualisation.manager.ViewManager;
import com.visualisation.view.BaseView;
import net.sf.jsqlparser.JSQLParserException;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;

public class CSVInputView extends BaseView implements InputView {

    private static final String CREATE_SQL = "create table tableName as select * from csvread ({0} {1})";

    private File csv;

    /**
     * csv options:
     * <p>
     * "caseSensitiveColumnNames"-->true or false; disabled by default
     * <p>
     * "charset"-->for example 'UTF-8'
     * <p>
     * "escape"-->the character that escapes the field delimiter
     * <p>
     * "fieldDelimiter"-->a double quote by default
     * <p>
     * "fieldSeparator"-->a comma by default
     * <p>
     * "lineComment"-->disabled by default
     * <p>
     * "lineSeparator"-->the line separator used for writing; ignored for reading
     * <p>
     * "quotedNulls"-->quotes the nullString. true of false; disabled by default
     * <p>
     * "preserveWhitespace"-->true or false; disabled by default
     * <p>
     * "writeColumnHeader"-->true or false; enabled by default
     */


    public static InputView produce(Pair<Map<String, Object>, ViewManager> p) {
        CSVInputView view = new CSVInputView();
        ViewManager viewManager = p.getSecond();
        view.id = viewManager.getId();
        view.properties = p.getFirst();
        view.federationDataSource = viewManager.getFederationDataSource();
        view.jdbcTemplate = new JdbcTemplate(view.federationDataSource);
        view.param = (Map<String, String>) view.properties.get("param");
        return view;
    }


    @Override
    public int getId() {
        return id;
    }

    private String prepareSQL() throws IOException, SQLException, JSQLParserException {
        StringBuilder sb = new StringBuilder();
        if (!CollectionUtils.isEmpty(param)) {
            String headers;
            if ((headers = param.get("headers")) != null) {
                sb.append(",'").append(headers).append('\'');
                param.remove("headers");
            }
            StringBuilder options = new StringBuilder();
            boolean flag = !param.isEmpty();
            if (flag){
                sb.append(", STRINGDECODE");
            }
            param.forEach((k, v) -> options.append(' ').append(k).append('=').append(v));
            if (flag) {
                options.setCharAt(0, '(');
                options.insert(1, '\'');
                options.append('\'');
                options.append(')');
            }
            sb.append(options);
        }
        StringBuilder path = new StringBuilder();
        path.append('\'').append(properties.get("filePath")).append('\'');
        String sql = MessageFormat.format(CREATE_SQL, path, sb.toString());
        return SQLHandler.changeCreateTableSQL(sql, getRealTableName());
    }

    public void loadData() {
        try {
            String sql = prepareSQL();
            jdbcTemplate.execute(sql);
        } catch (IOException | SQLException | JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void generate() {
        try {
            prepareCsv();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadData();
    }

    /**
     * 这里根据传参下载文件
     */
    private void prepareCsv() throws IOException {
        String path = String.valueOf(properties.get("filePath"));
        csv = FileHandler.getFileByPath(path);
        properties.put("filePath", csv.getAbsolutePath());
    }


}