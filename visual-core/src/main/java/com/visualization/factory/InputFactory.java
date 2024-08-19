package com.visualization.factory;

import com.visualization.constant.ViewConstant;
import com.visualization.constant.ViewTypeConstant;
import com.visualization.manager.ViewManager;
import com.visualization.view.base.ViewConf;
import com.visualization.view.in.CSVInputView;
import com.visualization.view.in.InputView;
import com.visualization.view.in.JDBCInputView;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class InputFactory {

    private static final Map<String, Function<Pair<Map<String, Object>, ViewManager>, ? extends InputView>> map = new HashMap<>();

    static {
        InputFactory.registerFactory(ViewTypeConstant.CSV, CSVInputView::produce);
        InputFactory.registerFactory(ViewTypeConstant.JDBC, JDBCInputView::produce);
    }

    public static void registerFactory(String id, Function<Pair<Map<String, Object>, ViewManager>, ? extends InputView> fun) {
        if (map.containsKey(id)) throw new RuntimeException("view type重名");
        map.put(id, fun);
    }

    public static InputView produce(ViewConf viewConf, ViewManager viewManager) {
        Map<String, Object> properties = viewConf.getProperties();
        String viewType = String.valueOf(properties.get(ViewConstant.VIEW_TYPE));
        Function<Pair<Map<String, Object>, ViewManager>, ? extends InputView> function = map.get(viewType);
        if (function == null) throw new RuntimeException("无该viewType: " + viewType);
        return function.apply(Pair.of(properties, viewManager));
    }
}
