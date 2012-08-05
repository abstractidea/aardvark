package com.serym.hackathon.aardvark.guest;

import com.google.android.gcm.GCMRegistrar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents;
import com.serym.hackathon.aardvark.BarcodeAppDownloadDialog;

import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
	
	/** 
	 * Temporary token used for testing the service
	 */
	
	/**
	 * Prefix to indicate an Aardvark barcode
	 */
	private static final String AARDVARK_PREFIX = "AARDVARK";
	
	/** 
	 * The temp token used 
	 */
	private static final String TEMP_TOKEN = "a1d55fd74153f0be0e873772b7673563";
	
	/**
	 * The registryId from the device
	 */
	private String regId;

	private Button generateButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guest);

		generateButton = (Button) findViewById(R.id.generateButton);

		generateButton.setOnClickListener(generateButtonListener);
			
		registerReceiver(mHandleMessageReceiver, new IntentFilter("com.serym.hackathon.aardvark.PUSH_REG_ID"));
		
		// Google Cloud Messaging Code
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		regId = GCMRegistrar.getRegistrationId(this);
		if (regId.isEmpty()) {
			GCMRegistrar.register(this, SENDER_ID);
			// update regId to the current id.
			Log.v(TAG, "New Registration ID aquired. Waiting for Broadcast");
			generateButton.setEnabled(false);
		} else {
			Log.v(TAG, "Already registered with Google Cloud with "+regId);
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
					AARDVARK_PREFIX+TEMP_TOKEN+regId);

			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

			try {
				GuestActivity.this.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				(new BarcodeAppDownloadDialog(GuestActivity.this)).show();
			}
			
			finish();

		}
	};
	
	private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
		            regId = intent.getExtras().getString("REG_ID");
					generateButton.setEnabled(true);
					Log.d(TAG, "Got broadcast, setting regId to "+regId);
				}

    };

}
