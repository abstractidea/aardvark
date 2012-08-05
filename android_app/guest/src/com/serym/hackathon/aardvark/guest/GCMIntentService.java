package com.serym.hackathon.aardvark.guest;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onRegistered(Context context, String regId) {
		/*
		 * Not needed, since the the bouncer will send the regId
		 */
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		/*
		 * Not needed, since we only send one cloud message.
		 */
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Toast.makeText(context, "Got a message", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onError(Context context, String errorId) {
		Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();

	}

}
