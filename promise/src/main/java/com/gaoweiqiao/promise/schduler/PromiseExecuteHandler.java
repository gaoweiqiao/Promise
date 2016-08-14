package com.gaoweiqiao.promise.schduler;

import com.gaoweiqiao.quiff.promise.Promise;

/**
 * Created by patrick on 16/8/14.
 */
public interface PromiseExecuteHandler {
    void execute(Promise.State previousState, Promise.Deferred deferred);
}
