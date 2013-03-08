package com.mno.lab.fs.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.mno.lab.fs.R;
import com.mno.lab.fs.proc.ConfirmProtocol;
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
    private List<SessionStateListener> mSessionStateListener = new ArrayList<SessionStateListener>();

    /**
     * Data listener via this mSession
     */
    private DataReceivedListener mDataReceivedListener = new DataReceivedListener() {

        @Override
        public void onReceivedData(User user, byte[] data) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onReceivedData(Data data) {
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

    /**
     * Handles handshake and transfer data
     */
    private ConfirmProtocol mConfirmProtocol;

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    // implementation of the aidl interface
    private final ISessionManager.Stub mBinder = new ISessionManager.Stub() {

        @Override
        public void initSession() throws RemoteException {
            init();
        }

        @Override
        public void connect(String session, boolean randomGen)
                throws RemoteException {
            SessionManager.this.connect(session, randomGen);
        }

        @Override
        public String getSessionName() throws RemoteException {
            return mSessionName;
        }

        @Override
        public boolean isConnected() throws RemoteException {
            if (mSession == null) {
                return false;
            }
            return mSession.isConnected();
        }

        @Override
        public void shareVia(Intent intent) throws RemoteException {
            // TODO : it should pop-up User list to let user select destination then call
            // mConfirmProtocol.sendData(null, intent);
        }
    };

    public void init() {
        mMagnetAction = getResources().getString(
                R.string.magnet_service_action);

        if (TextUtils.isEmpty(mMagnetAction)) {
            throw new IllegalArgumentException(
                    "Margnet Service Action String is not ready");
        }

        Logs.SetILogger();
        // Initiate ManagedSession
        mSession = new ManagedSession(getApplicationContext(),
                mMagnetAction, this);
        mConfirmProtocol = new ConfirmProtocol(mContext, mSession);
        Logs.Log("initSession");
    }

    public void connect(String session, boolean randomGen) {
        Logs.Log("try to join channel : " + session + ", " + randomGen);
        if (!TextUtils.isEmpty(session)) {
            mSession.connect(session);
            mSessionName = session;
            Logs.Log("connect : session " + session);
        } else {
            Logs.Log("connect : session exists or invalid " + session);
            if (randomGen) {
                String uuid = UUID.randomUUID().toString();
                connect(uuid, false);
            }
        }
    }

    public Collection<User> getAllUsers() {
        return mSession.getUsers();
    }

    // TODO It works?
    /*
     * private boolean sessionExist(String sessionName) {
     * Logs.Log("sessionExist : " + sessionName); if (mSessionList == null) {
     * mSessionList = new SessionListManager(mContext, mMagnetAction); } for
     * (RemoteSessionInfo info : mSessionList.getCurrentSessionList()) { if
     * (TextUtils.equals(sessionName, info.getSessionName())) { return true; } }
     * return false; }
     */

    private void registerSessionStateListener(SessionStateListener listener) {
        if (listener == null) {
            return;
        }

        if (mSessionStateListener == null) {
            mSessionStateListener = new ArrayList<SessionStateListener>();
        }

        mSessionStateListener.add(listener);
    }

    private boolean unregisterSessionStateListener(SessionStateListener listener) {
        if (mSessionStateListener == null || listener == null) {
            return false;
        }

        return mSessionStateListener.remove(listener);
    }

    @Override
    public void onConnected(ManagedSession arg0) {
        Logs.Log("onConnected");

        for (SessionStateListener lis : mSessionStateListener) {
            lis.onConnected(arg0);
        }
    }

    @Override
    public void onConnectionFailed(String arg0) {
        Logs.Log("onConnectionFailed");

        for (SessionStateListener lis : mSessionStateListener) {
            lis.onConnectionFailed(arg0);
        }
    }

    @Override
    public void onDisconnected() {
        Logs.Log("onDisconnected");
        for (SessionStateListener lis : mSessionStateListener) {
            lis.onDisconnected();
        }
    }

    @Override
    public void onReconnected(ManagedSession arg0) {
        Logs.Log("onReconnected");
        for (SessionStateListener lis : mSessionStateListener) {
            lis.onReconnected(arg0);
        }
    }

    ViewGroup vg;

    @Override
    public void onCreate() {
        super.onCreate();
        Logs.Log("onCreate");
        init();
        /*
         * Log.d("GIL", "YYYYYYYYYYYYYYY"); LayoutInflater inflater =
         * (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         * vg = (ViewGroup) inflater.inflate(R.layout.test_slide, null);
         * WindowManager.LayoutParams params = new WindowManager.LayoutParams(
         * WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
         * WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
         * PixelFormat.TRANSLUCENT); WindowManager wm = (WindowManager)
         * getSystemService(WINDOW_SERVICE); wm.addView(vg, params);
         */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.Log("SessionManager : onDestroy()");
        if (vg != null) {
            Log.d("GIL", "NNNNNNNNNNNNNN");
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(vg);
            vg = null;
        }
    }
}
