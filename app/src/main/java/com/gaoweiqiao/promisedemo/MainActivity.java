package com.gaoweiqiao.promisedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.gaoweiqiao.promise.Promise;
import com.gaoweiqiao.promise.PromiseHandler;
import com.gaoweiqiao.promise.PromiseResult;
import com.gaoweiqiao.promise.schduler.PromiseExecuteHandler;
import com.gaoweiqiao.promise.schduler.Scheduler;

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
        Promise.newPromise(Scheduler.io(), new PromiseExecuteHandler<Void,Void,String,Integer,String>() {

            @Override
            public  void execute(PromiseResult<Void,Void> promiseResult, Promise<String, Integer, String>.Deferred deferred) {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    deferred.notify("first notify"+i);
                }
                deferred.resolve("first resolved");
            }

        }).onResolved(Scheduler.main(), new PromiseHandler<String>() {
            @Override
            public void handle(String param) {
//                promiseButton.setText(param);
            }
        }).onRejected(Scheduler.main(), new PromiseHandler<Integer>() {
            @Override
            public void handle(Integer param) {
//                promiseButton.setText(param);
            }
        }).onNotified(Scheduler.main(), new PromiseHandler<String>() {
            @Override
            public void handle(String param) {
                promiseButton.setText(param);
            }
        }).next(Scheduler.main(), new PromiseExecuteHandler<String,Integer, String,String,String>() {

            @Override
            public void execute(PromiseResult<String,Integer> promiseResult, final Promise<String, String, String>.Deferred deferred) {

                if(Promise.State.REJECTED == promiseResult.getState()){
                    Toast.makeText(MainActivity.this,promiseResult.getResolvedValue(),Toast.LENGTH_SHORT).show();
                }else if(Promise.State.RESOLVED == promiseResult.getState()){
                    Toast.makeText(MainActivity.this,promiseResult.getResolvedValue(),Toast.LENGTH_SHORT).show();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0;i<10;i++){
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            deferred.notify(i+" second notify ");
                        }
                        deferred.resolve("gao");

                    }
                }).start();
            }
        }).onResolved(Scheduler.main(), new PromiseHandler<String>() {
            @Override
            public void handle(String param) {
                promiseButton.setText(param);
            }
        }).onNotified(Scheduler.main(), new PromiseHandler<String>() {
            @Override
            public void handle(String param) {
                promiseButton.setText(param);
            }
        }).onRejected(Scheduler.main(), new PromiseHandler<String>() {
            @Override
            public void handle(String param) {
                promiseButton.setText(param);
            }
        });
    }
}
