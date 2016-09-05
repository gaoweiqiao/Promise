package com.gaoweiqiao.promise;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.gaoweiqiao.promise.exception.PromiseHasSettledException;
import com.gaoweiqiao.promise.schduler.Scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by patrick on 16/8/7.
 */
public class Promise<T,E,N> {
    /**
     * 状态
     * */
    public static final short PENDING = 0;
    public static final short RESOLVED = 1;
    public static final short REJECTED = 2;
    /**
     *  promise线程
     * */
    private static HandlerThread promiseHandlerThread = null;
    /**
     *  promise线程队列
     * */
    private static Handler promiseThreadHandler = null;
    /**
     *  下一个Promise
     * */
    private Promise next = null;
    /**
     *  成功的回调函数
     * */
    private PromiseHandler<T,E,N,?,?,?> promiseHandler = null;
    /**
     *  Promise的状态
     */
    private volatile short state = PENDING;
    //保存值
    private T resolvedValue = null;
    private E rejectedValue = null;
    private N notifyValue = null;
    public final Deferred deferred = new Deferred();

    protected <T,E,N>Promise() {
    }

    public E getRejectedValue() {
        return rejectedValue;
    }

    public N getNotifyValue() {
        return notifyValue;
    }

    public T getResolvedValue() {
        return resolvedValue;
    }

    public Promise getNext() {
        return next;
    }

    /**
     *
     **/
    public static synchronized <T,E,N> Promise<T,E,N> newPromise(){
        logThreadId("newPromise");
        Promise<T,E,N> promise = new Promise<T,E,N> ();
        return promise;
    }


    /***/
    public <A,B,C>Promise<A,B,C> then(final PromiseHandler<T,E,N,A,B,C> handler){
        getPromiseThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                promiseHandler = handler;
                promiseHandler.handle(Promise.this);
            }
        });
        logThreadId("next");
        synchronized(this){
            if(null == next){
                next = new Promise<A,B,C>();
            }
            return next;
        }
    }
    public short getState(){
        return state;
    }
    //
    public static Handler getPromiseThreadHandler(){
        if(null == promiseHandlerThread){
            synchronized (Promise.class){
                if(null == promiseHandlerThread){
                    promiseHandlerThread = new HandlerThread("com.gaoweiqiao.promise");
                    promiseHandlerThread.start();
                    promiseThreadHandler = new Handler(promiseHandlerThread.getLooper());
                }
            }
        }
        return promiseThreadHandler;
    }
    //
    private static void logThreadId(String methodName){
        Log.d("promise @",methodName + Thread.currentThread().getId());
    }
    //
    protected SettledListener listener;
    /**
     * Promise.all()
     * */
    public static Promise<Void,Void,Void> all(final Collection<Promise> promiseCollection){
        final AbstractCollectionPromise<Void,Void,Void> collectionPromise = new AllCollectionPromise<Void,Void,Void>(promiseCollection);
        getPromiseThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                for(Promise promise : promiseCollection){
                    if(REJECTED == promise.getState()){
                        collectionPromise.deferred.reject(null);
                        break;
                    }
                }
            }
        });

        return collectionPromise;
    }
    /**
     *  Promise.any()
     * */
    public static Promise<Void,Void,Void> any(final Collection<Promise> promiseCollection){
        final AbstractCollectionPromise collectionPromise = new AnyCollectionPromise(promiseCollection);
        getPromiseThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                for(Promise promise : promiseCollection){
                    if(RESOLVED == promise.getState()){
                        collectionPromise.deferred.resolve(null);
                        break;
                    }
                }
            }
        });
        return collectionPromise;
    }
    /**
     *  延迟
     * */
    public class Deferred{
        public void resolve(final T param){
            getPromiseThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    logThreadId("resolve");
                    if(PENDING == getState()){
                        state = RESOLVED;
                        Promise.this.resolvedValue = param;
                        if(null != listener ){
                            listener.listen(Promise.this);
                        }
                        if(null != promiseHandler){
                            promiseHandler.getHandleScheduler(RESOLVED).handle(new Runnable() {
                                @Override
                                public void run() {
                                    promiseHandler.onResolved(param);
                                }
                            });

                        }
                    }else {
                        throw new PromiseHasSettledException();
                    }
                }
            });
        }
        public void reject(final E param){
            getPromiseThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    logThreadId("reject");
                    if(PENDING == getState()){
                        state = REJECTED;
                        Promise.this.rejectedValue = param;
                        if(null != listener){
                            listener.listen(Promise.this);
                        }
                        if(null != promiseHandler){
                            promiseHandler.getHandleScheduler(REJECTED).handle(new Runnable() {
                                @Override
                                public void run() {
                                    promiseHandler.onRejected(param);
                                }
                            });
                        }

                    }else {
                        throw new PromiseHasSettledException();
                    }
                }
            });
        }
        public void notify(final N param){
            getPromiseThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    logThreadId("notify");
                    if(PENDING == getState()){
                        notifyValue = param;
                        if(null != promiseHandler){
                            promiseHandler.getHandleScheduler(PENDING).handle(new Runnable() {
                                @Override
                                public void run() {
                                    promiseHandler.onNotified(param);
                                }
                            });
                        }
                    }else {
                        throw new PromiseHasSettledException();
                    }
                }
            });
        }
    }
    /**
     *  解决的监听器
     * */
    protected static interface SettledListener{
        void listen(Promise promise);
    }

}
