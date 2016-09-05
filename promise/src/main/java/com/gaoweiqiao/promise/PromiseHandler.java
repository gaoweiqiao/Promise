package com.gaoweiqiao.promise;

/**
 * Created by patrick on 16/8/7.
 */
public abstract class PromiseHandler<T,E,N,A,B,C> {
    public abstract void onResolved(T param);
    public abstract void onRejected(E param);
    public abstract void onNotified(N param);
    private Promise<T,E,N> promise;
    public final void resolve(A param){
        promise.getNext().deferred.resolve(param);
    }
    public final void reject(B param){
        promise.getNext().deferred.reject(param);
    }
    public final void notify(C param){
        promise.getNext().deferred.notify(param);
    }
    public final void handle(Promise<T,E,N> promise){
        this.promise = promise;
        if(Promise.RESOLVED == promise.getState()){
            onResolved(promise.getResolvedValue());
        }else if(Promise.REJECTED == promise.getState()){
            this.onRejected(promise.getRejectedValue());
        }else if(Promise.PENDING == promise.getState()){
            if(null != promise.getNotifyValue()){
                this.onNotified(promise.getNotifyValue());
            }
        }
    }
}
