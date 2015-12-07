package com.gosoon.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.gosoon.MainActivity;
import com.gosoon.R;
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

import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * 找回密码时用来验证信息的Fragment
 * @author 潘建成
 * @since 4-23
 *
 */
public class CheckInfoFragment extends Fragment implements OnClickListener{
	
	static final int COUNT_DOWN_SECOND = 120;
	
	private Button mSubmitBtn,mSendVerifyCodeBtn;
	private EditText mPhoneNumberEdit,mVerifyCodeEdit;
	private InputNewPasdFragment mInputNewPasdFragment;
	private String mPhoneNumber;
	private String mVerifyCodeStr;
	private boolean isExist = false;
	private CountDownTimer mCountDown;

	public CheckInfoFragment() {
		mInputNewPasdFragment = new InputNewPasdFragment();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			View _View = initView(inflater, container);
			initListener();
			return _View;
	}
	/**
	 * 添加监听器
	 */
	private void initListener() {
		mSubmitBtn.setOnClickListener(this);
		mSendVerifyCodeBtn.setOnClickListener(this);
	}
	/**
	 * 初始化界面
	 * @param inflater
	 * @param container
	 * @return
	 */
	private View initView(LayoutInflater inflater, ViewGroup container) {
		View _View = inflater.inflate(R.layout.fragment_resetpasd_checkinfo, container,false);
		mSubmitBtn = (Button) _View.findViewById(R.id.btn_fragment_checkinfo_submit);
		mSendVerifyCodeBtn = (Button) _View.findViewById(R.id.btn_fragment_send_verify_code);
		mPhoneNumberEdit = (EditText) _View.findViewById(R.id.et_fragment_phone_number);
		mVerifyCodeEdit = (EditText) _View.findViewById(R.id.et_fragment_verify_code);
		return _View;
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btn_fragment_checkinfo_submit){
			clickOnSubmitBtn();
		}else if(v.getId()==R.id.btn_fragment_send_verify_code){
			clickOnSendVerifyCodeBtn();
		}
	}
	
	/**
	 *对“提交”按钮的响应 
	 */
	private void clickOnSubmitBtn() {
		if(checkVerifyCode()){
			stopCountDown();
			Bundle bundle = new Bundle();
			mPhoneNumber = mPhoneNumberEdit.getText().toString();
			bundle.putString("username", mPhoneNumber);
			mInputNewPasdFragment.setArguments(bundle);
			getActivity().getFragmentManager().beginTransaction().
				replace(R.id.layout_resetpasd, mInputNewPasdFragment).commit();
		}
	}
	
	/**
	 * 对点击“发送验证码”按钮的响应
	 */
	private void clickOnSendVerifyCodeBtn() {
		mPhoneNumber = mPhoneNumberEdit.getText().toString();
		if(checkPhoneNumber()){
			//手机号合法则发送验证码
//			sendVerifyCode();
			MyRequest myrequest = new MyRequest(HttpMethod.POST,
					MyRequest.ACTION_SQL);
			myrequest.setSql("SELECT * from gsw_users where user_name='"
					+ mPhoneNumber + "'");
			myrequest.setProcessDialogConfig(new ProgressDialogConfig(getActivity(),
					MainActivity.PROCESSDIALOG_ID_GETGOODSDETAIL));
			myrequest.setAlertDialogConfig(new AlertDialogConfig(getActivity(),
					MainActivity.ALERTDIALOG_ID_GETGOODSDETAIL));
			myrequest.send(new MyRequestCallback(){
				@Override
				public void onSuccess(MyResult result){
					super.onSuccess(result);
					//如果能得到数据，则说明手机号已被注册
					if(result.getFirstData() == null){
						isExist = false;
						ToastUtil.show(getActivity(), "该手机号没有注册");
					}else{
						sendVerifyCode();
					}
				}
				@Override
				public void onFailure(MyResult result){
					super.onFailure(result);
					ToastUtil.show(getActivity(), "操作失败请重试");
				}
			});
		}
	}
	
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
	
	/**
	 * 在“发送验证码”按钮显示剩余时间
	 * @param leftTime
	 */
	private void updateVerifyCodeStatus(int leftTime) {
		if (leftTime == 0) {
			mSendVerifyCodeBtn.setEnabled(true);
			mSendVerifyCodeBtn.setText(R.string.send_verify_code);
			mCountDown = null;
			mVerifyCodeStr = null;
		} else {
			mSendVerifyCodeBtn.setText(leftTime + "秒");
		}
	}
	
	private void stopCountDown() {
	    if (mCountDown != null) {
	    	mCountDown.cancel();
	    	mSendVerifyCodeBtn.setEnabled(true);
	    	mSendVerifyCodeBtn.setText(R.string.send_verify_code);
	    	mCountDown = null;
	    	mVerifyCodeStr = null;
	    }
	}
	
	/**
	 * 检查手机号是否合法，是否被注册
	 * @return 合法则输出true，否则输出false
	 */
	private boolean checkPhoneNumber(){
		 
		String username = mPhoneNumber;
		if (StringUtil.isEmpty(username)){
			ToastUtil.show(getActivity(), "用户名不能为空");
			return false;
		}
		if (StringUtil.hasSpace(username) || !Utils.isMobileNO(username)){
			ToastUtil.show(getActivity(), "手机号码不正确");
			return false;
		}
		
		return true;
	}
	
	//比较输入的验证码
	private boolean checkVerifyCode(){
		String verifyCode = mVerifyCodeEdit.getText().toString();
		if (StringUtil.isEmpty(mVerifyCodeStr)){
			ToastUtil.show(getActivity(), "请发送验证码");
			return false;
		}
		if (!mVerifyCodeStr.equals(verifyCode)) {
			ToastUtil.show(getActivity(), "验证码错误");
			return false;
		}
		return true;
	}
		
		
	private void sendVerifyCode(){
		mSendVerifyCodeBtn.setEnabled(false);
		mVerifyCodeStr = Utils.randVerifyCode();
		String username = "gsw";
		String password = "123456";
		String passwordMD5 = Utils.md5(password);
		String phoneNumber = mPhoneNumber;
		String content = "";
		try {
			content = URLEncoder.encode("【菓速网】会员注册验证码：" + mVerifyCodeStr,
					"utf-8");
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
			return;
		}
		Log.e("验证码", mVerifyCodeStr);
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
								ToastUtil.show(getActivity(),
										"验证码发送成功");
								startCountDown();
								mSendVerifyCodeBtn.setEnabled(false);
								return;
							}
						}
						ToastUtil.show(getActivity(), "验证码发送失败");
						mSendVerifyCodeBtn.setEnabled(true);
					}

					@Override
					public void onFailure(HttpException error, String msg){
						ToastUtil.show(getActivity(), "验证码发送失败");
						mSendVerifyCodeBtn.setEnabled(true);
					}
				});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopCountDown();
	}
}
