package com.visualisation.factory;

import com.visualisation.manager.ViewManager;
import com.visualisation.view.ViewConf;
import com.visualisation.view.in.CSVInputView;
import com.visualisation.view.in.InputView;
import com.visualisation.view.in.JDBCInputView;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class InputFactory {

    private static final Map<String, Function<Pair<Map<String, Object>, ViewManager>, ? extends InputView>> map = new HashMap<>();

    static {
        InputFactory.registerFactory("csv", CSVInputView::produce);
        InputFactory.registerFactory("jdbc", JDBCInputView::produce);
    }
    public static void registerFactory(String id, Function<Pair<Map<String, Object>, ViewManager>, ? extends InputView> fun) {
        if (map.containsKey(id)) throw new RuntimeException("view type重名");
        map.put(id, fun);
    }

    public static InputView produce(ViewConf viewConf, ViewManager viewManager) {
        Map<String, Object> properties = viewConf.getProperties();
        String viewType = String.valueOf(properties.get("viewType"));
        Function<Pair<Map<String, Object>, ViewManager>, ? extends InputView> function = map.get(viewType);
        if (function == null) throw new RuntimeException("无该viewType: " + viewType);
        return function.apply(Pair.of(properties, viewManager));
    }
}
