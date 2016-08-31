package com.gaoweiqiao.promise;

import android.os.Handler;
import android.os.HandlerThread;

import com.gaoweiqiao.promise.exception.PromiseHasSettledException;
import com.gaoweiqiao.promise.schduler.PromiseExecuteHandler;
import com.gaoweiqiao.promise.schduler.Scheduler;

/**
 * Created by patrick on 16/8/31.
 */
public class PromiseA {

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
    private Scheduler scheduler;
    /**
     *  成功的回调函数
     * */
    private PromiseHandler<T> resolvePromiseHandler = null;
    private Scheduler resolveScheduler = null;
    /**
     *  失败的回调函数
     * */
    private PromiseHandler<E> rejectPromiseHandler = null;
    private Scheduler rejectScheduler = null;
    /**
     *  通知的回调函数
     * */
    private PromiseHandler<N> notifyPromiseHandler = null;
    private Scheduler notifyScheduler= null;
    /**
     *  Promise的状态
     */
    private volatile State state = State.PENDING;


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
                            rejectScheduler.handle(new Runnable() {
                                @Override
                                public void run() {
                                    resolvePromiseHandler.handle(param);
                                }
                            });
                        }else{
                            resolvedValue = param;
                        }
                        if(null != next){
                            next.execute(new PromiseResult<T,E>(param,null,State.RESOLVED));
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
                            rejectScheduler.handle(new Runnable() {
                                @Override
                                public void run() {
                                    rejectPromiseHandler.handle(param);
                                }
                            });
                        }else {
                            rejectedValue = param;
                        }
                        if(null != next){
                            next.execute(new PromiseResult<T,E>(null,param,State.REJECTED));
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
                            notifyScheduler.handle(new Runnable() {
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
}
