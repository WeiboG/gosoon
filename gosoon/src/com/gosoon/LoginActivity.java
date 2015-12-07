package com.gosoon;

import com.gosoon.account.MyAccount;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.StringUtil;
import com.gosoon.util.ToastUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends BaseActivity{

	EditText mUsername;
	EditText mPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle("登录");

		mUsername = (EditText) findViewById(R.id.et_login_phone);
		mPassword = (EditText) findViewById(R.id.et_login_password);
	}

	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_login:
			login();
			break;
		case R.id.btn_register:
			register();
			break;
		case R.id.btn_forgetpassword:	//4-22:新增内容(忘记密码)
			findPassword();
			break;
		default:
			break;
		}
	}
	/**
	 * 我添加的内容
	 * 跳转到ResetPasswordActivity
	 */
	protected void findPassword(){
		startActivity(new Intent(this,ResetPasswordActivity.class));
	}
	protected void login() {
		String username = mUsername.getText().toString();
		String password = mPassword.getText().toString();
		if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
			return;
		}
		MyAccount.mUserName = username;
        MyAccount.mPassword = password;
        /**
         * 登录操作
         */
		MyRequest myrequest = new MyRequest(MyRequest.ACTION_LOGIN, username, password);
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(this, MainActivity.ALERTDIALOG_ID_LOGIN));
		myrequest.setAlertDialogConfig(new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_LOGIN));
		myrequest.send(loginCallback);
	}

	MyRequestCallback loginCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			MyAccount.login();
			ToastUtil.show(getApplicationContext(), "登录成功");
			finish();
		};
		@Override
		public void onFailure(MyResult result) {
//			ToastUtil.show(getApplicationContext(), "登录失败，请重试");
		};
	};

	protected void register() {
		startActivity(new Intent(this, RegisterActivity.class));
	}
}
