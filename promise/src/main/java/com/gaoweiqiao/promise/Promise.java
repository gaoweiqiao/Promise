package com.gaoweiqiao.promise;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.gaoweiqiao.promise.exception.PromiseHasSettledException;
import com.gaoweiqiao.promise.schduler.PromiseExecuteHandler;
import com.gaoweiqiao.promise.schduler.SchdulerHandler;

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
    private static Handler promiseHandler = null;
    /**
     *  下一个Promise
     * */
    private Promise next = null;
    /**
     *  Promise 执行的函数
     * */
    private PromiseExecuteHandler promiseExecuteHandler;
    /**
     *  Promise 执行函数的调度器
     * */
    private SchdulerHandler schdulerHandler;
    /**
     *  成功的回调函数
     * */
    private PromiseHandler<T> resolvePromiseHandler = null;
    private SchdulerHandler resolveSchdulerHandler = null;
    /**
     *  失败的回调函数
     * */
    private PromiseHandler<E> rejectPromiseHandler = null;
    private SchdulerHandler rejectSchdulerHandler = null;
    /**
     *  通知的回调函数
     * */
    private PromiseHandler<N> notifyPromiseHandler = null;
    private SchdulerHandler notifySchdulerHandler = null;
    /**
     *  Promise的状态
     */
    private volatile State state = State.PENDING;
    //保存值
    private T resolvedValue = null;
    private E rejectedValue = null;
    private N notifyValue = null;
    public final Deferred deferred = new Deferred();

    protected Promise() {
    }

    /**
     *
     **/
    public static synchronized <A,B,C> Promise<A,B,C> newPromise(SchdulerHandler schdulerHandler,PromiseExecuteHandler promiseExecuteHandler){
        logThreadId("newPromise");
        Promise<A,B,C> promise = new Promise<A,B,C>(schdulerHandler,promiseExecuteHandler);
        promise.execute(State.NONE);
        return promise;
    }
    public  <T,E,N>Promise(SchdulerHandler schdulerHandler,final PromiseExecuteHandler promiseExecuteHandler){
        this.schdulerHandler = schdulerHandler;
        this.promiseExecuteHandler = promiseExecuteHandler;
    }
    private void execute(final State state){
        schdulerHandler.handle(new Runnable() {
            @Override
            public void run() {
                promiseExecuteHandler.execute(state,deferred);
            }
        });
    }
    //
    public Promise<T,E,N> onResolved(final SchdulerHandler schdulerHandler, final PromiseHandler<T> successPromiseHandler){
        getPromiseHandler().post(new Runnable() {
            @Override
            public void run() {
                logThreadId("onResolved");
                if(State.RESOLVED == getState()){
                    successPromiseHandler.handle(resolvedValue);
                }else if(State.PENDING == getState()){
                    Promise.this.resolvePromiseHandler = successPromiseHandler;
                    Promise.this.resolveSchdulerHandler = schdulerHandler;
                }
            }
        });
        return this;
    }
    public Promise<T,E,N> onRejected(final SchdulerHandler schdulerHandler, final PromiseHandler<E> errorPromiseHandler){
        getPromiseHandler().post(new Runnable() {
            @Override
            public void run() {
                logThreadId("onRejected");
                if(State.REJECTED == getState()){
                    schdulerHandler.handle(new Runnable() {
                        @Override
                        public void run() {
                        errorPromiseHandler.handle(rejectedValue);
                        }
                    });

                }else if(State.PENDING == getState()){
                    Promise.this.rejectPromiseHandler = errorPromiseHandler;
                    Promise.this.rejectSchdulerHandler = schdulerHandler;
                }
            }
        });

        return this;
    }
    public Promise<T,E,N> onNotified(final SchdulerHandler schdulerHandler, final PromiseHandler<N> notifyPromiseHandler){
        getPromiseHandler().post(new Runnable() {
            @Override
            public void run() {
                logThreadId("onNotified");
                if(State.PENDING == getState()){
                    Promise.this.notifyPromiseHandler = notifyPromiseHandler;
                    Promise.this.notifySchdulerHandler = schdulerHandler;
                    if(null != notifyValue){
                        schdulerHandler.handle(new Runnable() {
                            @Override
                            public void run() {
                                notifyPromiseHandler.handle(notifyValue);
                            }
                        });
                    }
                }
            }
        });

        return this;
    }
    public <A,B,C> Promise next(SchdulerHandler schdulerHandler,PromiseExecuteHandler promiseExecuteHandler){
        synchronized (this){
            logThreadId("next");
            if(null == next){
                next = new Promise<A,B,C>(schdulerHandler,promiseExecuteHandler);
            }
        }

        return next;
    }
    public State getState(){
        return state;
    }
    //
    public static Handler getPromiseHandler(){
        if(null == promiseHandlerThread){
            synchronized (Promise.class){
                if(null == promiseHandlerThread){
                    promiseHandlerThread = new HandlerThread("com.gaoweiqiao.quiff.promise");
                    promiseHandlerThread.start();
                    promiseHandler = new Handler(promiseHandlerThread.getLooper());
                }
            }
        }
        return promiseHandler;
    }
    //
    private static void logThreadId(String methodName){
        Log.d("quiff",methodName + Thread.currentThread().getId());
    }
    //
    protected SettledListener listener;
    /**
     * Promise.all()
     * */
    public static <A,B,C> Promise all(Collection<Promise> promiseCollection){
        List<Promise> promiseList = new ArrayList<>(promiseCollection.size());
        AbstractCollectionPromise collectionPromise = new AllCollectionPromise(promiseList);
        for(Promise promise : promiseList){
            if(State.REJECTED == promise.getState()){
                collectionPromise.deferred.reject("reject");
                break;
            }else if(State.PENDING == promise.getState()){
                promiseList.add(promise);
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
            getPromiseHandler().post(new Runnable() {
                @Override
                public void run() {
                    logThreadId("resolve");
                    if(State.PENDING == getState()){
                        state = State.RESOLVED;
                        if(null != listener){
                            listener.listen(Promise.this);
                        }
                        if(null != resolvePromiseHandler){
                            rejectSchdulerHandler.handle(new Runnable() {
                                @Override
                                public void run() {
                                    resolvePromiseHandler.handle(param);
                                }
                            });
                        }else{
                            resolvedValue = param;
                        }
                        if(null != next){
                            next.execute(State.RESOLVED);
                        }
                    }else {
                        throw new PromiseHasSettledException();
                    }
                }
            });
        }
        public void reject(final E param){
            getPromiseHandler().post(new Runnable() {
                @Override
                public void run() {
                    logThreadId("reject");
                    if(State.PENDING == getState()){
                        state = State.REJECTED;
                        if(null != listener){
                            listener.listen(Promise.this);
                        }
                        if(null != rejectPromiseHandler){
                            rejectSchdulerHandler.handle(new Runnable() {
                                @Override
                                public void run() {
                                    rejectPromiseHandler.handle(param);
                                }
                            });
                        }else {
                            rejectedValue = param;
                        }
                        if(null != next){
                            next.execute(State.REJECTED);
                        }
                    }else {
                        throw new PromiseHasSettledException();
                    }
                }
            });
        }
        public void notify(final N param){
            getPromiseHandler().post(new Runnable() {
                @Override
                public void run() {
                    logThreadId("notify");
                    if(State.PENDING == getState()){
                        if(null != notifyPromiseHandler){
                            notifySchdulerHandler.handle(new Runnable() {
                                @Override
                                public void run() {
                                notifyPromiseHandler.handle(param);
                                }
                            });
                        }
                        notifyValue = param;
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
    /**
     *  线程调度器
     * */

}