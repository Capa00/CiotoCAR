package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private URL url = null;
    private HttpURLConnection urlConnection = null;
    private DataOutputStream out = null;
    private boolean connected = false;

    static String TAG = "MainActivity";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Elementi.update(this);
        Log.d(TAG, "conn: init....");

        new Thread(new Runnable() {
            @Override
            public void run() {
                float Vsx,Vdx, beta;
                float alfa;
                int vel = 50, maxVel = Elementi.velMax;
                float currVel = 0, nextVel = 0;
                float prevAlfa = Elementi.angle;

                ProgressBar ruotaSX = Elementi.ruotaSX;
                ProgressBar ruotaDX = Elementi.ruotaDX;

                while(true){
                    alfa = Elementi.angle;
                    if(alfa < 0) alfa = prevAlfa;
                    prevAlfa = alfa;
                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    currVel = nextVel;
                    nextVel = currVel + vel*Elementi.acceleration;
                    if(nextVel > maxVel)nextVel = maxVel;
                    else if(nextVel < 0)nextVel = 0;



                    alfa -= Elementi.discretizzazione/2;
                    beta = (2.0f*(int)alfa)/(Elementi.discretizzazione);

                    Vsx = beta*nextVel;
                    Vdx = -Vsx;

                    if(nextVel + Vsx < ruotaSX.getMin())Vsx = nextVel;
                    else if(nextVel + Vsx > ruotaSX.getMax()) Vsx = maxVel-nextVel;

                    if(nextVel + Vdx < ruotaDX.getMin())Vdx = nextVel;
                    else if(nextVel + Vdx > ruotaDX.getMax()) Vdx = maxVel-nextVel;

                    ruotaSX.setProgress((int)(nextVel + Vsx));
                    ruotaDX.setProgress((int)(nextVel + Vdx));


                    RequestPackage rq = new RequestPackage();
                    rq.setUrl("http://"+ Elementi.ipModulo+"/forward");
                    rq.setParam("x",""+ruotaSX.getProgress());
                    rq.setParam("y",""+ruotaDX.getProgress());
                    getData(rq);
                }
            }
        }).start();
    }

    public static String getData(RequestPackage requestPackage) {

        BufferedReader reader = null;
        String uri = requestPackage.getUrl();

        if (requestPackage.getMethod().equals("GET")) {
            uri += "?" + requestPackage.getEncodedParams();
            //As mentioned before, this only executes if the request method has been
            //set to GET
        }

        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(requestPackage.getMethod());

            if (requestPackage.getMethod().equals("POST")) {
                con.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(requestPackage.getEncodedParams());
                writer.flush();
            }

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    public class RequestPackage {
        private String url;
        private String method = "GET"; //method is set to GET by default
        private Map<String, String> params = new HashMap<>();//This will hold any values
        //that the server may require
        // e.g. product_id
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public void setParams(Map<String, String> params) {
            this.params = params;
        }

        public void setParam(String key, String value) {
            params.put(key, value); //adds a single value to the params member variable
        }

        //The method below is only called if the request method has been set to GET
        //GET requests sends data in the url and it has to be encoded correctly in order
        //for the server to understand the request. This method encodes the data in the
        //params variable so that the server can understand the request

        public String getEncodedParams() {
            StringBuilder sb = new StringBuilder();
            for (String key : params.keySet()) {
                String value = null;
                try {
                    value = URLEncoder.encode(params.get(key), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(key + "=" + value);
            }
            return sb.toString();
        }
    }


}
