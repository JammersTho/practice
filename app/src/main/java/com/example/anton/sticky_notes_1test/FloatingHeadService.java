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
    private Point screenSize;
    private final int TOP_SCREEN_PADDING = 100;
    private final int[] FLOATING_HEAD_SIZE = {50,50};
    private final int[] NOTE_EXTRAS_ICONS = {30,30};

    @Override
    public void onCreate() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        floatingLayout = new LinearLayout(this);

        Display display = windowManager.getDefaultDisplay();
        screenSize = new Point();
        display.getSize(screenSize);

        super.onCreate();
        defineFloatHead();
    }

    private void defineFloatHead() {

        //the parent layout that contains the floating head and the floating content
        // in horisontal order
        parentlayout = new LinearLayout(this);
        plParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        parentlayout.setOrientation(LinearLayout.HORIZONTAL);
        //parentlayout.setBackgroundColor(Color.parseColor("#f4f142"));
        parentlayout.setLayoutParams(plParams);

        //define overlay screen
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
        floatingBubble.setMinimumHeight(FLOATING_HEAD_SIZE[0]);
        floatingBubble.setMinimumWidth(FLOATING_HEAD_SIZE[1]);

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
                        if(floatingLayout.isShown()) {
                            floatingLayout.removeAllViews();
                            parentlayout.removeView(floatingLayout);
                        }

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
                            if(floatingLayout.isShown()) {
                                parentlayout.removeAllViews();
                            }else if (!floatingLayout.isShown()) {
                                    showFloatingNoteContent();
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

        int contentHeight = Integer.valueOf(screenSize.y) - TOP_SCREEN_PADDING;
        int contentWidth = Integer.valueOf(screenSize.x) - FLOATING_HEAD_SIZE[0];

        //define the floating content layout
        LinearLayout.LayoutParams llContextProperties = new LinearLayout.LayoutParams(
                contentWidth,
                contentHeight);
        floatingLayout.setLayoutParams(llContextProperties);
        floatingLayout.setOrientation(LinearLayout.VERTICAL);
        floatingLayout.setBackgroundColor(Color.parseColor("#E0E0E0"));

        floatingLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                floatingLayout.removeAllViews();
                parentlayout.removeView(floatingLayout);
            }
        });

        createFloatingContent();

        //update floating head new possition because it's pushed
        //to the top from the floating content. 50 is the size of the the floatie
        //we are removing it so we can touch it in the middle
        wmParams.y = TOP_SCREEN_PADDING - FLOATING_HEAD_SIZE[0];

        //check floating head possition
        if( floatingHeadPosition == false) {
            parentlayout.addView(floatingLayout);
        }else if ( floatingHeadPosition == true){
            parentlayout.removeAllViews();
            parentlayout.addView(floatingLayout);
            parentlayout.addView(floatingBubble);
        }


        //Add the Floating RelativeLayout to the windows manager
        windowManager.updateViewLayout(parentlayout, wmParams);
    }

    private void createFloatingContent(){

        TextView title = new TextView(this);
        title.setText("Example note name");
        title.setPadding(20,20,0,20);
        title.setBackgroundColor(Color.parseColor("#3F51B5"));
        title.setTextColor(Color.parseColor("#ffffff"));
        title.setTextSize(20);

        LinearLayout extrasL = createExtrasView();

        TextView tv2 = new TextView(this);
        tv2.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse purus felis, cursus non tempus id, aliquet aliquam leo. ");
        tv2.append("Morbi pulvinar, quam vel sagittis pulvinar, mi sem porttitor mi, quis sollicitudin ex justo a lorem. ");
        tv2.append("Quisque tristique, justo eu consectetur lacinia, tortor ipsum tempor nibh, sed consectetur magna lectus dignissim enim.");
        tv2.setBackgroundColor(Color.parseColor("#4286f4"));


        floatingLayout.addView(title);
        floatingLayout.addView(extrasL);
        floatingLayout.addView(tv2);

    }

    private LinearLayout createExtrasView(){
        LinearLayout extrasL = new LinearLayout(this);
        LinearLayout.LayoutParams extrasParams = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                50
        );
        extrasL.setOrientation(LinearLayout.HORIZONTAL);
        extrasL.setGravity(Gravity.CENTER_HORIZONTAL);
        extrasL.setLayoutParams(extrasParams);

        ImageView backup = new ImageView(this);
        backup.setImageResource(R.drawable.cloud);
        backup.setMinimumHeight(NOTE_EXTRAS_ICONS[0]);
        backup.setMinimumHeight(NOTE_EXTRAS_ICONS[1]);
        backup.setAdjustViewBounds(true);
        backup.setPadding(10,0,10,0);

        ImageView location = new ImageView(this);
        location.setImageResource(R.drawable.location);
        location.setMinimumHeight(NOTE_EXTRAS_ICONS[0]);
        location.setMinimumWidth(NOTE_EXTRAS_ICONS[1]);
        location.setPadding(10,0,10,0);
        location.setAdjustViewBounds(true);

        ImageView app_sticky = new ImageView(this);
        app_sticky.setImageResource(R.drawable.app_sticky);
        app_sticky.setMinimumHeight(NOTE_EXTRAS_ICONS[0]);
        app_sticky.setMinimumWidth(NOTE_EXTRAS_ICONS[1]);
        app_sticky.setAdjustViewBounds(true);
        app_sticky.setPadding(10,0,10,0);

        ImageView reminder = new ImageView(this);
        reminder.setImageResource(R.drawable.reminder);
        reminder.setMinimumHeight(NOTE_EXTRAS_ICONS[0]);
        reminder.setMinimumWidth(NOTE_EXTRAS_ICONS[1]);
        reminder.setAdjustViewBounds(true);
        reminder.setPadding(10,0,10,0);

        extrasL.addView(backup);
        extrasL.addView(location);
        extrasL.addView(app_sticky);
        extrasL.addView(reminder);

        return extrasL;
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
