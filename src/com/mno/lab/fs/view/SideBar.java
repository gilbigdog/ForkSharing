package com.mno.lab.fs.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mno.lab.fs.utils.Logs;

public class SideBar {

    /**
     * Close Animation duration
     */
    private static final long CLOSE_ANIMATION_DURATION = 300L;

    /**
     * it indicates the width of {@link #alertView}
     */
    private static final int DEFAULT_SENSITIVITY = 40;

    /**
     * total scroll time
     */
    final long TOTAL_SCROLL_TIME = Long.MAX_VALUE;

    private Context mContext;
    private WindowManager mWm;

    /**
     * This layout will contain all views for sidebar class
     */
    private LinearLayout barGroup;
    private LayoutParams barGroupParam;

    /**
     * Effect to show alert also obtain all touch events
     */
    private NotificationAlertView alertView;
    private LayoutParams alertViewParams;

    /**
     * List View
     */
    private ListView barListView;

    /**
     * Side Helper Adapter
     */
    private SideHelperAdapter mSideHelperAdapter;

    /**
     * Animation to close {@link #barListView}
     */
    private TranslateAnimation closeAnimation;

    /**
     * Adapter for this class
     */
    private SideHelperView barHelperView;
    private LayoutParams barHelperViewParams;

    /**
     * Screen Size
     */
    private Point mScreenSize = new Point();

    /**
     * the size of an item in {@link SideHelperView}
     */
    private Point mChildSize;

    final private Handler mHandler = new Handler();
    private Runnable hideListThread = new Runnable() {

        @Override
        public void run() {
            barListView.clearAnimation();
            hideBarView(true);
            isClosing = false;

            Logs.Log("hideListThread");
        }
    };

    /**
     * Whether side list view is fully closed or not
     */
    private boolean isClosing = false;

    /**
     * Whether side list view is fully expanded or not
     */
    private boolean isOpened = false;

    /**
     * Previous scroll state
     */
    private ScrollState prevScrollState = ScrollState.NONE;

    private static enum ScrollState {
        UP,
        UP_2X,
        NONE,
        DOWN,
        DOWN_2X
    };

    /**
     * to scroll instead of using while inside of thread
     */
    private CountDownTimer cdt;

    /**
     * Scroll update Thread
     */
    private Runnable scrollThread = new Runnable() {

        @Override
        public void run() {
            cdt.start();
        }
    };

