package com.gosoon.fragment;

import com.gosoon.MainActivity;
import com.gosoon.R;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.StringUtil;
import com.gosoon.util.ToastUtil;
import com.lidroid.xutils.db.sqlite.CursorUtils.FindCacheSequence;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * 输入新的密码的Fragment
 * @author 潘建成
 * @since 4-23
 *
 */
public class InputNewPasdFragment extends Fragment implements OnClickListener{
	
	private Button mInputNewPasdBtn;
	private EditText mNewPassowrdEdit,mNewConformPasswordEdit;
	private static String mUserName;
	private String mPassword;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View _View = initView(inflater, container);
		initListener();
		bindData();
		return _View;
	}
	
	/**
	 * 绑定数据
	 */
	private void bindData() {
		mUserName = getArguments().getString("username");
	}
	
	/**
	 * 添加监视器
	 */
	private void initListener() {
		mInputNewPasdBtn.setOnClickListener(this);
	}
	
	/**
	 * 初始化界面
	 * @param inflater
	 * @param container
	 * @return
	 */
	private View initView(LayoutInflater inflater, ViewGroup container) {
		View _View = inflater.inflate(R.layout.fragment_resetpasd_input_newpasd, container,false);
		mInputNewPasdBtn = (Button) _View.findViewById(R.id.btn_fragment_inputnewpasd_submit);
		mNewPassowrdEdit = (EditText) _View.findViewById(R.id.et_edit_password);
		mNewConformPasswordEdit = (EditText) _View.findViewById(R.id.et_edit_conform_password);
		return _View;
	}
	@Override
	public void onClick(View v){
		if(v.getId()==R.id.btn_fragment_inputnewpasd_submit){
			if(checkPassword()){
				MyRequest myrequest = new MyRequest(MyRequest.ACTION_SMS_EDIT_PASSWORD,
						mUserName, mPassword);
				myrequest.setProcessDialogConfig(new ProgressDialogConfig(getActivity(),
						MainActivity.ALERTDIALOG_ID_REGISTER));
				myrequest.setAlertDialogConfig(new AlertDialogConfig(getActivity(),
						MainActivity.ALERTDIALOG_ID_REGISTER));
				myrequest.send(registerCallback);
				getActivity().finish();
			}
		}
	}
	MyRequestCallback registerCallback = new MyRequestCallback(){

		@Override
		public void onSuccess(MyResult result) {
			ToastUtil.show(getActivity(), "修改成功");
			getActivity().finish();
		};
		@Override
		public void onFailure(MyResult result) {
			ToastUtil.show(getActivity(), "失败");
		};
	};
	private boolean checkPassword(){
		String _ConformPassword = mNewConformPasswordEdit.getText().toString();
		mPassword = mNewPassowrdEdit.getText().toString();
		if(StringUtil.isEmpty(_ConformPassword)||StringUtil.isEmpty(mPassword)){
			ToastUtil.show(getActivity(), "密码不能为空");
			return false;
		}
		if(StringUtil.hasSpace(_ConformPassword)||StringUtil.hasSpace(mPassword)){
			ToastUtil.show(getActivity(), "密码中不能含有空格");
			return false;
		}
		if(mPassword.length()<6){
			ToastUtil.show(getActivity(), "密码长度最少为6位");
			return false;
		}
		if(_ConformPassword.equals(mPassword)==false){
			ToastUtil.show(getActivity(), "两次输入密码不一致");
			return false;
		}
		return true;
	}
}
