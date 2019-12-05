package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
    double x = 0;
    double v = 15;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("provsdfsfdf");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Elementi.update(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int vel = 1;
                float currVel = 0, nextVel = 0;

                ProgressBar ruotaSX = Elementi.ruotaSX;
                ProgressBar ruotaDX = Elementi.ruotaDX;

                while(true){
                    currVel = nextVel;
                    nextVel = currVel + vel*Elementi.acceleration;

                    if(nextVel < ruotaSX.getMin())nextVel = ruotaSX.getMin();
                    else if(nextVel > ruotaSX.getMax()) nextVel = ruotaSX.getMax();

                    ruotaSX.setProgress((int)nextVel);
                    ruotaDX.setProgress((int)nextVel);

                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
        //button.setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View view) {
        //        x+=0.5;
        //        v+=x;
        //        if(v>50){
        //            v=50;
        //        }
        //        progressBar.setProgress((int)v);
        //    }
        //});



    }


}
