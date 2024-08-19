package com.visualization.view.in;

import com.visualization.view.base.View;

import java.util.Map;

public interface InputView extends View {

    void loadData();

    default void registerTableName(Map<String, Object> names) {
        names.put(getRawTableName(), getRealTableName());
    }

}
