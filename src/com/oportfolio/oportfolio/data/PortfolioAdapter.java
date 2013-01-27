package com.oportfolio.oportfolio.data;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oportfolio.oportfolio.R;
import com.oportfolio.oportfolio.data.interfaces.IReflection;

public class PortfolioAdapter extends CursorAdapter {

	public PortfolioAdapter(Context context, Cursor c) {
		super(context, c);
	}

	public PortfolioAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	public PortfolioAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	class ViewHolder{
		ImageView mPhotoImage = null;
		TextView mTitleText = null;
		TextView mDateText = null;
		ImageView mSyncImage = null;
	}

	SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if(view != null){
			ViewHolder viewHolder = (ViewHolder) view.getTag();

			if(viewHolder.mPhotoImage != null){

			};
			if(viewHolder.mSyncImage != null){

			}
			if(viewHolder.mDateText != null){
				viewHolder.mDateText.setText(mSimpleDateFormat.format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(IReflection.IColumns.CREATE_AT)))));
			}
			if(viewHolder.mTitleText != null){
				viewHolder.mTitleText.setText(cursor.getString(cursor.getColumnIndexOrThrow(IReflection.IColumns.TITLE)));
			}
		}

	}

	private LayoutInflater mInflater = null;
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		if(mInflater == null){
			mInflater = LayoutInflater.from(context);
		}
		View view = null;
		if(mInflater != null){
			view = mInflater.inflate(com.oportfolio.oportfolio.R.layout.reflection_item, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mPhotoImage = (ImageView) view.findViewById(R.id.imagePhoto);
			viewHolder.mSyncImage = (ImageView) view.findViewById(R.id.imagePhoto);
			viewHolder.mTitleText = (TextView) view.findViewById(R.id.txtTitle);
			viewHolder.mDateText = (TextView) view.findViewById(R.id.txtDateTime);

			view.setTag(viewHolder);
		}

		return view;
	}

}
