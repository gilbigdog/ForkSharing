package com.mno.lab.fs.datatype;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    static Handler mHandler = new Handler();

    Runnable destoryThread = new Runnable() {

        @Override
        public void run() {
            onDestory();
        }
    };

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
    public abstract void send(ManagedSession session, User user);

    /**
     * BroadCast a file or data
     */
    public abstract void broadcast(ManagedSession session);

    /**
     * @return Drawable corresponding to a type
     */
    public abstract Drawable getIcon();

    /**
     * @return View that displays contents corresponding to a type
     */
    public abstract View getHelperView(LayoutInflater inflater, View convertView);

    /**
     * When user releases finger on this helper, it gets fired. Once it is done,
     * then let other knows what will happen via Callback
     * 
     * @param callback
     */
    public abstract void onSelect(Handler callback);

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
            // TODO : 디폴트 아이콘 로드 하기
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