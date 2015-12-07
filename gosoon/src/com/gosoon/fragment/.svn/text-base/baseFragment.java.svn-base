package com.gosoon.fragment;


import com.gosoon.MainActivity;
import com.gosoon.R;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

abstract public class baseFragment extends Fragment implements OnClickListener{
	
	@SuppressLint("ValidFragment")
	public baseFragment(Bundle params) {
		int index = params.getInt(MainActivity.FRAGMENT_INDEX, MainActivity.FRAGMENT_INDEX_NULL);

		Utils.logd("tag", "fragment create" + index);
	}
	
	void setTitle(View v, String title){
		TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
		if(tvTitle != null){
			tvTitle.setText(title);
		}
	}
	void hideLeftButton(View v, boolean hide){
		View button = v.findViewById(R.id.btn_back);
		if(button != null){
			button.setVisibility(hide ? View.GONE : View.VISIBLE);
		}
	}
	void setRightButton(View v, int resId){
		ImageView button = (ImageView) v.findViewById(R.id.btn_rignt);
		if(button != null){
			button.setVisibility(View.VISIBLE);
			button.setImageResource(resId);
			button.setOnClickListener(this);
		}
	}
	@Override
	public void onClick(View arg0) {
	}
	protected void getDataFromNet(){
		if(!isNetworkConnected()){
			return;
		}
	}
	@Override
	public void onResume() {
		if(!isNetworkConnected()){
			AlertDialogConfig config = new AlertDialogConfig(getActivity(), MainActivity.ALERTDIALOG_ID_ADDRESS_EDIT);
			config.message = "无网络连接，请连接网络后再试。";
			config.onPositiveListener = new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					getActivity().finish();
				}
			};
			config.show();
		}
		super.onResume();
	}
	public boolean isNetworkConnected() { 
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
		if (mNetworkInfo != null) { 
			return mNetworkInfo.isAvailable(); 
		}
		return false; 
	}
}
