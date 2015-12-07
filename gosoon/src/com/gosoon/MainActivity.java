package com.gosoon;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.gosoon.account.MyAccount;
import com.gosoon.fragment.categoryFragment;
import com.gosoon.fragment.homeFragment;
import com.gosoon.fragment.settingFragment;
import com.gosoon.fragment.shoppingCartFragment;
import com.gosoon.fragment.userFragment;
import com.gosoon.fragment.shoppingCartFragment.OnViewGoods;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.BaiduUtils;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.Utils;
import com.gosoon.util.programSetting;
import com.gosoon.view.CircleView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	public final static String FRAGMENT_INDEX = "fragmentIndex";
	public final static int FRAGMENT_INDEX_NULL = 0;
	public final static int FRAGMENT_INDEX_HOME = 1;
	public final static int FRAGMENT_INDEX_CATEGORY = 2;
	public final static int FRAGMENT_INDEX_SHOPPINGCART = 3;
	public final static int FRAGMENT_INDEX_USER = 4;
	public final static int FRAGMENT_INDEX_SETTING = 5;

	public static final int PROCESSDIALOG_ID_GETCATEGORY = 1;
	public static final int PROCESSDIALOG_ID_GETGOODSLIST = 2;
	public static final int PROCESSDIALOG_ID_GETGOODSDETAIL = 3;
	public static final int PROCESSDIALOG_ID_GETUSERADDRESSLIST = 4;
	public static final int PROCESSDIALOG_ID_GETPAYMENT = 5;
	public static final int PROCESSDIALOG_ID_GETSHIPPING = 6;
	public static final int PROCESSDIALOG_ID_GETREGION = 7;
	public static final int PROCESSDIALOG_ID_ADDRESS_EDIT = 8;
	public static final int PROCESSDIALOG_ID_MY_ORDERS = 9;
	public static final int PROCESSDIALOG_ID_MY_COLLECTIONS = 10;
	public static final int PROCESSDIALOG_ID_CREATE_ORDER = 100;

	public static final int ALERTDIALOG_ID_GETCATEGORY = 1;
	public static final int ALERTDIALOG_ID_GETGOODSLIST = 2;
	public static final int ALERTDIALOG_ID_GETGOODSDETAIL = 3;
	public static final int ALERTDIALOG_ID_GETUSERADDRESSLIST = 4;
	public static final int ALERTDIALOG_ID_GETPAYMENT = 5;
	public static final int ALERTDIALOG_ID_GETSHIPPING = 6;
	public static final int ALERTDIALOG_ID_GETREGION = 7;
	public static final int ALERTDIALOG_ID_ADDRESS_EDIT = 8;
	public static final int ALERTDIALOG_ID_MY_ORDERS = 9;
	public static final int ALERTDIALOG_ID_MY_COLLECTIONS = 10;

	public static final int ALERTDIALOG_ID_CREATE_ORDER = 100;
	public static final int ALERTDIALOG_ID_SELECT_SHOPPING_CART = 101;
	public static final int ALERTDIALOG_ID_LOGIN = 102;
	public static final int ALERTDIALOG_ID_REGISTER = 102;
	public static final int ALERTDIALOG_ID_DEL_SHOPPINGCART = 103;
	public static final int ALERTDIALOG_ID_CONFIRM_CANCEL_ORDER = 104;

	public static final int REQUEST_GET_ORDER = 0;
	Vector<NaviButton> mNaviButtons = new Vector<NaviButton>();
	// start
	static CircleView circleView;
	// end
	public static View mViewGoods;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String a = "\u5305\u88c5\u89c4\u683c:1\u4e2a";
		PushManager.startWork(getApplicationContext(),PushConstants.LOGIN_TYPE_API_KEY,
				BaiduUtils.getMetaValue(this, "baidu_api_key"));

		initNaviButton();
		// start
		circleView = (CircleView) findViewById(R.id.tv_navi_shoppingcart_size);
		circleView.setBackgroundColor(Color.RED);
		updateShoppingCartSize();
		// end
		/*强制更新软件*/
