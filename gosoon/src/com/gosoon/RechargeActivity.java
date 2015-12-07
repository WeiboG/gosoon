package com.gosoon;


import org.json.JSONException;

import com.gosoon.account.MyAccount;
import com.gosoon.entity.AccountLogEntity;
import com.gosoon.entity.CashCardEntity;
import com.gosoon.entity.UserEntity;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.StringUtil;
import com.gosoon.util.ToastUtil;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class RechargeActivity extends BaseActivity{
	EditText et_cashcard_code;
	CashCardEntity cardEntity;
	double userMoney = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);
		setTitle(getString(R.string.label_recharge));
		et_cashcard_code = (EditText) findViewById(R.id.et_cashcard_code);
	}
	public void recharge(View v){
		String cashcard_code = et_cashcard_code.getText().toString();
		if(StringUtil.isEmpty(cashcard_code)){
			ToastUtil.show(this, "请输入充值卡密");
			return;
		}
		//查询充值卡是否有效
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		myrequest.setSql("select * from gsw_cashcard where code='" + cashcard_code + "' AND use_id=0");
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(this, MainActivity.ALERTDIALOG_ID_LOGIN)); 
		myrequest.setAlertDialogConfig(new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_LOGIN));
		myrequest.send(validateCardCallback);
	}
	MyRequestCallback validateCardCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			if(result.getFirstData() != null){
				cardEntity = new CashCardEntity(result.getFirstData());
				showCardPriceAlert();
			}else{
				ToastUtil.show(getApplicationContext(), "请输入正确的充值卡密");
			}
		};
		@Override
		public void onFailure(MyResult result) {
			ToastUtil.show(getApplicationContext(), "操作失败，请重试");
		};
	};
	protected void showCardPriceAlert() {
		AlertDialogConfig alertDialogConfig = new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_CONFIRM_CANCEL_ORDER);
		alertDialogConfig.message = "本次充值额度：" + cardEntity.getValueAsString(CashCardEntity.PRICE, "0.0") + "元";
		alertDialogConfig.title = R.string.prompt;
		alertDialogConfig.showNegative = true;
		alertDialogConfig.onPositiveListener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				queryUserMoney();
			}
		};
		alertDialogConfig.show();
	}
	private void queryUserMoney(){
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		myrequest.setSql("select user_money, user_id from gsw_users where user_id="
				+ MyAccount.getUser().getValueAsString(UserEntity.USER_ID, ""));
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(this, MainActivity.ALERTDIALOG_ID_LOGIN)); 
		myrequest.setAlertDialogConfig(new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_LOGIN));
		myrequest.send(queryUserMoneyCallback);
	}
	MyRequestCallback queryUserMoneyCallback = new MyRequestCallback() {
		@Override
		public void onSuccess(MyResult result) {
			if(result.getFirstData() != null){
				try {
					userMoney = result.getFirstData().getDouble("user_money");
					updateUserMoney(userMoney + cardEntity.getValueAsDouble(CashCardEntity.PRICE, 0) + "");
				} catch (JSONException e) {
					ToastUtil.show(getApplicationContext(), "操作失败，请重试");
				}
			}else{
				ToastUtil.show(getApplicationContext(), "操作失败，请重试");
			}
		};
		@Override
		public void onFailure(MyResult result) {
			ToastUtil.show(getApplicationContext(), "操作失败，请重试");
		};
	};
	private void updateUserMoney(String totalMoney){
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		String sql = "UPDATE gsw_users SET user_money='" + totalMoney + "' WHERE user_id="
				+ MyAccount.getUser().getValueAsString(UserEntity.USER_ID, "");
		myrequest.setSql(sql);
		myrequest.send(updateUserMoneyCallback);
	}
	MyRequestCallback updateUserMoneyCallback = new MyRequestCallback() {
		@Override
		public void onSuccess(MyResult result){
			//此时已经充值到用户账户
			upadteCashCard();
		};
		@Override
		public void onFailure(MyResult result) {
			ToastUtil.show(getApplicationContext(), "操作失败，请重试");
		};
	};
	protected void upadteCashCard() {
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		long time = System.currentTimeMillis()/1000 + 8*60*60;
		String sql = "UPDATE gsw_cashcard SET use_id='"
				+ MyAccount.getUser().getValueAsString(UserEntity.USER_ID, "")
				+ "',use_date='" + time
				+ "' WHERE id="
				+ cardEntity.getValueAsString(CashCardEntity.ID, "");
		myrequest.setSql(sql);
		myrequest.send(upadteCashCardCallback);
	}
	MyRequestCallback upadteCashCardCallback = new MyRequestCallback() {
		@Override
		public void onSuccess(MyResult result) {
			addLog();
		};
		@Override
		public void onFailure(MyResult result) {
			ToastUtil.show(getApplicationContext(), "操作失败，请重试");
		};
	};
	protected void addLog() {
		AccountLogEntity logEntity = new AccountLogEntity();
		logEntity.setValueAsString(AccountLogEntity.USER_ID, MyAccount.getUser().getValueAsString(UserEntity.USER_ID, ""));
		logEntity.setValueAsString(AccountLogEntity.USER_MONEY, cardEntity.getValueAsString(CashCardEntity.PRICE, ""));
		logEntity.setValueAsString(AccountLogEntity.FROZEN_MONEY, "0");
		logEntity.setValueAsString(AccountLogEntity.RANK_POINTS, "0");
		logEntity.setValueAsString(AccountLogEntity.PAY_POINTS, "0");
		logEntity.setValueAsString(AccountLogEntity.CHANGE_TIME, System.currentTimeMillis() + "");
		logEntity.setValueAsString(AccountLogEntity.CHANGE_DESC, "现金卡充值");
		logEntity.setValueAsString(AccountLogEntity.CHANGE_TYPE, "0");
		MyRequest myrequest = new MyRequest(logEntity.getTable().mTableName, logEntity.toString());
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(this, MainActivity.ALERTDIALOG_ID_LOGIN)); 
		myrequest.setAlertDialogConfig(new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_LOGIN));
		myrequest.send(new MyRequestCallback(){
			@Override
			public void onSuccess(MyResult result) {
				showConfirmAlert();
			};
			@Override
			public void onFailure(MyResult result) {
				ToastUtil.show(getApplicationContext(), "操作失败，请重试");
			};
		});
	}
	private void showConfirmAlert() {
		AlertDialogConfig alertDialogConfig = new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_CONFIRM_CANCEL_ORDER);
		alertDialogConfig.message = "恭喜你，充值成功！";
		alertDialogConfig.title = R.string.prompt;
		alertDialogConfig.onPositiveListener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				setResult(RESULT_OK);
				finish();
			}
		};
		alertDialogConfig.show();
	}
}
