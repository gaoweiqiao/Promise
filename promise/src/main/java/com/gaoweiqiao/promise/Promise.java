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
     *  Promise 执行函数的调度器
     * */
    private Scheduler scheduler;
    /**
     *  成功的回调函数
     * */
    private PromiseHandler<T,E,N> promiseHandler = null;
    /**
     *  Promise的状态
     */
    private volatile State state = State.PENDING;
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

    /**
     *
     **/
    public static synchronized <T,E,N> Promise<T,E,N> newPromise(){
        logThreadId("newPromise");
        Promise<T,E,N> promise = new Promise<T,E,N> ();
        return promise;
    }


    /***/
    public <A,B,C>Promise<A,B,C> then(final PromiseHandler<T,E,N> handler){
        getPromiseThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                promiseHandler = handler;
                if(State.RESOLVED == getState()){
                    promiseHandler.handle(Promise.this);
                }else if(State.REJECTED == getState()){
                    promiseHandler.handle(Promise.this);
                }else if(State.PENDING == getState()){
                    promiseHandler.handle(Promise.this);
                }
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
    public State getState(){
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
        Log.d("promise",methodName + Thread.currentThread().getId());
    }
    //
    protected SettledListener listener;
    /**
     * Promise.all()
     * */
    public static <A,B,C> Promise<A,B,C> all(Collection<Promise> promiseCollection){
        AbstractCollectionPromise<A,B,C> collectionPromise = new AllCollectionPromise<A,B,C>(promiseCollection);
        for(Promise promise : promiseCollection){
            if(State.REJECTED == promise.getState()){
                collectionPromise.deferred.reject("reject");
                break;
            }else if(State.PENDING == promise.getState()){
                promiseCollection.add(promise);
            }
        }
        return collectionPromise;
    }
    /**
     *  Promise.any()
     * */
    public static <A,B,C> Promise any(Collection<Promise> promiseCollection){
        List<Promise> promiseList = new ArrayList<>(promiseCollection.size());
        AbstractCollectionPromise collectionPromise = new AnyCollectionPromise(promiseList);
        for(Promise promise : promiseList){
            if(State.RESOLVED == promise.getState()){
                collectionPromise.deferred.resolve("reject");
                break;
            }else if(State.PENDING == promise.getState()){
                promiseList.add(promise);
            }
        }
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
                    if(State.PENDING == getState()){
                        state = State.RESOLVED;
                        Promise.this.resolvedValue = param;
                        if(null != listener){
                            listener.listen(Promise.this);
                        }
                        if(null != promiseHandler){
                            promiseHandler.handle(Promise.this);
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
                    if(State.PENDING == getState()){
                        state = State.REJECTED;
                        Promise.this.rejectedValue = param;
                        if(null != listener){
                            listener.listen(Promise.this);
                        }
                        if(null != promiseHandler){
                            promiseHandler.handle(Promise.this);
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
                    if(State.PENDING == getState()){
                        notifyValue = param;
                        if(null != promiseHandler){
                            promiseHandler.handle(Promise.this);
                        }
                    }else {
                        throw new PromiseHasSettledException();
                    }
                }
            });
        }
    }
    /**
     * 状态
     * */
    public static enum State{
        PENDING,
        RESOLVED,
        REJECTED,
        NONE
    }
    /**
     *  解决的监听器
     * */
    protected static interface SettledListener{
        void listen(Promise promise);
    }

}
