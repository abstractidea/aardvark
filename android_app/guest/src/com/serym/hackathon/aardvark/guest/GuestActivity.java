package com.serym.hackathon.aardvark.guest;

import com.google.android.gcm.GCMRegistrar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class GuestActivity extends Activity {

	/**
	 * Request code for Barcode Scanner scan intent.
	 */
	private static final int SCAN_REQUESTCODE = 0x478;

	/**
	 * Sender ID for Google Cloud Messaging (GCM).
	 */
	private static final String SENDER_ID = "0";

	/**
	 * Tag for LogCat.
	 */
	private static final String TAG = "AARDVARK-GUEST";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guest);

		Button generateButton = (Button) findViewById(R.id.generateButton);

		generateButton.setOnClickListener(generateButtonListener);

		// Google Cloud Messaging Code
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.isEmpty()) {
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			Log.v(TAG, "Already registered with Google Cloud");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_guest, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == Activity.RESULT_OK && requestCode == SCAN_REQUESTCODE) {
			String result = intent.getStringExtra(Intents.Scan.RESULT);
			Log.i(TAG, "Read QR result: " + result);
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
					.show();
		}
	}

	private OnClickListener generateButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intents.Encode.ACTION);

			intent.putExtra(Intents.Encode.SHOW_CONTENTS, false);
			intent.putExtra(Intents.Encode.FORMAT,
					BarcodeFormat.QR_CODE.toString());
			intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
			intent.putExtra(Intents.Encode.DATA, "CATS CATS CATS YOSHI CATS");

			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

			try {
				GuestActivity.this.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				GuestActivity.this.showDownloadDialog();
			}

		}
	};

	// From com.google.zxing.integration.android.IntentIntegrator
	private AlertDialog showDownloadDialog() {
		AlertDialog.Builder downloadDialog = new AlertDialog.Builder(this);
		downloadDialog.setTitle("Install Barcode Scanner?");
		downloadDialog
				.setMessage("This application requires Barcode Scanner. Would you like to install it?");
		downloadDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						Uri uri = Uri
								.parse("market://details?id=com.google.zxing.client.android");
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						try {
							GuestActivity.this.startActivity(intent);
						} catch (ActivityNotFoundException anfe) {
							// Hmm, market is not installed
							Log.w(TAG,
									"Android Market is not installed; cannot install Barcode Scanner");
						}
					}
				});
		downloadDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					// @Override
					public void onClick(DialogInterface dialogInterface, int i) {
					}
				});
		return downloadDialog.show();
	}

}