package com.visualization.manager.orphan;

import com.google.gson.Gson;
import com.visualization.view.base.VisualStage;
import okhttp3.*;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class HttpManager {

    private enum AllowMethod {
        GET, POST;

        public static AllowMethod getMethod(String s) {
            if (POST.name().equals(s)) return POST;
            return GET;
        }
    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String HEADER = "header";
    private static final String URL = "url";
    private static final String METHOD = "method";
    private static final String BODY = "body";
    private static final String TIMEOUT = "timeout";
    private static final int DEFAULT_TIMEOUT = 30;

    public static void execute(VisualStage stage) {

        Map<String, Object> http = stage.getHttp();
        Map<String, String> headers = (Map<String, String>) http.get(HEADER);
        int timeout = Double.valueOf(http.getOrDefault(TIMEOUT, DEFAULT_TIMEOUT).toString()).intValue();
        timeout = Math.min(timeout, DEFAULT_TIMEOUT);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(timeout, TimeUnit.SECONDS);
        builder.readTimeout(timeout, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        OkHttpClient client = builder.build();
        Request.Builder requestBuilder = new Request.Builder();

        requestBuilder.headers(Headers.of(headers));
        AllowMethod method = AllowMethod.getMethod(http.get(METHOD).toString());
        Request request = buildRequest(requestBuilder, method, http);

        try {
            ResponseBody body = client.newCall(request).execute().body();
            System.err.println(body.string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static Request buildRequest(Request.Builder builder, AllowMethod method, Map<String, Object> http) {
        String path = String.valueOf(http.get(URL));
        if (AllowMethod.GET.equals(method)) {
            Map<String, String> body = (Map<String, String>) http.getOrDefault(BODY, new HashMap<>());
            if (!CollectionUtils.isEmpty(body)) {
                HttpUrl.Builder urlBuilder = HttpUrl.parse(path).newBuilder();
                body.forEach(urlBuilder::addQueryParameter);
                path = urlBuilder.toString();
            }
            return builder.url(path).get().build();
        }
        Gson gson = new Gson();
        String json = gson.toJson(http.getOrDefault(BODY, new HashMap<>()));
        return builder.post(RequestBody.create(json, JSON)).build();
    }

}
