package com.gaoweiqiao.promise;

import java.util.Collection;

/**
 * Created by patrick on 16/8/9.
 */
public class AnyCollectionPromise<A,B,C> extends AbstractCollectionPromise<A,B,C> {
    public AnyCollectionPromise(Collection<Promise> promiseCollection) {
        super(promiseCollection);
    }

    @Override
    public void listen(final Promise promise) {
        getPromiseHandler().post(new Runnable() {
            @Override
            public void run() {
                if(State.RESOLVED == promise.getState()){
                    deferred.resolve("resolve");
                }else{
                    promiseCollection.remove(promise);
                    if(0 == promiseCollection.size()){
                        deferred.reject("reject");
                    }
                }
            }
        });

    }
}
