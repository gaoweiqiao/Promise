package com.gaoweiqiao.promise;

import java.util.Collection;

/**
 * Created by patrick on 16/8/9.
 */
public class AllCollectionPromise extends AbstractCollectionPromise {
    public AllCollectionPromise(Collection<Promise> promiseCollection) {
        super(promiseCollection);
    }

    @Override
    public void listen(Promise promise) {
        if(State.REJECTED == promise.getState()){
            deferred.reject("reject");
        }else{
            promiseCollection.remove(promise);
            if(0 == promiseCollection.size()){
                deferred.resolve("resolve");
            }
        }

    }
}
