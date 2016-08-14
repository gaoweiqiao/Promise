package com.gaoweiqiao.promise;

import java.util.Collection;

/**
 * Created by patrick on 16/8/9.
 */
public class AnyCollectionPromise extends AbstractCollectionPromise {
    public AnyCollectionPromise(Collection<Promise> promiseCollection) {
        super(promiseCollection);
    }

    @Override
    public void listen(Promise promise) {
        if(State.RESOLVED == promise.getState()){
            deferred.resolve("resolve");
        }else{
            promiseCollection.remove(promise);
            if(0 == promiseCollection.size()){
                deferred.reject("reject");
            }
        }
    }
}
