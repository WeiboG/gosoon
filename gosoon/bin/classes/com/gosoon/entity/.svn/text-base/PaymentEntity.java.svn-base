package com.gosoon.entity;

import java.util.ArrayList;

import org.json.JSONObject;

import com.gosoon.MainActivity;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class PaymentEntity extends BaseEntity {
	
	public static final String PAY_CODE_ALIPAY = "alipay";
	
	public static final String PAY_ID = "pay_id";
	public static final String PAY_CODE = "pay_code";
	public static final String PAY_NAME = "pay_name";
	public static final String PAY_FEE = "pay_fee";
	public static final String PAY_DESC = "pay_desc";
	public static final String PAY_ORDER = "pay_order";
	public static final String PAY_CONFIG = "pay_config";
	public static final String ENABLED = "enabled";
	public static final String IS_COD = "is_cod";
	public static final String IS_ONLINE = "is_online";
	
	public PaymentEntity(JSONObject json){
		super(json);
	}

	static tableConfig mTable = null;
	@Override
	public tableConfig getTable() {
		if (mTable == null) {
			mTable = new tableConfig("gsw_payment");
		}
		return mTable;
	}

	static ArrayList<PaymentEntity> mData = null;
	static ArrayList<MyRequestCallback> mCallbacks = new ArrayList<MyRequestCallback>();
	public static void getPaymentsFromNet(MyRequestCallback callback) {
		if (mData == null) {
			if (!myCallback.getMyResult().isLoading()) {
				MyRequest myrequest = new MyRequest(HttpMethod.POST, MyRequest.ACTION_SQL);
				myrequest.setSql("SELECT * from gsw_payment");
				myrequest.setProcessDialogConfig(new ProgressDialogConfig(null, MainActivity.PROCESSDIALOG_ID_GETPAYMENT));
				myrequest.setAlertDialogConfig(new AlertDialogConfig(null, MainActivity.ALERTDIALOG_ID_GETPAYMENT));
				myrequest.send(myCallback);
			}
			if (callback != null) {
				mCallbacks.add(callback);
			}
		} else {
			callback.onSuccess(myCallback.getMyResult());
		}
	}
	public static void unRegisterCallback(MyRequestCallback callback) {
		mCallbacks.remove(callback);
	}
	public static ArrayList<PaymentEntity> getPayments() {
		return mData;
	}
	static MyRequestCallback myCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			mData = new ArrayList<PaymentEntity>();
			ArrayList<JSONObject> jsons = result.getData();
			for (JSONObject json : jsons) {
				PaymentEntity payment = new PaymentEntity(json);
				mData.add(payment);
			}
			for (MyRequestCallback callback : mCallbacks) {
				callback.onSuccess(result);
			}
			mCallbacks.clear();
		};

		@Override
		public void onFailure(MyResult result) {
			super.onFailure(result);
			for (MyRequestCallback callback : mCallbacks) {
				callback.onFailure(result);
			}
			mCallbacks.clear();
		};
	};
}
