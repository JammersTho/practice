package com.example.anton.sticky_notes_1test;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.reflect.Field;

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

        Toast.makeText(this, "Background process strated", Toast.LENGTH_LONG).show();

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                count++;
                Log.e("Log number:"+String.valueOf(count), "The bgp is : " + getCurrentApp());
            }
        },0, 2000);

        return Service.START_STICKY;
    }

    public String getCurrentApp(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
        {
            currentApp = getCurrentAppOverLollipop();

            Log.e("Current App is ", currentApp);

            Log.e("ANDROID VERSION ", ">= LOLLIPOP");

        } else {
            getCurrentAppUnderLollipop();

            Log.e("ANDROID VERSION","< LOLLIPOP");
        }
        return currentApp;
    }

    private String currentApp = null;

    public String getCurrentAppOverLollipop()
    {
        final int PROCESS_STATE_TOP = 2;
        ActivityManager.RunningAppProcessInfo currentInfo = null;
        Field field = null;
        try {
            field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
        } catch (Exception ignored) {
        }
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo app : appList) {
            if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && app.importanceReasonCode == ActivityManager.RunningAppProcessInfo.REASON_UNKNOWN) {
                Integer state = null;
                try {
                    state = field.getInt(app);
                } catch (Exception e) {
                }
                if (state != null && state == PROCESS_STATE_TOP) {
                    currentInfo = app;
                    break;
                }
            }
        }
        return currentInfo.toString();
    }

    public void getCurrentAppUnderLollipop()
    {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        ActivityManager.RunningTaskInfo ar = tasks.get(0);
        currentApp = ar.topActivity.getPackageName();
    }

    @Override
    public void onDestroy() {
        t.cancel();
        super.onDestroy();
    }
}