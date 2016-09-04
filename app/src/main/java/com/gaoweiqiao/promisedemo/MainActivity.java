package com.gaoweiqiao.promisedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.gaoweiqiao.promise.Promise;
import com.gaoweiqiao.promise.PromiseHandler;
import com.gaoweiqiao.promise.PromiseResult;
import com.gaoweiqiao.promise.schduler.PromiseExecuteHandler;
import com.gaoweiqiao.promise.schduler.Scheduler;
import com.gaoweiqiao.promise.schduler.SchedulerFactory;

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
       getPromise("gao")
               .<Integer,String,String>then(new PromiseHandler<String, String, String>() {
                   @Override
                   public void onResolved(String param) {
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
               .then(new PromiseHandler<Integer, String, String>() {
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
    private Promise<String,String,String> getPromise(final String tag){
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
}
