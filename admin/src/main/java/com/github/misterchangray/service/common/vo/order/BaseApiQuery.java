package com.github.misterchangray.service.common.vo.order;


import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.common.utils.SignUtil;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by riecard on 2018/5/25.
 */
public class BaseApiQuery {

    static final int CONN_TIMEOUT = 5;
    static final int READ_TIMEOUT = 5;
    static final int WRITE_TIMEOUT = 5;

    private OkHttpClient client;

    public BaseApiQuery() {
        client = new OkHttpClient.Builder().connectTimeout(CONN_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS).writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
//                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1080)))
                .build();
    }

    public OkHttpClient getClient() {
        return client;
    }

    public String get(String url) {
        Request.Builder builder = new Request.Builder().url(url).get();
        Request request = builder.build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = null;
        try {
            s = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public String get(String url, Map<String, String> headers) {
        Request.Builder builder = new Request.Builder().url(url).get();
        if (headers!=null) {
            for (String key:headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
        }
        Request request = builder.build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = null;
        try {
            s = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    static final MediaType JSON = MediaType.parse("application/json");
    static final MediaType FORM = MediaType.parse("application/x-www-form-urlencoded");
    public String post(String uri, Object object, Map<String, String> params, Map<String, String> headers) {
        Request.Builder builder = null;
        RequestBody formBody = FormBody.create(FORM, SignUtil.toQueryString(params));
        builder = new Request.Builder().url(uri).post(formBody);
        if (headers!=null) {
            for (String key:headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
        }
        Request request = builder.build();
        String s = null;
        try {
            Response response = client.newCall(request).execute();
            s = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public String postJson(String uri,Map<String, String> params, Map<String, String> headers) {
        Request.Builder builder = null;
        RequestBody formBody = FormBody.create(JSON, JSONUtils.obj2json(params));
        builder = new Request.Builder().url(uri).post(formBody);
        if (headers!=null) {
            for (String key:headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
        }
        Request request = builder.build();
        String s = null;
        try {
            Response response = client.newCall(request).execute();
            s = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }
}
