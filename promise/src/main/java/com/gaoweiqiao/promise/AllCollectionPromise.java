package com.gaoweiqiao.promise;

import java.util.Collection;

/**
 * Created by patrick on 16/8/9.
 */
public class AllCollectionPromise<A,B,C> extends AbstractCollectionPromise<A,B,C> {
    public AllCollectionPromise(Collection<Promise> promiseCollection) {
        super(promiseCollection);
    }

    @Override
    public void listen(final Promise promise) {
        getPromiseHandler().post(new Runnable() {
            @Override
            public void run() {
                if(State.REJECTED == promise.getState()){
                    deferred.reject("reject");
                }else{
                    promiseCollection.remove(promise);
                    if(0 == promiseCollection.size()){
                        deferred.resolve("resolve");
                    }
                }
            }
        });


    }
}
