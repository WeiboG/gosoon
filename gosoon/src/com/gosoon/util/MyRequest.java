package com.gosoon.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

import com.app.tool.logger.Logger;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class MyRequest {

	private String Tag = MyRequest.class.getSimpleName();

	public static final String ACTION_REGISTER = "register";
	public static final String ACTION_LOGIN = "login";
	public static final String ACTION_SQL = "sql";
	public static final String ACTION_ADD_SQL = "add_sql";
	public static final String ACTION_DONE = "done";
	public static final String ACTION_QUERY_BONUS_PAY = "query_bonus_pay";
	public static final String ACTION_QUERY_BONUS_LIST = "query_bonus_list";
	public static final String ACTION_USER_BONUS = "use_bonus";
	public static final String ACTION_CANCEL_ORDER = "cancel_order";
	public static final String  ACTION_ACT_ADD_BONUS = "act_add_bonus";
	public static final String  ACTION_INTEGRAL = "integral";
	public static final String  ACTION_USE_INTEGRAL = "use_integral";
	public static final String  ACTION_GIVE_INTEGRAL = "give_integral";
	
	public static final String GOODS_PRICE = "goods_price";
	
	//修改密码
	public static final String ACTION_SMS_EDIT_PASSWORD = "sms_act_edit_password";
	
	public static final String PARAM_ACTION = "act";
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_SQL = "sql";
	public static final String PARAM_TABLE = "table";
	public static final String PARAM_JSON = "json";
	public static final String PARAM_GOODS_PRICE = "goods_price";
	public static final String PARAM_TUIJIAN = "tuijian";
	public static final String PARAM_BONUS_ID = "bonus_id";
	public static final String PARAM_ORDER_ID = "order_id";
	public static final String PARAM_BONUS_SN = "bonus_sn";
	public static final String  PARAM_INTEGRAL = "integral";
	
	
	public static final int BONUS_SIGN = 1;
	public static final int INTEGRAL_SIGN = 2;

	private HttpRequest.HttpMethod mMethod;
    private String mAction;
    private HttpUtils mHttpUtils;
    private MyQueryPlug mQueryPlug = new MyQueryPlug();
    private MyRequestCallback mCallback;
    private MyResult mResult = new MyResult();

    ProgressDialogConfig mProgressDialogConfig;
    AlertDialogConfig mAlertDialogConfig;

    public MyRequest(HttpRequest.HttpMethod method, String action){
    	this.mMethod = method;
    	this.mAction = action;
    	mQueryPlug.setQueryCondition(PARAM_ACTION, action);
    	if (ACTION_DONE.equals(action)){
    		interfaceName = "service.php";
    	}
    }

    public MyRequest(String action, String username, String password){
    	this.mMethod = HttpRequest.HttpMethod.POST;
    	this.mAction = action;
    	mQueryPlug.setQueryCondition(PARAM_ACTION, action);
    	mQueryPlug.setQueryCondition(PARAM_USERNAME, username);
    	mQueryPlug.setQueryCondition(PARAM_PASSWORD, password);
    }
    
    public MyRequest(String action,String[] keys,String[] values){
    	this.mMethod = HttpRequest.HttpMethod.POST;
    	this.mAction = action;
    	mQueryPlug.setQueryCondition(PARAM_ACTION, action);
    	for(int i=0;i<keys.length;i++){
    		mQueryPlug.setQueryCondition(keys[i], values[i]);
    	}
    }
    /**
     * 添加优惠券
     * @param action
     * @param username
     * @param bonus_sn
     * @param sign
     */
    public MyRequest(String action,String username,String bonus_sn,int sign){
    	this.mMethod = HttpRequest.HttpMethod.POST;
    	this.mAction = action;
    	mQueryPlug.setQueryCondition(PARAM_ACTION, action);
    	mQueryPlug.setQueryCondition(PARAM_USERNAME, username);
    	mQueryPlug.setQueryCondition(PARAM_BONUS_SN, bonus_sn);
    }
    
    /**
     * 取消订单返还优惠券
     * @param action
     * @param 
     * @param 
     */
    public MyRequest(String order_id){
    	this.mMethod = HttpRequest.HttpMethod.POST;
    	this.mAction = ACTION_CANCEL_ORDER;
    	mQueryPlug.setQueryCondition(PARAM_ACTION, ACTION_CANCEL_ORDER);
    	mQueryPlug.setQueryCondition(PARAM_ORDER_ID, order_id);
    }
    
    /**
     * 使用优惠券
     * @param action
     * @param 
     * @param 
     */
    public MyRequest(String action, int bonus_id, String order_id){
    	this.mMethod = HttpRequest.HttpMethod.POST;
    	this.mAction = action;
    	mQueryPlug.setQueryCondition(PARAM_ACTION, action);
    	mQueryPlug.setQueryCondition(PARAM_BONUS_ID, bonus_id+"");
    	mQueryPlug.setQueryCondition(PARAM_ORDER_ID, order_id);
    }
    
    /**
     * 我的菓速查询我的优惠券&&查询积分
     * @param action
     * @param username
     * @param sign 标志变量，无实际意义，构造函数重载区分
     */
    public MyRequest(String action, String username,int sign){
    	this.mMethod = HttpRequest.HttpMethod.POST;
    	this.mAction = action;
    	mQueryPlug.setQueryCondition(PARAM_ACTION, action);
    	mQueryPlug.setQueryCondition(PARAM_USERNAME, username);
    }
    
    /**
     * 结算时查询可用优惠券
     * @param action
     * @param username
     * @param password
     */
    
    public MyRequest(String action, String username, double goods_money){
    	this.mMethod = HttpRequest.HttpMethod.POST;
    	this.mAction = action;
    	mQueryPlug.setQueryCondition(PARAM_ACTION, action);
    	mQueryPlug.setQueryCondition(PARAM_USERNAME, username);
    	mQueryPlug.setQueryCondition(GOODS_PRICE, goods_money+"");
    }
    
    /**
     * 带推荐人的注册请求处理
     * @param action
     * @param username
     * @param password
     * @param tuijian
     */
    public MyRequest(String action, String username, String password,String tuijian){
    	this.mMethod = HttpRequest.HttpMethod.POST;
    	this.mAction = action;
    	mQueryPlug.setQueryCondition(PARAM_ACTION, action);
    	mQueryPlug.setQueryCondition(PARAM_USERNAME, username);
    	mQueryPlug.setQueryCondition(PARAM_PASSWORD, password);
    	mQueryPlug.setQueryCondition(PARAM_TUIJIAN, tuijian);
    }
    
    public MyRequest(String table, String json) {
    	this.mMethod = HttpRequest.HttpMethod.POST;
    	this.mAction = ACTION_ADD_SQL;
    	mQueryPlug.setQueryCondition(PARAM_ACTION, ACTION_ADD_SQL);
    	mQueryPlug.setQueryCondition(PARAM_TABLE, table);
    	mQueryPlug.setQueryCondition(PARAM_JSON, json);
    }

    public HttpUtils getHttpUtils() {
        if (mHttpUtils == null) {
        	mHttpUtils = new HttpUtils();
        	mHttpUtils.configCurrentHttpCacheExpiry(1000 * 10);
        }
        return mHttpUtils;
    }
    
    public void setProcessDialogConfig(ProgressDialogConfig config) {
    	mProgressDialogConfig = config;
    }

    public void setAlertDialogConfig(AlertDialogConfig config){
    	mAlertDialogConfig = config;
    }

    public HttpHandler<String> send(MyRequestCallback callBack){
    	mCallback = callBack;
//    	Logger.init("getUrl").hideThreadInfo();
//    	Logger.e(getUrl());
    	return getHttpUtils().send(mMethod, getUrl(), preHandleCallback);
    }
    public HttpHandler<String> send2(MyRequestCallback callBack){
    	mCallback = callBack;
//    	Logger.init("getUrl").hideThreadInfo();
//    	Logger.e(getUrl());
    	return getHttpUtils().send(mMethod, getUrl2(), preHandleCallback);
    }
private String getUrl2() {
    	
    	String url = "http://192.168.0.116/gosoon/" + interfaceName + "?";    	
        if (mQueryPlug != null) {
        	String condition = mQueryPlug.toString();
        	if (!StringUtil.isEmpty(condition)) {
        		url += "&";
        		url += condition;
        	}
        }
        System.out.println("Q "+url);
    	return url;
    }
    private String interfaceName = "interface.php";
    public void setInterfaceName(String interfaceName){
    	this.interfaceName = interfaceName;
    }
    
    private String getUrl() {
    	
    	String url = programSetting.getRequestUrl() + interfaceName + "?";    	
        if (mQueryPlug != null) {
        	String condition = mQueryPlug.toString();
        	if (!StringUtil.isEmpty(condition)) {
        		url += "&";
        		url += condition;
        	}
        }
    	return url;
    }
    
    public void setQueryCondition(String key, String value){
    	mQueryPlug.setQueryCondition(key, value);
    }

	public void setSql(String sql){
		if (mQueryPlug == null) {
			mQueryPlug = new MyQueryPlug();
		}
		if (!StringUtil.isEmpty(sql)) {
			mQueryPlug.setQueryCondition(PARAM_SQL, sql);
		}
	}
	
	public MyResult getResult(){
		return mResult;
	}

	protected void showProcessDialog() {
		mProgressDialogConfig.show();
	}
	
	protected void closeProcessDialog() {
		mProgressDialogConfig.close();
	}
	
	protected void updateProcessDialog(long total, long current, boolean isUploading) {
		
	}

    protected void showAlertDialog(String msg){
    	if(msg.contains("TimeoutException")){
    		mAlertDialogConfig.message = "网络连接超时，请重试。";
    	}else if (msg.contains("TimeoutException")){
    		mAlertDialogConfig.message = "当前无网络，请检查您的网络连接。";
    	}else{
    		mAlertDialogConfig.message = msg;
    	}
    	mAlertDialogConfig.show();
	}

	RequestCallBack<String> preHandleCallback = new RequestCallBack<String>(){

		@Override
		public void onStart() {
			super.onStart();
			Utils.logd(Tag, getRequestUrl() + "-----onStart");
			mResult.setStarted();
			if (mProgressDialogConfig != null){
				showProcessDialog();
			}
			if (mCallback != null) {
				mCallback.onStart(mResult);
			}
		};
		@Override
		public void onCancelled() {
			super.onCancelled();
			Utils.logd(Tag, getRequestUrl() + "-----onCancelled");
			mResult.setCancelled();

			if (mProgressDialogConfig != null){
				closeProcessDialog();
			}
			if (mCallback != null) {
				mCallback.onCancelled(mResult);
				mCallback.onEnd(mResult);
			}
	    }

		@Override
	    public void onLoading(long total, long current, boolean isUploading){
			super.onLoading(total, current, isUploading);
			Utils.logd(Tag, getRequestUrl() + "-----onloading:" + total + "," + current + "," + isUploading);
			mResult.setOnLoading(total, current, isUploading);

			if (mProgressDialogConfig != null){
				updateProcessDialog(total, current, isUploading);
			}
			if (mCallback != null) {
				mCallback.onLoading(mResult);
			}
	    }

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo){
			Utils.logd(Tag, getRequestUrl() + "-----onSuccess:" + responseInfo.result);
			
			mResult.setResponse(responseInfo);
			
//			if (MyApplication.TopActivity != null) {
			
//				ToastUtil.show(MyApplication.TopActivity, mResult.mMsg);
//			}
			if (mResult.mResult == MyResult.RESULT_SUCCESS) {
				if (mProgressDialogConfig != null){
					closeProcessDialog();
				}
				if (mCallback != null) {
					mCallback.onSuccess(mResult);
					mCallback.onEnd(mResult);
				}
			} else if (mResult.mResult == MyResult.RESULT_FAIL){
				if (mProgressDialogConfig != null) {
					closeProcessDialog();
				}
				if (mAlertDialogConfig != null) {
					showAlertDialog(mResult.mFailReason);
				}
				if (mCallback != null) {
					mCallback.onFailure(mResult);
					mCallback.onEnd(mResult);
				}
			}
		}

		@Override
		public void onFailure(HttpException error, String msg){
			Utils.loge(Tag, getRequestUrl() + "-----onFailure:" + msg);
			mResult.setFailInfo(error, msg);
			if (mProgressDialogConfig != null) {
				closeProcessDialog();
			}
			if (mAlertDialogConfig != null) {
				showAlertDialog(msg);
			}
			if (mCallback != null) {
				mCallback.onFailure(mResult);
				mCallback.onEnd(mResult);
			}
			
//			Log.e("MyRequest356", msg);
		}
	};
}
