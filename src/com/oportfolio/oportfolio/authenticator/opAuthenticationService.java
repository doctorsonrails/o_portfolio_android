package com.oportfolio.oportfolio.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class opAuthenticationService extends Service {
	private static final String TAG = "AuthenticationService";
	private opAuthenticator mAuthenticator;

	@Override
	public void onCreate() {
		if (Log.isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG,TAG + " Authentication Service started.");
		}
		mAuthenticator = new opAuthenticator(this);
	}

	@Override
	public void onDestroy() {
		if (Log.isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG, TAG + " Authentication Service stopped.");
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (Log.isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG,TAG + "getBinder()...  returning the AccountAuthenticator binder for intent "	+ intent);
		}
		return mAuthenticator.getIBinder();
	}

}
