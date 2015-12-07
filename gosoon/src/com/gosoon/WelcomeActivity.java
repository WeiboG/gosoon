package com.gosoon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeActivity extends Activity{

	Handler mHandler = getEndActivityhandler();
	
	public static final int DURATION = 1000*4;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		mHandler.sendEmptyMessageDelayed(0, DURATION);
	}
	
	private Handler getEndActivityhandler(){
		return new Handler(){
			public void handleMessage(android.os.Message msg) {
				startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				finish();
			};
		};
	}
}
