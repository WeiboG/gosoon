package com.gosoon;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.gosoon.account.MyAccount;
import com.gosoon.entity.CollectGoodsEntity;
import com.gosoon.entity.GoodsEntity;
import com.gosoon.entity.GoodsGalleryEntity;
import com.gosoon.entity.UserEntity;
import com.gosoon.fragment.shoppingCartFragment;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.StringUtil;
import com.gosoon.util.ToastUtil;
import com.gosoon.util.Utils;
import com.gosoon.view.NumberSelectButton;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class goodsDetailActivity extends BaseActivity implements
		OnClickListener {

	public static final String PARAM_GOODS = "param_goods";
	public static final String PARAM_GOODS_ID = "param_goods_id";

	ArrayList<GoodsGalleryEntity> mGoodsGallerys = null;;
	GoodsEntity mGoods = null;
	String mGoodsId = null;

	TextView mGoodsName;
	TextView mGoodsSize;
	TextView mGoodsPromote;
	TextView mGoodsStock;
	TextView mGoodsPricePromote;
	TextView mGoodsPriceMarket;
	View mAddToShoppingCart;
	View mCollect;
	ViewPager mImageViewPager;
	NumberSelectButton mGoodsNumber;
	WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.products_detail);
		initDataFromIntent();

		mGoodsName = (TextView) findViewById(R.id.tv_products_name_item);
		mGoodsSize = (TextView) findViewById(R.id.tv_products_size);
		mGoodsPromote = (TextView) findViewById(R.id.tv_products_promote);
		mGoodsStock = (TextView) findViewById(R.id.tv_products_stock);
		mGoodsPricePromote = (TextView) findViewById(R.id.tv_products_price_promote);
		mGoodsPriceMarket = (TextView) findViewById(R.id.tv_products_price_market);
		mAddToShoppingCart = findViewById(R.id.btn_add_to_shoppingcart);
		mCollect = findViewById(R.id.btn_collect);
		mGoodsNumber = (NumberSelectButton) findViewById(R.id.nsb_products_number);
		mImageViewPager = (ViewPager) findViewById(R.id.image_pager);
		mWebView = (WebView) findViewById(R.id.wv_goods_detail);

		configWebView();

		mAddToShoppingCart.setOnClickListener(this);
		mCollect.setOnClickListener(this);

		updateGoods();
		if (mGoodsGallerys == null) {
			getGoodsGallery();
		} else {
			updateGoodsGallery();
		}
	}

	private void configWebView() {
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings()
				.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress == 100) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							mWebView.setVisibility(View.VISIBLE);
						}
					}, 500);
				}
			}
		});
	}

	protected void initDataFromIntent() {
		Intent intent = getIntent();
		if (intent.hasExtra(PARAM_GOODS)) {
			mGoods = new GoodsEntity(null);
			mGoods.fromString(intent.getStringExtra(PARAM_GOODS));
			mGoodsId = mGoods.getValueAsString(GoodsEntity.GOODS_ID, "");
		}
		if (intent.hasExtra(PARAM_GOODS_ID)) {
			mGoodsId = intent.getStringExtra(PARAM_GOODS_ID);
		}
	}

	protected void updateGoods() {
		if (mGoods != null) {
			mGoodsName.setText(mGoods.getValueAsString(GoodsEntity.GOODS_NAME,
					""));
			mGoodsSize.setText(mGoods.getWeight());
			mGoodsPricePromote.setText(mGoods.getValueAsString(
					GoodsEntity.SHOP_PRICE, ""));
			mGoodsPriceMarket.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			mGoodsPriceMarket.setText("￥ "
					+ mGoods.getValueAsString(GoodsEntity.MARKET_PRICE, ""));
			mWebView.loadData(
					mGoods.getValueAsString(GoodsEntity.GOODS_DESC, ""),
					"text/html", "utf-8");
			mWebView.loadDataWithBaseURL("http://www.gosoon60.com",
					mGoods.getValueAsString(GoodsEntity.GOODS_DESC, ""),
					"text/html", "utf-8", "");
		} else if (!StringUtil.isEmpty(mGoodsId)) {
			
			getDataFromNet();
		}
	}
	@Override
	protected void getDataFromNet() {
		super.getDataFromNet();
		if(isNetworkConnected()){
			getGoodsById();
		}
	}
	protected void getGoodsById() {
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		myrequest.setSql("SELECT * from gsw_goods where goods_id=" + mGoodsId);
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(this,
				MainActivity.PROCESSDIALOG_ID_GETGOODSDETAIL));
		myrequest.setAlertDialogConfig(new AlertDialogConfig(this,
				MainActivity.ALERTDIALOG_ID_GETGOODSDETAIL));
		myrequest.send(getGoodsCallback);
	}

	protected void getGoodsGallery() {
		if (mGoodsGallerys == null) {
			MyRequest myrequest = new MyRequest(HttpMethod.POST,
					MyRequest.ACTION_SQL);
			myrequest.setSql("SELECT * from gsw_goods_gallery where goods_id="
					+ mGoodsId);
			myrequest.send(getGoodsGalleryCallback);
		}
	}

	protected void updateGoodsGallery() {
		if (mGoodsGallerys != null) {
			ArrayList<String> imageUrls = new ArrayList<String>();
			for (GoodsGalleryEntity goodsGallery : mGoodsGallerys) {
				imageUrls.add(goodsGallery.getUrlAsString(
						GoodsGalleryEntity.GOODSGALLERY_IMG_URL, ""));
			}

			final ArrayList<View> imageViews = new ArrayList<View>();
			for (int i = 0; i < mGoodsGallerys.size(); i++) {
				ImageView image = new ImageView(this);
				image.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				image.setScaleType(ScaleType.FIT_XY);
				Utils.getDefaultBitmapUtils().display(image,
						mGoodsGallerys.get(i).getUrlAsString(
								GoodsGalleryEntity.GOODSGALLERY_IMG_URL, ""), Utils.getConfig(this, R.drawable.lost_goods_detail));
				imageViews.add(image);
			}
			PagerAdapter adapter = new PagerAdapter() {

				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return imageViews.size();
				}

				@Override
				public boolean isViewFromObject(View arg0, Object arg1) {
					// TODO Auto-generated method stub
					return arg0 == arg1;
				}

				@Override
				public void destroyItem(ViewGroup container, int position,
						Object object) {
					container.removeView(imageViews.get(position));// 删除页卡
				}

				@Override
				public Object instantiateItem(ViewGroup container, int position) {
					container.addView(imageViews.get(position), 0);
					return imageViews.get(position);
				}
			};
			mImageViewPager.setAdapter(adapter);
		}
	}

	MyRequestCallback getGoodsCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			JSONObject json = result.getFirstData();
			if (json != null) {
				mGoods = new GoodsEntity(json);
				updateGoods();
			}
		};
	};

	MyRequestCallback getGoodsGalleryCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			mGoodsGallerys = new ArrayList<GoodsGalleryEntity>();
			ArrayList<JSONObject> jsons = result.getData();
			for (JSONObject json : jsons) {
				mGoodsGallerys.add(new GoodsGalleryEntity(json));
			}
			updateGoodsGallery();
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add_to_shoppingcart:
			addToShoppingCart();
			break;
		case R.id.btn_collect:
			collectGoods();
			break;
		case R.id.btn_back_detail:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	protected void addToShoppingCart() {
		shoppingCartFragment.addShoppingCart(mGoods,
				Integer.parseInt(mGoodsNumber.getText()));
	}

	protected void collectGoods() {
        if (MyAccount.mbLogin) {
        	MyAccount.getUserFromNet(new MyRequestCallback(){
        		@Override
        		public void onSuccess(MyResult result) {
        			super.onSuccess(result);
        			MyRequest myrequest = new MyRequest(HttpMethod.POST,
        					MyRequest.ACTION_SQL);
        			myrequest.setSql("SELECT * from gsw_collect_goods where goods_id="
        					+ mGoodsId + " and user_id=" + MyAccount.getUser().getValueAsString(UserEntity.USER_ID, ""));
        			myrequest.setProcessDialogConfig(new ProgressDialogConfig(goodsDetailActivity.this,
        					MainActivity.PROCESSDIALOG_ID_GETGOODSDETAIL));
        			myrequest.setAlertDialogConfig(new AlertDialogConfig(goodsDetailActivity.this,
        					MainActivity.ALERTDIALOG_ID_GETGOODSDETAIL));
        			myrequest.send(new MyRequestCallback(){
        				@Override
                    	public void onSuccess(MyResult result) {
                    		super.onSuccess(result);
                    		if(result.getFirstData() == null){
                    			CollectGoodsEntity collectGoods = new CollectGoodsEntity();
                    			collectGoods.setValueAsString(CollectGoodsEntity.GOODS_ID, mGoodsId);
                    			collectGoods.setValueAsString(CollectGoodsEntity.USER_ID, MyAccount.getUser().getValueAsString(UserEntity.USER_ID, ""));
                    			collectGoods.setValueAsString(CollectGoodsEntity.ADD_TIME, System.currentTimeMillis() / 1000l + "");
                    			MyRequest myrequest = new MyRequest(collectGoods.getTable().mTableName, collectGoods.toString());
                    			myrequest.setProcessDialogConfig(new ProgressDialogConfig(goodsDetailActivity.this,
                    					MainActivity.PROCESSDIALOG_ID_GETGOODSDETAIL));
                    			myrequest.setAlertDialogConfig(new AlertDialogConfig(goodsDetailActivity.this,
                    					MainActivity.ALERTDIALOG_ID_GETGOODSDETAIL));
                                myrequest.send(new MyRequestCallback(){
                                	@Override
                                	public void onSuccess(MyResult result) {
                                		super.onSuccess(result);
                                		ToastUtil.show(getApplicationContext(), "收藏成功");
                                	}
                                	@Override
                                	public void onFailure(MyResult result) {
                                		super.onFailure(result);
                                		ToastUtil.show(getApplicationContext(), "收藏失败");
                                	}
                                });
                    		}else{
                    			ToastUtil.show(getApplicationContext(), "该商品已经在您的收藏夹中");
                    		}
                    	}
                    	@Override
                    	public void onFailure(MyResult result) {
                    		super.onFailure(result);
                    		ToastUtil.show(getApplicationContext(), "收藏失败");
                    	}
        			});
        			
        		}
        	}, false);
        } else {
        	Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
	}
}
