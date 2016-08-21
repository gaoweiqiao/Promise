package com.gaoweiqiao.promise.schduler;


import com.gaoweiqiao.promise.Promise;
import com.gaoweiqiao.promise.PromiseResult;

/**
 * Created by patrick on 16/8/14.
 */
public interface PromiseExecuteHandler<T,E,A,B,C> {
     void execute(PromiseResult<T,E> promiseResult, Promise<A, B, C>.Deferred deferred);
}
