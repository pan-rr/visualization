package com.visualization.utils;

import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapToBeanUtil {

    public static <T> List<T> mapListToBeanList(List<Map<String, Object>> mapList, Class<T> clazz) {
        if (CollectionUtils.isEmpty(mapList)) return new ArrayList<>();
        List<T> res = new ArrayList<>(mapList.size());
        Gson gson = new Gson();
        for (Map<String, Object> map : mapList) {
            String json = gson.toJson(map);
            res.add(gson.fromJson(json, clazz));
        }
        return res;
    }


}
