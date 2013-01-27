package com.oportfolio.oportfolio.authenticator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.oportfolio.oportfolio.HttpUtils;
import com.oportfolio.oportfolio.R;
import com.oportfolio.oportfolio.data.interfaces.IBase;

public class opAuthenticatorActivity extends AccountAuthenticatorActivity {
	public static final String PARAM_AUTHTOKEN_TYPE = "com.oportfolio.AUTHTOKENTYPE";
	public static final String PARAM_CONFIRMCREDENTIALS = "com.oportfolio.CONFIRMCREDENTIALS";
	public static final String PARAM_PASSWORD = "com.oportfolio.PASSWORD";
	public static final String PARAM_USERNAME = "com.oportfolio.USERNAME";

	AccountManager mAccountManager = null;


	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail = "admin@nhs.com";
	private String mPassword;

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

		mAccountManager = AccountManager.get(this);

		// Set up the login form.
		mEmail = getIntent().getStringExtra(PARAM_USERNAME);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText("test");
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
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

		findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
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
			JSONObject userName = new JSONObject();
			try {
				userName.put(IBase.UserTags.username.name(), mEmailView.getText().toString());
				userName.put(IBase.UserTags.password.name(), mPasswordView.getText().toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//byte[] encoded =  Base64.encode(userName.toString().getBytes(),0);
			mAuthTask.execute(userName.toString());
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
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate()
			.setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate()
			.setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}


	class ResponseClass{
		Integer mResultCode;
		String  mErrorMessage;
		JSONObject mBody;
		String  mToken;
	}
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<String, Void, ResponseClass> {
		@Override
		protected ResponseClass doInBackground(String... params) {
			ResponseClass resultClass = new ResponseClass();
			PasswordAuthentication passwordAuthentication = null;
			try {
				String messageBody = null;
				if(params.length > 0){
					messageBody = params[0];
					JSONObject obj = new JSONObject(messageBody);
					passwordAuthentication = new  PasswordAuthentication(obj.getString(IBase.UserTags.username.name()), 
							obj.getString(IBase.UserTags.password.name()).toCharArray());
				}

				
				
				//HttpURLConnection resultConnection = HttpUtils.openUrl(IBase.URL_LOGIN, HttpUtils.Method.POST, null, null , messageBody);
				HttpURLConnection resultConnection = HttpUtils.openUrl(IBase.URL_LOGIN, HttpUtils.Method.GET, null, null , null,passwordAuthentication);
				try {
				
				resultClass.mResultCode = resultConnection.getResponseCode();
				if (resultClass.mResultCode == HttpURLConnection.HTTP_OK ||
						resultClass.mResultCode == HttpURLConnection.HTTP_CREATED) {
					JSONObject jsonObj = new JSONObject(HttpUtils.readStream(resultConnection.getInputStream()));
					resultClass.mBody = jsonObj;
					
				} else {
					resultClass.mErrorMessage = HttpUtils.readStream(resultConnection.getErrorStream());
				}
				} finally {
					resultConnection.disconnect();
				}
			}  catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return resultClass;
		}

		@Override
		protected void onPostExecute(final ResponseClass resultClass) {
			mAuthTask = null;
			showProgress(false);

			if (resultClass.mResultCode == HttpURLConnection.HTTP_OK ||
					resultClass.mResultCode == HttpURLConnection.HTTP_CREATED) {
				createAccount(resultClass.mToken, resultClass.mBody);
				finish();
			} else {
				if(TextUtils.isEmpty(resultClass.mErrorMessage)){
					mPasswordView.setError(getString(R.string.error_incorrect_password));
				} else {
					mPasswordView.setError(resultClass.mErrorMessage);
				}
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	public void createAccount(String authToken, JSONObject body ){
		// This is the magic that addes the account to the Android Account Manager  
		final Account account = new Account(mEmailView.getText().toString(), IBase.ACCOUNT_TYPE);
		Bundle bundle = new Bundle();
		bundle.putString(AccountManager.KEY_USERDATA, body.toString());
		bundle.putString(AccountManager.KEY_ACCOUNT_NAME, mEmailView.getText().toString());
		bundle.putString(AccountManager.KEY_PASSWORD, mPasswordView.getText().toString());
		mAccountManager.addAccountExplicitly(account, mPasswordView.getText().toString(), bundle);



		final Intent intent = new Intent();  

		intent.putExtra(AccountManager.KEY_PASSWORD, mPasswordView.getText());
		intent.putExtra(AccountManager.KEY_USERDATA, body.toString());
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mEmailView.getText().toString());  
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, IBase.ACCOUNT_TYPE);  
		intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);  
		this.setAccountAuthenticatorResult(intent.getExtras());  
		this.setResult(RESULT_OK, intent);
	}

}
