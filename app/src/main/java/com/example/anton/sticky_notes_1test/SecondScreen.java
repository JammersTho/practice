package com.example.anton.sticky_notes_1test;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SecondScreen extends AppCompatActivity {

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;
    private boolean permitionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);
        final Intent notificationHeadIntent = new Intent(getBaseContext(), FloatingHeadService.class);

        //stop the service for the floating head and return to the main activitys
        final Button buttonBack = (Button) findViewById(R.id.button);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopService(notificationHeadIntent);
                finish();
            }
        });

        //start the service for the floating head
        final Button buttonShowHead = (Button) findViewById(R.id.button2);
        buttonShowHead.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    checkPermission();
                    if (permitionGranted){
                        startService(notificationHeadIntent);
                    }
                }else{
                        startService(notificationHeadIntent);
                }
            }
        });

    }

    public void checkPermission() {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }else{
                permitionGranted = true;
            }
    }

    @TargetApi(23)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                checkPermission();
            }
            else
            {
                permitionGranted = true;
            }

        }
    }

}
