package com.visualization.factory;

import com.visualization.constant.ViewConstant;
import com.visualization.constant.ViewTypeConstant;
import com.visualization.manager.ViewManager;
import com.visualization.view.base.ViewConf;
import com.visualization.view.out.*;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class OutputFactory {

    private static final Map<String, Function<Pair<Map<String, Object>, ViewManager>, ? extends OutputView>> map = new HashMap<>();

    static {
        OutputFactory.registerFactory(ViewTypeConstant.CONSOLE, ConsoleOutPutView::produce);
        OutputFactory.registerFactory(ViewTypeConstant.JDBC, JDBCOutPutView::produce);
        OutputFactory.registerFactory(ViewTypeConstant.CSV, CSVOutPutView::produce);
        OutputFactory.registerFactory(ViewTypeConstant.EXCEL, ExcelOutPutView::produce);
    }

    public static void registerFactory(String id, Function<Pair<Map<String, Object>, ViewManager>, ? extends OutputView> fun) {
        if (map.containsKey(id)) throw new RuntimeException("view type重名");
        map.put(id, fun);
    }

    public static OutputView produce(ViewConf viewConf, ViewManager viewManager) {
        Map<String, Object> properties = viewConf.getProperties();
        String viewType = String.valueOf(properties.get(ViewConstant.VIEW_TYPE));
        Function<Pair<Map<String, Object>, ViewManager>, ? extends OutputView> function = map.get(viewType);
        if (function == null) throw new RuntimeException("无该viewType" + viewType);
        return function.apply(Pair.of(properties, viewManager));
    }
}
