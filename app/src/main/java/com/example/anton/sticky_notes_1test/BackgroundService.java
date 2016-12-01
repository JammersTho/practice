package com.example.anton.sticky_notes_1test;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Anton on 30-Nov-16.
 */

//Create a background process
//https://developer.android.com/training/run-background-service/create-service.html

public class BackgroundService extends IntentService {

    public BackgroundService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
//        Gets data from the incoming Intent
//        String dataString = workIntent.getDataString();
//        ...
//        Do work here, based on the contents of dataString
//        ...
    }

}
