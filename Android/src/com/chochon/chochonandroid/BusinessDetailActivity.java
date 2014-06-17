package com.chochon.chochonandroid;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BusinessDetailActivity extends ActionBarActivity {

	public static final String ARG_BUSINESS_ID = "ARG_BUSINESS_ID";
	private String id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_detail);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Fragment fragment = new BusinessDetailFragment();
		Bundle args = new Bundle();
		args.putString(BusinessDetailFragment.ARG_BUSINESS_ID, getIntent().getStringExtra(BusinessDetailActivity.ARG_BUSINESS_ID));
		fragment.setArguments(args);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container_detail, fragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.business_detail, menu);
		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
}
