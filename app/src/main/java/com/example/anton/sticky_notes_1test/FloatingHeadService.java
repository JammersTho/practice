package com.example.anton.sticky_notes_1test;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FloatingHeadService extends Service {

    private WindowManager windowManager;
    private ImageView floatingBubble;
    private LinearLayout floatingLayout;
    private WindowManager.LayoutParams params;


    @Override
    public void onCreate() {
        super.onCreate();
            defineFloatHead();
            onTouchAction();

    }

    private void defineFloatHead() {

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        floatingBubble = new ImageView(this);
        floatingBubble.setImageResource(R.drawable.android_head);
        floatingBubble.setMinimumHeight(50);
        floatingBubble.setMinimumWidth(50);

        params= new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        //initial head position
        params.x = 0;
        params.y = 300;
        windowManager.addView(floatingBubble, params);
    }

    private void onTouchAction() {

        //OnTouchListener
        floatingBubble.setOnTouchListener(new View.OnTouchListener() {
            //buble position
            private int initialX;
            private int initialY;
            //touch positions
            private float initialTouchX;
            private float initialTouchY;
            //actual moved distance params
            private float distanceX;
            private float distanceY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        distanceX = event.getRawX() - initialTouchX;
                        distanceY = event.getRawY() - initialTouchY;

                        //check if the floatie is moved or clicked
                        if(distanceX >= -20 && distanceX <= 20 && distanceY >= -20 && distanceY <= 20) {
                            //check if the floatie is already shown
                            if (floatingLayout == null || !floatingLayout.isShown()) {
                                showFloatingNoteContent();
                            }
                        }

                        //stuck the floatie to left or right
                        possitionTheFloatieSide();

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        windowManager.updateViewLayout(floatingBubble, params);

                        return true;
                }
                return false;
            }
        });
    }

    private void possitionTheFloatieSide() {
        //Get the midle of the screen
        Display display = windowManager.getDefaultDisplay();
        final Point screenSize = new Point();
        display.getSize(screenSize);
        int theMiddleOfTheScreen = screenSize.x / 2;

        if(params.x <= theMiddleOfTheScreen){
            params.x = 0;
        }
        else if(params.x > theMiddleOfTheScreen){
            params.x = screenSize.x;
        }

        windowManager.updateViewLayout(floatingBubble, params);
    }


    private void showFloatingNoteContent(){

        //create relative layout
        floatingLayout = new LinearLayout(this);
        floatingLayout.setClickable(true);

        //add parameters of this relative layout
        WindowManager.LayoutParams contentLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        contentLayoutParams.gravity = Gravity.TOP;


        //set backgroujnd color to the layout
        floatingLayout.setBackgroundColor(Color.parseColor("#380707"));

        floatingLayout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                windowManager.removeView(floatingLayout);
            }
        });


        LinearLayout.LayoutParams textViewProperties = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        TextView tv = new TextView(this);
        tv.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse purus felis, cursus non tempus id, aliquet aliquam leo. ");
        tv.append("Morbi pulvinar, quam vel sagittis pulvinar, mi sem porttitor mi, quis sollicitudin ex justo a lorem. ");
        tv.append("Quisque tristique, justo eu consectetur lacinia, tortor ipsum tempor nibh, sed consectetur magna lectus dignissim enim.");
        tv.setLayoutParams(textViewProperties);

//        Button btn = new Button(this);
//        btn.setText("Don't touch me");



        floatingLayout.addView(tv);
//        floatingLayout.addView(btn);

        //Add the Floating RelativeLayout to the windows manager
        windowManager.addView(floatingLayout, contentLayoutParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingBubble.isShown())
            windowManager.removeView(floatingBubble);
        if(floatingLayout != null && floatingLayout.isShown())
            windowManager.removeView(floatingLayout);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
