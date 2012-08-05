package com.serym.hackathon.aardvark.guest;

import com.google.android.gcm.GCMRegistrar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents;
import com.serym.hackathon.aardvark.BarcodeAppDownloadDialog;

import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GuestActivity extends Activity {

	/**
	 * Tag for LogCat.
	 */
	private static final String TAG = "AARDVARK-GUEST";

	/**
	 * Sender ID for Google Cloud Messaging (GCM).
	 */
	private static final String SENDER_ID = "159055884591";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guest);

		Button generateButton = (Button) findViewById(R.id.generateButton);

		generateButton.setOnClickListener(generateButtonListener);

		// Google Cloud Messaging Code
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.isEmpty()) {
			GCMRegistrar.register(this, SENDER_ID);
			// update regId to the current id.
			regId = GCMRegistrar.getRegistrationId(this);
		} else {
			Log.v(TAG, "Already registered with Google Cloud");
		}
		Log.d(TAG, "regId = " + regId);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_guest, menu);
		return true;
	}

	private OnClickListener generateButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intents.Encode.ACTION);

			intent.putExtra(Intents.Encode.SHOW_CONTENTS, false);
			intent.putExtra(Intents.Encode.FORMAT,
					BarcodeFormat.QR_CODE.toString());
			intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
			intent.putExtra(
					Intents.Encode.DATA,
					"APA91bGyRllaDap-DU1nQZi1uspn90X7bgHJO2XZewbbnwL8lrGFf7IcLhaK6vmwimW6ALdSHLJJqGY9s0JB61b2m6Smw8leuVlFrPkZRh0nejGm5GxTmMZ9z_fTId9wiHUAMDOzUpcxDkZ03gk86KWmTYShH0NkJKd5ZkyT5cqEubE7PqHcNKI11111111111xxxxxxxxxxxxxxxx");

			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

			try {
				GuestActivity.this.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				(new BarcodeAppDownloadDialog(GuestActivity.this)).show();
			}

		}
	};

}
