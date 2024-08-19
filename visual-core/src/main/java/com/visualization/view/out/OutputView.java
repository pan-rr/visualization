package com.visualization.view.out;

import com.visualization.view.base.View;

import java.util.Map;

public interface OutputView extends View {

    void setTableNames(Map<String, Object> names);

    void execute();

    void output();
}
