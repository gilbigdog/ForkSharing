package com.mno.lab.fs.datatype;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.mno.lab.fs.utils.Logs;
import com.samsung.ssl.smeshnet.ManagedSession;
import com.samsung.ssl.smeshnet.data.User;

public abstract class DefaultType {

    static final int DESTROY_TIMEOUT = 6000 * 5;
    static Handler mHandler = new Handler();
    Runnable destoryThread = new Runnable() {

        @Override
        public void run() {
            onDestory();
        }
    };

    public static enum TRANSFER_TYPE {
        SEND,
        RECEIVE
    };

    TRANSFER_TYPE mTransferType;

    public static enum DATA_TYPE {
        IMAGE,
        VIDEO,
        FILE,
        APP,
        MESSAGE,
        CONTACT,
        RAW
    };

    public interface SelectListener {

        public void onSelectListener();
    }

    List<SelectListener> mSelectListener;

    /**
     * Intent sent from System
     */
    Intent mIntent;

    DestroyListener mDestroyListener;

    Context mContext;

    public interface DestroyListener {

        public void onDestroyListener();
    }

    /**
     * Send a file or data to user
     * 
     * @param user
     *            User
     */
    public void send(ManagedSession session, User user) {
        onPostDestory(DESTROY_TIMEOUT);
    }

    /**
     * BroadCast a file or data
     */
    public void broadcast(ManagedSession session) {
        onPostDestory(DESTROY_TIMEOUT);
    }

    /**
     * @return Drawable corresponding to a type
     */
    public abstract Drawable getIcon();

    /**
     * @return DATA_TYPE
     */
    public abstract DATA_TYPE getType();

    /**
     * @return TRANSFER_TYPE
     */
    public TRANSFER_TYPE getTransferType() {
        return mTransferType;
    }

    /**
     * @return View that displays contents corresponding to a type
     */
    public abstract View getHelperView(LayoutInflater inflater, View convertView);

    /**
     * When user releases finger on this helper, it gets fired.
     */
    public void onSelect() {
        if (mSelectListener != null) {
            for (SelectListener listener : mSelectListener) {
                listener.onSelectListener();
            }
        }
    }

    public void setOnSelectListener(SelectListener listener) {
        if (mSelectListener == null) {
            mSelectListener = new ArrayList<SelectListener>();
        }

        mSelectListener.add(listener);
    }

    /**
     * Free memory
     */
    public abstract void onDestory();

    public DefaultType(Context ctx) {
        mContext = ctx;
    }

    /**
     * onDestory will be called after mil
     * 
     * @param mil
     *            int
     */
    public void onPostDestory(int mil) {
        stopDestory();
        mHandler.postDelayed(destoryThread, mil);
    }

    /**
     * if there any postDestroy then remove it.
     */
    public void stopDestory() {
        mHandler.removeCallbacks(destoryThread);
    }

    /**
     * Callback when onDestory is invoked
     * 
     * @param dl
     *            DestroyListener
     */
    public void setOnDestroyListener(DestroyListener dl) {
        mDestroyListener = dl;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public static Drawable GetDefaultIcon(Context ctx, String pacakgeName) {
        try {
            return ctx.getPackageManager().getApplicationIcon(pacakgeName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void WriteDataToFile(FileInputStream fin, File path) {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(path);
            byte[] buffer = new byte[1024 * 1024];
            int len = 0;
            while ((len = fin.read(buffer)) > 0) {
                fout.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            Logs.Log(e.toString());
        } catch (IOException e) {
            Logs.Log(e.toString());
        }
        try {
            if (fout != null) {
                fout.close();
            }
            if (fin != null) {
                fin.close();
            }
        } catch (IOException e) {
            Logs.Log(e.toString());
        }
    }

    /**
     * Creates a unique subdirectory of the designated app cache directory.
     * Tries to use external but if not mounted, falls back on internal storage.
     * 
     * @param context
     * @param uniqueName
     *            fileName
     * @return
     */
    public static File GetCacheDir(Context context, String uniqueName) {
        final String cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
                || !Environment.isExternalStorageRemovable() ?
                context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }
}