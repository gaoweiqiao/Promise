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
        getPromiseThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                if(PENDING == getState()){
                    if(RESOLVED == promise.getState()){
                        deferred.resolve(null);
                    }else{
                        promiseCollection.remove(promise);
                        if(0 == promiseCollection.size()){
                            deferred.reject(null);
                        }
                    }
                }
            }
        });

    }
}
