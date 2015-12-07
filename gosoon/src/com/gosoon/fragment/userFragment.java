package com.gosoon.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gosoon.BonusActivity;
import com.gosoon.IntegralActivity;
import com.gosoon.LoginActivity;
import com.gosoon.MainActivity;
import com.gosoon.MyCollectionActivity;
import com.gosoon.MyOrderActivity;
import com.gosoon.R;
import com.gosoon.RechargeActivity;
import com.gosoon.addressListActivity;
import com.gosoon.account.MyAccount;
import com.gosoon.entity.BonusEntity;
import com.gosoon.entity.UserEntity;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;

@SuppressLint("ValidFragment")
public class userFragment extends baseFragment{
	TextView tv_user_name, tv_user_banlance;
	Button btn_logout;
	View user_my_order, user_my_colletion, user_my_address, user_charge,user_my_bonus,user_my_integral;
	TextView tv_bonus_count;
	TextView tv_user_integral;
	@SuppressLint("ValidFragment")
	public userFragment(Bundle params) {
		super(params);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View main = inflater.inflate(R.layout.fragment_user, container, false);
		setTitle(main, getString(R.string.navi_user));
		hideLeftButton(main, true);
		
		tv_user_name = (TextView) main.findViewById(R.id.tv_user_name);
		tv_user_banlance = (TextView) main.findViewById(R.id.tv_user_banlance);
		tv_bonus_count = (TextView) main.findViewById(R.id.bonus_count);
		tv_user_integral = (TextView) main.findViewById(R.id.tv_user_integral);
		
		user_my_order = main.findViewById(R.id.user_my_order);
		user_my_colletion = main.findViewById(R.id.user_my_colletion);
		user_my_address = main.findViewById(R.id.user_my_address);
		user_charge = main.findViewById(R.id.user_charge);
		user_my_bonus = main.findViewById(R.id.user_my_bonus);
		user_my_integral = main.findViewById(R.id.user_my_integral);
		
		btn_logout = (Button) main.findViewById(R.id.btn_logout);
		btn_logout.setOnClickListener(this);
		user_my_order.setOnClickListener(this);
		user_my_colletion.setOnClickListener(this);
		user_my_address.setOnClickListener(this);
		user_charge.setOnClickListener(this);
		user_my_bonus.setOnClickListener(this);
		user_my_integral.setOnClickListener(this);
		updateView();
		return main;
	}
	
	private void updateView(){
		if (MyAccount.mbLogin) {
			MyAccount.getUserFromNet(getUserCallback, false);
		} else {
			tv_user_name.setText("未登录");
			btn_logout.setText(getString(R.string.login));
		}
	}
	
	MyRequestCallback getUserCallback = new MyRequestCallback() {
		
		@Override
		public void onSuccess(com.gosoon.util.MyResult result) {
			UserEntity user = MyAccount.getUser();
			if (user != null) {
				tv_user_name.setText("登录名："+ user.getValueAsString(UserEntity.USER_NAME, ""));
				tv_user_banlance.setText("我的余额："+ user.getValueAsString(UserEntity.USER_MONEY, "") + "元");
				tv_user_integral.setText("我的积分："+user.getValueAsString(UserEntity.PAY_POINTS, "0"));
				btn_logout.setText(getString(R.string.logout));
			}
		};
	};
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_logout:
			if (MyAccount.mbLogin) {
				showLogoutAlert();
			} else {
				logout();
			}
			break;
		case R.id.user_my_order:
			if (MyAccount.mbLogin) {
				startActivityForResult(new Intent(getActivity(), MyOrderActivity.class), MainActivity.REQUEST_GET_ORDER);
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.user_my_colletion:
			if (MyAccount.mbLogin) {
				startActivityForResult(new Intent(getActivity(), MyCollectionActivity.class), MainActivity.REQUEST_GET_ORDER);
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.user_my_address:
			if (MyAccount.mbLogin) {
				Intent intent = new Intent(getActivity(), addressListActivity.class);
				intent.putExtra("fromUser", true);
				startActivity(intent);
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.user_charge:
			if (MyAccount.mbLogin){
				startActivityForResult(new Intent(getActivity(), RechargeActivity.class), REQUEST_RECHARGE);
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.user_my_bonus:
			if(MyAccount.mbLogin){
				startActivityForResult(new Intent(getActivity(), BonusActivity.class), REQUEST_RECHARGE);
			}else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}break;
		case R.id.user_my_integral:
			if(MyAccount.mbLogin){
				startActivityForResult(new Intent(getActivity(), IntegralActivity.class), REQUEST_RECHARGE);
			}else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		default:
			break;
		}
	}

	private void showLogoutAlert() {
		AlertDialogConfig alertDialogConfig = new AlertDialogConfig(getActivity(), MainActivity.ALERTDIALOG_ID_DEL_SHOPPINGCART);
		alertDialogConfig.message = "确定要退出登录吗？";
		alertDialogConfig.title = R.string.prompt;
		alertDialogConfig.showNegative = true;
		alertDialogConfig.onPositiveListener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1){
				logout();
			}
		};
		alertDialogConfig.show();
	}

	protected void logout(){
		if(MyAccount.mbLogin == true){
			MyAccount.logout();
			tv_user_name.setText("未登录");
			tv_user_banlance.setText("用户余额：0.00元");
			btn_logout.setText(getString(R.string.login));
		}else{
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
		}
		
	}
	public static final int REQUEST_RECHARGE = 0;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_RECHARGE && resultCode == Activity.RESULT_OK){
			MyAccount.getUserFromNet(getUserCallback, true);
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		updateView();
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!MyAccount.mbLogin) {
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
		}
		
	}
}
