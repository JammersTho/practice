package com.example.anton.sticky_notes_1test;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

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
                Log.e("Log number:"+String.valueOf(count), "The bgp is : " + getCurrentApp());
            }
        },0, 2000);

        return Service.START_STICKY;
    }

    public String getCurrentApp(){
        String currentApp = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // intentionally using string value as Context.USAGE_STATS_SERVICE was
            // strangely only added in API 22 (LOLLIPOP_MR1)
            @SuppressWarnings("WrongConstant")
            UsageStatsManager usm = (UsageStatsManager) getSystemService("usagestats");
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    Log.e("ANDROID VERSION ", ">= LOLLIPOP");
                }
            }

        } else {
            //this method is getting the package name of the current opened application
            
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
            ActivityManager.RunningTaskInfo ar = tasks.get(0);
            currentApp = ar.topActivity.getPackageName();

            Log.e("ANDROID VERSION","<= LOLLIPOP");
        }

        return currentApp;
    }

    @Override
    public void onDestroy() {
        t.cancel();
        super.onDestroy();
    }



}
