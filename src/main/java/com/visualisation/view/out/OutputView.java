package com.visualisation.view.out;

import com.visualisation.view.View;

import java.util.Map;

public interface OutputView extends View {

    void setTableNames(Map<String, Object> names);

    void execute();

    void output();
}
