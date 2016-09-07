package com.gaoweiqiao.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;

/**
 * Created by patrick on 16/9/7.
 */
public class HttpParam {
    private Map<String,List<String>> params = new HashMap<>();
    public HttpParam add(String name,String value){
        if(params.containsKey(name)){
            params.get(name).add(value);
        }else{
            List<String> valueArray = new ArrayList<String>();
            valueArray.add(value);
            params.put(name,valueArray);
        }
        return this;
    }
    public void buildHttpUrl(HttpUrl.Builder buider){
        for (Map.Entry<String,List<String>> param : params.entrySet()) {
            for(String value : param.getValue()){
                buider.addQueryParameter(param.getKey(),value);
            }
        }
    }
}
