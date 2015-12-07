package com.gosoon;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.gosoon.account.MyAccount;
import com.gosoon.entity.UserEntity;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.StringUtil;
import com.gosoon.util.ToastUtil;
import com.gosoon.util.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity{
	
	static final int COUNT_DOWN_SECOND = 120;
	
	EditText mUsername;
	EditText mPassword;
	EditText mRePassword;
	EditText mVerifyCode;
	EditText mTuiJian;
    Button btn_verify_code;
	
	String mVerifyCodeStr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		//设置标题
		setTitle("用户注册");

		mUsername = (EditText) findViewById(R.id.et_reg_phone);				//手机号
		mPassword = (EditText) findViewById(R.id.et_reg_password);			//密码
		mRePassword = (EditText) findViewById(R.id.et_reg_repassword);		//再输入一次密码
		mVerifyCode = (EditText) findViewById(R.id.et_verify_code);			//输入验证码
		btn_verify_code = (Button) findViewById(R.id.btn_verify_code);		//发送验证码
		mTuiJian = (EditText) findViewById(R.id.et_reg_tuijian);            //推荐人账号
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		stopCountDown();
	}
	
	/**
	 * 在布局文件中已经定义了点击的方法，所以不需要设置监听器
	 */
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_submit:		//提交按钮
			register();
			break;
		case R.id.btn_verify_code:  //发送验证码
			clickOnSendVerifyCodebtn();
			break;
		default:
			break;
		}
	}
	/**
	 * 修改，之前版本把检查手机号和发送验证码写在一起，没有实现分离，导致“提交”按钮出现Bug
	 */
	private void clickOnSendVerifyCodebtn() {
		if(checkPhoneNumber()){
			String _Username = mUsername.getText().toString();
			MyRequest myRequest = getSendVerifyCodeRequest(_Username);
			myRequest.send(new MyRequestCallback(){
				@Override
				public void onSuccess(MyResult result){
					super.onSuccess(result);
					//如果能得到数据，则说明手机号已被注册
					if(result.getFirstData() != null){
						ToastUtil.show(RegisterActivity.this, "该手机号已被注册");
					}else{
						/**
						 * 如果输入了推荐人账号，则进行判断，如果没输入，则直接发送验证码
						 */
						if(mTuiJian.getText().toString().length()!=0){
							if(checkReferee()){
								checkRefereeExists();
							}
						}else{
							sendVerifyCode();
						}
					}
				}
				@Override
				public void onFailure(MyResult result){
					super.onFailure(result);
					ToastUtil.show(RegisterActivity.this, "操作失败请重试");
				}
			});
		}
	}
	
	/**
	 * 得到一个通用的MyRequest对象，查询用户存在与否
	 */
	private MyRequest getSendVerifyCodeRequest(String name){
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		myrequest.setSql("SELECT * from gsw_users where user_name='"
				+ name+ "'");
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(this,
				MainActivity.PROCESSDIALOG_ID_GETGOODSDETAIL));
		myrequest.setAlertDialogConfig(new AlertDialogConfig(this,
				MainActivity.ALERTDIALOG_ID_GETGOODSDETAIL));
		return myrequest;
	}
	
	/**
	 * 检查推荐人账号是否存在,如果存在则发送验证码
	 */
	
	private void checkRefereeExists() {
		String referee = mTuiJian.getText().toString();
		MyRequest myRequest = getSendVerifyCodeRequest(referee);
		myRequest.send(new MyRequestCallback(){
			@Override
			public void onSuccess(MyResult result) {
				super.onSuccess(result);
				if(result.getFirstData()==null){
					ToastUtil.show(RegisterActivity.this, "推荐人账号不存在");
				}else{
					sendVerifyCode();
				}
			}
			
			@Override
			public void onFailure(MyResult result) {
				// TODO Auto-generated method stub
				super.onFailure(result);
			}
		});
		
	}
	/**
	 * 发送验证码
	 */
	private void sendVerifyCode(){
		mVerifyCode.setText("");
		btn_verify_code.setEnabled(false);
		mVerifyCodeStr = Utils.randVerifyCode();
		Log.e("VerifyCode",mVerifyCodeStr);
		String username = "gsw";
		String password = "123456";
		String passwordMD5 = Utils.md5(password);
		String phoneNumber = mUsername.getText().toString();
		String content = "";
		try {
			content = URLEncoder.encode("【菓速网】会员注册验证码：" + mVerifyCodeStr,
					"utf-8");
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
			return;
		}
		String requestUrl = "http://www.cangcool.com/sms.action?u=" + username
				+ "&p=" + passwordMD5 + "&m=" + phoneNumber + "&c=" + content + "&g=147";
		
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);
		httpUtils.send(HttpMethod.POST, requestUrl,
				new RequestCallBack<String>(){
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo){
						String result = responseInfo.result;
						int index = result.indexOf('\n');
						if (index > 0 && index < result.length()) {
							
							result = result.substring(0, index);
							if (result.equals("0")) {
								ToastUtil.show(getApplicationContext(),
										"验证码发送成功");
								startCountDown();
								return;
							}
						}
						ToastUtil.show(getApplicationContext(), "验证码发送失败");
						btn_verify_code.setEnabled(true);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						ToastUtil.show(getApplicationContext(), "验证码发送失败");
						btn_verify_code.setEnabled(true);
					}

				});
	}

	private void register(){
		if (!checkPhoneNumber() || !checkPassword() || !checkVerifyCode()){
			String referee = mTuiJian.getText().toString();
			//推荐人账号可能没有输入，如果输入则判断是否正确，如果没输入则不做判断
			if(referee.length()>=1&&referee.length()<=11)
				checkReferee();
			return;
		}
		stopCountDown();
		String username = mUsername.getText().toString();
		String password = mPassword.getText().toString();
		String tuijian = mTuiJian.getText().toString();
		if(tuijian==null)
			registerWithoutReferee(username, password);
		else{
			registerWithReferee(username, password, tuijian);
		}
	}
	
	/**
	 * 带推荐人的注册
	 * @param username 用户名
	 * @param password 密码
	 * @param tuijian 推荐人
	 */
	private void registerWithReferee(String username, String password,String tuijian){
		MyRequest myrequest = new MyRequest(MyRequest.ACTION_REGISTER,
				username, password,tuijian);
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(this,
				MainActivity.ALERTDIALOG_ID_REGISTER));
		myrequest.setAlertDialogConfig(new AlertDialogConfig(this,
				MainActivity.ALERTDIALOG_ID_REGISTER));
		myrequest.send(registerCallback);
	}
	
	/**
	 * 不带推荐人的注册
	 * @param username 用户名
	 * @param password 密码
	 */
	private void registerWithoutReferee(String username, String password) {
		MyRequest myrequest = new MyRequest(MyRequest.ACTION_REGISTER,
				username, password);
		
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(this,
				MainActivity.ALERTDIALOG_ID_REGISTER));
		myrequest.setAlertDialogConfig(new AlertDialogConfig(this,
				MainActivity.ALERTDIALOG_ID_REGISTER));
		myrequest.send(registerCallback);
	}

	MyRequestCallback registerCallback = new MyRequestCallback(){

		@Override
		public void onSuccess(MyResult result) {
			ToastUtil.show(getApplicationContext(), "注册成功");
			finish();
		};
		@Override
		public void onFailure(MyResult result) {
			ToastUtil.show(getApplicationContext(), "注册失败，请重试");
		};
	};
	
	/**
	 * 检查推荐人账号是否合法
	 * @return
	 */
	private boolean checkReferee(){
		
		String referee = mTuiJian.getText().toString();
		if (StringUtil.hasSpace(referee) || !Utils.isMobileNO(referee)){
			ToastUtil.show(this, "推荐人账号不正确");
			return false;
		}
		return true;
		
	}
	
	/**
	 * 检查手机号的合法性
	 * @return
	 */
	private boolean checkPhoneNumber(){
		String username = mUsername.getText().toString();
		if (StringUtil.isEmpty(username)) {
			ToastUtil.show(this, "用户名不能为空");
			return false;
		}
		if (StringUtil.hasSpace(username) || !Utils.isMobileNO(username)){
			ToastUtil.show(this, "手机号码不正确");
			return false;
		}
		return true;
	}
	
	private boolean checkPassword(){
		String password = mPassword.getText().toString();
		String rePassword = mRePassword.getText().toString();
		if (StringUtil.isEmpty(password) || StringUtil.isEmpty(rePassword)){
			ToastUtil.show(this, "密码不能为空");
			return false;
		}
		if (StringUtil.hasSpace(password) || StringUtil.hasSpace(rePassword)){
			ToastUtil.show(this, "密码不能有空格");
			return false;
		}
		if(password.length()<6){
			ToastUtil.show(this, "密码长度最少为6位");
			return false;
		}
		if (!password.equals(rePassword)) {
			ToastUtil.show(this, "两次密码输入不一致");
			return false;
		}
		return true;
	}
	
	//比较输入的验证码
	private boolean checkVerifyCode() {
		String verifycode = mVerifyCode.getText().toString();
		if (StringUtil.isEmpty(mVerifyCodeStr)){
			ToastUtil.show(this, "请发送验证码");
			return false;
		}
		if (!mVerifyCodeStr.equals(verifycode)) {
			ToastUtil.show(this, "验证码错误");
			return false;
		}
		return true;
	}

	CountDownTimer mCountDown;
	private void startCountDown() {
		
		mCountDown = new CountDownTimer(COUNT_DOWN_SECOND * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished){
				updateVerifyCodeStatus((int) (millisUntilFinished / 1000));
			}
			@Override
			public void onFinish(){
				updateVerifyCodeStatus(0);
			}
		};
		mCountDown.start();
	}

	private void stopCountDown(){
	    if (mCountDown != null){
	    	mCountDown.cancel();
	    	btn_verify_code.setEnabled(true);
			btn_verify_code.setText(R.string.send_verify_code);
	    	mCountDown = null;
	    	mVerifyCodeStr = null;
	    }
	}

	private void updateVerifyCodeStatus(int leftTime) {
		if (leftTime == 0) {
			btn_verify_code.setEnabled(true);
			btn_verify_code.setClickable(true);
			btn_verify_code.setText(R.string.send_verify_code);
			mCountDown = null;
			mVerifyCodeStr = null;
		} else {
			btn_verify_code.setEnabled(false);
			btn_verify_code.setClickable(false);
			btn_verify_code.setText(leftTime + "秒");
		}
	}
}
