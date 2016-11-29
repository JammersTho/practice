package com.example.anton.sticky_notes_1test;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
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

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

        final Button button = (Button) findViewById(R.id.acttionbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getRunningActivities();
                getCurrentApp();
            }
        });
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
        current_app.append("\n\t" + getApplicationContext().getPackageName());
        Log.e("Current app",getApplicationContext().getPackageName());



        //Continuously print the current app package name in a period of 5 secods.

       /* Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.e("Current app",getApplicationContext().getPackageName());
            }
        },0, 5000); */

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
