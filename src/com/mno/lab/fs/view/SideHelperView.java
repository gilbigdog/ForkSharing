package com.mno.lab.fs.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.mno.lab.fs.utils.Logs;

public class SideHelperView extends LinearLayout {

    private Context mContext;
    private SideHelperAdapter mAdapter;

    /**
     * Selected Item bitmap
     */
    private View selectedItem;

    /**
     * Currently selected Item index
     */
    private long mPos = 0;

    /**
     * Paint to draw Bitmap
     */
    private Paint mPaint;
    private boolean mEnable;

    public SideHelperView(Context context) {
        super(context);
        mContext = context;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public void setAdapter(SideHelperAdapter adapter) {
        if (adapter == null) {
            return;
        }
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(new DataSetObserver() {

            @Override
            public void onChanged() {
                Logs.Log("SideHelperView::mAdapter::onChanged()");
                setPosition(mPos, true);
                invalidate();
            }

            @Override
            public void onInvalidated() {
                Logs.Log("SideHelperView::mAdapter::onInvalidated()");
                onChanged();
            }
        });
        if (mAdapter.getCount() != 0) {
            setPosition(0, true);
        }
    }

    @Override
    public void setEnabled(boolean enable) {
        // To save memory usage, recycle bitmap
        if (enable) {
            setPosition(mPos, true);
        } else if (selectedItem != null) {
            removeView(selectedItem);
            selectedItem = null;
        }
        mEnable = enable;
        invalidate();
        super.setEnabled(enable);
    }

    @Override
    public boolean isEnabled() {
        return mEnable;
    }

    public void setPosition(long position, boolean force) {
        if (mAdapter == null) {
            return;
        }

        if (mPos != position) {
            mPos = position;
        } else if (!force) {
            return;
        }

        if (!mEnable) {
            return;
        }

        if (selectedItem != null) {
            removeView(selectedItem);
        }
        if (position < 0 || position >= mAdapter.getHelperSize()) {
            Logs.Log("WHAT! , mAdapter.getHelperSize() " + mAdapter.getHelperSize());
            selectedItem = null;
        } else {
            selectedItem = mAdapter.getHelperView((int) position);
            WindowManager.LayoutParams barListViewParams = new WindowManager.LayoutParams();
            barListViewParams.format = PixelFormat.RGBA_8888;
            barListViewParams.gravity = Gravity.CENTER;
            barListViewParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            barListViewParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            addView(selectedItem, barListViewParams);
        }

        invalidate();
    }
}