package com.serym.hackathon.aardvark.bouncer;

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

public class BouncerActivity extends Activity {

	/**
	 * Request code for Barcode Scanner scan intent.
	 */
	private static final int SCAN_REQUESTCODE = 0x478;

	/**
	 * Tag for LogCat.
	 */
	private static final String TAG = "AARDVARK-BOUNCER";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bouncer);

		Button scanButton = (Button) findViewById(R.id.scanButton);

		scanButton.setOnClickListener(scanButtonListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_bouncer, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == Activity.RESULT_OK && requestCode == SCAN_REQUESTCODE) {
			String result = intent.getStringExtra(Intents.Scan.RESULT);
			Log.i(TAG, "Read QR result: " + result);
			Toast.makeText(this.getApplicationContext(), result,
					Toast.LENGTH_LONG).show();
		}
	}

	private OnClickListener scanButtonListener = new OnClickListener() {
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
				BouncerActivity.this.showDownloadDialog();
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
							BouncerActivity.this.startActivity(intent);
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