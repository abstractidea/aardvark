package com.serym.hackathon.aardvark.bouncer;

import com.google.zxing.client.android.Intents;
import com.serym.hackathon.aardvark.BarcodeAppDownloadDialog;
import com.serym.hackathon.aardvark.ServerRequestException;
import com.serym.hackathon.aardvark.SoundManager;
import com.serym.hackathon.aardvark.SoundType;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * BouncerActivity is where bouncers can scan guest codes.
 */
public class BouncerActivity extends Activity {

	/**
	 * The name of the extra field for the token.
	 */
	public static final String INTENT_EXTRA_TOKEN = "token";

	// TODO
	private static final String TEMP_EVENT_ID = "12345678";

	// TODO
	private static final String TEST_QRCODE = "AARDVARKa1d55fd74153f0be0e873772b7673563APA91bGO8G-mf9GJB98xXBM4bvLe-adSn7vyy-l_CAyWIXhTo52x8zEUHOQmg7R2lCY015rg8a6QakNsjkGUTjDweJYnnxs584irTpwTxZPoCZ1z6rWW8hMis_-By9h2qS5s5mjVLlQTRmd7yGg2wvrYEEm_FqPFv3dQb-YK4-W4qLGxcrG3bW0";

	/**
	 * Tag for LogCat.
	 */
	private static final String TAG = "AARDVARK-BOUNCER";

	/**
	 * Request code for Barcode Scanner scan intent.
	 */
	private static final int SCAN_REQUESTCODE = 0x478;

	/**
	 * The name of the CharSequence in the icicle to sore the text of
	 * statusViewText.
	 */
	private static final String ICICLE_STATUSTEXTVIEW_TEXT = "statusTextView_text";

	/**
	 * The status TextView.
	 */
	private TextView statusTextView = null;

	/**
	 * The token for this bouncer.
	 */
	private String bouncerToken;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get the bouncer token
		Intent originIntent = getIntent();
		if (originIntent == null || !originIntent.hasExtra(INTENT_EXTRA_TOKEN)) {
			throw new IllegalStateException(
					"Activity must be started with extra: "
							+ INTENT_EXTRA_TOKEN);
		}
		bouncerToken = originIntent.getExtras().getString(INTENT_EXTRA_TOKEN);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bouncer);

		// Get view elements by id
		statusTextView = (TextView) findViewById(R.id.statusTextView);
		Button scanButton = (Button) findViewById(R.id.scanButton);
		Button testRequestButton = (Button) findViewById(R.id.testRequestButton);

		// Initialize sound manager
		SoundManager.setContext(this);

		// Setup listeners
		scanButton.setOnClickListener(new ScanButtonListener());
		testRequestButton.setOnClickListener(new TestRequestButtonListener());
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
	 * Handles the Barcode Scanner scan intent result.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == Activity.RESULT_OK && requestCode == SCAN_REQUESTCODE) {

			String result = intent.getStringExtra(Intents.Scan.RESULT);

			Log.d(TAG, "Guest code contents: " + result);

			CheckinRequest request = null;
			try {
				request = CheckinRequest.createFromCode(result, bouncerToken,
						TEMP_EVENT_ID);
			} catch (IllegalArgumentException e) {
				statusTextView.setText(R.string.invalid_guest_code_msg);
			}

			if (request != null) {
				(new SendCheckinRequestTask()).execute(request);
			}
		}
	}

	/**
	 * Listener class for the scan guest code button.
	 */
	private class ScanButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intents.Scan.ACTION);

			intent.putExtra(Intents.Scan.MODE, Intents.Scan.QR_CODE_MODE);
			intent.putExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, 500L);
			intent.putExtra(Intents.Scan.SAVE_HISTORY, false);
			intent.putExtra(Intents.Scan.PROMPT_MESSAGE,
					R.string.scan_prompt_msg);

			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

			try {
				BouncerActivity.this.startActivityForResult(intent,
						BouncerActivity.SCAN_REQUESTCODE);
			} catch (ActivityNotFoundException e) {
				(new BarcodeAppDownloadDialog(BouncerActivity.this)).show();
			}
		}
	};

	/**
	 * Listener class for the test request button.
	 */
	private class TestRequestButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			CheckinRequest request = null;
			try {
				request = CheckinRequest.createFromCode(TEST_QRCODE,
						bouncerToken, TEMP_EVENT_ID);
			} catch (IllegalArgumentException e) {
				statusTextView.setText(R.string.invalid_guest_code_msg);
			}

			if (request != null) {
				(new SendCheckinRequestTask()).execute(request);
			}
		}
	};

	/**
	 * Task class for sending a CheckinRequest and handling the CheckinResponse.
	 */
	private class SendCheckinRequestTask extends
			AsyncTask<CheckinRequest, Object, CheckinResponse> {
		/**
		 * The exception, if any, that occurred while executing this task.
		 * Otherwise, null.
		 */
		private Exception executeException = null;

		@Override
		protected CheckinResponse doInBackground(CheckinRequest... requests) {
			CheckinResponse response = null;
			try {
				response = requests[0].send();
			} catch (ServerRequestException e) {
				executeException = e;
			}
			return response;
		}

		@Override
		protected void onPostExecute(CheckinResponse response) {
			if (this.executeException == null) {
				if (response.getResponseCode() == CheckinResponseCode.APPROVED) {
					SoundManager.playSound(SoundType.ACCEPT);

					statusTextView
							.setText(getString(R.string.checkin_accepted_msg)
									+ " " + response.getUserName());
				} else {
					SoundManager.playSound(SoundType.REJECT);

					statusTextView
							.setText(getString(R.string.checkin_rejected_msg));
				}
			} else {
				this.executeException.printStackTrace();
				Log.e(TAG, "Check-in exception", this.executeException);
				statusTextView.setText(this.executeException.getMessage());
			}
		}
	}

}
