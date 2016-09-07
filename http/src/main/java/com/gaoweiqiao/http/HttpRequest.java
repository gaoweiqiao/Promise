package com.gaoweiqiao.http;

import com.gaoweiqiao.promise.Promise;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by patrick on 16/9/7.
 */
public class HttpRequest {
    private okhttp3.Request.Builder builder;
    private JSONObject params;
    private HttpUrl url;
    public HttpRequest(){
        this.builder = new Request.Builder();
    }
    public HttpRequest url(String url){
        this.url = HttpUrl.parse(url);
        return this;
    }
    public Promise<T,Exception,N> get(HttpParam data){
        HttpUrl.Builder httpUrlBuilder = url.newBuilder();

        data.buildHttpUrl(httpUrlBuilder);
        HttpUrl httpUrl = httpUrlBuilder.build();


        return this;
    }
    public void buildUrl

}
