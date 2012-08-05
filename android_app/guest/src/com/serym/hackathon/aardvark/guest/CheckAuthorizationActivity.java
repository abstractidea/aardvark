package com.serym.hackathon.aardvark.guest;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class CheckAuthorizationActivity  extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_authorization);
		TextView successMessage = (TextView) findViewById(R.id.successMessage);
		if(getIntent().getExtras().containsKey("username") && getIntent().getExtras().containsKey("authorized")) {
			if (getIntent().getExtras().getBoolean("authorized")) {
				successMessage.setText("SUCCESS!");
				successMessage.setTextColor(getResources().getColor(R.color.green));
			} else {
				successMessage.setText("FAILURE!");
				successMessage.setTextColor(getResources().getColor(R.color.red));
			}
		}
		
		GCMRegistrar.unregister(this);
		
	}

}
