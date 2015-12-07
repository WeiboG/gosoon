package com.gosoon.util;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

public class MyResult {

	public static final int RESULT_NONE = 0;
	public static final int RESULT_STARTED = 1;
	public static final int RESULT_LOADING = 2;
	public static final int RESULT_SUCCESS = 3;
	public static final int RESULT_FAIL = 4;
	public static final int RESULT_CANCEL = 5;

	public int mResult = RESULT_NONE;
	public String mFailReason = "";
	public String mMsg = "";
	public HttpException mHttpException;
	public ResponseInfo<String> mResponseInfo;
	
	public long mTotal;
	public long mCurrent;
	public boolean mIsUploading;
	
	private ArrayList<JSONObject> mData = new ArrayList<JSONObject>();
	
	public void setFailInfo(HttpException httpException, String failmsg){
		mResult = RESULT_FAIL;
		mHttpException = httpException;
		mFailReason = failmsg;
	}
	
	public void setResponse(ResponseInfo<String> response){
		mResponseInfo = response;
		praseResponseInfo(response);
	}
	public static final int RESPONSE_SUCCESS = 1;
	public static final int RESPONSE_FAILED = 0;
	private void praseResponseInfo(ResponseInfo<String> responseInfo){
		JSONObject result = null;
		try {
			
			result = new JSONObject(responseInfo.result);	
			Log.d("response", responseInfo.result);
		} catch (JSONException e){
			e.printStackTrace();
			setFailInfo(null, e.getMessage());
		}
		if (result != null) {
			try {
				int type = result.getInt("type");
				if (type == RESPONSE_SUCCESS) {
					mResult = RESULT_SUCCESS;
				} else if (type == RESPONSE_FAILED){
					setFailInfo(null, result.getString("msg"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				setFailInfo(null, e.getMessage());
			}
			try {
				mMsg = result.getString("msg");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			mData.clear();
			try {
				JSONObject data = result.getJSONObject("data");
				mData.add(data);
			} catch (JSONException e) {
			}
			try {
				JSONArray datas = result.getJSONArray("data");
				for (int i = 0; i < datas.length(); i++){
					mData.add(datas.getJSONObject(i));
				}
			} catch (JSONException e) {
			}
		}
	}
	
	public void setOnLoading(long total, long current, boolean isUploading) {
		mResult = RESULT_LOADING;
		mTotal = total;
		mCurrent = current;
		mIsUploading = isUploading;
	}
	
	public void setStarted(){
		mResult = RESULT_STARTED;
	}

	public void setCancelled() {
		mResult = RESULT_CANCEL;
	}
	
	public void setSuccess() {
		mResult = RESULT_SUCCESS;
	}
	
	public ArrayList<JSONObject> getData(){
		return mData;
	}
	
	public JSONObject getFirstData() {
		if (mData != null && mData.size() > 0) {
			return mData.get(0);
		}
		return null;
	}
	
	public boolean isLoading() {
		return (mResult == RESULT_STARTED || mResult == RESULT_LOADING);
	}
}
