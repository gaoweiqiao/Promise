package com.gaoweiqiao.promisedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.gaoweiqiao.promise.Promise;
import com.gaoweiqiao.promise.PromiseHandler;
import com.gaoweiqiao.promise.schduler.Scheduler;
import com.gaoweiqiao.promise.schduler.SchedulerFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.promise)
    protected Button promiseButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.promise)
    protected void test_promise(){
       getResolvedPromise("gao")
               .then(new PromiseHandler<String, String, String,Integer,String,String>() {
                   @Override
                   public Scheduler getHandleScheduler(short promiseState) {
                       switch (promiseState){
                           case Promise.PENDING:
                               return SchedulerFactory.io();
                           case Promise.RESOLVED:
                               return SchedulerFactory.main();
                           case Promise.REJECTED:
                               return SchedulerFactory.promise();
                       }
                       return SchedulerFactory.promise();
                   }
                   @Override
                   public void onResolved(final String param) {
                       Promise.logThreadId("onResolved");
                       Log.d("gao","resolve param is "+param);

                       new Thread(new Runnable() {
                           @Override
                           public void run() {
                               try {
                                   Thread.sleep(10000);
                               } catch (InterruptedException e) {
                                   e.printStackTrace();
                               }
                               reject(""+1);
                           }
                       }).start();

                   }

                   @Override
                   public void onRejected(String param) {
                       Promise.logThreadId("onRejected");
                       Log.d("gao","reject param is "+param);
                       resolve(2);
                   }

                   @Override
                   public void onNotified(String param) {
                       Promise.logThreadId("onNotified");
                       Log.d("gao","notify param is "+param);
                   }
               })
               .then(new PromiseHandler<Integer, String, String,Void,Void,Void>() {
                   @Override
                   public Scheduler getHandleScheduler(short promiseState) {
                       switch (promiseState){
                           case Promise.PENDING:
                               return SchedulerFactory.io();
                           case Promise.RESOLVED:
                               return SchedulerFactory.io();
                           case Promise.REJECTED:
                               return SchedulerFactory.io();
                       }
                       return SchedulerFactory.promise();
                   }
                   @Override
                   public void onResolved(Integer param) {
                       Promise.logThreadId("onResolved");
                       Log.d("gao","resolve param is "+param);
                   }

                   @Override
                   public void onRejected(String param) {
                       Promise.logThreadId("onRejected");
                       Log.d("gao","reject param is "+param);
                   }

                   @Override
                   public void onNotified(String param) {
                       Promise.logThreadId("onNotified");
                       Log.d("gao","resolve param is "+param);
                   }
               });
    }
    @OnClick(R.id.sync_promise)
    protected void test_sync_promise(){
        Promise<String,String,String> promise = Promise.newPromise();
        promise.deferred.resolve("sync success");
        promise.then(new PromiseHandler<String, String, String, Integer, String, String>() {
            @Override
            public Scheduler getHandleScheduler(short promiseState) {
                switch (promiseState){
                    case Promise.PENDING:
                        return SchedulerFactory.io();
                    case Promise.RESOLVED:
                        return SchedulerFactory.io();
                    case Promise.REJECTED:
                        return SchedulerFactory.io();
                }
                return SchedulerFactory.promise();
            }

            @Override
            public void onNotified(String param) {
                Promise.logThreadId("first onNotified");
            }

            @Override
            public void onRejected(String param) {
                Promise.logThreadId("first onRejected");
            }

            @Override
            public void onResolved(String param) {
                Promise.logThreadId("first onResolved");
                reject("llll");
            }
        }).then(new PromiseHandler<Integer, String, String, Void, Void, Void>() {
            @Override
            public Scheduler getHandleScheduler(short promiseState) {
                switch (promiseState){
                    case Promise.PENDING:
                        return SchedulerFactory.io();
                    case Promise.RESOLVED:
                        return SchedulerFactory.io();
                    case Promise.REJECTED:
                        return SchedulerFactory.io();
                }
                return SchedulerFactory.promise();
            }

            @Override
            public void onNotified(String param) {
                Promise.logThreadId("second onNotified");
            }

            @Override
            public void onRejected(String param) {
                Promise.logThreadId("second onRejected");
            }

            @Override
            public void onResolved(Integer param) {
                Promise.logThreadId("second onResolved");
            }
        });
    }
    @OnClick(R.id.promise_all)
    protected void test_promise_all(){
        List<Promise> promiseList = new ArrayList<>();
        promiseList.add(getResolvedPromise("gao"));
        promiseList.add(getRejectedPromise("gao"));
        Promise.all(promiseList)
                .then(new PromiseHandler<Void, Void, Void, Object, Object, Object>() {
                    @Override
                    public void onResolved(Void param) {
                        Log.d("gao","all promise is success");
                    }

                    @Override
                    public void onRejected(Void param) {
                        Log.d("gao","all promise is not success");
                    }
                });
    }
    @OnClick(R.id.promise_any)
    protected void test_promise_any(){
        List<Promise> promiseList = new ArrayList<>();
        promiseList.add(getRejectedPromise("gao"));
        promiseList.add(getRejectedPromise("gao"));
        Promise.any(promiseList)
                .then(new PromiseHandler<Void, Void, Void, Object, Object, Object>() {

                    @Override
                    public void onResolved(Void param) {
                        Log.d("gao","any promise is success");
                    }

                    @Override
                    public void onRejected(Void param) {
                        Log.d("gao","any promise is not success");
                    }
                });
    }
    private Promise<String,String,String> getResolvedPromise(final String tag){
        final Promise<String,String,String> promise = Promise.newPromise();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i<10){
                    try {
                        Thread.sleep(1000);
                        promise.deferred.notify("tag : "+tag+" , i == "+i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                promise.deferred.resolve("YES");
            }
        }).start();
        return promise;
    }
    private Promise<String,String,String> getRejectedPromise(final String tag){
        final Promise<String,String,String> promise = Promise.newPromise();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i<10){
                    try {
                        Thread.sleep(1000);
                        promise.deferred.notify("tag : "+tag+" , i == "+i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                promise.deferred.reject("YES");
            }
        }).start();
        return promise;
    }
}
