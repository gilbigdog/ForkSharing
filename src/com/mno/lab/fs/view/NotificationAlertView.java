package com.mno.lab.fs.view;

import com.mno.lab.fs.utils.Logs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class NotificationAlertView extends View {

    private static final int DEFAULT_COLOR = Color.argb(255, 109, 207, 246);
    private static final GradientDrawable.Orientation DEFAULT_ORIENTATION = GradientDrawable.Orientation.LEFT_RIGHT;

    private GradientDrawable alertDrawable;

    private boolean isNotiEnabled = false;

    private Handler handler = new Handler();
    private Thread uiUpdateThread = new Thread() {

        @Override
        public void run() {
            while (isNotiEnabled) {
                for (int i = 0; i < 255 * 2; i++) {
                    final float j;
                    if (i > 255) {
                        j = 255 * 2 - i;
                    } else {
                        j = i;
                    }

                    handler.post(new Runnable() {

                        public void run() {
                            alertDrawable.setAlpha((int) j);
                        }
                    });
                    // next will pause the thread for some time
                    try {
                        sleep(10);
                    }
                    catch (Exception e) {
                        break;
                    }
                }
            }
        }
    };

    public NotificationAlertView(Context context) {
        super(context);
        init();
    }

    public NotificationAlertView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NotificationAlertView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setAlertColor(DEFAULT_COLOR);
        alertDrawable.setAlpha(0);
    }

    public void enableNotificationAlert(boolean enable) {
        if (enable && !uiUpdateThread.isAlive()) {
            isNotiEnabled = true;
            uiUpdateThread.start();
            Logs.Log("enableNotificationAlert = " + enable);
        } else if (!enable && uiUpdateThread.isAlive()) {
            uiUpdateThread.interrupt();
            isNotiEnabled = false;
            setAlpha(0);
            Logs.Log("enableNotificationAlert = " + enable);
        }
    }

    public void setAlertColor(int color) {
        if (alertDrawable == null) {
            alertDrawable = new GradientDrawable(
                    DEFAULT_ORIENTATION,
                    new int[] { color, Color.TRANSPARENT });
        } else {
            alertDrawable.setColors(new int[] { color, Color.TRANSPARENT });
        }
        setBackground(alertDrawable);
    }

    public void setOrientation(GradientDrawable.Orientation orientation) {
        if (alertDrawable == null) {
            alertDrawable = new GradientDrawable(
                    orientation,
                    new int[] { DEFAULT_COLOR, Color.TRANSPARENT });
        } else {
            alertDrawable.setOrientation(orientation);
        }
    }
}