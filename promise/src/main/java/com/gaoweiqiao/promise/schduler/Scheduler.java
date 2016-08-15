package com.gaoweiqiao.promise.schduler;

/**
 * Created by patrick on 16/8/11.
 */
public class Scheduler {

    public static SchedulerHandler io(){
        return IoSchedulerHandler.getInstance();
    }
    public static SchedulerHandler main(){
        return MainThreadSchedulerHandler.getInstance();
    }
    public static SchedulerHandler promise(){
        return PromiseSchedulerHandler.getInstance();
    }

}

