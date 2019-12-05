package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
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

public class Elementi {
    static Button acc,dec;
    static SeekBar ruotaSX, ruotaDX;

    static float acceleration = 0;
    static float angle;
    static SensorManager sensorManager;
    static Sensor giroscopio;
    private static float attrito = 0;//-0.15f;

    public static void update(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // LISTENER BOTTONI
        dec = activity.findViewById(R.id.dec);
        dec.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View vi, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    acceleration = 1;
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
                    acceleration = -1.3f;
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
        ruotaSX.setMin(0);
        ruotaSX.setMax(100);

        ruotaDX = activity.findViewById(R.id.ruotaDX);
        ruotaDX.setMin(0);
        ruotaDX.setMax(100);

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
                float pitch = orientation[1] * (float)(20/Math.PI);
                float roll = orientation[2] * (float)(20/Math.PI);

                Log.i("pitch",""+((roll)));
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