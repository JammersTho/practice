package com.example.anton.sticky_notes_1test;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSecondPage();
            }
        });

        final Button buttonStart = (Button) findViewById(R.id.acttionbutton);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Checks permission, if not available, requests permission
                //May need to repeat after getting permission!
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    fillStats();
                }
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

    private void fillStats()
    {
        if (hasPermission())
        {
            getStats();
        }
        else
        {
            requestPermission();
        }
    }

    private void requestPermission()
    {
        Toast.makeText(this, "Need to request permission", Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
    }

    @TargetApi(23)
    private void getStats()
    {
        UsageStatsManager lUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> lUsageStatsList = lUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis()- TimeUnit.DAYS.toMillis(1),System.currentTimeMillis()+ TimeUnit.DAYS.toMillis(1));

        //TODO change this to be readable, ugly AF atm
        TextView lTextView = (TextView) findViewById(R.id.someTextThing);

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


        lTextView.setText(lStringBuilder.toString());
    }

    @TargetApi(23)
    private boolean hasPermission()
    {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void gotoSecondPage()
    {
        Intent secondPage = new Intent(this, SecondScreen.class);
        startActivity(secondPage);
    }

    private void createBackgroundService()
    {
        startService(new Intent(getBaseContext(), BackgroundService.class));
    }

    private void stopBackgroudService()
    {
        String stoping = "Stopping background process";
        String stopped = "Background process is stopped";

        Log.e("Log: ", stoping);
        stopService(new Intent(getBaseContext(), BackgroundService.class));
        Log.e("Log: ", stopped);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
