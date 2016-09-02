package com.gaoweiqiao.promise;

/**
 * Created by patrick on 16/8/7.
 */
public abstract class PromiseHandler<T,E,N> {
    public abstract void onResolved(T param);
    public abstract void onRejected(E param);
    public abstract void onNotified(N param);
    public final void handle(Promise<T,E,N> promise){
        if(Promise.State.RESOLVED == promise.getState()){
            T resolvedValue = promise.getResolvedValue();
            if(resolvedValue instanceof Promise){
                ((Promise) resolvedValue).then(new PromiseHandler() {
                    @Override
                    public void onResolved(Object param) {
                        this.onResolved(param);
                    }

                    @Override
                    public void onRejected(Object param) {
                        this.onRejected(param);
                    }

                    @Override
                    public void onNotified(Object param) {
                        this.onNotified(param);
                    }
                });
            }else {
                this.onResolved(resolvedValue);
            }
        }else if(Promise.State.REJECTED == promise.getState()){
            E rejectedValue = promise.getRejectedValue();
            if(rejectedValue instanceof Promise){
                ((Promise) rejectedValue).then(new PromiseHandler() {
                    @Override
                    public void onResolved(Object param) {
                        this.onResolved(param);
                    }

                    @Override
                    public void onRejected(Object param) {
                        this.onRejected(param);
                    }

                    @Override
                    public void onNotified(Object param) {
                        this.onNotified(param);
                    }
                });
            }else {
                this.onRejected(rejectedValue);
            }
        }else if(Promise.State.PENDING == promise.getState()){
            this.onNotified(promise.getNotifyValue());
        }
    }
}
