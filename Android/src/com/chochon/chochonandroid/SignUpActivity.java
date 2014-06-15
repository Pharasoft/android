package com.chochon.chochonandroid;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class SignUpActivity extends Activity {
	
	
	private UserSignUpTask mSignUpTask = null;

	// SocialAuth Components
	SocialAuthAdapter adapter;
	Profile profileMap;
		
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	private String mRepeatPassword;
	private String mUserName;
	private String mFullName;
	

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mUserNameView;
	private EditText mFullNameView;
	private EditText mRepeatPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	ImageView image;
	
	//
	ImageLoader imageLoader;
	
	//Json Object facebook profile
	private JSONObject facebookProfileJsonObject = new JSONObject();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sign_up);

		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptSignUp();
							return true;
						}
						return false;
					}
				});

		mUserNameView = (EditText) findViewById(R.id.userName);
		mRepeatPasswordView = (EditText) findViewById(R.id.repeatPassword);
		mFullNameView = (EditText) findViewById(R.id.fullName);
		
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		//Get profile from sign up with social account
		profileMap = (Profile) getIntent().getSerializableExtra("profile");
		
		if(profileMap != null){
			String email = profileMap.getEmail();
			mEmailView.setText(email);
			mUserNameView.setText(email.substring(0, email.indexOf("@")));
			if (profileMap.getFullName() == null)
				mFullNameView.setText(profileMap.getFirstName() +" " + profileMap.getLastName());
			else
				mFullNameView.setText( profileMap.getFullName());
		
			//Load image	
			image = (ImageView) findViewById(R.id.imgView);
			imageLoader = new ImageLoader(SignUpActivity.this);
			imageLoader.DisplayImage(profileMap.getProfileImageURL(), image);
			
			//Set not enable, not allow edit for sign up with social account
			//mEmailView.setEnabled(false);
			//mUserNameView.setEnabled(false);
			
			//Json Object facebook
			
//			"facebook": {
//				"id": "516048164",
//				"birthday": "06/23/1981",
//				"email": "vdoan331@yahoo.com",
//				"first_name": "Viet",
//				"last_name": "Doan",
//				"link": "https://www.facebook.com/doanv",
//				"locale": "en_US",
//				"name": "Viet Doan",
//				"quotes": "Fall seven times, stand up eight.\r\n\r\n\r\nDream as if you'll live forever,\r\nLive as if you'll die today.\r\n- James Dean",
//				"timezone": -7,
//				"updated_time": "2014-04-07T12:22:49+0000",
//				"username": "doanv",
//				"verified": true
//				},
			
			try {
				facebookProfileJsonObject.put("id", profileMap.getValidatedId());
				facebookProfileJsonObject.put("birthday", profileMap.getDob());
				facebookProfileJsonObject.put("email", profileMap.getEmail());
				facebookProfileJsonObject.put("first_name", profileMap.getFirstName());
				facebookProfileJsonObject.put("last_name", profileMap.getLastName());
				facebookProfileJsonObject.put("link", profileMap.getCountry());
				facebookProfileJsonObject.put("locale", profileMap.getLanguage());
				facebookProfileJsonObject.put("name", profileMap.getDisplayName());
				facebookProfileJsonObject.put("quotes", profileMap.getProviderId());
				facebookProfileJsonObject.put("timezone", profileMap.getContactInfo());
				facebookProfileJsonObject.put("updated_time", profileMap.getContactInfo());
				facebookProfileJsonObject.put("username", profileMap.getProviderId());
				facebookProfileJsonObject.put("verified", "true");
								
			} catch (JSONException e) {
				  e.printStackTrace();
			}

		}
		
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptSignUp();
					}
				});
	}

	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptSignUp() {
		if (mSignUpTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		mUserNameView.setError(null);
		mFullNameView.setError(null);
		mRepeatPasswordView.setError(null);
		
		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mRepeatPassword = mRepeatPasswordView.getText().toString();
		mUserName = mUserNameView.getText().toString();
		mFullName = mFullNameView.getText().toString();
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
		} else if (!mPassword.contentEquals(mRepeatPassword)) {
			mRepeatPasswordView.setError(getString(R.string.error_password_not_match));
			focusView = mRepeatPasswordView;
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
			// There was an error; don't register and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user sign up.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mSignUpTask = new UserSignUpTask();
			mSignUpTask.execute((Void) null);
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
	public class UserSignUpTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
		
			// TODO: register the new account here.
			return signUpNewUser();

		}

		@Override
		protected void onPostExecute(final Boolean result) {
			mSignUpTask = null;
			showProgress(false);

			//Get user signed up from jsonObject return.
			if (result) {
				//auto login with email and password registered.
				Intent intent = new Intent();
				intent.putExtra(LoginActivity.ARG_EMAIL, mEmail);
				intent.putExtra(LoginActivity.ARG_PASSWORD, mPassword);
				setResult(RESULT_OK, intent);
				finish();
				
			} else {
	
				mEmailView.setError(getString(R.string.register_user_fail));
				mEmailView.requestFocus();
//				mPasswordView.setError(getString(R.string.action_sign_up_fail));
//				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mSignUpTask = null;
			showProgress(false);
		}
		
		
	}
	
	/**
	 * Make an json object wiht user informaition and POST to webservice to sign up user.
	 * And then receive information of user that have signed up.
	 */
	private boolean signUpNewUser(){

		boolean result = false;
		//Make json object.
		JSONObject userJsonObject = new JSONObject();
		try {
			
			//{"email":"dao@gmail.com", "username":"daotest", "password":"123456789", 
			//"confirmPassword":"123456789","name":"test"}
			userJsonObject.put("username", mUserName);
			userJsonObject.put("name", mFullName);
			userJsonObject.put("password", mPassword);
			userJsonObject.put("confirmPassword", mPassword);
			userJsonObject.put("email", mEmail);
			userJsonObject.put("facebook", facebookProfileJsonObject);
			userJsonObject.put("provider", "facebook");
	
		} catch (JSONException e) {
			  e.printStackTrace();
		}
		try {
			String userJsonString = userJsonObject.toString();
            System.out.println(userJsonString);
            StringEntity  stringEntity = new StringEntity(userJsonString);
//            stringEntity.setContentType("application/json;charset=UTF-8");
//            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));

			String url = getString(R.string.chochon_webserver) + "/register";
            HttpClient httpClient = new DefaultHttpClient();
            
            HttpPost httpPost = new HttpPost(url);            
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
           
            
            HttpResponse response = httpClient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.d("Json Put Sign up user", " code  " +statusCode);
            
            if (statusCode == 200) {
            	            	
                result = true;
            } 
            
        } catch (Exception e) {
            Log.d("Error", e.getLocalizedMessage());
        } 
		    
        return result;
 
        
	}
}
