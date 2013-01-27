package com.oportfolio.oportfolio.fragments;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.oportfolio.oportfolio.R;
import com.oportfolio.oportfolio.data.interfaces.IBase;
import com.oportfolio.oportfolio.data.interfaces.IReflection;

public class PortfolioItem extends Activity {

	Long mRecordId = 0l;
	private Long mNoteId = 0l;
	// date and time
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;
	private TextView mEventDate;
	private TextView mEventTime;
	private EditText mTitle;
	private EditText mReflection;
	private EditText mDescription;
	private EditText mTags;
	private View mNoteContainer;
	private View mNoteList;

	static final int TIME_DIALOG_ID = 0;
	static final int DATE_DIALOG_ID = 1;
	private static final String TAG = "PortfolioItem";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.portfolioitem);

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);


		mNoteContainer = findViewById(R.id.noteContainer);
		mNoteList = findViewById(R.id.notesList);
		mTitle = (EditText)findViewById(R.id.txtTitle);
		mDescription = (EditText)findViewById(R.id.txtDescription);
		mReflection = (EditText)findViewById(R.id.txtReflection);
		mTags = (EditText)findViewById(R.id.tags);
		mDuration = (Spinner) findViewById(R.id.dropDuration);
		if(mDuration != null){
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
					this, R.array.duration, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mDuration.setAdapter(adapter);
			mDuration.setSelection(0);
		}
		mDurationType = (Spinner) findViewById(R.id.dropDurationType);
		if(mDurationType != null){
			ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(
					this , R.array.durationtype, android.R.layout.simple_spinner_item);
			adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mDurationType.setAdapter(adapterType);
			mDurationType.setSelection(0);
		}

		mEventDate = (TextView) findViewById(R.id.eventDate);
		if(mEventDate != null){
			mEventDate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialog(DATE_DIALOG_ID);

				}
			});
		}

		mEventTime = (TextView) findViewById(R.id.eventTime);
		if(mEventTime != null){
			mEventTime.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialog(TIME_DIALOG_ID);

				}
			});
		}

		Intent intent = getIntent();
		if(intent != null && intent.getExtras() != null){
			if(intent.getExtras().containsKey(IBase.REFLECTION_ID)){
				mRecordId = intent.getExtras().getLong(IBase.REFLECTION_ID);
				loadRefectionRecord(mRecordId);
			}
		}

		updateDisplay();

	}

	Spinner mDuration = null;
	Spinner mDurationType = null;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this,
					mTimeSetListener, mHour, mMinute, false);
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this,
					mDateSetListener,
					mYear, mMonth, mDay);
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case TIME_DIALOG_ID:
			((TimePickerDialog) dialog).updateTime(mHour, mMinute);
			break;
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}    

	private void updateDisplay() {
		mEventDate.setText(
				new StringBuilder()
				// Month is 0 based so add 1
				.append(mDay).append("-")
				.append(mMonth + 1).append("-")
				.append(mYear).append(" "));

		mEventTime.setText(
				new StringBuilder()
				.append(pad(mHour)).append(":")
				.append(pad(mMinute)));
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener =
			new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	private TimePickerDialog.OnTimeSetListener mTimeSetListener =
			new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;
			updateDisplay();
		}
	};


	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_portfolioitem, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch(item.getItemId()){
		case R.id.menu_saveItem :
			ContentValues refletionValues = new ContentValues();
			if(mTitle != null){
				refletionValues.put(IReflection.IColumns.TITLE, mTitle.getText().toString());
			}
			refletionValues.put(IReflection.IColumns.DATE, new Date(mYear, mMonth, mDay, mHour, mMinute).getTime());
			if(mTags != null){
				refletionValues.put(IReflection.IColumns.TAGS, mTags.getText().toString());
			}

			if(mDuration != null){
				refletionValues.put(IReflection.IColumns.TITLE, mTitle.getText().toString());
			}

			if(mDurationType != null){
				refletionValues.put(IReflection.IColumns.DURATION_TYPE, mDurationType.getSelectedItemId());
			}
			if(mDuration != null){
				refletionValues.put(IReflection.IColumns.DURATION_LENGTH, mDuration.getSelectedItemId());
			}

			if(mDescription != null){
				refletionValues.put(IReflection.IColumns.DESCRIPTION, mDescription.getText().toString());
			}
			if(mReflection != null){
				refletionValues.put(IReflection.IColumns.REFLECTION, mReflection.getText().toString());
			}

			refletionValues.put(IReflection.IColumns.DIRTY, 1);
			if(mRecordId == 0){
				Uri uri = getContentResolver().insert(IBase.REFLECTION_CONTENT_URI, refletionValues);
				Log.d(TAG, "inserted record  " + uri.toString());
				finish();

			} else {
				refletionValues.put(IReflection.IColumns.ID, mRecordId);
				int updated = getContentResolver().update(ContentUris.withAppendedId(IBase.REFLECTION_CONTENT_URI, mRecordId), refletionValues, null, null);
				Log.d(TAG, "updated record  " + updated);

				finish();
			}


			return true;


		}
		return super.onMenuItemSelected(featureId, item);
	}


	void loadRefectionRecord(long id){
		Uri reflectionUri = ContentUris.withAppendedId(IBase.REFLECTION_CONTENT_URI, id); 
		Cursor reflectCursor = getContentResolver().query(reflectionUri, null, null, null, null);

		if(reflectCursor != null){
			try{
			reflectCursor.moveToFirst();
			if(mTitle != null){
				mTitle.setText(reflectCursor.getString(reflectCursor.getColumnIndexOrThrow(IReflection.IColumns.TITLE)));
			}

			Date date = new Date(reflectCursor.getLong(reflectCursor.getColumnIndexOrThrow(IReflection.IColumns.DATE)));
			final Calendar c = Calendar.getInstance();
			c.setTime(date);
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
			mHour = c.get(Calendar.HOUR_OF_DAY);
			mMinute = c.get(Calendar.MINUTE);


			if(mDuration != null){
				mDuration.setSelection(reflectCursor.getInt(reflectCursor.getColumnIndexOrThrow(IReflection.IColumns.DURATION_LENGTH)));
			}
			if(mDurationType != null){
				mDurationType.setSelection(reflectCursor.getInt(reflectCursor.getColumnIndexOrThrow(IReflection.IColumns.DURATION_TYPE)));
			}
			if(mDescription != null){
				mDescription.setText(reflectCursor.getString(reflectCursor.getColumnIndexOrThrow(IReflection.IColumns.DESCRIPTION)));	
			}

			if(mReflection != null){
				mReflection.setText(reflectCursor.getString(reflectCursor.getColumnIndexOrThrow(IReflection.IColumns.REFLECTION)));
			}
			
			if(mTags != null){
				mTags.setText(reflectCursor.getString(reflectCursor.getColumnIndexOrThrow(IReflection.IColumns.TAGS)));
			}
			} finally {
				reflectCursor.close();
			}
		}
	}

}
