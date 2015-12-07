package com.gosoon;

import com.gosoon.fragment.CheckInfoFragment;

import android.app.Fragment;
import android.os.Bundle;

public class ResetPasswordActivity extends BaseActivity{
	
	Fragment mCheckInfoFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpassword);
		setTitle("重置密码");
		mCheckInfoFragment = new CheckInfoFragment();
		getFragmentManager().beginTransaction().add(R.id.layout_resetpasd, mCheckInfoFragment).commit();
		
	}
}
