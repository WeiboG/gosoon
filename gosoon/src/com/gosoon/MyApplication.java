package com.gosoon;

import com.gosoon.account.MyAccount;
import com.baidu.frontia.FrontiaApplication;
import com.gosoon.util.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class MyApplication extends FrontiaApplication{
    
	public static Activity TopActivity;
	private static Context context;
	@Override
	public void onCreate(){
		super.onCreate();
		context = this.getApplicationContext();
		Utils.initUtils(context);
		MyAccount.init();
	}
	public static Context getContext(){
		return context;
	}
}
