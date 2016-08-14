package com.gaoweiqiao.promise;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by patrick on 16/8/9.
 */
public abstract class AbstractCollectionPromise extends Promise implements Promise.SettledListener{
    protected ConcurrentLinkedQueue<Promise> promiseCollection;

    protected AbstractCollectionPromise(Collection<Promise> promiseCollection) {
        super();
        this.promiseCollection = new ConcurrentLinkedQueue<Promise>(promiseCollection);
        for(Promise promise : promiseCollection){
            promise.listener = this;
        }
    }

    @Override
    public abstract void listen(Promise promise);
}