    private DataSetObserver mAdapterObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            Logs.Log("SideBar::mAdapterObserver::onChanged()");
            // wrap_content not work for ListView
            // Calculate width of child view and set the value as
            // width of Listview
            if (mChildSize == null) {
                mChildSize = GetSizeOfChildView(mContext, mSideHelperAdapter);
                Logs.Log("mChildSize width = " + mChildSize.x + " height = " + mChildSize.y);
            }
        }

        @Override
        public void onInvalidated() {
            Logs.Log("SideBar::mAdapterObserver::onInvalidated()");
            onChanged();
        }
    };

    /**
     * Current Touch Point
     */
    private PointF currTouchPoint = new PointF();

    private OnScrollListener listViewSCrollListener = new OnScrollListener() {

        private int prevFirstVisibleItem = 0;

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (prevFirstVisibleItem != firstVisibleItem) {
                prevFirstVisibleItem = firstVisibleItem;
                moveHelperViewY(currTouchPoint.y);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    };

    /**
     * For {@link #alertView} to dispatch all touch events created on this view
     * to underneath view(actual list view)
     */
    private OnTouchListener sideTouch = new OnTouchListener() {

        private boolean isDown = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mChildSize == null) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isClosing) {
                        Logs.Log("ACTION_DOWN but Closing. Clear Animation");
                        mHandler.removeCallbacks(hideListThread);
                        isClosing = false;
                        barListView.clearAnimation();
                    }
                    hideBarView(false);
                    setPrevCoordinate(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!isDown) {
                        Logs.Log("Unexpect Input");
                        return false;
                    }
                    moveBarX(event.getX() - currTouchPoint.x);
                    smoothScroll(event.getY(), 20, 20);
                    moveHelperViewY(event.getY());
                    setPrevCoordinate(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    barHelperView.setEnabled(false);
                    closeBarView();
                    if (mSideHelperAdapter != null) {
                        long currentItemPos = barListView.pointToRowId(mChildSize.x / 2, (int) currTouchPoint.y
                                - mChildSize.y / 2);
                        if (currentItemPos >= 0) {
                            mSideHelperAdapter.onSelectListener((int) currentItemPos);
                        }
                    }
                default:
                    barListView.removeCallbacks(scrollThread);
                    isOpened = false;
                    isDown = false;
                    setPrevCoordinate(-1, -1);
                    break;
            }
            return true;
        }

        private void setPrevCoordinate(float x, float y) {
            isDown = true;
            currTouchPoint.x = x;
            currTouchPoint.y = y;
        }
    };

    public SideBar(Context context, WindowManager windowManager) {
        mContext = context;
        mWm = windowManager;

        mWm.getDefaultDisplay().getSize(mScreenSize);
        mScreenSize.y -= Math.ceil(25.0f * mContext.getResources().getDisplayMetrics().density);

        addAlertView();
        addListView();
    }

    private void addAlertView() {
        alertView = new NotificationAlertView(mContext);
        alertView.setOnTouchListener(sideTouch);

        alertViewParams = new WindowManager.LayoutParams();
        alertViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        alertViewParams.format = PixelFormat.TRANSLUCENT;
        alertViewParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        alertViewParams.width = DEFAULT_SENSITIVITY;
        alertViewParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        alertViewParams.gravity = Gravity.AXIS_SPECIFIED |
                Gravity.AXIS_PULL_BEFORE |
                Gravity.TOP;

        mWm.addView(alertView, alertViewParams);
    }

    private void addListView() {
        barGroup = new LinearLayout(mContext);
        barGroupParam = new WindowManager.LayoutParams();
        barGroupParam.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        barGroupParam.format = PixelFormat.RGBA_8888;
        barGroupParam.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        barGroupParam.gravity = Gravity.AXIS_SPECIFIED | Gravity.AXIS_PULL_BEFORE | Gravity.TOP;
        barGroupParam.height = WindowManager.LayoutParams.MATCH_PARENT;
        barGroupParam.width = WindowManager.LayoutParams.MATCH_PARENT;

        barListView = new ListView(mContext);
        barListView.setFocusable(false);
        barListView.setOnScrollListener(listViewSCrollListener);
        WindowManager.LayoutParams barListViewParams = new WindowManager.LayoutParams();
        barListViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        barListViewParams.format = PixelFormat.RGBA_8888;
        barListViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        barListViewParams.gravity = Gravity.AXIS_SPECIFIED | Gravity.AXIS_PULL_BEFORE | Gravity.TOP;
        barListViewParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        barListViewParams.width = 0;
        barGroup.addView(barListView, barListViewParams);

        barHelperView = new SideHelperView(mContext);
        barHelperView.setFocusable(false);
        barHelperViewParams = new WindowManager.LayoutParams();
        barHelperViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        barHelperViewParams.format = PixelFormat.RGBA_8888;
        barHelperViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        barHelperViewParams.gravity = Gravity.AXIS_SPECIFIED | Gravity.AXIS_PULL_BEFORE | Gravity.TOP;
        barHelperViewParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        barHelperViewParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        barGroup.addView(barHelperView, barHelperViewParams);

        mWm.addView(barGroup, barGroupParam);
    }

    private void moveHelperViewY(float y) {
        if (y - mChildSize.y >= 0) {
            barHelperView.setY(y - mChildSize.y);
        }
        long currentItemPos = barListView.pointToPosition(mChildSize.x / 2, (int) y - mChildSize.y / 2);
        barHelperView.setPosition(currentItemPos, false);
    }

    /**
     * Hide or show {@link #barListView} NOT {@link #alertView}
     * 
     * @param hide
     *            boolean
     */
    public void hideBarView(boolean hide) {
        Logs.Log("hideBarView = " + hide);
        LinearLayout.LayoutParams pl;
        if (hide) {
            barListView.removeCallbacks(scrollThread);
            pl = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        } else {
            pl = new LinearLayout.LayoutParams(mChildSize.x, LinearLayout.LayoutParams.MATCH_PARENT);
            barListView.setTranslationX(-mChildSize.x);
        }
        barListView.setLayoutParams(pl);
    }

    /**
     * updates the width of {@link #alertView}
     */
    public void updateSensitivity(int sensitivity) {
        alertViewParams.width = sensitivity;
        mWm.updateViewLayout(alertView, alertViewParams);
    }

    /**
     * Get the width of {@link #alertView}
     */
    public int getSensitivity() {
        return alertViewParams.width;
    }

    /**
     * Move bar as much as X, in pixels.
     * 
     * @param x
     */
    public void moveBarX(float x) {
        // Side List view is fully opened
        // lock moving until finger is released.
        if (isOpened) {
            return;
        }

        float nextX = barListView.getX() + x;
        if (0 < nextX) {
            isOpened = true;
            barHelperView.setEnabled(true);
            nextX = 0;
        } else if (nextX < -mChildSize.x) {
            nextX = -mChildSize.x;
        }
        barListView.setTranslationX(nextX);
    }

    public void setBarAdapter(SideHelperAdapter adapter) {
        barListView.setAdapter(adapter);
        barHelperView.setAdapter(adapter);
        mSideHelperAdapter = adapter;
        mSideHelperAdapter.registerDataSetObserver(mAdapterObserver);
    }

    public void onPause() {
        if (barGroup != null) {
            barGroup.setVisibility(View.GONE);
        }
    }

    public void onResume() {
        if (barGroup != null) {
            barGroup.setVisibility(View.VISIBLE);
        }
    }

    public void onDestroy() {
        if (barGroup != null) {
            mWm.removeView(barGroup);
        }
    }

    public NotificationAlertView getAlertView() {
        return alertView;
    }

    public void setEnableHelperView(boolean enable) {
        barHelperView.setEnabled(enable);
    }

    public boolean helperViewEnabled() {
        return barHelperView.isEnabled();
    }

    /**
     * Computes width of view in an adapter, best used when you need to
     * wrap_content on a ListView, please be careful and don't use it on an
     * adapter that is extremely numerous in items or it will take a long time.
     * 
     * @param context
     *            Some context
     * @param adapter
     *            The adapter to process
     * @return The pixel width of the widest View
     */
    public static Point GetSizeOfChildView(Context context, Adapter adapter) {
        View view = null;
        FrameLayout fakeParent = new FrameLayout(context);
        view = adapter.getView(0, view, fakeParent);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return new Point(view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    private void closeBarView() {
        if (isClosing) {
            Logs.Log("closeBarView  still Closing");
            return;
        }
        Logs.Log("closeBarView");
        isClosing = true;
        if (closeAnimation == null) {
            closeAnimation = new TranslateAnimation(
                    Animation.ABSOLUTE, 0.0F,
                    Animation.RELATIVE_TO_PARENT, -1.0F,
                    Animation.ABSOLUTE, 0.0F,
                    Animation.ABSOLUTE, 0.0F);
            closeAnimation.setDuration(CLOSE_ANIMATION_DURATION);
        }
        barListView.startAnimation(closeAnimation);
        mHandler.postDelayed(hideListThread, CLOSE_ANIMATION_DURATION);
    }

    /**
     * 
     * @param scrollPeriod
     *            every x ms scoll will happened. smaller values for smooth
     * @param heightToScroll
     *            will be scrolled to x px every time. smaller values for
     *            smoother scrolling
     */
    private void smoothScroll(float y, int scrollPeriod, int heightToScroll) {
        ScrollState ss;
        if (y < mChildSize.y) {
            heightToScroll *= -1;
            scrollPeriod /= 2;
            ss = ScrollState.UP_2X;
        } else if (y < mChildSize.y * 2) {
            heightToScroll *= -1;
            ss = ScrollState.UP;
        } else if (y > mScreenSize.y - mChildSize.y) {
            scrollPeriod /= 2;
            ss = ScrollState.DOWN_2X;
        } else if (y > mScreenSize.y - mChildSize.y * 2) {
            ss = ScrollState.DOWN;
        } else {
            prevScrollState = ScrollState.NONE;
            barListView.removeCallbacks(scrollThread);
            if (cdt != null) {
                cdt.cancel();
            }
            return;
        }

        if (prevScrollState != ss) {
            prevScrollState = ss;
            smoothScroll(scrollPeriod, heightToScroll);
        }
    }

    /**
     * Helper method for {@link #smoothScroll(float, int, int)}
     */
    private void smoothScroll(final int scrollPeriod, final int heightToScroll) {
        barListView.removeCallbacks(scrollThread);
        if (cdt != null) {
            cdt.cancel();
        }

        cdt = new CountDownTimer(TOTAL_SCROLL_TIME, scrollPeriod) {

            public void onTick(long millisUntilFinished) {
                barListView.smoothScrollBy(heightToScroll, scrollPeriod);
            }

            public void onFinish() {
                prevScrollState = ScrollState.NONE;
            }
        };

        barListView.post(scrollThread);
    }
}