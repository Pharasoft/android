package com.chochon.chochonandroid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.json.JSONException;
import org.json.JSONObject;

import com.chochon.chochonandroid.DBObjects.UserInfo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	static final int SIGN_UP_REQUEST = 0;
	
	public static final String ARG_EMAIL = "ARG_EMAIL";
	public static final String ARG_PASSWORD = "ARG_PASSWORD";
	
	// User Logged in infor
	private UserInfo userInfo;

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// SocialAuth Components
	private static SocialAuthAdapter mSocialAuthAdapter;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		
		// Set up the login form.
		// mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		// mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});

		// Display form sign in
		findViewById(R.id.sign_up_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intentSignup = new Intent(LoginActivity.this,
								SignUpActivity.class);
						startActivity(intentSignup);
					}
				});

		// Sign up with facebook
		findViewById(R.id.imageButtonSignUpFacebook).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// Adapter initialization
						mSocialAuthAdapter = new SocialAuthAdapter(
								new ResponseListener());
						mSocialAuthAdapter.authorize(LoginActivity.this,
								Provider.FACEBOOK);
					}
				});
		
		// Sign up with google
				findViewById(R.id.imageButtonSignUpGoogle).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								// Adapter initialization
								mSocialAuthAdapter = new SocialAuthAdapter(
										new ResponseListener());
								mSocialAuthAdapter.authorize(LoginActivity.this,
										Provider.GOOGLE);
							}
						});
		
				// Sign up with twitter
				findViewById(R.id.imageButtonSignUpTwitter).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								// Adapter initialization
								mSocialAuthAdapter = new SocialAuthAdapter(
										new ResponseListener());
								mSocialAuthAdapter.authorize(LoginActivity.this,
										Provider.TWITTER);
							}
						});
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	//Get email and password after registered successfull
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SIGN_UP_REQUEST) {
            if (resultCode == RESULT_OK) {
            	
                // attemptLogin with email and password
            	mEmailView.setText(data.getStringExtra(ARG_EMAIL));
            	mPasswordView.setText(data.getStringExtra(ARG_PASSWORD));
            	attemptLogin();
                
            }
        }
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute(mEmail, mPassword);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO: attempt authentication against a network service.

			return postLogin(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(final String result) {
			mAuthTask = null;
			showProgress(false);

			if (result != null) {

				finish();
				// Get user have logged in
				try {
					// userInfo = new UserInfo();
					// JSONArray userLoggedInItems = new JSONArray(result);
					JSONObject userItem0 = new JSONObject(result);

					JSONObject userItem = userItem0.getJSONObject("user");
					// userInfo.setEmail(userItem.getString("email"));
					// userInfo.setFullName(userItem.getString("name"));
					//
					// Log.d("Logged in success", userInfo.getEmail());
					// Save user logged in state
					SharedPreferences sharedPref = getSharedPreferences(
							MainActivity.PREFERENCE_USER_LOGGED_NAME,
							MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putBoolean(
							MainActivity.PREFERENCE_USER_LOGGED_IN_STATE_KEY,
							true);
					editor.putString(
							MainActivity.PREFERENCE_USER_LOGGED_IN_ID_KEY,
							userItem.getString("_id"));
					editor.putString(
							MainActivity.PREFERENCE_USER_LOGGED_IN_EMAIL_KEY,
							userItem.getString("email"));
					editor.putString(
							MainActivity.PREFERENCE_USER_LOGGED_IN_FULLNAME_KEY,
							userItem.getString("name"));
					//editor.putStringSet(arg0, arg1)
					editor.commit();

					Log.d("Login", userItem.getString("name"));

				} catch (Exception e) {
					Log.d("Exception onPostExecute", e.getLocalizedMessage());
				}
			} else {
				mPasswordView.setError(getString(R.string.action_sign_up_fail));
				mPasswordView.requestFocus();

			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	/**
	 * Make an json object wiht user informaition and POST to webservice to sign
	 * up user. And then receive information of user that have signed up.
	 */
	private String postLogin(String email, String password) {

		String result = null;

		// Make json object.
		JSONObject userJsonObject = new JSONObject();
		try {

			userJsonObject.put("email", email);
			userJsonObject.put("password", password);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			String userJsonString = userJsonObject.toString();
			System.out.println(userJsonString);
			StringEntity stringEntity = new StringEntity(userJsonString);
			String url = getString(R.string.chochon_webserver) + "/login";
			HttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(stringEntity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse response = httpClient.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			Log.d("Json Put Sign up user", " code  " + statusCode);

			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
				String line;
				StringBuilder stringBuilder = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}
				result = stringBuilder.toString();
				inputStream.close();
			} else {
				result = null;
			}

		} catch (Exception e) {
			// Log.d("Error", e.getLocalizedMessage());
		}

		return result;

	}

	/**
	 * To receive the response after authentication
	 * 
	 */
	private final class ResponseListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {

			Log.d("ChoChon sign in facebook", "Successful: ");
			// Get profile and call SignUpActivity.
			mSocialAuthAdapter.getUserProfileAsync(new ProfileDataListener());
		}

		@Override
		public void onError(SocialAuthError error) {
			Log.d("Custom-UI", "Error");
			error.printStackTrace();
		}

		@Override
		public void onCancel() {
			Log.d("Custom-UI", "Cancelled");
		}

		@Override
		public void onBack() {
			Log.d("Custom-UI", "Dialog Closed by pressing Back Key");

		}
	}

	// To receive the profile response after authentication
	private final class ProfileDataListener implements
			SocialAuthListener<Profile> {

		@Override
		public void onExecute(String provider, Profile t) {

			Profile profileMap = t;

			Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
			// intent.putExtra("provider", provider);
			intent.putExtra("profile", profileMap);
			startActivityForResult(intent, SIGN_UP_REQUEST);

		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

}
