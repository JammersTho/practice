package com.example.anton.sticky_notes_1test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SecondScreen extends AppCompatActivity {

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
                startService(notificationHeadIntent);
            }
        });


    }

}
