package com.example.anton.sticky_notes_1test;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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

        Toast.makeText(this, "Background process started", Toast.LENGTH_LONG).show();

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

    @TargetApi(android.os.Build.VERSION_CODES.LOLLIPOP)
    public String getCurrentAppOverLollipop()
    {
        printUsageStats(getUsageStatsList(this));
        getStats();
        return getUsageStatsList(this).toString();
    }

    @TargetApi(23)
    private void getStats()
    {
        UsageStatsManager lUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> lUsageStatsList = lUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis()- TimeUnit.DAYS.toMillis(1),System.currentTimeMillis()+ TimeUnit.DAYS.toMillis(1));

        //TODO change this to be readable, ugly AF atm
//        TextView lTextView = (TextView) findViewById(R.id.someTextThing);
// Cant access Layout Views from service???

        //TODO Use a map, then sort by the lastTimeUsed
        HashMap<String, Long> appList = new HashMap<String, Long>();

        StringBuilder lStringBuilder = new StringBuilder();

        for (UsageStats lUsageStats:lUsageStatsList)
        {
            appList.put(lUsageStats.getPackageName(), lUsageStats.getLastTimeUsed());

//            lStringBuilder.append(lUsageStats.getPackageName());
//            lStringBuilder.append(" - ");
//            lStringBuilder.append(lUsageStats.getLastTimeUsed());
//            lStringBuilder.append("\r\n");
        }



        List<Map.Entry<String, Long>> list = new LinkedList<>( appList.entrySet() );

        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<String, Long> result = new LinkedHashMap<>();

        for (Map.Entry<String, Long> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }

        // Just checking whats in here
        int i = result.size();


        for (String name:result.keySet())
        {
            StringBuilder resultString = new StringBuilder();

            resultString.append(name.toString());
            resultString.append(" - ");
            resultString.append(result.get(name).toString());
            resultString.append("\r\n");
        }
        //TODO get the most recent one from the list!

        //Prints running apps sorted by lasttime used
        for (String name: result.keySet()){
            String key = name.toString();
            String value = result.get(name).toString();
            Log.d(TAG, key + " " + value);
        }
//        lTextView.setText(lStringBuilder.toString());
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    public static final String TAG = BackgroundService.class.getSimpleName();

    @TargetApi(android.os.Build.VERSION_CODES.LOLLIPOP)
    public static List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime) );
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        return usageStatsList;
    }
    @TargetApi(android.os.Build.VERSION_CODES.LOLLIPOP)
    public static void printUsageStats(List<UsageStats> usageStatsList){
        for (UsageStats u : usageStatsList)
        {
            Log.d(TAG, "Pkg: " + u.getPackageName() +  "\t" + "ForegroundTime: "
                    + u.getTotalTimeInForeground()) ;
        }
    }

    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
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