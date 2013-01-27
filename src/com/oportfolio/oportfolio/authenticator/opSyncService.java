package com.oportfolio.oportfolio.authenticator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.oportfolio.oportfolio.HttpUtils;
import com.oportfolio.oportfolio.data.interfaces.IBase;
import com.oportfolio.oportfolio.data.interfaces.IReflection;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class opSyncService extends Service {



	@Override
	public IBinder onBind(Intent intent) {
		IBinder ret = null;
		ret = getSyncAdapter().getSyncAdapterBinder();
		return ret;
	}
	private OpThreadedSyncAdapter mSyncAdapter = null;
	private static final Object mSyncAdapterLock = new Object();


	private OpThreadedSyncAdapter getSyncAdapter() {
		if (mSyncAdapter == null)
			mSyncAdapter = new OpThreadedSyncAdapter(this);
		return mSyncAdapter;
	}
	private static final String HEADER_CONTENTTYPE_PARAM = "Content-Type";

	private static final String HEADER_CONTENTTYPE_VALUE = "application/json";

	public static Map<String, List<String>> getDefaultHeaders(){
		Map<String, List<String>> headers = new HashMap<String, List<String>>();

		headers.put(HEADER_CONTENTTYPE_PARAM, Arrays.asList(new String[] { HEADER_CONTENTTYPE_VALUE }));

		return headers;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		AccountManager accManager = AccountManager.get(this);
		Account[] mAccounts = accManager.getAccountsByType(IBase.ACCOUNT_TYPE);

		if(mAccounts != null && mAccounts.length > 0){
			ContentResolver.setIsSyncable(mAccounts[0], IBase.AUTHORITY, 1);
			ContentResolver.setSyncAutomatically(mAccounts[0], IBase.AUTHORITY, true);
		}
	}
	private static class OpThreadedSyncAdapter extends AbstractThreadedSyncAdapter  {

		private static final String TAG = "OpThreadedSyncAdapter";

		public OpThreadedSyncAdapter(Context context) {
			super(context, true);
		}

		public OpThreadedSyncAdapter(Context context, boolean autoInitialize) {
			super(context, autoInitialize);
		}

		public OpThreadedSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
			super(context, autoInitialize, allowParallelSyncs);
		}

		SimpleDateFormat simpleDateParser = new SimpleDateFormat(IBase.DATE_ISO8601);
		@Override
		public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
			try {
				synchronized (mSyncAdapterLock) {

					Log.d(TAG,"Perform Sync Started");
					AccountManager accManager = AccountManager.get(getContext());
					String password = accManager.getPassword(account);
					PasswordAuthentication passwordAuthentication = new  PasswordAuthentication(account.name, password.toCharArray());
					try {
						syncUp(passwordAuthentication);
						syncDown(passwordAuthentication);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}								

			} finally{
				signal();
			}
		}

		private void syncDown(PasswordAuthentication passwordAuthentication) throws MalformedURLException, IOException{
			HttpURLConnection urlConnection = HttpUtils.openUrl(IBase.URL_ENTRIES, HttpUtils.Method.GET, null, null, null, passwordAuthentication);
			if(urlConnection != null){
				try{
					if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
						try {
							JSONArray jsonArray = new JSONArray(HttpUtils.readStream(urlConnection.getInputStream()));
							for(int i = 0; i < jsonArray.length(); i++){
								JSONObject jsonObj = jsonArray.getJSONObject(i);
								ContentValues values = new ContentValues();
								String whereClause = IReflection.IColumns.SYNC_ID + "=" + jsonObj.getString(IBase.EntrieTags.id.name());
								values.put(IReflection.IColumns.SYNC_ID, jsonObj.getString(IBase.EntrieTags.id.name()));
								if(!jsonObj.isNull(IBase.EntrieTags.title.name())){
									values.put(IReflection.IColumns.TITLE, jsonObj.getString(IBase.EntrieTags.title.name()));
								}
								if(!jsonObj.isNull(IBase.EntrieTags.description.name())){
									values.put(IReflection.IColumns.DESCRIPTION, jsonObj.getString(IBase.EntrieTags.description.name()));
								}
								if(!jsonObj.isNull(IBase.EntrieTags.reflection.name())){
									values.put(IReflection.IColumns.REFLECTION, jsonObj.getString(IBase.EntrieTags.reflection.name()));
								}
								if(!jsonObj.isNull(IBase.EntrieTags.image_latitude.name())){
									values.put(IReflection.IColumns.IMAGE_LAT, jsonObj.getString(IBase.EntrieTags.image_latitude.name()));
								}
								if(!jsonObj.isNull(IBase.EntrieTags.image_longitude.name())){
									values.put(IReflection.IColumns.IMAGE_LONG, jsonObj.getString(IBase.EntrieTags.image_longitude.name()));
								}
								if(!jsonObj.isNull(IBase.EntrieTags.image_url.name())){
									values.put(IReflection.IColumns.IMAGE_URL, jsonObj.getString(IBase.EntrieTags.image_url.name()));
								}

								if(!jsonObj.isNull(IBase.EntrieTags.occurred_at.name())){
									values.put(IReflection.IColumns.DATE, simpleDateParser.parse(jsonObj.getString(IBase.EntrieTags.occurred_at.name())).getTime());
								}
								if(!jsonObj.isNull(IBase.EntrieTags.created_at.name())){
									values.put(IReflection.IColumns.CREATE_AT, simpleDateParser.parse(jsonObj.getString(IBase.EntrieTags.created_at.name())).getTime());
								}

								values.put(IReflection.IColumns.SYNCED_AT, new Date().getTime());
								values.put(IReflection.IColumns.DIRTY, 0);

								if(getContext().getContentResolver().update(IBase.REFLECTION_CONTENT_URI, values, whereClause, null) == 0){
									Uri uri = getContext().getContentResolver().insert(IBase.REFLECTION_CONTENT_URI, values);
									Log.d(TAG, "Inserted Record " + uri.toString());
								} else {
									Log.d(TAG, "Updated Record ");
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} finally {
					urlConnection.disconnect();
				}
			}
		}

		private void syncUp(PasswordAuthentication passwordAuthentication) throws JSONException, MalformedURLException, IOException{
			String selection = IReflection.IColumns.SYNC_ID + " is null or " + IReflection.IColumns.DIRTY + " = 1";
			Cursor sendCursor = getContext().getContentResolver().query(IBase.REFLECTION_CONTENT_URI, null, selection, null, null);

			if(sendCursor != null){
				while(sendCursor.moveToNext()){
					Long id = sendCursor.getLong(sendCursor.getColumnIndexOrThrow(IReflection.IColumns.ID));
					Long remoteid = sendCursor.getLong(sendCursor.getColumnIndexOrThrow(IReflection.IColumns.SYNC_ID));
					JSONObject postObj = new JSONObject();
					if(remoteid > 0){
						postObj.put(IBase.EntrieTags.id.name(), remoteid);
					}
					postObj.put(IBase.EntrieTags.title.name(), sendCursor.getString(sendCursor.getColumnIndexOrThrow(IReflection.IColumns.TITLE)));
					postObj.put(IBase.EntrieTags.description.name(), sendCursor.getString(sendCursor.getColumnIndexOrThrow(IReflection.IColumns.DESCRIPTION)));
					postObj.put(IBase.EntrieTags.reflection.name(), sendCursor.getString(sendCursor.getColumnIndexOrThrow(IReflection.IColumns.REFLECTION)));
//					postObj.put(IBase.EntrieTags.occurred_at.name(), simpleDateParser.format(new Date(sendCursor.getLong(sendCursor.getColumnIndexOrThrow(IReflection.IColumns.DATE)))));
					postObj.put(IBase.EntrieTags.created_at.name(), simpleDateParser.format(new Date(sendCursor.getLong(sendCursor.getColumnIndexOrThrow(IReflection.IColumns.CREATE_AT)))));

					HttpURLConnection connection = null;
					if(remoteid == 0){
						connection = HttpUtils.openUrl(IBase.URL_ENTRIES, HttpUtils.Method.POST, null, getDefaultHeaders(), postObj.toString(), passwordAuthentication);
					} else {
						connection = HttpUtils.openUrl(IBase.URL_ENTRIES + "/" + remoteid, HttpUtils.Method.PUT, null, getDefaultHeaders(), postObj.toString(), passwordAuthentication);
					}
					if(connection != null && connection.getResponseCode() == HttpURLConnection.HTTP_OK){
						try {
							String responseText = HttpUtils.readStream(connection.getInputStream());
							JSONObject jsonObject = new JSONObject(responseText);
							ContentValues updateValues = new ContentValues();
							updateValues.put(IReflection.IColumns.SYNC_ID, jsonObject.getLong(IBase.EntrieTags.id.name()));
							updateValues.put(IReflection.IColumns.SYNCED_AT, new Date().getTime());
							updateValues.put(IReflection.IColumns.ID, id);

							String updSelection = IReflection.IColumns.ID + "=" + id;

							if(getContext().getContentResolver().update(IBase.REFLECTION_CONTENT_URI, updateValues, updSelection, null) > 0){
								Log.d(TAG, "Sync update successful for id " + id);
							} else {
								Log.d(TAG, "Sync update failed for id " + id);
							}

							Log.d(TAG,"Response Text " + responseText);
						} finally {
							connection.disconnect();
						}

					}

				}
			}
		}
		@Override
		public void onSyncCanceled(Thread thread) {
			signal();
			super.onSyncCanceled(thread);
		}

		@Override
		public void onSyncCanceled() {
			signal();
			super.onSyncCanceled();
		}

		private void signal() {
			synchronized (mSyncAdapterLock) {
				mSyncAdapterLock.notify();
			}
		}
	}
}
