package com.gaoweiqiao.promise;

/**
 * Created by patrick on 16/8/7.
 */
public interface PromiseHandler<T,E,N> {
    void onResolved(T param);
    void onRejected(E param);
    void onNotified(N param);
}
