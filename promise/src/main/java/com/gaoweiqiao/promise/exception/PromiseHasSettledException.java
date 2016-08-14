package com.gaoweiqiao.promise.exception;

/**
 * Created by patrick on 16/8/14.
 */
public class PromiseHasSettledException extends RuntimeException{
    public PromiseHasSettledException() {
        super(EXCEPTION_MESSAGE);
    }
    private static String EXCEPTION_MESSAGE = "promise has already be settled,must not settle again";
}
