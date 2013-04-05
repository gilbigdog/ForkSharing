package com.mno.lab.fs.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.mno.lab.fs.datatype.DefaultType;
import com.mno.lab.fs.utils.Logs;

public class FSAdapter extends SideHelperAdapter {

    private List<DefaultType> mTypes;
    private List<OnSelectListener> mOnSelectListeners;
    private LayoutInflater mInflater;

    public FSAdapter(Context ctx) {
        mInflater = LayoutInflater.from(ctx);
    }

    public void addType(DefaultType type) {
        if (mTypes == null) {
            mTypes = new ArrayList<DefaultType>();
        }

        mTypes.add(type);
        notifyDataSetChanged();
    }

    public void removeType(DefaultType type) {
        if (mTypes == null) {
            return;
        }

        if (mTypes.remove(type)) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (mTypes == null) {
            return 0;
        }
        return mTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mTypes == null) {
            return null;
        }

        Drawable icon = mTypes.get(position).getIcon();
        if (icon == null) {
            Logs.Log("For Position " + position + ", Icon is null");
            return null;
        }
        ImageView iv = new ImageView(parent.getContext());
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        iv.setLayoutParams(lp);
        iv.setBackground(icon);
        return iv;
    }

    @Override
    public View getHelperView(int position) {
        if (mTypes == null) {
            return null;
        }

        return mTypes.get(position).getHelperView(mInflater, null);
    }

    @Override
    public int getHelperSize() {
        return getCount();
    }

    @Override
    public void onSelectListener(int position) {
        if (mOnSelectListeners != null) {
            mOnSelectListeners.get(position).onSelect();
        }
    }

    public static interface OnSelectListener {

        public void onSelect();
    }
}