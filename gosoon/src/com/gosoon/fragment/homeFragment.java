package com.gosoon.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.alipay.sdk.util.LogUtils;
import com.gosoon.MyApplication;
import com.gosoon.R;
import com.gosoon.goodsDetailActivity;
import com.gosoon.goodslistActivity;
import com.gosoon.entity.AdsEntity;
import com.gosoon.entity.CategoryEntity;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.StringUtil;
import com.gosoon.util.ToastUtil;
import com.gosoon.util.Utils;
import com.gosoon.util.programSetting;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

@SuppressLint("ValidFragment")
public class homeFragment extends baseFragment {

	ArrayList<View> mCategoryActionViews = new ArrayList<View>();
	ArrayList<TextView> mCategoryLabels = new ArrayList<TextView>();
	ArrayList<ImageView> mCategoryImages = new ArrayList<ImageView>();
	EditText mSearchText;
	View progressBar;
	LinearLayout mAdsLayout;

	@SuppressLint("ValidFragment")
	public homeFragment(Bundle params){
		super(params);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View main = inflater.inflate(R.layout.fragment_home, container, false);
		progressBar = main.findViewById(R.id.progressBar);
		mCategoryActionViews.clear();
		mCategoryLabels.clear();
		mCategoryActionViews.add(main.findViewById(R.id.ly_category1));
		mCategoryActionViews.add(main.findViewById(R.id.ly_category2));
		mCategoryActionViews.add(main.findViewById(R.id.ly_category3));
		mCategoryActionViews.add(main.findViewById(R.id.ly_category4));
		mCategoryActionViews.add(main.findViewById(R.id.ly_category5));
		mCategoryActionViews.add(main.findViewById(R.id.ly_category6));
		mCategoryActionViews.add(main.findViewById(R.id.ly_category7));
		mCategoryActionViews.add(main.findViewById(R.id.ly_category8));
		mCategoryLabels.add((TextView) main.findViewById(R.id.tv_category1));
		mCategoryLabels.add((TextView) main.findViewById(R.id.tv_category2));
		mCategoryLabels.add((TextView) main.findViewById(R.id.tv_category3));
		mCategoryLabels.add((TextView) main.findViewById(R.id.tv_category4));
		mCategoryLabels.add((TextView) main.findViewById(R.id.tv_category5));
		mCategoryLabels.add((TextView) main.findViewById(R.id.tv_category6));
		mCategoryLabels.add((TextView) main.findViewById(R.id.tv_category7));
		mCategoryLabels.add((TextView) main.findViewById(R.id.tv_category8));
		mCategoryImages.add((ImageView) main.findViewById(R.id.iv_category1));
		mCategoryImages.add((ImageView) main.findViewById(R.id.iv_category2));
		mCategoryImages.add((ImageView) main.findViewById(R.id.iv_category3));
		mCategoryImages.add((ImageView) main.findViewById(R.id.iv_category4));
		mCategoryImages.add((ImageView) main.findViewById(R.id.iv_category5));
		mCategoryImages.add((ImageView) main.findViewById(R.id.iv_category6));
		mCategoryImages.add((ImageView) main.findViewById(R.id.iv_category7));
		mCategoryImages.add((ImageView) main.findViewById(R.id.iv_category8));
		mAdsViews.add((ImageView) main.findViewById(R.id.iv_ad1));
		mAdsViews.add((ImageView) main.findViewById(R.id.iv_ad2));
		mAdsViews.add((ImageView) main.findViewById(R.id.iv_ad3));
		mAdsLayout = (LinearLayout) main.findViewById(R.id.ll_ads);
		for (View view : mCategoryActionViews) {
			view.setVisibility(View.INVISIBLE);
		}
		getDataFromNet();
		mSearchText = (EditText) main.findViewById(R.id.et_search);
		mSearchText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_GO
						|| actionId == EditorInfo.IME_ACTION_NEXT
						|| actionId == EditorInfo.IME_ACTION_NONE
						|| actionId == EditorInfo.IME_ACTION_PREVIOUS
						|| actionId == EditorInfo.IME_ACTION_SEND
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					searchGoods(v.getText().toString());
				}
				return false;
			}
		});
		return main;
	}
	@Override
	protected void getDataFromNet() {
		super.getDataFromNet();
		if(isNetworkConnected()){
			categoryFragment.getCategorysFromNet(getCategoryFromNetCallback);
			preAds = "";
			getAds();
		}
	};
	MyRequestCallback getCategoryFromNetCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
