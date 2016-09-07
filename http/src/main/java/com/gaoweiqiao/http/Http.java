package com.gaoweiqiao.http;

import okhttp3.OkHttpClient;

/**
 * Created by patrick on 16/9/7.
 */
public class Http {
    private static OkHttpClient client = new OkHttpClient();
    //
    public static HttpRequest newRequest(String url){
        return new HttpRequest();
    }
}
