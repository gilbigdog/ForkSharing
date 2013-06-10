package com.mno.lab.fs;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.mno.lab.fs.service.ISessionManager;
import com.mno.lab.fs.service.SessionManager;
import com.mno.lab.fs.utils.Logs;

public class StarterActivity extends Activity implements
        CreateNdefMessageCallback, OnNdefPushCompleteCallback {

    public static final String PACKAGE_NAME = "com.mno.lab.fs";
    private static final String MIME_TYPE = "application/com.mno.lab.fs";
    private static final int SLEEP_TO_BIND = 2000;

    private NfcAdapter mNfcAdapter;
    private ISessionManager mSessionManager = null;

    private TextView tv;

    private ForkServiceConnection mServiceConnection = new ForkServiceConnection();

    class ForkServiceConnection implements ServiceConnection {

        public String ndefMsg;
        public Intent shareIntent;

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logs.Log("onServiceDisconnected");

            mSessionManager = null;
            ndefMsg = null;
            shareIntent = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // get instance of the aidl binder
            mSessionManager = ISessionManager.Stub.asInterface(service);

            if (!TextUtils.isEmpty(ndefMsg)) {
                Logs.Log("onServiceConnected : handle NDEF Message : " + ndefMsg);
                try {
                    mSessionManager.connect(ndefMsg);
                } catch (RemoteException e) {
                    Logs.Log("Not able to connect to " + ndefMsg);
                    e.printStackTrace();
                } finally {
                    ndefMsg = null;
                }
            } else if (shareIntent != null) {
                Logs.Log("onServiceConnected : handle Intent : " + shareIntent.toString());
                try {
                    mSessionManager.shareVia(shareIntent);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } finally {
                    shareIntent = null;
                }
            }
            notifyTo();
        }
    }

    private final Handler mConnectHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Logs.Log("ACTION_NDEF_DISCOVERED");
            if (isWiFiConnected()) {
                List<String> messages = readNFCmessage(intent);
                mServiceConnection.ndefMsg = messages.get(0);
                bindToSessionManager();
            } else {
                String summ = getResources().getString(R.string.nv_no_wifi_string);
                Toast.makeText(getApplicationContext(), summ, Toast.LENGTH_LONG).show();
            }
            finish();
        } else if (Intent.ACTION_SEND.equals(action)) {
            Logs.Log("ACTION_SEND");
            if (mSessionManager == null) {
                mServiceConnection.shareIntent = intent;
                bindToSessionManager();
            } else {
                try {
                    mSessionManager.shareVia(intent);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            finish();
        } else {
            setContentView(R.layout.activity_nfc);
            tv = (TextView) findViewById(R.id.tv_nfc);

            if (mSessionManager == null) {
                bindToSessionManager();
            }
        }
    }

    private List<String> readNFCmessage(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs != null) {
            List<String> res = new ArrayList<String>();
            for (int i = 0; i < rawMsgs.length; i++) {
                String msg = new String(((NdefMessage) rawMsgs[i]).getRecords()[0].getPayload());
                res.add(msg);
                Logs.Log("Received message via NFC : " + msg);
            }
            return res;
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (tv == null) {
            return;
        }

        enableComponent("NFCFire", false);
        boolean hasNFC = nfcAvailality();
        String summ = null;
        if (hasNFC) {
            if (!mNfcAdapter.isEnabled()) {
                summ = getResources().getString(R.string.nv_no_nfc_string);
                if (!isWiFiConnected()) {
                    summ += ", " + getResources().getString(R.string.nv_no_wifi_string);
                }
            } else if (!isWiFiConnected()) {
                summ = getResources().getString(R.string.nv_no_wifi_string);
            } else {
                summ = getResources().getString(R.string.nv_nfc_string);
                enableComponent("NFCFire", true);
            }
        } else {
            summ = getResources().getString(R.string.nv_no_nfc_capable_string);
        }
        tv.setText(summ);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mServiceConnection);
    }

    private boolean nfcAvailality() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        enableComponent("NFCFire", true);
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        return true;
    }

    private void enableComponent(String name, boolean enable) {
        PackageManager pkgMgr = getPackageManager();
        ComponentName comp = new ComponentName(PACKAGE_NAME,
                PACKAGE_NAME + "." + name);
        pkgMgr.setComponentEnabledSetting(comp,
                enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    synchronized void notifyTo() {
        Logs.Log("notifyTo");
        this.notifyAll();
    }

    synchronized void waitRes(int msec) throws InterruptedException {
        Logs.Log("waitRes : " + msec);
        this.wait(msec);
    }

    /**
     * A callback to be invoked when another NFC device capable of NDEF push
     * (Android Beam) is within range.
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        Logs.Log("Detect NFC device");
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

        if (mSessionManager == null) {
            bindToSessionManager();

            // Wait until it binds to SessionManager
            try {
                waitRes(SLEEP_TO_BIND);
            } catch (InterruptedException e) {
                Logs.Log("Fail to sleep");
                e.printStackTrace();
            }
        }

        // Get Channel Name
        String session = new String();
        try {
            if (mSessionManager != null) {
                if (!mSessionManager.isConnected()) {
                    session = UUID.randomUUID().toString();
                    mSessionManager.connect(null);
                    Logs.Log("No Exsiting connection, Create New Session");
                }
                session = mSessionManager.getSessionName();
            } else {
                Logs.Log("Session Manager is null");
            }
        } catch (RemoteException e) {
            Logs.Log("Fail to connect");
            e.printStackTrace();
        }

        // Prepare NDEF message
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMimeRecord(
                        MIME_TYPE, session.getBytes())
                });
        Logs.Log("Send NDEF message : " + session);
        return msg;
    }

    private void bindToSessionManager() {
        Intent callService = new Intent(this, SessionManager.class);
        startService(callService);
        if (!bindService(callService, mServiceConnection, BIND_AUTO_CREATE)) {
            Logs.Log("Cannot bind to SessionManager");
        }
    }

    /**
     * Creates a custom MIME type encapsulated in an NDEF record
     */
    private NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

    /**
     * A callback to be invoked when the system successfully delivers your
     * NdefMessage to another device.
     */
    @Override
    public void onNdefPushComplete(NfcEvent event) {
        Logs.Log("System successfully delivers message to another device");
    }

    private boolean isWiFiConnected() {
        // Setup WiFi
        WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
        if (wifi == null) {
            return false;
        }

        // Get WiFi status
        WifiInfo info = wifi.getConnectionInfo();
        return !TextUtils.isEmpty(info.getBSSID());
    }
}
