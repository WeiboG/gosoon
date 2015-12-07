package com.gosoon;

import java.util.ArrayList;

import org.json.JSONObject;

import com.gosoon.R;
import com.gosoon.adapter.GoodsAdapter;
import com.gosoon.entity.CategoryEntity;
import com.gosoon.entity.GoodsEntity;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.StringUtil;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ListView;

public class goodslistActivity extends BaseActivity implements
		OnItemClickListener {

	public static final String PARAM_CATEGORY = "param_category";
	public static final String PARAM_GOODS_NAME = "param_goods_name";

	CategoryEntity mParentCategory = null;
	ArrayList<GoodsEntity> mGoods = null;
	GoodsAdapter mGoodsAdapter = null;
	ListView mGoodsListView = null;
	String mGoodsName = null;

	EditText mSearchText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_products_list);
		initDataFromIntent();

		mSearchText = (EditText) findViewById(R.id.et_search);
		mSearchText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
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
		
		mGoodsListView = (ListView) findViewById(R.id.lv_products);
		mGoodsListView.setDividerHeight(0);
		mGoodsListView.setOnItemClickListener(this);
		mGoodsListView.setAdapter(mGoodsAdapter);
		getDataFromNet();
	}
	@Override
	protected void getDataFromNet() {
		super.getDataFromNet();
		if(isNetworkConnected()){
			getGoodsList(false);
		}
	}
	protected void initDataFromIntent() {
		Intent intent = getIntent();
		if (intent.hasExtra(PARAM_CATEGORY)) {
			mParentCategory = new CategoryEntity(null);
			mParentCategory.fromString(intent.getStringExtra(PARAM_CATEGORY));
			setTitle(mParentCategory.getValueAsString(CategoryEntity.CATEGORY_NAME, ""));
		}
		if (intent.hasExtra(PARAM_GOODS_NAME)) {
			mGoodsName = intent.getStringExtra(PARAM_GOODS_NAME);
			setTitle("商品搜索");
		}
	}

	private void searchGoods(String goodsName) {
		mGoodsName = goodsName;
		getGoodsList(true);
	}

	protected void getGoodsList(boolean force) {
		if (mGoods == null || force) {
			if (mParentCategory == null && StringUtil.isEmpty(mGoodsName)) {
				return;
			}
			MyRequest myrequest = new MyRequest(HttpMethod.POST,
					MyRequest.ACTION_SQL);
			String whereCondition = "";
			if (mParentCategory != null) {
				String catId = mParentCategory.getValueAsString(
						CategoryEntity.CATEGORY_ID, "0");
				myrequest.setSql("SELECT A.* from gsw_goods A LEFT JOIN gsw_goods_cat B " +
						"on A.goods_id=B.goods_id where " +
						"(A.cat_id=" + catId + " OR B.cat_id=" + catId + ") " +
						"AND A.is_on_sale=1 AND A.is_delete=0 " +
						"GROUP by A.goods_id order by A.sort_order desc");

			} else if (!StringUtil.isEmpty(mGoodsName)) {
				whereCondition += " AND goods_name like '%" + mGoodsName + "%'";
				myrequest.setSql("SELECT * from gsw_goods where is_on_sale=1 AND is_delete=0 " + whereCondition);
			}
			myrequest.setProcessDialogConfig(new ProgressDialogConfig(this, MainActivity.PROCESSDIALOG_ID_GETGOODSLIST));
			myrequest.setAlertDialogConfig(new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_GETGOODSLIST));
			myrequest.send(getGoodsCallback);
		}
	}

	MyRequestCallback getGoodsCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			mGoods = new ArrayList<GoodsEntity>();
			ArrayList<JSONObject> jsons = result.getData();
			for (JSONObject json : jsons) {
				mGoods.add(new GoodsEntity(json));
			}
			updateGoodsList();
		};
	};

	protected void updateGoodsList() {
		if (mGoodsListView != null) {
			if (mGoodsAdapter == null) {
				mGoodsAdapter = new GoodsAdapter(this, mGoods);
				mGoodsListView.setAdapter(mGoodsAdapter);
			} else {
				mGoodsAdapter.changeData(mGoods);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		GoodsEntity goods = mGoodsAdapter.getItem(position);
		if (goods != null) {
			Intent intent = new Intent(this, goodsDetailActivity.class);
			intent.putExtra(goodsDetailActivity.PARAM_GOODS, goods.toString());
			startActivity(intent);
		}
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
	}
}
