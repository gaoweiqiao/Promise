package com.gaoweiqiao.promise;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by patrick on 16/8/9.
 */
public abstract class AbstractCollectionPromise<A,B,C> extends Promise implements Promise.SettledListener{
    protected ConcurrentLinkedQueue<Promise> promiseCollection;

    protected AbstractCollectionPromise(Collection<Promise> promiseCollection) {
        getPromiseHandler().post(new Runnable() {
            @Override
            public void run() {
                AbstractCollectionPromise.this.promiseCollection = new ConcurrentLinkedQueue<Promise>( AbstractCollectionPromise.this.promiseCollection);
                for(Promise promise : AbstractCollectionPromise.this.promiseCollection){
                    promise.listener = AbstractCollectionPromise.this;
                }
            }
        });
    }

    @Override
    public abstract void listen(Promise promise);
}
