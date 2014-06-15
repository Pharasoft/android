package com.chochon.chochonandroid;



import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks, OnSharedPreferenceChangeListener {

	// ===========================================================
	// Constants
	// ===========================================================	
	public static final String PREFERENCE_USER_LOGGED_IN_STATE_KEY = "com.chochon.chochonandroid.PREFERENCE_USER_LOGGED_IN_STATE_KEY";
	public static final String PREFERENCE_USER_LOGGED_IN_ID_KEY = "com.chochon.chochonandroid.PREFERENCE_USER_LOGGED_IN_ID_KEY";
	public static final String PREFERENCE_USER_LOGGED_IN_EMAIL_KEY = "com.chochon.chochonandroid.PREFERENCE_USER_LOGGED_EMAIL_ID_KEY";
	public static final String PREFERENCE_USER_LOGGED_IN_FULLNAME_KEY = "com.chochon.chochonandroid.PREFERENCE_USER_LOGGED_FULLNAME_ID_KEY";
	public static final String PREFERENCE_USER_LOGGED_NAME = "PREFERENCE_USER_LOGGED_NAME";
	
		
	//For demo, we can set thi by get data from service.
	private final String[] 	listNavigationItem =  { "Home page", "Search", "Near by", "Events", "Review", "Messages",  "Talk", "Activities"};
	
		
	private SharedPreferences sharedPref ;
	
	/**
	 * Fragment managing the behaviors, interactions and presentation of thenavigation_drawer
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout),
				listNavigationItem);
		
		//On change user log in, log out
		 sharedPref = getSharedPreferences(MainActivity.PREFERENCE_USER_LOGGED_NAME, MODE_PRIVATE);
		 sharedPref.registerOnSharedPreferenceChangeListener(this);
		
				
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
	
		selectItem(position);
		mTitle = listNavigationItem[position];
	}


	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			
			 //Check if user logged in then display menu for actions of user.
			 sharedPref = getSharedPreferences(MainActivity.PREFERENCE_USER_LOGGED_NAME, MODE_PRIVATE);
			 
			 if(sharedPref.getBoolean(PREFERENCE_USER_LOGGED_IN_STATE_KEY, false)){
				 //
				 getMenuInflater().inflate(R.menu.main_user_action, menu);
			 } else{
				//esle display menu Login.
				 getMenuInflater().inflate(R.menu.main_log_in, menu);
					
			 }
           		
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		
		case R.id.action_log_out :
			sharedPref = getSharedPreferences(MainActivity.PREFERENCE_USER_LOGGED_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(MainActivity.PREFERENCE_USER_LOGGED_IN_STATE_KEY, false);
            editor.commit();
			return true;

		case R.id.action_user_signin:

			Intent intentLogin = new Intent(this,LoginActivity.class);
            startActivity(intentLogin);
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}

	}

	 Fragment fragment;
	 FragmentManager fragmentManager;
	 
	private void selectItem(int position) {
		switch (position) {
		case 0:
			//Home page
			 // Create a new fragment
		    fragment = new BusinessListFragment();
		    Bundle args = new Bundle();
		    args.putString(BusinessListFragment.ARG_LOCATION, listNavigationItem[position]);
		    fragment.setArguments(args);

		    // Insert the fragment by replacing any existing fragment
		    fragmentManager = getSupportFragmentManager();
		    fragmentManager.beginTransaction()
		                   .replace(R.id.container, fragment)
		                   .commit();

			break;

		case 1:
			break;
			
		case 7:
			//Check if user logged in then display menu for actions of user.
			fragment = new ArticlesListFragment();
			//Insert the fragment by replacing any existing fragment
		    fragmentManager = getSupportFragmentManager();
		    fragmentManager.beginTransaction()
		                   .replace(R.id.container, fragment)
		                   .commit();			 	
			break;
			
		default:
			
			break;
		}
	   
	    // Highlight the selected item, update the title, and close the drawer
	    
	    
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// TODO Auto-generated method stub
		finish();
		startActivity(getIntent());
	}
	
	
	
}