//		updateAppVersion();
	}
	
	/**
	 * 强制更新软件
	 */
	private void updateAppVersion() {
		HttpUtils httpUtils =  new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(1000*2);
		httpUtils.send(HttpMethod.GET, programSetting.getAppVersionUrl(), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				try {
					JSONObject object = new JSONObject(result);
					String version = object.getString("version");
					if(!getVersion().equals(version)){
						AlertDialog.Builder diaBuilder = new AlertDialog.Builder(MainActivity.this);
						diaBuilder.setTitle("提示");
						diaBuilder.setMessage("检测到当前不是最新版本，请更新我们的应用获得更好的服务！");
						diaBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								MainActivity.this.finish();
							}
						});
						
						diaBuilder.setPositiveButton("更新", new DialogInterface.OnClickListener(){
							
							@Override
							public void onClick(DialogInterface dialog, int which){
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri.parse(programSetting.APPURL));
								startActivity(intent);
								MainActivity.this.finish();
							}
						});
						diaBuilder.setCancelable(false);
						diaBuilder.create().show();
					}
				} catch (JSONException e) {}
			}
			@Override
			public void onFailure(HttpException error, String msg) {
				
			}
		});
	}
	
	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
	    try {
	        PackageManager manager = this.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	        String version = info.versionName;
	        return version;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "0.0";
	    }
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MyApplication.TopActivity = this;
		if(!isNetworkConnected()){
			AlertDialogConfig config = new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_ADDRESS_EDIT);
			config.message = "无网络连接，请连接网络后再试。";
			config.onPositiveListener = new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			};
			showAlertDialog(config);
		}
	}
	public boolean isNetworkConnected() { 
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
		if (mNetworkInfo != null) { 
			return mNetworkInfo.isAvailable(); 
		}
		return false; 
	}
	
	@Override
	protected void onDestroy() {
		if (MyApplication.TopActivity == this){
			MyApplication.TopActivity = null;
		}
		super.onDestroy();
	}

	private void initNaviButton() {
		mNaviButtons.add(new NaviButton(FRAGMENT_INDEX_HOME, R.id.rl_navi_home,
				R.id.iv_navi_home, R.id.tv_navi_home, R.drawable.navi_home,
				R.drawable.navi_home_selected));
		mNaviButtons.add(new NaviButton(FRAGMENT_INDEX_CATEGORY,
				R.id.rl_navi_category, R.id.iv_navi_category,
				R.id.tv_navi_category, R.drawable.navi_category,
				R.drawable.navi_category_selected));
		mNaviButtons.add(new NaviButton(FRAGMENT_INDEX_SHOPPINGCART,
				R.id.rl_navi_shoppingcart, R.id.iv_navi_shoppingcart,
				R.id.tv_navi_shoppingcart, R.drawable.navi_shoppingcart,
				R.drawable.navi_shoppingcart_selected));
		mNaviButtons.add(new NaviButton(FRAGMENT_INDEX_USER, R.id.rl_navi_user,
				R.id.iv_navi_user, R.id.tv_navi_user, R.drawable.navi_user,
				R.drawable.navi_user_selected));
//		mNaviButtons.add(new NaviButton(FRAGMENT_INDEX_SETTING,
//				R.id.rl_navi_setting, R.id.iv_navi_setting,
//				R.id.tv_navi_setting, R.drawable.navi_setting,
//				R.drawable.navi_setting_selected));

		for (NaviButton button : mNaviButtons) {
			View view = findViewById(button.mActionViewId);
			if (view != null) {
				view.setOnClickListener(mNaviButtonClickListener);
			}
		}
		mNaviButtons.get(0).onSelected(true);
	}

	OnClickListener mNaviButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			for (NaviButton button : mNaviButtons) {
				if (button.mActionViewId == v.getId()) {
					button.onSelected(true);
				} else {
					button.onSelected(false);
				}
			}
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == MyOrderActivity.RESULT_VIEW_GOODS) {
			for (NaviButton button : mNaviButtons) {
				if (button.mActionViewId == R.id.rl_navi_home) {
					button.onSelected(true);
				} else {
					button.onSelected(false);
				}
			}
		}
	}
	// start
	// 更新购物车数量方法
	public static void updateShoppingCartSize(){
		mHandler.sendEmptyMessage(SHOPPING_CART_SIZE_HANDLER);
	}

	private static final int SHOPPING_CART_SIZE_HANDLER = 0x122;
	// 创建handler用于更新购物车商品数量
	public static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == SHOPPING_CART_SIZE_HANDLER) {
				if (shoppingCartFragment.getShoppingCartSize() == 0) {
					circleView.setVisibility(View.INVISIBLE);
				} else {
					circleView.setVisibility(View.VISIBLE);
					circleView.setNotifiText(shoppingCartFragment
							.getShoppingCartSize());
				}
			}
		}
	};

	// end

	public void showFragment(Bundle params) {
		int index = params.getInt(FRAGMENT_INDEX, FRAGMENT_INDEX_NULL);
		Fragment fragment = null;
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		switch (index) {
		case FRAGMENT_INDEX_HOME:
			fragment = new homeFragment(params);
			break;
		case FRAGMENT_INDEX_CATEGORY:
			fragment = new categoryFragment(params);
			break;
		case FRAGMENT_INDEX_SHOPPINGCART:
			fragment = new shoppingCartFragment(params);
			((shoppingCartFragment)fragment).setOnViewGoodsClick(onViewGoods);
			break;
		case FRAGMENT_INDEX_USER:
			fragment = new userFragment(params);
			break;
		case FRAGMENT_INDEX_SETTING:
			fragment = new settingFragment(params);
			break;
		default:
			break;
		}
		if (fragment != null) {
			ft.replace(R.id.ll_main, fragment);
		}
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		int ret = ft.commit();
		Utils.logd("gosoon", "showFragment " + index + " commit:" + ret);
	}
	private OnViewGoods onViewGoods = new OnViewGoods() {
		@Override
		public void OnViewGoodsClick() {
			for (NaviButton button : mNaviButtons) {
				if (button.mActionViewId == R.id.rl_navi_home) {
					button.onSelected(true);
				} else {
					button.onSelected(false);
				}
			}
		}
	};
	protected class NaviButton {

		int mFragmentId;
		int mActionViewId;
		int mImageId;
		int mLabelId;
		int mImageNormalId;
		int mImageSelectedId;
		boolean mbIsSelected = false;

		public NaviButton(int fragmentId, int actionViewId, int imageViewId,
				int labelViewId, int imageNormalId, int imageSelectedId) {
			this.mFragmentId = fragmentId;
			this.mActionViewId = actionViewId;
			this.mImageId = imageViewId;
			this.mLabelId = labelViewId;
			this.mImageNormalId = imageNormalId;
			this.mImageSelectedId = imageSelectedId;
		}

		@SuppressLint("ResourceAsColor")
		public void onSelected(boolean select) {
			if (mbIsSelected == select) {
				return;
			}
			View image = findViewById(mImageId);
			TextView label = (TextView) findViewById(mLabelId);
			if (image != null && label != null) {
				if (select) {
					image.setBackgroundResource(mImageSelectedId);
					label.setTextColor(R.color.navi_label_selected);
				} else {
					image.setBackgroundResource(mImageNormalId);
					label.setTextColor(R.color.navi_label_normal);
				}
			}
			if (select) {
				Bundle bundle = new Bundle();
				bundle.putInt(FRAGMENT_INDEX, mFragmentId);
				showFragment(bundle);
			}
			mbIsSelected = select;
		}
	}

	Map<ProgressDialogConfig, ProgressDialog> mProcessDialogMap = new HashMap<ProgressDialogConfig, ProgressDialog>();
	public void showProcessDialog(final ProgressDialogConfig config) {
		if (!mProcessDialogMap.containsKey(config)) {
			ProgressDialog dialog = ProgressDialog.show(this, getString(config.title), getString(config.message), true, true);  
			if (config.onCalcelListener != null) {
				dialog.setOnCancelListener(config.onCalcelListener);
			}
			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					if (config.onDismissListener != null) {
						config.onDismissListener.onDismiss(dialog);
					}
					mProcessDialogMap.remove(config);
				}
			});
			mProcessDialogMap.put(config, dialog);			
		}
	}

	public void closeProcessDialog(ProgressDialogConfig config) {
		ProgressDialog dialog = mProcessDialogMap.get(config);
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	Map<AlertDialogConfig, AlertDialog> mAlertDialogMap = new HashMap<AlertDialogConfig, AlertDialog>();
	public void showAlertDialog(final AlertDialogConfig config){
		if (!mAlertDialogMap.containsKey(config)) {
			Builder builder = new AlertDialog.Builder(this);
			builder.setPositiveButton(config.positiveButton, new android.content.DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (config.onPositiveListener != null) {
						config.onPositiveListener.onClick(dialog, which);
					} 
					dialog.dismiss();
				}
			});
			if (config.showNegative) {
				builder.setNegativeButton(config.negativeButton, new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (config.onNegativeListener != null) {
							config.onNegativeListener.onClick(dialog, which);
						}
						dialog.dismiss();
					}
				});
			}
			AlertDialog alertDialog = builder.create();
			alertDialog.setTitle(config.title);
			alertDialog.setMessage(config.message);
			alertDialog.show();
			alertDialog.setOnDismissListener(new OnDismissListener(){

				@Override
				public void onDismiss(DialogInterface dialog){
					mAlertDialogMap.remove(config);
				}
			});
			mAlertDialogMap.put(config, alertDialog);
		}
	}
	private long mExitTime = 0;
	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - mExitTime) > 2000) {
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			mExitTime = System.currentTimeMillis();
		} else {
			MyAccount.logout();
			finish();
			System.exit(0);
			System.gc();
		}
	}
}
