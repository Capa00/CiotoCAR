package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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
                float Vsx,Vdx, beta;
                float alfa;
                int vel = 1;
                float currVel = 0, nextVel = 0;

                ProgressBar ruotaSX = Elementi.ruotaSX;
                ProgressBar ruotaDX = Elementi.ruotaDX;

                while(true){
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    currVel = nextVel;
                    nextVel = currVel + vel*Elementi.acceleration;

                    if(nextVel < ruotaSX.getMin())nextVel = ruotaSX.getMin();
                    else if(nextVel > ruotaSX.getMax()) nextVel = ruotaSX.getMax();

                    alfa = Elementi.angle;

                    if(alfa < 0) continue;

                    alfa -= Elementi.discretizzazione/2;
                    beta = (2.0f*(int)alfa)/(Elementi.discretizzazione-1);

                    Log.d("test","beta "+beta);
                    Vsx = beta*nextVel;
                    Vdx = -Vsx;

                    ruotaSX.setProgress((int)(nextVel + Vsx));
                    ruotaDX.setProgress((int)(nextVel + Vdx));



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
