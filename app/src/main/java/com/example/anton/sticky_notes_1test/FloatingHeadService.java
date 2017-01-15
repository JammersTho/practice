package com.example.anton.sticky_notes_1test;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FloatingHeadService extends Service {

    private WindowManager windowManager;
    private ImageView floatingBubble;
    private RelativeLayout floatingLayout;
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
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

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
                        if(floatingLayout == null || !floatingLayout.isShown())
                            showFloatingNoteContent();
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

    private void showFloatingNoteContent(){

        //create relative layout
        floatingLayout = new RelativeLayout(this);
        floatingLayout.setClickable(true);

        //add parameters of this relative layout
        WindowManager.LayoutParams rlp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        rlp.gravity = Gravity.TOP;


        //set backgroujnd color to the layout
        floatingLayout.setBackgroundColor(Color.parseColor("#380707"));

        floatingLayout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                windowManager.removeView(floatingLayout);
            }
        });

        TextView tv = new TextView(this);
        tv.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse purus felis, cursus non tempus id, aliquet aliquam leo. ");
        tv.append("Morbi pulvinar, quam vel sagittis pulvinar, mi sem porttitor mi, quis sollicitudin ex justo a lorem. ");
        tv.append("Quisque tristique, justo eu consectetur lacinia, tortor ipsum tempor nibh, sed consectetur magna lectus dignissim enim.");

        floatingLayout.addView(tv);

        //Add the Floating RelativeLayout to the windows manager
        windowManager.addView(floatingLayout, rlp);
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
