package com.visualisation;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.CreateTable;

//@SpringBootTest
public class ServiceTest {

    public static void main(String[] args) {
        CreateTable createTable = new CreateTable();
        createTable.setTable(new Table("test"));
        createTable.addColumns("aa","bb","cc");
        createTable.withColumnDefinitions()
        System.err.println(createTable.getTable());
        System.err.println(createTable);
    }
}
