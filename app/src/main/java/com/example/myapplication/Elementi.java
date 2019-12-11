package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.example.myapplication.MainActivity.TAG;

public class Elementi {
    static Button acc,dec,ret,turbo;
    static SeekBar ruotaSX, ruotaDX;
    static TextView marcia;
    static int discretizzazione = 20;
    static int velMax = 1000;
    static float acceleration = 0;
    static float angle;
    static SensorManager sensorManager;
    static Sensor giroscopio;
    static boolean retro = false;
    static String
            ipModulo = "192.168.43.71",
            marciaA = "> > > > > > >",
            marciaR = "< < < < < < <";
    private static float attrito = 0;//-0.2f;

    public static void update(Activity activity){
        // LISTENER BOTTONI
        marcia = activity.findViewById(R.id.textView);

        ret = activity.findViewById(R.id.retro);
        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retro = !retro;
                if(retro)marcia.setText(marciaR);
                else marcia.setText(marciaA);
            }
        });
        dec = activity.findViewById(R.id.brk);
        dec.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View vi, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    acceleration = -1.3f;
                    return false;
                }

                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    acceleration = attrito;
                    return false;
                }
                return true;
            }
        });

        acc = activity.findViewById(R.id.acc);
        acc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View vi, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    acceleration = 1;
                    return false;
                }

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    acceleration = attrito;
                    return false;

                }
                return true;
            }
        });

        ruotaSX = activity.findViewById(R.id.ruotaSX);
        ruotaSX.setFocusable(false);
        ruotaSX.setMin(0);
        ruotaSX.setMax(velMax);

        ruotaDX = activity.findViewById(R.id.ruotaDX);
        ruotaDX.setFocusable(false);
        ruotaDX.setMin(0);
        ruotaDX.setMax(velMax);

        //GIROSCOPIO
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        giroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        final SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor == giroscopio) {
                    if (event.values.length > 4) {
                        float[] truncatedRotationVector = new float[4];
                        System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                        update(truncatedRotationVector);
                    } else {
                        update(event.values);
                    }
                }
            }

            private void update(float[] vectors) {
                float[] rotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
                int worldAxisX = SensorManager.AXIS_X;
                int worldAxisZ = SensorManager.AXIS_Z;
                float[] adjustedRotationMatrix = new float[9];
                SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
                float[] orientation = new float[3];
                SensorManager.getOrientation(adjustedRotationMatrix, orientation);
                float pitch = orientation[1] * (float)(discretizzazione/Math.PI);
                angle = orientation[2] * (float)(discretizzazione/Math.PI);

                //((TextView)findViewById(R.id.pitch)).setText("Pitch: "+pitch);
                //((TextView)findViewById(R.id.roll)).setText("Roll: "+roll);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }
        };



        sensorManager.registerListener(sensorEventListener, giroscopio, SensorManager.SENSOR_DELAY_NORMAL);


    }

}
