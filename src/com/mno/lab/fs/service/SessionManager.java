package com.mno.lab.fs.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.mno.lab.fs.R;
import com.mno.lab.fs.utils.Logs;
import com.samsung.ssl.smeshnet.ManagedSession;
import com.samsung.ssl.smeshnet.ManagedSession.SessionStateListener;
import com.samsung.ssl.smeshnet.SessionListManager;
import com.samsung.ssl.smeshnet.data.Data;
import com.samsung.ssl.smeshnet.data.User;
import com.samsung.ssl.smeshnet.internal.session.BaseSession.DataReceivedListener;
import com.samsung.ssl.smeshnet.services.SessionManagementService.RemoteSessionInfo;

public class SessionManager extends Service implements SessionStateListener {

    private Context mContext;

    /**
     * Managed Session
     */
    private ManagedSession mSession;

    /**
     * Session State Listener
     */
    private List<SessionStateListener> mSessionStateListener;

    /**
     * Data listener via this mSession
     */
    private DataReceivedListener mDataReceivedListener = new DataReceivedListener() {

        @Override
        public void onReceivedData(Data data) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onReceivedData(User user, byte[] data) {
            // TODO Auto-generated method stub

        }
    };

    /**
     * Magnet Session Action name only for this app
     */
    private String mMagnetAction;

    /**
     * to retreive Session List
     */
    private SessionListManager mSessionList;

    /**
     * Session Name currently it is in.
     */
    private String mSessionName;
/*
    public SessionManager(Context context) {

        mMagnetAction = context.getResources().getString(R.string.magnet_service_action);

        if (TextUtils.isEmpty(mMagnetAction)) {
            throw new IllegalArgumentException("Margnet Service Action String is not ready");
        }

        Logs.SetILogger();
        // Initiate ManagedSession
        mSession = new ManagedSession(context, mMagnetAction, this);
    }*/

    public void connect(String session, boolean randomGen) {
        if (!TextUtils.isEmpty(session) && !sessionExist(session)) {
            mSession.connect(session);
            mSessionName = session;
        } else {
            Logs.Log("connect : session exists " + session);
            if (randomGen) {
                connect();
            }
        }
    }

    public void connect() {
        String uuid = UUID.randomUUID().toString();
        connect(uuid, false);
    }

    public Collection<User> getAllUsers() {
        return mSession.getUsers();
    }

    public void sendMessage(String message) {

    }

    public boolean sessionExist(String sessionName) {
        if (mSessionList == null) {
            mSessionList = new SessionListManager(mContext, mMagnetAction);
        }
        for (RemoteSessionInfo info : mSessionList.getCurrentSessionList()) {
            if (TextUtils.equals(sessionName, info.getSessionName())) {
                return true;
            }
        }
        return false;
    }

    public String getSessionName() {
        return mSessionName;
    }

    public void registerSessionStateListener(SessionStateListener listener) {
        if (listener == null) {
            return;
        }

        if (mSessionStateListener == null) {
            mSessionStateListener = new ArrayList<SessionStateListener>();
        }

        mSessionStateListener.add(listener);
    }

    public boolean unregisterSessionStateListener(SessionStateListener listener) {
        if (mSessionStateListener == null || listener == null) {
            return false;
        }

        return mSessionStateListener.remove(listener);
    }

    @Override
    public void onConnected(ManagedSession arg0) {
        for (SessionStateListener lis : mSessionStateListener) {
            lis.onConnected(arg0);
        }
    }

    @Override
    public void onConnectionFailed(String arg0) {
        for (SessionStateListener lis : mSessionStateListener) {
            lis.onConnectionFailed(arg0);
        }
    }

    @Override
    public void onDisconnected() {
        for (SessionStateListener lis : mSessionStateListener) {
            lis.onDisconnected();
        }
    }

    @Override
    public void onReconnected(ManagedSession arg0) {
        for (SessionStateListener lis : mSessionStateListener) {
            lis.onReconnected(arg0);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    ViewGroup vg;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("GIL", "YYYYYYYYYYYYYYY");
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vg = (ViewGroup) inflater.inflate(R.layout.test_slide, null);

        // 최상위 윈도우에 넣기 위한 설정
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, // 항상 최 상위에 있게
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, // 터치 인식
                PixelFormat.TRANSLUCENT); // 투명

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE); // 윈도
                                                                             // 매니저
        wm.addView(vg, params); // 최상위 윈도우에 뷰 넣기. permission필요.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vg != null) // 서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
        {
            Log.d("GIL", "NNNNNNNNNNNNNN");
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(vg);
            vg = null;
        }
    }
}
