package com.gosoon;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gosoon.entity.OrderInfoEntity;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
//import com.gosoon.util.MyUtil;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.ToastUtil;
import com.gosoon.entity.IntegralEntity;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.gosoon.account.MyAccount;
import com.gosoon.adapter.IntegralAdapter;

import android.R.string;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class IntegralActivity extends BaseActivity {
	List<IntegralEntity> mList;
	private ListView mintegralListView;
	private TextView mintegralTextView;
	private JSONObject integralJsonObject;
	private String integralnumber;
	private View no_integral;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_integral);
		mintegralTextView = (TextView) findViewById(R.id.integral_number);
		mintegralListView = (ListView) findViewById(R.id.integral_list);
		no_integral=findViewById(R.id.ly_no_integral);
		no_integral.setOnClickListener(onButtonClick);
		setTitle("我的积分");
		String keys[] = new String[]{MyRequest.PARAM_USERNAME}; 
		String values[] = new String[]{MyAccount.mUserName};
		MyRequest myrequest = new MyRequest(MyRequest.ACTION_INTEGRAL,keys,values);
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(null,
				MainActivity.PROCESSDIALOG_ID_GETSHIPPING));
		myrequest.setAlertDialogConfig(new AlertDialogConfig(null,
				MainActivity.ALERTDIALOG_ID_GETSHIPPING));
		myrequest.send(new MyRequestCallback() {
			@Override
			public void onSuccess(MyResult result) {
				super.onSuccess(result);
				mList = new ArrayList<IntegralEntity>();
				try {
					integralJsonObject = new JSONObject(
							result.mResponseInfo.result);
					integralnumber = integralJsonObject.getString("integral");

				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				Log.e("71", result.mResponseInfo.result);
				mintegralTextView.setText(integralnumber);
				ArrayList<JSONObject> jsons = praseResponseInfo(integralJsonObject);
				for (JSONObject json : jsons) {
					mList.add(new IntegralEntity(json));
				}
				if (mList.size() <= 0) {
					no_integral.setVisibility(View.VISIBLE);
				} else {
					mintegralListView.setAdapter(new IntegralAdapter(
							IntegralActivity.this, mList));
				}
			}

			@Override
			public void onFailure(MyResult result) {
				super.onFailure(result);
			}
		});

	}

	public ArrayList<JSONObject> praseResponseInfo(JSONObject mresult) {
		ArrayList<JSONObject> mData = new ArrayList<JSONObject>();
		if (mresult != null) {
			try {
				JSONArray datas = mresult.getJSONArray("integral_arr");
				for (int i = 0; i < datas.length(); i++) {
					mData.add(datas.getJSONObject(i));
				}
			} catch (JSONException e) {
			}
		}
		return mData;
	}
	OnClickListener onButtonClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			setResult(MyOrderActivity.RESULT_VIEW_GOODS);
			finish();
		}
	};
}
