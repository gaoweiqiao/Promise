package com.gaoweiqiao.promise.schduler;

/**
 * Created by patrick on 16/8/11.
 */
public class Schduler {

    public static SchdulerHandler io(){
        return IoSchdulerHandler.getInstance();
    }
    public static SchdulerHandler main(){
        return MainThreadSchduler.getInstance();
    }
    public static SchdulerHandler promise(){
        return PromiseSchdulerHandler.getInstance();
    }

}

