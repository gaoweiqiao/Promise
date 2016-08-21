package com.gaoweiqiao.promisedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.gaoweiqiao.promise.Promise;
import com.gaoweiqiao.promise.PromiseHandler;
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
        Promise.newPromise(Scheduler.io(), new PromiseExecuteHandler<Integer,String,String>() {
            @Override
            public void execute(Promise.State previousState, final Promise<Integer,String,String>.Deferred deferred) {
                for(int i=0;i<10;i++){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    deferred.notify();
                }
                deferred.resolve(1);
            }
        }).onResolved(Scheduler.main(), new PromiseHandler<Integer>() {
            @Override
            public void handle(Integer param) {
                promiseButton.setText(param);
            }
        }).onRejected(Scheduler.main(), new PromiseHandler<String>() {
            @Override
            public void handle(String param) {
                promiseButton.setText(param);
            }
        }).onNotified(Scheduler.main(), new PromiseHandler<String>() {
            @Override
            public void handle(String param) {
                promiseButton.setText(param);
            }
        }).next(Scheduler.main(), new PromiseExecuteHandler<String,String,String>() {
            @Override
            public void execute(Promise.State previousState, final Promise<String,String,String>.Deferred deferred) {
                if(Promise.State.REJECTED == previousState){
                    Toast.makeText(MainActivity.this,"rejected",Toast.LENGTH_SHORT).show();
                }else if(Promise.State.RESOLVED == previousState){
                    Toast.makeText(MainActivity.this,"resolved",Toast.LENGTH_SHORT).show();
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
                            deferred.notify(i+"");
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
