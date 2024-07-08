package com.visualisation.view.out;

import com.visualisation.handler.SQLHandler;
import com.visualisation.manager.ViewManager;
import com.visualisation.view.BaseView;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public class ConsoleOutPutView extends BaseView implements OutputView{


    public static OutputView produce(Pair<Map<String, Object>, ViewManager> p) {
        ConsoleOutPutView view = new ConsoleOutPutView();
        ViewManager viewManager = p.getSecond();
        view.id = viewManager.getId();
        view.properties = p.getFirst();
        view.federationDataSource = viewManager.getFederationDataSource();
        view.jdbcTemplate = new JdbcTemplate(view.federationDataSource);
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        String[] columnNames = sqlRowSet.getMetaData().getColumnNames();
        int n = columnNames.length;
        System.out.println("|------columnNames------|\n" + Arrays.toString(columnNames));
        StringBuilder sb;
        while (sqlRowSet.next()) {
            sb = new StringBuilder();
            for (int i = 1; i <= n; i++) sb.append(sqlRowSet.getString(i)).append('|');
            System.out.println(sb);
        }

    }
}
