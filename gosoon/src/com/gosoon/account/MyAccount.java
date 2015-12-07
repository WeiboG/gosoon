package com.gosoon.account;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gosoon.MyApplication;
import com.gosoon.addressListActivity;
import com.gosoon.entity.UserEntity;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.StringUtil;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class MyAccount {


//	18014373759
//	881017
	
	public static String mUserName;
	public static String mPassword;
	public static boolean mbLogin;
	static SharedPreferences mSharedPreferences;
	
	public static void logout() {
		mUserName = null;
		mPassword = null;
		mbLogin = false;
		mUser = null;
		mUserCallbacks.clear();
		addressListActivity.logout();
		
		Editor editer = mSharedPreferences.edit();
		editer.putBoolean("login", false);
		editer.putString("username", "");
		editer.putString("password", "");
		editer.putString("user", "");
		editer.commit();
	}
	
	public static void login() {
		mbLogin = true;
		getUserFromNet(null, false);
		
		Editor editer = mSharedPreferences.edit();
		editer.putBoolean("login", true);
		editer.putString("username", mUserName);
		editer.putString("password", mPassword);
		editer.commit();
	}
	
	
	public static void init() {
		mSharedPreferences = MyApplication.getContext().getSharedPreferences("gosoon", Context.MODE_PRIVATE);
		mbLogin = mSharedPreferences.getBoolean("login", false);
		if (mbLogin) {
			mUserName = mSharedPreferences.getString("username", "");
			mPassword = mSharedPreferences.getString("password", "");
			getUserFromNet(null, false);
		}
	}
	
	static UserEntity mUser = null;
	static ArrayList<MyRequestCallback> mUserCallbacks = new ArrayList<MyRequestCallback>();

	public static void getUserFromNet(MyRequestCallback callback, boolean forgetLocal){
		if (!mbLogin || StringUtil.isEmpty(mUserName)){
			MyResult result = new MyResult();
			result.setFailInfo(null, "请先登录");
			callback.onFailure(new MyResult());
		}
		if (mUser == null || forgetLocal) {
			if (!getUserCallback.getMyResult().isLoading()){
				MyRequest myrequest = new MyRequest(HttpMethod.POST,
						MyRequest.ACTION_SQL);
				myrequest.setSql("SELECT * from gsw_users where user_name="
						+ mUserName);
				myrequest.send(getUserCallback);
			}
			if (callback != null){
				mUserCallbacks.add(callback);
			}
		} else {
			callback.onSuccess(getUserCallback.getMyResult());
		}
	}

	public static void unRegisterUserCallback(MyRequestCallback callback){
		mUserCallbacks.remove(callback);
	}

	public static UserEntity getUser() {
		return mUser;
	}

	static MyRequestCallback getUserCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			if (result.getFirstData() != null) {
				mUser = new UserEntity(result.getFirstData());
				for (MyRequestCallback callback : mUserCallbacks){
					callback.onSuccess(result);
				}
				mUserCallbacks.clear();
				
				Editor editer = mSharedPreferences.edit();
				editer.putString("user", result.getFirstData().toString());
				editer.commit();
			}else {
				for (MyRequestCallback callback : mUserCallbacks){
					callback.onFailure(result);
				}
				mUserCallbacks.clear();
			}
		};

		@Override
		public void onFailure(MyResult result) {
			super.onFailure(result);
			for (MyRequestCallback callback : mUserCallbacks) {
				callback.onFailure(result);
			}
			mUserCallbacks.clear();
		};
	};
	
}
