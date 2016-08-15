package com.gaoweiqiao.promise.schduler;


import com.gaoweiqiao.promise.Promise;

/**
 * Created by patrick on 16/8/14.
 */
public interface PromiseExecuteHandler<T,E,N> {
    void execute(Promise.State previousState, Promise<T,E,N>.Deferred deferred);
}
