package com.visualisation.factory;

import com.visualisation.manager.ViewManager;
import com.visualisation.view.ViewConf;
import com.visualisation.view.out.*;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class OutputFactory {

    private static final Map<String, Function<Pair<Map<String, Object>, ViewManager>, ? extends OutputView>> map = new HashMap<>();

    static {
        OutputFactory.registerFactory("console", ConsoleOutPutView::produce);
        OutputFactory.registerFactory("jdbc", JDBCOutPutView::produce);
        OutputFactory.registerFactory("csv", CSVOutPutView::produce);
        OutputFactory.registerFactory("excel", ExcelOutPutView::produce);
    }

    public static void registerFactory(String id, Function<Pair<Map<String, Object>, ViewManager>, ? extends OutputView> fun) {
        if (map.containsKey(id)) throw new RuntimeException("view type重名");
        map.put(id, fun);
    }

    public static OutputView produce(ViewConf viewConf, ViewManager viewManager) {
        Map<String, Object> properties = viewConf.getProperties();
        String viewType = String.valueOf(properties.get("viewType"));
        Function<Pair<Map<String, Object>, ViewManager>, ? extends OutputView> function = map.get(viewType);
        if (function == null) throw new RuntimeException("无该viewType" + viewType);
        return function.apply(Pair.of(properties, viewManager));
    }
}
