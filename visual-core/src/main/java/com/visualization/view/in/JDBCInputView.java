package com.visualization.view.in;

import com.visualization.handler.SQLHandler;
import com.visualization.manager.ViewManager;
import com.visualization.view.base.JDBCBaseView;
import net.sf.jsqlparser.JSQLParserException;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Map;

public class JDBCInputView extends JDBCBaseView implements InputView {

    public static InputView produce(Pair<Map<String, Object>, ViewManager> p) {
        JDBCInputView view = new JDBCInputView();
        ViewManager viewManager = p.getSecond();
        view.id = viewManager.getId();
        view.properties = p.getFirst();
        view.setParam((Map<String, Object>) view.properties.get("param"));
        view.initHandler(view.param);
        view.federationDataSource = viewManager.getFederationDataSource();
        view.jdbcTemplate = new JdbcTemplate(view.federationDataSource);
        view.script = String.valueOf(view.properties.get("script"));
        return view;
    }


    private void createTable(SqlRowSetMetaData metaData) throws JSQLParserException, SQLException {
        String createTableSQL = SQLHandler.getCreateTableSQLByMetaData(metaData, getRealTableName(), getDialectId());
        jdbcTemplate.execute(createTableSQL);
        index();
    }

    private void index() {
        if (properties.containsKey("indexOn")){
            String indexOn = String.valueOf(properties.get("indexOn"));
            StringBuilder sb = new StringBuilder("create index idx_")
                    .append(getRealTableName())
                    .append(" on ").append(getRealTableName())
                    .append('(').append(indexOn).append(')');
            jdbcTemplate.execute(sb.toString());
        }
    }

    public void loadData() {
        NamedParameterJdbcTemplate template = getTargetJDBCTemplate();
        SqlRowSet sqlRowSet = template.queryForRowSet(script, param);
        SqlRowSetMetaData metaData = sqlRowSet.getMetaData();
        try {
            createTable(metaData);
        } catch (JSQLParserException | SQLException e) {
            throw new RuntimeException(e);
        }
        String[] columnNames = metaData.getColumnNames();
        int[] types = new int[columnNames.length];
        Arrays.fill(types, Types.VARCHAR);
        String insertSQL = SQLHandler.getInsertSQL(getRealTableName(), columnNames);
        BatchSqlUpdate batchSqlUpdate = new BatchSqlUpdate(federationDataSource, insertSQL, types, 10000);
        Object[] row;
        while (sqlRowSet.next()) {
            row = new Object[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                row[i] = sqlRowSet.getString(columnNames[i]);
            }
            batchSqlUpdate.update(row);
        }
        batchSqlUpdate.flush();
    }

    @Override
    public void generate() {
        loadData();
    }


}
