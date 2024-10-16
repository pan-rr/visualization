package com.visualization.handler;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.validation.Validation;
import net.sf.jsqlparser.util.validation.ValidationError;
import net.sf.jsqlparser.util.validation.feature.FeaturesAllowed;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLHandler {

    private static final Map<String, String> STRING_MAP = new HashMap<>();

    static {
        //   h2 字符串类型
        STRING_MAP.put(DatabaseDriver.H2.getId(), "CHARACTER");
        // MySQL 字符串类型
        STRING_MAP.put(DatabaseDriver.MYSQL.getId(), "varchar");
        STRING_MAP.put(DatabaseDriver.POSTGRESQL.getId(), "varchar");
    }

    private static StringBuilder changeType(String columnName, int len, String dialect) {
        StringBuilder sb = new StringBuilder();
        sb.append(columnName).append(' ');
        sb.append(STRING_MAP.get(dialect)).append('(').append(len).append(')');
        return sb;
    }


    public static String changeCreateTableSQL(String sql, String tableName) throws JSQLParserException {
        CreateTable parse = (CreateTable) CCJSqlParserUtil.parse(sql);
        parse.setTable(new Table(tableName));
        return parse.toString();
    }

    public static String getCreateTableSQLByMetaData(SqlRowSetMetaData metaData, String tableName, String dialectId) throws JSQLParserException, SQLException {
        String[] columnNames = metaData.getColumnNames();
        StringBuilder sb = new StringBuilder("create table if not exists ");
        sb.append(tableName).append(" (");
        int end = columnNames.length - 1;
        int len;
        for (int i = 0, j = 1; i <= end; i++, j++) {
            len = Math.min(500, metaData.getColumnDisplaySize(j));
            sb.append(changeType(columnNames[i], len, dialectId));
            if (i != end) sb.append(',');
        }
        sb.append(')');
        return sb.toString();
    }

    private static void validateSQL(String sql, List<FeaturesAllowed> list) throws SQLException {
        Validation validation = new Validation(list, sql);
        List<ValidationError> errors = validation.validate();
        if (!CollectionUtils.isEmpty(errors)) throw new SQLException("SQL语法有误");
    }

    public static String getSelectSQL(String rawSQL, Map<String, Object> map) throws SQLException, JSQLParserException, ParseException {
        Select select = (Select) CCJSqlParserUtil.parse(rawSQL);
        PlainSelect selectBody = (PlainSelect) select.getSelectBody();
        SimpleNode node = selectBody.getASTNode();
        node.jjtAccept(new TableNameHandler(map), null);
        return selectBody.toString();

    }

    public static String getInsertSQL(String tableName, String[] columnNames) {
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < columnNames.length; i++) {
            if (i > 0) {
                sb1.append(',');
                sb2.append(',');
            }
            sb1.append(columnNames[i]);
            sb2.append('?');
        }
        String pattern = "insert into {0}({1}) values ({2})";
        return MessageFormat.format(pattern, tableName, sb1.toString(), sb2.toString());
    }


    public static void createTable(SqlRowSetMetaData metaData, String tableName, String dialectId, JdbcTemplate template) throws JSQLParserException, SQLException {
        String createTableSQL = SQLHandler.getCreateTableSQLByMetaData(metaData, tableName, dialectId);
        template.execute(createTableSQL);
    }


    private static class TableNameHandler extends CCJSqlParserDefaultVisitor {
        private final Map<String, Object> names;

        TableNameHandler(Map<String, Object> names) {
            this.names = names;
        }

        @Override
        public Object visit(SimpleNode node, Object data) {
            Object value = node.jjtGetValue();
            if (node.getId() == CCJSqlParserTreeConstants.JJTCOLUMN) {
                Column column = (Column) value;
                Table table = column.getTable();
                if (table != null && names.containsKey(table.getName())) {
                    table.setName(String.valueOf(names.get(table.getName())));
                }
            } else if (node.getId() == CCJSqlParserTreeConstants.JJTTABLENAME) {
                Table table = (Table) value;
                if (table != null && names.containsKey(table.getName())) {
                    table.setName(String.valueOf(names.get(table.getName())));
                }
            }
            return super.visit(node, data);
        }
    }
}
