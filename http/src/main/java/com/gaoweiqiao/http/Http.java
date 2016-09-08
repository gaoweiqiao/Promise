package com.gaoweiqiao.http;

import com.gaoweiqiao.promise.Promise;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by patrick on 16/9/7.
 */
public class Http {
    private static OkHttpClient client = new OkHttpClient();
    public static final String POST = "post";
    public static final String GET = "get";
    public static final String PUT = "put";
    //
    private  <T,N> Promise<T,Exception,N> request(Request request,final Class<T> clazz){
        Call call = client.newCall(request);
        final Promise<T,Exception,N> promise = Promise.newPromise();
        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                promise.deferred.reject(e);
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()){
                    promise.deferred.reject(new IOException("Unexpected code " + response));
                }
                String responseString = response.body().string();
                T result = new Gson().fromJson(responseString, clazz);
                promise.deferred.resolve(result);
            }
        });
        return promise;
    }
    public <T,N> Promise<T,Exception,N> get(String url, HttpParam data,final Class<T> clazz){
        HttpUrl originUrl = HttpUrl.parse(url);
        HttpUrl.Builder httpUrlBuilder = originUrl.newBuilder();

        data.buildHttpUrl(httpUrlBuilder);
        HttpUrl httpUrl = httpUrlBuilder.build();

        final Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .build();
        //
        return request(request,clazz);
    }
}
