package com.oportfolio.oportfolio;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.oportfolio.oportfolio.data.PortfolioAdapter;
import com.oportfolio.oportfolio.data.interfaces.IBase;
import com.oportfolio.oportfolio.data.interfaces.IReflection;
import com.oportfolio.oportfolio.fragments.PortfolioItem;

public class PortfolioListActivity extends Activity {

	private ListView mPortfolioList;
	AccountManager accManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.portfoliolist);
		getActionBar().show();
		accManager = AccountManager.get(this);
		mPortfolioList = (ListView) findViewById(R.id.protfolioList);
		if(mPortfolioList != null){
			mPortfolioList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> views, View view, int pos, long id) {
					Intent intent = new Intent(PortfolioListActivity.this, PortfolioItem.class);
					intent.putExtra(IBase.REFLECTION_ID, id);
					startActivity(intent);
					
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Cursor cursor = getContentResolver().query(IBase.REFLECTION_CONTENT_URI, null, null, null, IReflection.IColumns.CREATE_AT + " desc");
		PortfolioAdapter adapter = new PortfolioAdapter(this, cursor);

		if(mPortfolioList != null){
			mPortfolioList.setAdapter(adapter);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_portfolio, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch(item.getItemId()){
		case R.id.menu_AddItem :
			Intent intent = new Intent(this, PortfolioItem.class);
			this.startActivity(intent);
			return true;
			
		case R.id.menu_SyncItem :
			
			Account[] accounts = accManager.getAccountsByType(IBase.ACCOUNT_TYPE);
			if(accounts.length == 0){
				accManager.addAccount(IBase.ACCOUNT_TYPE, null, null, null, null, null, null);
			}
			Bundle bundle = new Bundle();
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			ContentResolver.requestSync(null, IBase.ACCOUNT_TYPE, bundle);
			return true;

		}
		return super.onMenuItemSelected(featureId, item);
	}
}
