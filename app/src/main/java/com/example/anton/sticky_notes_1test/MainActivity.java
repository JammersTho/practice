package com.example.anton.sticky_notes_1test;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "WHAT THE FUCK ARE YOU DOING HERE ?", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final Button buttonStart = (Button) findViewById(R.id.acttionbutton);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getRunningActivities();
//                getCurrentApp();
                createBackgroundService();
            }
        });

        final Button buttonStop = (Button) findViewById(R.id.stopbgp);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopBackgroudService();
            }
        });
    }

    private void createBackgroundService() {
        startService(new Intent(getBaseContext(), BackgroundService.class));
    }

    private void stopBackgroudService(){
        String stoping = "Stopping background process";
        String stopped = "Background process is stopped";

        Log.e("Log: ", stoping);
        stopService(new Intent(getBaseContext(), BackgroundService.class));
        Log.e("Log: ", stopped);
    }

    private void getRunningActivities() {

        TextView content = (TextView)findViewById(R.id.content);
        content.setMovementMethod(new ScrollingMovementMethod());

        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProccessInfo = am.getRunningAppProcesses();

        for (int i=0; i<runningAppProccessInfo.size(); i++){
            content.append(runningAppProccessInfo.get(i).processName + "\n");
        }

    }

    private void getCurrentApp(){
        TextView current_app = (TextView)findViewById(R.id.current_app);

        String currentApp;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // intentionally using string value as Context.USAGE_STATS_SERVICE was
            // strangely only added in API 22 (LOLLIPOP_MR1)
            @SuppressWarnings("WrongConstant")
            UsageStatsManager usm = (UsageStatsManager) getSystemService("usagestats");
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),
                            usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    current_app.append("\n\t" + currentApp);
                    Log.e("Current app >= Lollipop",currentApp);
                }
            }
        } else {
            //this method is getting the package name of the first process which is not
            //always the currently oped application
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am
                    .getRunningAppProcesses();
            currentApp = tasks.get(0).processName;

            //this is showing only our application name. It might work if
            //we run the application in background - not tested yet
            //currentApp = getApplicationContext().getPackageName();

            current_app.append("\n\t" + currentApp);
            Log.e("Current app <= Lollipop",currentApp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
