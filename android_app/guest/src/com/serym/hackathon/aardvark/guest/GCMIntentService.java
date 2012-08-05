package com.serym.hackathon.aardvark.guest;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onRegistered(Context context, String regId) {
		/*
		 * Not needed, since the the bouncer will send the regId
		 */
		
		// broadcast the RegId for reception by the activity
		Intent intent = new Intent("com.serym.hackathon.aardvark.PUSH_REG_ID");
        intent.putExtra("REG_ID", regId);
        context.sendBroadcast(intent);
		
		Log.d("AARDVARK", "REGID: "+regId);
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		/*
		 * Not needed, since we only send one cloud message.
		 */
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		boolean wasAccepted = Boolean.parseBoolean(intent.getExtras().getString("authorization"));
		Intent guestActivity = new Intent(context, CheckAuthorizationActivity.class);
		guestActivity.putExtra("authorized", wasAccepted);
		guestActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		guestActivity.putExtra("username", intent.getExtras().getString("user_id"));
		startActivity(guestActivity);
	}

	@Override
	protected void onError(Context context, String errorId) {
		Log.d("AARDVARK", "ERROR: "+errorId);
	}

}
