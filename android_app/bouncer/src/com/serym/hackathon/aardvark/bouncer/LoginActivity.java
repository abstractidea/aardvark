package com.serym.hackathon.aardvark.bouncer;

import com.serym.hackathon.aardvark.LoginRequest;
import com.serym.hackathon.aardvark.LoginResponse;
import com.serym.hackathon.aardvark.LoginResponseCode;
import com.serym.hackathon.aardvark.ServerRequestException;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * LoginActivity allows bouncers to login to Aardvark.
 */
public class LoginActivity extends Activity {

	/**
	 * Tag for LogCat.
	 */
	private static final String TAG = "AARDVARK-BOUNCER-LOGINACTIVITY";

	/**
	 * The name of the CharSequence in the icicle to sore the text of
	 * statusViewText.
	 */
	private static final String ICICLE_STATUSTEXTVIEW_TEXT = "statusTextView_text";

	/**
	 * The URI for the signup page.
	 */
	private static final String SIGNUP_URI = "http://hackathon.serym.com/signup";

	/**
	 * The status TextView.
	 */
	private TextView statusTextView = null;

	/**
	 * The username EditText.
	 */
	private EditText usernameEditText = null;

	/**
	 * The password EditText.
	 */
	private EditText passwordEditText = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		// Get view elements by id
		statusTextView = (TextView) findViewById(R.id.statusTextView);
		usernameEditText = (EditText) findViewById(R.id.username_edit);
		passwordEditText = (EditText) findViewById(R.id.password_edit);
		Button loginButton = (Button) findViewById(R.id.login_button);
		Button signupButton = (Button) findViewById(R.id.signup_button);

		// Setup listeners
		loginButton.setOnClickListener(new LoginButtonListener());
		signupButton.setOnClickListener(new SignupButtonListener());
	}

	@Override
	public void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);

		icicle.putCharSequence(ICICLE_STATUSTEXTVIEW_TEXT,
				statusTextView.getText());
	}

	@Override
	public void onRestoreInstanceState(Bundle icicle) {
		super.onRestoreInstanceState(icicle);

		// Restore statusTextView text
		CharSequence statusTextView_text = icicle
				.getCharSequence(ICICLE_STATUSTEXTVIEW_TEXT);
		if (statusTextView_text != null) {
			statusTextView.setText(statusTextView_text);
		}
	}

	/**
	 * Listener class for the login button.
	 */
	private class LoginButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String username = usernameEditText.getText().toString();
			String password = passwordEditText.getText().toString();

			if (username.isEmpty()) {
				statusTextView.setText(getString(R.string.no_username_msg));
			} else if (password.isEmpty()) {
				statusTextView.setText(getString(R.string.no_password_msg));
			} else {
				// TODO hash password
				
				LoginRequest request = new LoginRequest(username, password);
				(new SendLoginRequestTask()).execute(request);
			}
		}
	};

	/**
	 * Listener class for the signup button.
	 */
	private class SignupButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(SIGNUP_URI));
			startActivity(intent);
		}
	};

	/**
	 * Task class for sending a LoginRequest and handling the LoginResponse.
	 */
	private class SendLoginRequestTask extends
			AsyncTask<LoginRequest, Object, LoginResponse> {
		/**
		 * The exception, if any, that occurred while executing this task.
		 * Otherwise, null.
		 */
		private Exception executeException = null;

		@Override
		protected LoginResponse doInBackground(LoginRequest... requests) {
			LoginResponse response = null;
			try {
				response = requests[0].send();
			} catch (ServerRequestException e) {
				executeException = e;
			}
			return response;
		}

		@Override
		protected void onPostExecute(LoginResponse response) {
			if (this.executeException == null) {
				if (response.getResponseCode() == LoginResponseCode.APPROVED) {
					Intent intent = new Intent(LoginActivity.this,
							BouncerActivity.class);
					intent.putExtra(BouncerActivity.INTENT_EXTRA_TOKEN,
							response.getToken());
					startActivity(intent);
					finish();
				} else {
					statusTextView
							.setText(getText(R.string.login_rejected_msg));
				}
			} else {
				this.executeException.printStackTrace();
				Log.e(TAG, "Login exception", this.executeException);
				statusTextView.setText(this.executeException.getMessage());
			}
		}
	}

}
