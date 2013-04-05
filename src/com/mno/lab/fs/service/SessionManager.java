package com.mno.lab.fs.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import com.mno.lab.fs.R;
import com.mno.lab.fs.StarterActivity;
import com.mno.lab.fs.datatype.ApplicationType;
import com.mno.lab.fs.datatype.ContactType;
import com.mno.lab.fs.datatype.DefaultType;
import com.mno.lab.fs.datatype.ImageType;
import com.mno.lab.fs.datatype.VideoType;
import com.mno.lab.fs.proc.ConfirmProtocol;
import com.mno.lab.fs.utils.Logs;
import com.mno.lab.fs.view.FSAdapter;
import com.mno.lab.fs.view.SideBar;
import com.mno.lab.fs.view.UserSelectorAdapter;
import com.samsung.ssl.smeshnet.ManagedSession;
import com.samsung.ssl.smeshnet.ManagedSession.SessionStateListener;
import com.samsung.ssl.smeshnet.SessionListManager;
import com.samsung.ssl.smeshnet.data.User;
import com.samsung.ssl.smeshnet.filetransfer.FileTransferStatusListener;
import com.samsung.ssl.smeshnet.filetransfer.SmeshnetFileTransfer;
import com.samsung.ssl.smeshnet.internal.session.BaseSession.UserStatusListener;

public class SessionManager extends Service implements SessionStateListener, UserStatusListener {

    private static String IntentSharer = "IntentSharer";

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
    private FileTransferStatusListener mFileTransferStatusListener = new FileTransferStatusListener() {

        @Override
        public void onCancel(SmeshnetFileTransfer arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProgressUpdate(SmeshnetFileTransfer arg0, long arg1, long arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTransferComplete(SmeshnetFileTransfer arg0, File arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTransferFailed(SmeshnetFileTransfer arg0, Exception arg1) {
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

    private SideBar mSideBar;
    private FSAdapter mSideBarAdapter;

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
            Logs.Log("SessionManager::shareVia");
            // TODO : it should pop-up User list to let user select destination
            // TODO : support not only SEND also MULTIPLE_SEND
            DefaultType type = null;
            type = getTypeInstance(intent);

            if (type != null) {
                mSideBarAdapter.addType(type);
            }
        }
    };

    private DefaultType getTypeInstance(Intent intent) {
        Context ctx = getApplicationContext();
        String mime = intent.getType();

        if (mime.contains("vcard")) {
            try {
                return ContactType.ParseContactUri(ctx, intent);
            } catch (IOException e1) {
                Logs.Log("Not able to share vcard now");
                e1.printStackTrace();
            }
        } else if (mime.startsWith("image")) {
            try {
                return ImageType.ParseImageUri(ctx, intent);
            } catch (IOException e) {
                Logs.Log("Not able to share image now");
                e.printStackTrace();
            }
        } else if (mime.startsWith("video")) {
            try {
                return VideoType.ParseVideoUri(ctx, intent);
            } catch (IOException e) {
                Logs.Log("Not able to share video now");
                e.printStackTrace();
            }
        } else if (mime.startsWith("application")) {
            try {
                return ApplicationType.ParseVideoUri(ctx, intent);
            } catch (IOException e) {
                Logs.Log("Not able to share application now");
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * When Start sharing and user selection dialog pops-up, it is list adapter
     * for the dialog.
     */
    private UserSelectorAdapter mUserListAdapter;

    private void userSelectionDialog(final Intent intent) {
        mUserListAdapter = new UserSelectorAdapter(getAllUsers());
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setAdapter(mUserListAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                User user = mUserListAdapter.getUser(which);
                if (user == null) {
                    Logs.Log("Wrong User Item is selected on SelectionDialog");
                    toastMessge("Sharing failure. Please try again");
                    dialog.dismiss();
                    return;
                }
                mConfirmProtocol.sendData(user, intent);
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void toastMessge(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

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
        mSession.getFileTransferManager().addFileTransferStatusListener(mFileTransferStatusListener);
        mConfirmProtocol = new ConfirmProtocol(getApplicationContext(), mSession);
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

        // enableComponent(IntentSharer, true);

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

        // enableComponent(IntentSharer, false);

        for (SessionStateListener lis : mSessionStateListener) {
            lis.onDisconnected();
        }
    }

    @Override
    public void onReconnected(ManagedSession arg0) {
        Logs.Log("onReconnected");

        // enableComponent(IntentSharer, true);

        for (SessionStateListener lis : mSessionStateListener) {
            lis.onReconnected(arg0);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logs.Log("onCreate");
        init();

        enableComponent(IntentSharer, true);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mSideBar = new SideBar(getApplicationContext(), wm);
        mSideBarAdapter = new FSAdapter(getApplicationContext());
        mSideBar.setBarAdapter(mSideBarAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.Log("SessionManager : onDestroy()");
        if (mSideBar != null) {
            mSideBar.onDestroy();
            mSideBar = null;
        }
        // enableComponent(IntentSharer, false);
    }

    private void enableComponent(String name, boolean enable) {
        Logs.Log("Component " + name + " is enabled? " + enable);
        PackageManager pkgMgr = getPackageManager();
        ComponentName comp = new ComponentName(StarterActivity.PACKAGE_NAME,
                StarterActivity.PACKAGE_NAME + "." + name);
        pkgMgr.setComponentEnabledSetting(comp,
                enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
    }

    @Override
    public void onUserJoin(User user) {
        if (mUserListAdapter != null) {
            mUserListAdapter.addUser(user);
        }
    }

    @Override
    public void onUserLeave(User user) {
        if (mUserListAdapter != null) {
            mUserListAdapter.removeUser(user);
        }
    }
}
