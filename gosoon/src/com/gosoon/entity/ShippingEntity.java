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

public class ShippingEntity extends BaseEntity{
	
	public static final String SHIPPING_ID = "shipping_id";
	public static final String SHIPPING_CODE = "shipping_code";
	public static final String SHIPPING_NAME = "shipping_name";
	public static final String SHIPPING_DESC = "shipping_desc";
	public static final String INSURE = "insure";
	public static final String SUPPORT_COD = "support_cod";
	public static final String ENABLED = "enabled";
	public static final String SHIPPING_PRINT = "shipping_print";
	public static final String PRINT_BG = "print_bg";
	public static final String CONFIG_LABEL = "config_label";
	public static final String PRINT_MODEL = "print_model";
	public static final String SHIPPING_ORDER = "shipping_order";
	public static final String SHIPPING_PRICE = "shipping_price";
	public ShippingEntity(JSONObject json) {
		super(json);
	}

	static tableConfig mTable = null;
	@Override
	public tableConfig getTable() {
		if (mTable == null) {
			mTable = new tableConfig("gsw_shipping");
		}
		return mTable;
	}

	static ArrayList<ShippingEntity> mData = null;
	static ArrayList<MyRequestCallback> mCallbacks = new ArrayList<MyRequestCallback>();
	public static void getShippingsFromNet(MyRequestCallback callback) {
		if (mData == null) {
			if (!myCallback.getMyResult().isLoading()){
				MyRequest myrequest = new MyRequest(HttpMethod.POST, MyRequest.ACTION_SQL);
				myrequest.setSql("SELECT * from gsw_shipping");
				myrequest.setProcessDialogConfig(new ProgressDialogConfig(null, MainActivity.PROCESSDIALOG_ID_GETSHIPPING));
				myrequest.setAlertDialogConfig(new AlertDialogConfig(null, MainActivity.ALERTDIALOG_ID_GETSHIPPING));
				myrequest.send(myCallback);
//				myrequest.send2(myCallback);
			}
			if (callback != null) {
				mCallbacks.add(callback);
			}
		} else {
			callback.onSuccess(myCallback.getMyResult());
		}
	}
	
	public static void unRegisterCallback(MyRequestCallback callback){
		
		mCallbacks.remove(callback);
	}
	
	public static ArrayList<ShippingEntity> getShippings(){
		
		return mData;
	}
	static MyRequestCallback myCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result){
			super.onSuccess(result);
			mData = new ArrayList<ShippingEntity>();
			ArrayList<JSONObject> jsons = result.getData();
			for (JSONObject json : jsons){
				ShippingEntity shipping = new ShippingEntity(json);
				mData.add(shipping);
			}
			for (MyRequestCallback callback : mCallbacks){
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
