package com.gaoweiqiao.promise;

/**
 * Created by patrick on 16/8/7.
 */
public interface PromiseHandler<T> {
    void handle(T param);
}
