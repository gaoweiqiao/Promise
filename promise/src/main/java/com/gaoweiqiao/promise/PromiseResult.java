package com.gaoweiqiao.promise;

/**
 * Created by patrick on 16/8/21.
 */
public  class PromiseResult<A,B>{
    private A resolvedValue;
    private B rejectValue;
    private Promise.State state;
    public PromiseResult(A resolve ,B reject,Promise.State stateFlag){
        resolvedValue = resolve;
        rejectValue = reject;
        state = stateFlag;
    }
    public A getResolvedValue(){
        return resolvedValue;
    }
    public B getRejectValue(){
        return rejectValue;
    }
    public Promise.State getState(){
        return state;
    }
}
