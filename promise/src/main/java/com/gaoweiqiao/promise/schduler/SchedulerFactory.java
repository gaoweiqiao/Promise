package com.gaoweiqiao.promise.schduler;

/**
 * Created by patrick on 16/8/11.
 */
public class SchedulerFactory {

    public static Scheduler io(){
        return IoScheduler.getInstance();
    }
    public static Scheduler main(){
        return MainThreadScheduler.getInstance();
    }
    public static Scheduler promise(){
        return PromiseScheduler.getInstance();
    }

}

