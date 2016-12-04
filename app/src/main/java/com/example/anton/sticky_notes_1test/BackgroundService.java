package com.example.anton.sticky_notes_1test;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Anton on 30-Nov-16.
 */

//Create a background process
//https://developer.android.com/training/run-background-service/create-service.html

public class BackgroundService extends Service {


    public int count = 0;
    public Timer t = new Timer();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Background process strarted", Toast.LENGTH_LONG).show();

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                count++;
                Log.e("Log number:"+String.valueOf(count), "The bgp is : ");
            }
        },0, 2000);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        t.cancel();
        super.onDestroy();
    }

}
