package com.gaoweiqiao.promisedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.gaoweiqiao.promise.Promise;
import com.gaoweiqiao.promise.PromiseHandler;

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
                   public void onResolved(final String param) {
                       Log.d("gao","resolve param is "+param);

                       new Thread(new Runnable() {
                           @Override
                           public void run() {
                               try {
                                   Thread.sleep(10000);
                               } catch (InterruptedException e) {
                                   e.printStackTrace();
                               }
                               resolve(1);
                           }
                       }).start();

                   }

                   @Override
                   public void onRejected(String param) {
                       Log.d("gao","reject param is "+param);
                       resolve(2);
                   }

                   @Override
                   public void onNotified(String param) {
                       Log.d("gao","notify param is "+param);
                   }
               })
               .then(new PromiseHandler<Integer, String, String,Void,Void,Void>() {
                   @Override
                   public void onResolved(Integer param) {
                       Log.d("gao","resolve param is "+param);
                   }

                   @Override
                   public void onRejected(String param) {
                       Log.d("gao","resolve param is "+param);
                   }

                   @Override
                   public void onNotified(String param) {
                       Log.d("gao","resolve param is "+param);
                   }
               });
    }
    @OnClick(R.id.promise_all)
    protected void test_promise_all(){
        List<Promise> promiseList = new ArrayList<>();
        promiseList.add(getResolvedPromise("gao"));
        promiseList.add(getResolvedPromise("gao"));
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
