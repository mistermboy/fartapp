package com.example.uo252406.fartapp;

import android.app.Dialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorService;
    private MediaPlayer mp;

    private int delay = 0;

    private float rotation;
    private float position;
    private float prox;

    private boolean start;
    private boolean pocket;

    private int countProximity;
    private int counterFart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorService =	(SensorManager)	getSystemService(SENSOR_SERVICE);

        start = false;
        pocket = false;
        countProximity = 0;
        counterFart = 1;

        //Start de info dialog
        final Dialog info = new Dialog(this);
        info.setContentView(R.layout.info_dialog);
        info.show();

        Button button = info.findViewById(R.id.btnAceptInfoDialog);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start = true;
                info.dismiss();
            }
        });


    }

    @Override
    public void onSensorChanged(SensorEvent event) {


       if(start) {

           checkPosition();

            //Take de accelerometer sensor values
           if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
               rotation = event.values[1];
               position = event.values[2];
           }
           //Take de proximity sensor value
           if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
               prox = event.values[0];
           }

           if (pocket) {
               //If the user do the correct action and the delay is over -> fart()
               if (rotation > -5 && prox == 0.0 && delay == 0) {
                   fart();
               }
               //Decrements the delay
               if (delay > 0)
                   delay--;
           }


       }

    }

    /**
     * Check if the position of the phone is the right one
     */
    private void checkPosition() {

        if (position > -1 && position < 1 && rotation < -8 && rotation > -10 && prox == 0.0) {
            countProximity++;
        }else
            countProximity = 0;


        if (countProximity >= 8) {
            pocket = true;
        }

    }


    /**
     * Choose an audio each time is called and plays it.
     * Set a delay between audio and audio.
     */
    private void fart(){

        int rawID = getApplicationContext().getResources().getIdentifier("fart"+counterFart,"raw",getApplicationContext().getPackageName());
        mp = MediaPlayer.create(getApplicationContext(),rawID);

        mp.stop();
        getReady();
        mp.start();

        delay = 25;

        if(counterFart < 11)
            counterFart++;
        else
            counterFart = 1;

    }

    /**
     * Prepares the MediaPlayer
     */
    private	void getReady()
    {
        try {
            mp.prepare();
        }
        catch	(Exception	e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume(){
        super.onResume();

        sensorService.registerListener(this,	sensorService.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER),	sensorService.SENSOR_DELAY_UI);

        sensorService.registerListener(this,	sensorService.getDefaultSensor(
                Sensor.TYPE_PROXIMITY),	sensorService.SENSOR_DELAY_UI);

    }


    @Override
    protected void onPause(){
        super.onPause();
        sensorService.unregisterListener(this);

    }

}
