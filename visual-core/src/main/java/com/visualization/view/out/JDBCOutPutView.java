package com.visualization.view.out;

import com.visualization.handler.SQLHandler;
import com.visualization.manager.ViewManager;
import com.visualization.view.base.JDBCBaseView;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.ParseException;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Map;

public class JDBCOutPutView extends JDBCBaseView implements OutputView {


    public static OutputView produce(Pair<Map<String, Object>, ViewManager> p) {

        JDBCOutPutView view = new JDBCOutPutView();
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
        String sql;
        try {
            sql = SQLHandler.getSelectSQL(getScript(), tableNames);
        } catch (SQLException | JSQLParserException | ParseException e) {
            throw new RuntimeException(e);
        }
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        SqlRowSetMetaData metaData = sqlRowSet.getMetaData();
        NamedParameterJdbcTemplate template = getTargetJDBCTemplate();
        DataSource targetDataSource = template.getJdbcTemplate().getDataSource();
        try {
            SQLHandler.createTable(metaData, getRawTableName(), getDialectId(), template.getJdbcTemplate());
        } catch (JSQLParserException | SQLException e) {
            throw new RuntimeException(e);
        }
        String[] columnNames = metaData.getColumnNames();
        int[] types = new int[columnNames.length];
        Arrays.fill(types, Types.VARCHAR);
        String insertSQL = SQLHandler.getInsertSQL(getRawTableName(), columnNames);
        BatchSqlUpdate batchSqlUpdate = new BatchSqlUpdate(targetDataSource, insertSQL, types, sqlRowSet.getRow() / 2);
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
}
