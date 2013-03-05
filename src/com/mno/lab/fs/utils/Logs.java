package com.mno.lab.fs.utils;

import android.util.Log;

import com.samsung.ssl.smeshnet.utils.ILogger;
import com.samsung.ssl.smeshnet.utils.Logging;

public class Logs {

    private static final String TAG = "FS";

    public static void SetILogger() {
        Logging.logger = new ILogger() {

            @Override
            public void LogW(Exception exception) {
                Log.w(TAG, exception.toString());
            }

            @Override
            public void LogW(String message) {
                android.util.Log.w(TAG, message);
            }

            @Override
            public void LogI(String message) {
                android.util.Log.i(TAG, message);
            }

            @Override
            public void Log(String message) {
                android.util.Log.d(TAG, message);
            }

            @Override
            public void Log(Exception e) {
                Log.e(TAG, "", e);
            }
        };
    }
    
    public static void Log(String message){
        Log.d(TAG, message);
    }
}
