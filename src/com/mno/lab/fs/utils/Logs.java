package com.mno.lab.fs.utils;

import android.util.Log;

import com.samsung.ssl.smeshnet.utils.ILogger;
import com.samsung.ssl.smeshnet.utils.Logging;

public class Logs {

    private static final String TAG = "FS";
    private static final String TAG2 = TAG + ":Smeshnet";

    public static void SetILogger() {
        Logging.logger = new ILogger() {

            @Override
            public void LogW(Exception exception) {
                Log.w(TAG2, exception.toString());
            }

            @Override
            public void LogW(String message) {
                android.util.Log.w(TAG2, message);
            }

            @Override
            public void LogI(String message) {
                android.util.Log.i(TAG2, message);
            }

            @Override
            public void Log(String message) {
                android.util.Log.d(TAG2, message);
            }

            @Override
            public void Log(Exception e) {
                Log.e(TAG2, "", e);
            }
        };
    }
    
    public static void Log(String message){
        Log.v(TAG, message);
    }
}
