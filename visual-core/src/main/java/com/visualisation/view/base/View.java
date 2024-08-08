package com.visualisation.view.base;

public interface View {

    String destroySQL = "drop table if exists {0} ";

    String getRawTableName();

    String getRealTableName();

    void generate();

    void destroy();
}
