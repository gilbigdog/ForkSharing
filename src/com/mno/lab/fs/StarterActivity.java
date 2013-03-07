package com.mno.lab.fs;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.mno.lab.fs.service.ISessionManager;
import com.mno.lab.fs.utils.Logs;
import com.mno.lab.fs.view.Panel;

public class StarterActivity extends Activity implements
		CreateNdefMessageCallback {

	private static final String PACKAGE_NAME = "com.mno.lab.fs";

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		private ISessionManager mSessionManager;

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mSessionManager = null;
			Logs.Log("onServiceDisconnected");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Logs.Log("onServiceConnected");

			// get instance of the aidl binder
			mSessionManager = ISessionManager.Stub.asInterface(service);
		}
	};

	private Panel panel;

	private NfcAdapter mNfcAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String action = getIntent().getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Logs.Log("ACTION_NDEF_DISCOVERED");
			mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
			if (mNfcAdapter == null) {
				Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG)
						.show();

				PackageManager pkgMgr = getPackageManager();
				ComponentName comp = new ComponentName(PACKAGE_NAME,
						PACKAGE_NAME + ".NFCFire");
				pkgMgr.setComponentEnabledSetting(comp,
						PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
			}

			// 콜백 등록
			mNfcAdapter.setNdefPushMessageCallback(this, this);
		} else if (Intent.ACTION_SEND.equals(action)) {
			Logs.Log("ACTION_SEND");

		} else {
			setContentView(R.layout.activity_starter);
		}
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		// TODO Auto-generated method stub
		return null;
	}
}
