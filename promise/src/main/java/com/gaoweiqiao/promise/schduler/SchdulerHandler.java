package com.gaoweiqiao.promise.schduler;

import com.gaoweiqiao.quiff.promise.PromiseHandler;

/**
 * Created by patrick on 16/8/11.
 */
public interface SchdulerHandler {
    void handle(Runnable runnable);
}
