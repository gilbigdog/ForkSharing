package com.mno.lab.fs.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
<<<<<<< HEAD
import android.graphics.PixelFormat;
=======
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
>>>>>>> efd8a6f... New Update
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;

<<<<<<< HEAD
import com.mno.lab.fs.R;
=======
import com.mno.lab.fs.StarterActivity;
import com.mno.lab.fs.datatype.ApplicationType;
import com.mno.lab.fs.datatype.ApplicationType.ApplicationPkgData;
import com.mno.lab.fs.datatype.ContactType;
import com.mno.lab.fs.datatype.DefaultType;
import com.mno.lab.fs.datatype.DefaultType.TRANSFER_TYPE;
import com.mno.lab.fs.datatype.ImageType;
import com.mno.lab.fs.datatype.VideoType;
import com.mno.lab.fs.proc.ConfirmProtocol;
>>>>>>> efd8a6f... New Update
import com.mno.lab.fs.utils.Logs;
import com.samsung.ssl.smeshnet.ManagedSession;
import com.samsung.ssl.smeshnet.ManagedSession.SessionStateListener;
<<<<<<< HEAD
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

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	// implementation of the aidl interface
	private final ISessionManager.Stub mBinder = new ISessionManager.Stub() {

		@Override
		public void initSession() throws RemoteException {
			mMagnetAction = getResources().getString(
					R.string.magnet_service_action);

			if (TextUtils.isEmpty(mMagnetAction)) {
				throw new IllegalArgumentException(
						"Margnet Service Action String is not ready");
			}

			Logs.SetILogger();
			// Initiate ManagedSession
			mSession = new ManagedSession(getApplicationContext(),
					mMagnetAction, SessionManager.this);
			Logs.Log("initSession");
		}

		@Override
		public void connect(String session, boolean randomGen)
				throws RemoteException {
			if (!TextUtils.isEmpty(session) && !sessionExist(session)) {
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

		@Override
		public void sendMessage(String message) throws RemoteException {

		}

		@Override
		public String getSessionName() throws RemoteException {
			return mSessionName;
		}
	};

	public Collection<User> getAllUsers() {
		return mSession.getUsers();
	}

	private boolean sessionExist(String sessionName) {
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

	ViewGroup vg;

	@Override
	public void onCreate() {
		super.onCreate();

		Log.d("GIL", "YYYYYYYYYYYYYYY");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vg = (ViewGroup) inflater.inflate(R.layout.test_slide, null);

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);

		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		wm.addView(vg, params);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (vg != null) {
			Log.d("GIL", "NNNNNNNNNNNNNN");
			((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(vg);
			vg = null;
		}
	}
=======
import com.samsung.ssl.smeshnet.data.Data;
import com.samsung.ssl.smeshnet.data.User;
import com.samsung.ssl.smeshnet.filetransfer.FileTransferStatusListener;
import com.samsung.ssl.smeshnet.filetransfer.SmeshnetFileTransfer;
import com.samsung.ssl.smeshnet.internal.session.BaseSession.DataReceivedListener;
import com.samsung.ssl.smeshnet.internal.session.BaseSession.UserStatusListener;

public class SessionManager extends Service implements SessionStateListener, UserStatusListener {

    private static String IntentSharer = "IntentSharer";

    private static String MAGNET_SESSION_STRING = "com.mno.lab.fs.magnet";
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
            Logs.Log("onTransferFailed");
        }

        @Override
        public void onProgressUpdate(SmeshnetFileTransfer arg0, long arg1, long arg2) {
            arg0.getTransferName();
            Logs.Log("onTransferFailed : " + arg1 + ", " + arg2);
        }

        @Override
        public void onTransferComplete(SmeshnetFileTransfer arg0, File arg1) {
            Logs.Log("onTransferFailed : " + arg1.getName());
        }

        @Override
        public void onTransferFailed(SmeshnetFileTransfer arg0, Exception arg1) {
            Logs.Log("onTransferFailed");
        }
    };

    private DataReceivedListener mDataReceivedListener = new DataReceivedListener() {

        @Override
        public void onReceivedData(Data data) {
            if (data instanceof ApplicationPkgData) {
                Logs.Log("onReceivedData : Application data Type received " + ((ApplicationPkgData) data).appName);
            } else {
                Logs.Log("onReceivedData : Unexpected data type received");
            }
        }

        @Override
        public void onReceivedData(User user, byte[] data) {
            Logs.Log("onReceivedData : Size " + data.length + " from " + user.getName());
        }
    };

    /**
     * Handles handshake and transfer data
     */
    private ConfirmProtocol mConfirmProtocol;

    private SideBar mSideBar;
    private FSAdapter mSideBarAdapter;

    private static final int DISABLE_SHARER_COMPONENT = 0x01;
    private static final int ENABLE_SHARER_COMPONENT = 0x02;
    private static final int DISABLE_VIEWS = 0x03;
    private static final int ENABLE_VIEWS = 0x04;

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DISABLE_SHARER_COMPONENT:
                    enableComponent(IntentSharer, false);
                    break;
                case ENABLE_SHARER_COMPONENT:
                    enableComponent(IntentSharer, true);
                    break;
                case DISABLE_VIEWS:
                    setViewEnable(false);
                    break;
                case ENABLE_VIEWS:
                    setViewEnable(true);
                    break;
            }
            Logs.Log("Receive Message via Handler in SessionManager : " + msg.what);
        }
    };

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
        public void connect(String session)
                throws RemoteException {
            SessionManager.this.connect(session);
        }

        @Override
        public String getSessionName() throws RemoteException {
            return mSession.getSessionId();
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

            DefaultType type = null;
            type = getTypeInstance(intent);

            if (type != null) {
                // TODO : confirmation feature
                // mConfirmProtocol.sendData(user, type);
                type.broadcast(mSession);
                if (mSideBarAdapter != null) {
                    mSideBarAdapter.addType(type);
                }
            }

            // TODO : it should pop-up User list to let user select destination
            // TODO : support not only SEND also MULTIPLE_SEND
            // showUserSelectionDialog(type);
        }
    };

    private DefaultType getTypeInstance(Intent intent) {
        Context ctx = getApplicationContext();
        String mime = intent.getType();

        if (mime.contains("vcard")) {
            try {
                return ContactType.ParseContactUri(ctx, intent, TRANSFER_TYPE.SEND);
            } catch (IOException e1) {
                Logs.Log("Not able to share vcard now");
                e1.printStackTrace();
            }
        } else if (mime.startsWith("image")) {
            try {
                return ImageType.ParseImageUri(ctx, intent, TRANSFER_TYPE.SEND);
            } catch (IOException e) {
                Logs.Log("Not able to share image now");
                e.printStackTrace();
            }
        } else if (mime.startsWith("video")) {
            try {
                return VideoType.ParseVideoUri(ctx, intent, TRANSFER_TYPE.SEND);
            } catch (IOException e) {
                Logs.Log("Not able to share video now");
                e.printStackTrace();
            }
        } else if (mime.startsWith("application")) {
            try {
                return ApplicationType.ParseVideoUri(ctx, intent, TRANSFER_TYPE.SEND);
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

    private void showUserSelectionDialog(final DefaultType type) {
        mUserListAdapter = new UserSelectorAdapter(getAllUsers());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                mConfirmProtocol.sendData(user, type);
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void toastMessge(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void init() {

        Logs.SetILogger();
        Context context = getApplicationContext();
        // Initiate ManagedSession
        mSession = new ManagedSession(context, MAGNET_SESSION_STRING, this);
        mConfirmProtocol = new ConfirmProtocol(context, mSession);

        if (mSideBar == null || mSideBarAdapter == null) {
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            mSideBar = new SideBar(getApplicationContext(), wm);
            mSideBarAdapter = new FSAdapter(getApplicationContext());
            mSideBar.setBarAdapter(mSideBarAdapter);
        }

        setViewEnable(false);
        enableComponent(IntentSharer, false);

        Logs.Log("initSession");
    }

    public static String GetSessionStorage(Context context, String unique) {
        final String cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
                || !Environment.isExternalStorageRemovable() ?
                context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();

        // Assume if this is called we want to create the directory.
        unique += "/" + unique;
        new File(cachePath).mkdirs();
        return cachePath;
    }

    public void connect(String session) {
        if (TextUtils.isEmpty(session)) {
            session = UUID.randomUUID().toString();
        }
        Logs.Log("try to join channel : " + session);
        mSession.connect(session);
    }

    public Collection<User> getAllUsers() {
        return mSession.getUsers();
    }

    @Override
    public void onConnected(ManagedSession arg0) {
        Logs.Log("onConnected");

        mHandler.sendEmptyMessage(ENABLE_VIEWS);
        mHandler.sendEmptyMessage(ENABLE_SHARER_COMPONENT);

        // Set the callback for file transfering
        mSession.setSessionStorage(GetSessionStorage(getApplicationContext(), "setsessionStorage"));
        mSession.getFileTransferManager().addFileTransferStatusListener(mFileTransferStatusListener);

        // Set the callback that will handle when new data is received.
        mSession.addDataReceivedListener(mDataReceivedListener);

        for (SessionStateListener lis : mSessionStateListener) {
            lis.onConnected(arg0);
        }
    }

    @Override
    public void onConnectionFailed(String arg0) {
        Logs.Log("onConnectionFailed");

        mHandler.sendEmptyMessage(DISABLE_SHARER_COMPONENT);
        mHandler.sendEmptyMessage(DISABLE_VIEWS);

        for (SessionStateListener lis : mSessionStateListener) {
            lis.onConnectionFailed(arg0);
        }
    }

    @Override
    public void onDisconnected() {
        Logs.Log("onDisconnected");

        mHandler.sendEmptyMessage(DISABLE_SHARER_COMPONENT);
        mHandler.sendEmptyMessage(DISABLE_VIEWS);

        for (SessionStateListener lis : mSessionStateListener) {
            lis.onDisconnected();
        }
    }

    @Override
    public void onReconnected(ManagedSession arg0) {
        Logs.Log("onReconnected");

        mHandler.sendEmptyMessage(ENABLE_VIEWS);
        mHandler.sendEmptyMessage(ENABLE_SHARER_COMPONENT);

        for (SessionStateListener lis : mSessionStateListener) {
            lis.onReconnected(arg0);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logs.Log("SessionManager Service onCreate");
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.Log("SessionManager : onDestroy()");

        enableComponent(IntentSharer, false);

        if (mSideBar != null) {
            mSideBar.onDestroy();
            mSideBar = null;
        }

        if (mSideBarAdapter != null) {
            mSideBarAdapter.clearData();
            mSideBarAdapter = null;
            Logs.Log("NULL");
        }

        if (mSession != null) {
            mSession.onDestroy();
        }
    }

    private void setViewEnable(boolean enable) {
        Logs.Log("All Side views will be " + enable);
        if (enable) {
            if (mSideBar != null) {
                mSideBar.onPause();
            }
        } else {
            if (mSideBarAdapter != null) {
                mSideBarAdapter.clearData();
            }
            if (mSideBar != null) {
                mSideBar.onResume();
            }
        }
    }

    private void enableComponent(String name, boolean enable) {
        Logs.Log("Component " + name + " is enabled? " + enable);
        PackageManager pkgMgr = getPackageManager();
        ComponentName comp = new ComponentName(StarterActivity.PACKAGE_NAME,
                StarterActivity.PACKAGE_NAME + "." + name);
        pkgMgr.setComponentEnabledSetting(comp,
                enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
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
>>>>>>> efd8a6f... New Update
}
