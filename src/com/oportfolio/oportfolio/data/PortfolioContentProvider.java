package com.oportfolio.oportfolio.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.oportfolio.oportfolio.data.interfaces.IBase;
import com.oportfolio.oportfolio.data.interfaces.IReflection;

public class PortfolioContentProvider extends ContentProvider {

	private static PortfolioContentProvider mSelf;

	public static PortfolioContentProvider getInstance(Context context) {
		if (mSelf == null) {
			mSelf = new PortfolioContentProvider();
			mSelf.attachInfo(context, null);
		}
		return mSelf;
	}


	private static UriMatcher mUriMatcher;
	static {

		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		// _ID
		mUriMatcher.addURI(IBase.AUTHORITY, IBase.REFLECTION_PREFIX + "/#", IBase.REFLECTION_ITEM);
		mUriMatcher.addURI(IBase.AUTHORITY, IBase.REFLECTION_PREFIX, IBase.REFLECTION_ITEMS);
	}




	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = this.mSqlHelper.getWritableDatabase();
		ContentResolver resolver = getContext().getContentResolver();

		int match = mUriMatcher.match(uri);
		Integer rCount = -1;
		switch (match) {

		case IBase.REFLECTION_ITEM:
			String reflectionItemWhere = IReflection.IColumns.ID + " = " + uri.getPathSegments().get(1);
			if (!TextUtils.isEmpty(selection)) {
				reflectionItemWhere += " and " + selection;
			}
			rCount = db.delete(IReflection.TAG, reflectionItemWhere, selectionArgs);
			break;
			
		case IBase.REFLECTION_ITEMS:
			rCount = db.delete(IReflection.TAG, selection, selectionArgs);
			break;
		}

		return rCount;
	}

	@Override
	public String getType(Uri uri) {
		int match = mUriMatcher.match(uri);
		switch (match) {

		case IBase.REFLECTION_ITEM:
			return "vnd.android.cursor.file/" + IBase.AUTHORITY;

		case IBase.REFLECTION_ITEMS:
			return "vnd.android.cursor.file/" + IBase.AUTHORITY;
		}
		return null;
	}
	
	

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = this.mSqlHelper.getWritableDatabase();
		ContentResolver resolver = getContext().getContentResolver();

		int match = mUriMatcher.match(uri);
		Long recordId = -1l;
		switch (match) {

		case IBase.REFLECTION_ITEM:
			values.put(IReflection.IColumns.ID, uri.getPathSegments().get(1));
			
		case IBase.REFLECTION_ITEMS:
			values.put(IReflection.IColumns.CREATE_AT, java.lang.System.currentTimeMillis());
			recordId = db.replace(IReflection.TAG, null, values);
			if (recordId > -1) {
				resolver.notifyChange(IBase.REFLECTION_CONTENT_URI, null);
			}
			break;
		}

		if (recordId != -1) {
			Uri insertUri = ContentUris.withAppendedId(uri, recordId);
			return insertUri;
		}

		return null;
	}

	private PortfolioSQLiteOpenHelper mSqlHelper = null;
	@Override
	public boolean onCreate() {
		this.mSqlHelper = new PortfolioSQLiteOpenHelper(getContext(), IBase.DB_NAME, null, IBase.DB_VERSION);
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = this.mSqlHelper.getReadableDatabase();
		ContentResolver resolver = getContext().getContentResolver();

		int match = mUriMatcher.match(uri);
		switch (match) {

		case IBase.REFLECTION_ITEM:
			String reflectionItemWhere = IReflection.IColumns.ID + " = " + uri.getPathSegments().get(1);
			if (!TextUtils.isEmpty(selection)) {
				reflectionItemWhere += " and " + selection;
			}
			return db.query(IReflection.TAG, projection, reflectionItemWhere, selectionArgs, null, null, sortOrder);

			
		case IBase.REFLECTION_ITEMS:
			return db.query(IReflection.TAG, projection, selection, selectionArgs, null, null, sortOrder);

		}

		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = this.mSqlHelper.getWritableDatabase();
		ContentResolver resolver = getContext().getContentResolver();

		int match = mUriMatcher.match(uri);
		Integer rCount = 0;
		switch (match) {

		case IBase.REFLECTION_ITEM:
			String reflectionItemWhere = IReflection.IColumns.ID + " = " + uri.getPathSegments().get(1);
			if (!TextUtils.isEmpty(selection)) {
				reflectionItemWhere += " and " + selection;
			}
			rCount = db.update(IReflection.TAG, values, reflectionItemWhere, selectionArgs);
			if (rCount > -1) {
				resolver.notifyChange(IBase.REFLECTION_CONTENT_URI, null);
			}
			break;
			
			
		case IBase.REFLECTION_ITEMS:
			rCount = db.update(IReflection.TAG, values, selection, selectionArgs);
			if (rCount > 0) {
				resolver.notifyChange(IBase.REFLECTION_CONTENT_URI, null);
			}
			break;
			
		}

		return rCount;
	}

}
