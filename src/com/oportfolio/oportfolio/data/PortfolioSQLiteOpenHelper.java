package com.oportfolio.oportfolio.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.oportfolio.oportfolio.BuildConfig;
import com.oportfolio.oportfolio.data.interfaces.IBase;
import com.oportfolio.oportfolio.data.interfaces.IReflection;

public class PortfolioSQLiteOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = "PortfolioSQLiteOpenHelper";

	public PortfolioSQLiteOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
		createTableIndexes(db);
		createTableTiggers(db);
		preloadData(db);
	}

	private void preloadData(SQLiteDatabase db) {

	}

	private void createTableIndexes(SQLiteDatabase db) {
	}

	private void createTables(SQLiteDatabase db) {
		createReflectionTable(db);
	}

	private void createTableTiggers(SQLiteDatabase db) {
	}


	private void addDefaultColumns(StringBuilder sb) {
		sb.append(IBase.IColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT ");
		sb.append(" ," + IBase.IColumns.TAGS + " TEXT");
		sb.append(" ," + IBase.IColumns.CREATE_AT + " INTEGER");
		sb.append(" ," + IBase.IColumns.UPDATED_AT + " INTEGER");
		sb.append(" ," + IBase.IColumns.DIRTY + " INTEGER");
		sb.append(" ," + IBase.IColumns.SYNCED_AT + " INTEGER");
		sb.append(" ," + IBase.IColumns.SYNC_ID + " INTEGER");
	}


	private void createReflectionTable(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS " + IReflection.TAG);
		sb.append(" ( ");
		addDefaultColumns(sb);
		sb.append(" ," + IReflection.IColumns.ACCOUNT + " TEXT  ");
		sb.append(" ," + IReflection.IColumns.TITLE + " TEXT  ");
		sb.append(" ," + IReflection.IColumns.DATE + " INTEGER  ");
		sb.append(" ," + IReflection.IColumns.DURATION_LENGTH + " INTEGER  ");
		sb.append(" ," + IReflection.IColumns.DURATION_TYPE + " INTEGER  ");
		sb.append(" ," + IReflection.IColumns.IMAGE_LONG + " TEXT  ");
		sb.append(" ," + IReflection.IColumns.IMAGE_LAT + " TEXT  ");
		sb.append(" ," + IReflection.IColumns.IMAGE_URL + " TEXT  ");
		sb.append(" ," + IReflection.IColumns.DESCRIPTION + " TEXT  ");
		sb.append(" ," + IReflection.IColumns.REFLECTION + " TEXT  ");

		sb.append(" ); ");
		db.execSQL(sb.toString());

		if (BuildConfig.DEBUG) {
			Log.d(TAG, TAG + " Table created " + IReflection.TAG);
		}
	}

}
