package com.mno.lab.fs.view;

import android.view.View;
import android.widget.BaseAdapter;

public abstract class SideHelperAdapter extends BaseAdapter {

    /**
     * 
     * Returns a view that has extra information corresponding to a view at the
     * position
     *
     * @param position
     *            int Position of a view in Adapter
     * @return View
     */
    public abstract View getHelperView(int position); //TODO : Recycle Feature

    /**
     * To verify whether the length of helper and baseadapter size are
     * identical.
     * 
     * @return int length of helper size
     */
    public abstract int getHelperSize();

    /**
     * When user release a finger, callback for the latest selected item.
     */
    public abstract void onSelectListener(int position);
}