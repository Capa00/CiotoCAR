package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    Handler mHandler;
    Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mRunnable=new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
            }
        };

        mHandler=new Handler();
        mHandler.postDelayed(mRunnable,3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mRunnable!=null && mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
    }
}