//			Log.e("146", result.mResponseInfo.result);
			updateCategorys();
		};
	};

	static HashMap<String, Integer> mLogos = new HashMap<String, Integer>();
	static {
		mLogos.put("今日抢鲜", R.drawable.category1);
		mLogos.put("当季预售", R.drawable.category2);
		mLogos.put("时令水果", R.drawable.category3);
		mLogos.put("水产海鲜", R.drawable.category4);
		mLogos.put("热卖食品", R.drawable.category5);
		mLogos.put("肉禽蛋品", R.drawable.category6);
		mLogos.put("淘遍南通", R.drawable.category7);
		mLogos.put("特价促销", R.drawable.category8);
	}
	
	protected void updateCategorys() {
		ArrayList<CategoryEntity> categorys = categoryFragment.getCategorys();
		for (int i = 0; i < mCategoryActionViews.size(); i++) {
			View actionView = mCategoryActionViews.get(i);
			TextView label = mCategoryLabels.get(i);
			ImageView logo = mCategoryImages.get(i);
			if (categorys.size() > i) {
				final CategoryEntity category = categorys.get(i);
				progressBar.setVisibility(View.GONE);
				actionView.setVisibility(View.VISIBLE);
				String categoryName = category.getValueAsString(CategoryEntity.CATEGORY_NAME, "");
				label.setText(categoryName);
				if (mLogos.containsKey(categoryName)) {
					logo.setImageResource(mLogos.get(categoryName));
				} else {
					LogUtils.e(categoryName + " unknow category");
				}
				actionView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								goodslistActivity.class);
						intent.putExtra(goodslistActivity.PARAM_CATEGORY,
								category.toString());
						startActivity(intent);
					}
				});
			} else {
				actionView.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void searchGoods(String goodsName) {
		if (!StringUtil.isEmpty(goodsName)) {
			Intent intent = new Intent(getActivity(), goodslistActivity.class);
			intent.putExtra(goodslistActivity.PARAM_GOODS_NAME, goodsName);
			startActivity(intent);
		}
	}

	protected void showAds() {
		int size = Math.max(mAdsViews.size(), mAds.size());
		for (int i = 0; i < size; i++) {
			if (mAdsViews.size() <= i) {
				ImageView image = new ImageView(MyApplication.getContext());
				image.setScaleType(ScaleType.FIT_XY);
				image.setBackgroundResource(R.drawable.lost_main_other); 				
				mAdsViews.add(image);
				mAdsLayout.addView(image);
			}
			ImageView image = mAdsViews.get(i);
			if (mAds.size() <= i) {
				if (i >= MIN_ADS) {
					image.setVisibility(View.GONE);
				} else {
					image.setVisibility(View.VISIBLE);
					image.setBackgroundResource(R.drawable.lost_main_other);
				}
				image.setOnClickListener(null);
			} else {
				image.setVisibility(View.VISIBLE);
				final AdsEntity ad = mAds.get(i);
				if(i == 0){
					Utils.getDefaultBitmapUtils().display(image, ad.getUrlAsString(AdsEntity.IMAGE, ""), 
							Utils.getConfig(MyApplication.getContext(), R.drawable.lost_main_other), new BitmapLoadCallBack<ImageView>() {

						@Override
						public void onLoadCompleted(final ImageView container, String uri,
								final Bitmap bitmap, BitmapDisplayConfig config,
								BitmapLoadFrom from) {
							container.postDelayed(new Runnable() {
								@Override
								public void run() {
									container.setScaleType(ScaleType.FIT_XY);
									double viewHeight = container.getWidth() / 2;
									LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (int) viewHeight);
									params.setMargins(10, 10, 10, 0);
									container.setLayoutParams(params);
									container.setImageBitmap(bitmap);
									
								}
							}, 200);
						}

						@Override
						public void onLoadFailed(ImageView container, String uri,
								Drawable drawable) {
							
						}
						
					});
					image.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (ad.has(AdsEntity.TARGET)) {
								Intent intent = new Intent(getActivity(),
										goodsDetailActivity.class);
								intent.putExtra(goodsDetailActivity.PARAM_GOODS_ID,
										ad.getValueAsString(AdsEntity.TARGET, ""));
								startActivity(intent);
							}
						}
					});
				}
				if(i > 0){
					Utils.getDefaultBitmapUtils().display(image, ad.getUrlAsString(AdsEntity.IMAGE, ""), 
							Utils.getConfig(MyApplication.getContext(), R.drawable.lost_main_other), new BitmapLoadCallBack<ImageView>() {

						@Override
						public void onLoadCompleted(final ImageView container, String uri,
								final Bitmap bitmap, BitmapDisplayConfig config,
								BitmapLoadFrom from) {
							container.postDelayed(new Runnable() {
								@Override
								public void run() {
									container.setScaleType(ScaleType.FIT_XY);
									double scale = (double)bitmap.getHeight() / (double)bitmap.getWidth();
									double viewHeight = container.getWidth() * scale;
									LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (int) viewHeight);
									params.setMargins(10, 10, 10, 0);
									container.setLayoutParams(params);
									container.setImageBitmap(bitmap);
									
								}
							}, 200);
						}

						@Override
						public void onLoadFailed(ImageView container, String uri,
								Drawable drawable) {
							
						}
						
					});
					image.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (ad.has(AdsEntity.TARGET)) {
								Intent intent = new Intent(getActivity(),
										goodsDetailActivity.class);
								intent.putExtra(goodsDetailActivity.PARAM_GOODS_ID,
										ad.getValueAsString(AdsEntity.TARGET, ""));
								startActivity(intent);
							}
						}
					});
				}
				
			}
		}
	}
	static String preAds = "";
	static final int MIN_ADS = 3;
	ArrayList<ImageView> mAdsViews = new ArrayList<ImageView>();
	ArrayList<AdsEntity> mAds = new ArrayList<AdsEntity>();

	protected void getAds() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);
		httpUtils.send(HttpMethod.GET, programSetting.getADSUrl(),
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo responseInfo) {
						String result = (String) responseInfo.result;
                        if (preAds != null && preAds.equals(result)) {
							return;
						} else {
							preAds = result;
						}
						try {
							JSONObject adJson = new JSONObject(result);
							JSONArray ads = adJson.getJSONArray("ads");
							mAds.clear();
							for (int i = 0; i < ads.length(); i++) {
								mAds.add(new AdsEntity(ads.getJSONObject(i)));
							}
							showAds();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
                        ToastUtil.show(getActivity(), msg);
					}
				});
	}
	
	Timer mTimer;
	TimerTask mTask;
	Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				getAds();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	private void startTimer() {
		mTimer = new Timer(true);
		mTask = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 1;
				mHandler.sendMessage(message);
			}
		};
		mTimer.schedule(mTask, 30 * 1000, 30 * 1000);
	}

	private void stopTimer() {
		mTimer.cancel();
		mTask.cancel();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		startTimer();
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		stopTimer();
	}
}
