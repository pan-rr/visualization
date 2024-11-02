package com.visualization.util;

import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class HeaderUtil {

    public static List<String> getHeader(HttpHeaders httpHeaders , String headerName){
        return httpHeaders.getOrEmpty(headerName);
    }

    public static String getOneHeader(HttpHeaders httpHeaders , String headerName){
        List<String> list = getHeader(httpHeaders, headerName);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }
}
