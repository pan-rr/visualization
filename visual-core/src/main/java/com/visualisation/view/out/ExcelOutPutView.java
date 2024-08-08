package com.visualisation.view.out;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.visualisation.constant.LocalFileConstant;
import com.visualisation.constant.ViewConstant;
import com.visualisation.handler.SQLHandler;
import com.visualisation.manager.FileManager;
import com.visualisation.manager.ViewManager;
import com.visualisation.view.base.BaseView;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.ParseException;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExcelOutPutView extends BaseView implements OutputView {


    public static OutputView produce(Pair<Map<String, Object>, ViewManager> p) {
        ExcelOutPutView view = new ExcelOutPutView();
        ViewManager viewManager = p.getSecond();
        view.id = viewManager.getId();
        view.properties = p.getFirst();
        view.setParam((Map<String, Object>) view.properties.get(ViewConstant.PARAM));
        view.federationDataSource = viewManager.getFederationDataSource();
        view.jdbcTemplate = new JdbcTemplate(view.federationDataSource);
        view.script = String.valueOf(view.properties.get(ViewConstant.SCRIPT));
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

    private File getTempFile(String path) {
        File file = new File(path);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        return file;
    }


    @Override
    public void output() {
        String sql;
        try {
            sql = SQLHandler.getSelectSQL(getScript(), tableNames);
        } catch (SQLException | JSQLParserException | ParseException e) {
            throw new RuntimeException(e);
        }
        String targetPath = String.valueOf(properties.get("filePath"));
        String[] split = targetPath.split("/");
        String tempFilePath = LocalFileConstant.getRandomTempFilePath(split[split.length - 1]);
        File tempFile = getTempFile(tempFilePath);
        List<List<String>> headers = (List<List<String>>) param.get("headers");
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        SqlRowSetMetaData metaData = sqlRowSet.getMetaData();
        String[] columnNames = metaData.getColumnNames();
        int col = columnNames.length;
        int total = sqlRowSet.getRow();
        List<List<String>> data = new ArrayList<>(total);
        List<String> row;
        while (sqlRowSet.next()) {
            row = new ArrayList<>(col);
            for (String columnName : columnNames) {
                row.add(sqlRowSet.getString(columnName));
            }
            data.add(row);
        }
        ExcelWriter excelWriter = EasyExcel.write(tempFile).build();
        WriteSheet sheet = new WriteSheet();
        sheet.setSheetNo(1);
        WriteTable table = new WriteTable();
        table.setTableNo(1);
        if (CollectionUtils.isEmpty(headers)) {
            headers = new ArrayList<>(col);
            for (String columnName : columnNames) {
                headers.add(Collections.singletonList(columnName));
            }
        }
        table.setHead(headers);
        excelWriter.write(data, sheet, table);
        excelWriter.finish();
        excelWriter.close();
        Object o = properties.get("fileHandlerId");
        String fileHandlerId = o == null ? "" : String.valueOf(o);
        FileManager.outputFile(tempFile, targetPath, fileHandlerId, (Map<?, ?>) properties.get("fileParam"));
    }
}
