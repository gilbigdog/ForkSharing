package com.mno.lab.fs.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.samsung.ssl.smeshnet.data.User;

public class UserSelectorAdapter implements ListAdapter {

    private static final String BROADCAST_FILE = "ALL";

    private List<User> mUsersList;
    private List<DataSetObserver> mDataSetObserverList;

    public UserSelectorAdapter(Collection<User> users) {
        mUsersList = new ArrayList<User>();
        User all = new User();
        all.setName(BROADCAST_FILE);

        mUsersList.add(all);
        mUsersList.addAll(users);

        mDataSetObserverList = new ArrayList<DataSetObserver>();
    }

    public synchronized void removeUser(User user) {
        if (mUsersList.remove(user)) {
            notifyToAllObservers();
        }
    }

    public synchronized void addUser(User user) {
        if (!mUsersList.contains(user)) {
            mUsersList.add(user);
            notifyToAllObservers();
        }
    }

    public synchronized User getUser(int pos) {
        if (pos < 0 || pos > mUsersList.size()) {
            return null;
        }
        return mUsersList.get(pos);
    }

    @Override
    public synchronized int getCount() {
        return mUsersList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return Adapter.IGNORE_ITEM_VIEW_TYPE;
    }

    @Override
    public synchronized View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextView(parent.getContext());
        }

        ((TextView) convertView).setText(mUsersList.get(position).getName());
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public synchronized boolean isEmpty() {
        return mUsersList.isEmpty();
    }

    private void notifyToAllObservers() {
        for (DataSetObserver observer : mDataSetObserverList) {
            observer.onChanged();
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObserverList.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObserverList.remove(observer);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}