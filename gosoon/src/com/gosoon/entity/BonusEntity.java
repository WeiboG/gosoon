package com.gosoon.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gosoon.MainActivity;
import com.gosoon.account.MyAccount;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class BonusEntity extends BaseEntity{

	public static final String BONUS_ID = "bonus_id";
	public static final String TYPE_NAME = "type_name";
	public static final String TYPE_MONEY = "type_money";
	public static final String STATUS = "status";
	public static final String USE_START_DATE = "use_start_date";
	public static final String USE_END_DATE = "use_end_date";
	public static final String MIN_GOODS_AMOUNT = "min_goods_amount";
	public static final String BONUS_SN = "bonus_sn";
	
	private String bonus_id;
	private String type_name;
	private  String type_money;
	private String status;
	private String use_end_date;
	private String min_goods_amount;
	
	private static double goods_money;
	
	private static MyRequestCallback mCallBack;
	private static MyRequestCallback mAllBonusCallBack;
	private static ArrayList<BonusEntity> mBonusEntitys;
	/**
	 * 我的优惠券列表
	 */
	private static ArrayList<BonusEntity> mMyAllBonusEntitys;
	
	@Override
	public tableConfig getTable(){
		
		return null;
	}
	
	/**
	 * 从服务器获取所有可用的优惠券
	 * @param callback
	 */
	public static void getBonusFromNet(MyRequestCallback callback){
		mCallBack = callback;
		MyRequest myrequest = new MyRequest(MyRequest.ACTION_QUERY_BONUS_PAY,MyAccount.mUserName,goods_money);
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(null, MainActivity.PROCESSDIALOG_ID_GETSHIPPING));
		myrequest.setAlertDialogConfig(new AlertDialogConfig(null, MainActivity.ALERTDIALOG_ID_GETSHIPPING));
		myrequest.send(myCallback);
	}
	
	static MyRequestCallback myCallback = new MyRequestCallback(){
		
		@Override
		public void onSuccess(MyResult result){
			super.onSuccess(result);
			ResponseInfo<String> responseInfo = result.mResponseInfo;
			Log.e("RESPO", responseInfo.result);
			mBonusEntitys = new ArrayList<BonusEntity>();
			try {
				JSONObject object = new JSONObject(responseInfo.result);
				JSONArray _Bonus_Id_Array = object.getJSONArray(BONUS_ID);  
				JSONArray _Type_Name_Array = object.getJSONArray(TYPE_NAME); 
				JSONArray _Type_Money_Array = object.getJSONArray(TYPE_MONEY);
				for(int i=0;i<_Bonus_Id_Array.length();i++){
					BonusEntity _BonusEntity = new BonusEntity();
					_BonusEntity.setBonusId(_Bonus_Id_Array.optInt(i)+"");
					_BonusEntity.setTypeMoney(_Type_Money_Array.optDouble(i)+"");
					_BonusEntity.setTypeName(_Type_Name_Array.optString(i));
					mBonusEntitys.add(_BonusEntity);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mCallBack.onSuccess(result);
		}
		@Override
		public void onFailure(MyResult result) {
			super.onFailure(result);
			mCallBack.onFailure(result);
		};
	};
	
	/**
	 * 获取用户所有的优惠券
	 * @param callback
	 */
	public static void getMyAllBonusFromNet(MyRequestCallback callback){
		mAllBonusCallBack = callback;
		MyRequest myrequest = new MyRequest(MyRequest.ACTION_QUERY_BONUS_LIST,MyAccount.mUserName,1);
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(null, MainActivity.PROCESSDIALOG_ID_GETSHIPPING));
		myrequest.setAlertDialogConfig(new AlertDialogConfig(null, MainActivity.ALERTDIALOG_ID_GETSHIPPING));
		myrequest.send(myAllCallback);
	}
	
	static MyRequestCallback myAllCallback = new MyRequestCallback(){
		
		@Override
		public void onSuccess(MyResult result){
			super.onSuccess(result);
			ResponseInfo<String> responseInfo = result.mResponseInfo;
			mMyAllBonusEntitys = new ArrayList<BonusEntity>();
			try {
				JSONObject object = new JSONObject(responseInfo.result);
				JSONArray _Type_Name_Array = object.getJSONArray(TYPE_NAME); 
				JSONArray _Type_Money_Array = object.getJSONArray(TYPE_MONEY);
				JSONArray _Type_Status_Array = object.getJSONArray(STATUS); 
				JSONArray _Type_Use_End_Date_Array = object.getJSONArray(USE_END_DATE); 
				JSONArray _Type_Min_Goods_Amount_Array = object.getJSONArray(MIN_GOODS_AMOUNT); 
				for(int i=0;i<_Type_Name_Array.length();i++){
					BonusEntity _BonusEntity = new BonusEntity();
					_BonusEntity.setTypeMoney(_Type_Money_Array.optDouble(i)+"");
					_BonusEntity.setTypeName(_Type_Name_Array.optString(i));
					_BonusEntity.setStatus(_Type_Status_Array.optString(i));
					_BonusEntity.setUse_end_date(_Type_Use_End_Date_Array.optString(i));
					_BonusEntity.setMin_goods_amount(_Type_Min_Goods_Amount_Array.optString(i));
					mMyAllBonusEntitys.add(_BonusEntity);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mAllBonusCallBack.onSuccess(result);
		}
		@Override
		public void onFailure(MyResult result) {
			super.onFailure(result);
			mAllBonusCallBack.onFailure(result);
		};
	};
	
	/**
	 * 得到所有的用户优惠券的实体列表
	 * @return
	 */
	public static ArrayList<BonusEntity> getMyAllBonusEntities(){
		return mMyAllBonusEntitys;
	}
	
	/**
	 * 得到用户所有可用的优惠券的实体的列表
	 * @return
	 */
	public static ArrayList<BonusEntity> getBonusEntities(){
		return mBonusEntitys;
	}
	
	public static  void setGoodsMoney(double p_goods_money){
		goods_money  = p_goods_money;
	}
	private void setBonusId(String p_Bonus_Id){
		bonus_id = p_Bonus_Id;
	}
	
	private void setTypeName(String p_type_name){
		type_name = p_type_name;
	}
	
	private  void setTypeMoney(String p_type_money){
		type_money = p_type_money;
	}

	public String getBonus_id() {
		return bonus_id;
	}

	public String getType_name() {
		return type_name;
	}

	
	public String getType_money() {
		return type_money;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUse_end_date() {
		return use_end_date;
	}

	public void setUse_end_date(String use_end_date) {
		this.use_end_date = use_end_date;
	}

	public String getMin_goods_amount() {
		return min_goods_amount;
	}

	public void setMin_goods_amount(String min_goods_amount) {
		this.min_goods_amount = min_goods_amount;
	}
	
	
	
	
	
	
}
