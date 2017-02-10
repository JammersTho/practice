package com.example.anton.sticky_notes_1test;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FloatingHeadService extends Service {

    private WindowManager windowManager;
    private WindowManager.LayoutParams wmParams;

    private LinearLayout parentlayout;
    private LinearLayout.LayoutParams plParams;

    private LinearLayout floatingLayout;
    private ImageView floatingBubble;
    //false - left / true - right
    private boolean floatingHeadPosition = false;

    @Override
    public void onCreate() {
        floatingLayout = new LinearLayout(this);
        super.onCreate();
        defineFloatHead();
    }

    private void defineFloatHead() {

        //the parent layout that contains the floating head and the floating content
        // in horisontal order
        parentlayout = new LinearLayout(this);
        plParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        parentlayout.setOrientation(LinearLayout.HORIZONTAL);
        //parentlayout.setBackgroundColor(Color.parseColor("#f4f142"));
        parentlayout.setLayoutParams(plParams);


        //define overlay screen
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        wmParams.gravity = Gravity.TOP | Gravity.LEFT;
        wmParams.y = 300;
        floatingHeadPosition = true;


        //define floating bubble view
        floatingBubble = new ImageView(this);
        floatingBubble.setImageResource(R.drawable.android_head);
        floatingBubble.setMinimumHeight(50);
        floatingBubble.setMinimumWidth(50);

        floatingBubble.setOnTouchListener(new View.OnTouchListener() {
            int initialX;
            int initialY;
            //touch positions
            float initialTouchX;
            float initialTouchY;
            //actual moved distance params
            float distanceX;
            float distanceY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        initialX = wmParams.x;
                        initialY = wmParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        distanceX = event.getRawX() - initialTouchX;
                        distanceY = event.getRawY() - initialTouchY;

                        //stuck the floatie to left or right
                        possitionTheFloatieSide();

                        //check if the floatie is moved or clicked
                        if (distanceX >= -20 && distanceX <= 20 && distanceY >= -20 && distanceY <= 20) {
                            //check if the floatie is already shown
                            if (!floatingLayout.isShown()) {
                                showFloatingNoteContent();
                            }else if(floatingLayout.isShown()) {
                                parentlayout.removeView(floatingLayout);
                            }
                        }

                        return true;
                    case MotionEvent.ACTION_MOVE:

                        wmParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        wmParams.y = initialY + (int) (event.getRawY() - initialTouchY);

                        parentlayout.updateViewLayout(floatingBubble, plParams);
                        windowManager.updateViewLayout(parentlayout, wmParams);

                        return true;
                }
                return false;
            }
        });

        //attach the floating head to the parent layout
        parentlayout.addView(floatingBubble);

        //attach parent layout to the windows manager
        windowManager.addView(parentlayout, wmParams);
    }


    private void possitionTheFloatieSide() {
        //Get the midle of the screen
        Display display = windowManager.getDefaultDisplay();
        final Point screenSize = new Point();
        display.getSize(screenSize);
        int theMiddleOfTheScreen = screenSize.x / 2;

        if (wmParams.x <= theMiddleOfTheScreen) {
            wmParams.x = 0;
            floatingHeadPosition = false;
        } else if (wmParams.x > theMiddleOfTheScreen) {
            wmParams.x = screenSize.x;
            floatingHeadPosition = true;
        }

        parentlayout.updateViewLayout(floatingBubble, plParams);
        windowManager.updateViewLayout(parentlayout, wmParams);
    }


    private void showFloatingNoteContent() {

        //parentlayout.setBackgroundColor(Color.parseColor("#380788"));

        //define the floating content layout
        LinearLayout.LayoutParams llContextProperties = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.VERTICAL);
        llContextProperties.width = 200;
        llContextProperties.height = 200;
        floatingLayout.setLayoutParams(llContextProperties);
        floatingLayout.setBackgroundColor(Color.parseColor("#380707"));

        floatingLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                parentlayout.removeView(floatingLayout);
            }
        });

        TextView tv = new TextView(this);
        tv.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse purus felis, cursus non tempus id, aliquet aliquam leo. ");
        tv.append("Morbi pulvinar, quam vel sagittis pulvinar, mi sem porttitor mi, quis sollicitudin ex justo a lorem. ");
        tv.append("Quisque tristique, justo eu consectetur lacinia, tortor ipsum tempor nibh, sed consectetur magna lectus dignissim enim.");

        floatingLayout.addView(tv);

        //check floating head possition
        if( floatingHeadPosition == false) {
            parentlayout.addView(floatingLayout);
        }else if ( floatingHeadPosition == true){
            parentlayout.removeView(floatingBubble);
            parentlayout.addView(floatingLayout);
            parentlayout.addView(floatingBubble);
        }


        //Add the Floating RelativeLayout to the windows manager
        windowManager.updateViewLayout(parentlayout, wmParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingBubble.isShown())
            windowManager.removeView(parentlayout);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
