package com.gosoon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.gosoon.account.MyAccount;
import com.gosoon.adapter.MyCollectionAdapter;
import com.gosoon.entity.CollectGoodsEntity;
import com.gosoon.entity.GoodsEntity;
import com.gosoon.entity.UserEntity;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.StringUtil;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class MyCollectionActivity extends BaseActivity implements OnItemClickListener {

	ArrayList<CollectGoodsEntity> mCollectGoods = null;
	MyCollectionAdapter myCollectionAdapter;
	ListView listView;
	View ly_no_collection, btn_view_goods;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_collection);
		setTitle("我的收藏");

		MyAccount.getUserFromNet(getUserCallback, false);
		listView = (ListView) findViewById(R.id.lv_colltions);
		ly_no_collection = findViewById(R.id.ly_no_collection);
		btn_view_goods = findViewById(R.id.btn_view_goods);
		btn_view_goods.setOnClickListener(onButtonClick);
		ImageView button = (ImageView) findViewById(R.id.btn_rignt);
		if(button != null){
			button.setVisibility(View.VISIBLE);
			button.setImageResource(R.drawable.btn_delete_shoppingcart);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					deleteCollections();
				}
			});
		}		
		getDataFromNet();
		listView.setOnItemClickListener(this);
	}
	OnClickListener onButtonClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			setResult(MyOrderActivity.RESULT_VIEW_GOODS);
			finish();
		}
	};
	@Override
	protected void getDataFromNet() {
		super.getDataFromNet();
		if(isNetworkConnected()){
			updateCollectionGoodsList();
		}
	}
	protected void deleteCollections() {
		if (myCollectionAdapter != null && myCollectionAdapter.getSelectedItems().size() > 0) {
			ArrayList<CollectGoodsEntity> collections = myCollectionAdapter.getSelectedItems();
			String recIds = "";
			for (int i = 0; i < collections.size(); i++) {
				if (!StringUtil.isEmpty(recIds)) {
					recIds += ", ";
				}
				recIds += collections.get(i).getValueAsString(
						CollectGoodsEntity.REC_ID, "");
			}
			MyRequest myrequest = new MyRequest(HttpMethod.POST,
					MyRequest.ACTION_SQL);
			myrequest.setProcessDialogConfig(new ProgressDialogConfig(this, MainActivity.PROCESSDIALOG_ID_MY_COLLECTIONS));
			myrequest.setAlertDialogConfig(new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_MY_COLLECTIONS));
			myrequest.setSql("DELETE from gsw_collect_goods where rec_id in (" + recIds + ")");
			myrequest.send(deleteCollectionsCallback);
		}
	}
	
	protected void updateCollectionGoodsList() {
		if(mCollectGoods != null && mCollectGoods.size() > 0){
			myCollectionAdapter = new MyCollectionAdapter(this, mCollectGoods);
			listView.setAdapter(myCollectionAdapter);
			ly_no_collection.setVisibility(View.GONE);
		}else{
			ly_no_collection.setVisibility(View.VISIBLE);
		}
	}

	protected void getCollections() {
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(this, MainActivity.PROCESSDIALOG_ID_MY_COLLECTIONS));
		myrequest.setAlertDialogConfig(new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_MY_COLLECTIONS));
		myrequest.setSql("SELECT * from gsw_collect_goods where user_id="
				+ MyAccount.getUser().getValueAsString(UserEntity.USER_ID, ""));
		myrequest.send(getCollectionsCallback);
	}

	protected void getGoods() {
		String goodsIds = "";
		for (int i = 0; i < mCollectGoods.size(); i++) {
			if (!StringUtil.isEmpty(goodsIds)) {
				goodsIds += ", ";
			}
			goodsIds += mCollectGoods.get(i).getValueAsString(
					CollectGoodsEntity.GOODS_ID, "");
		}
		if (StringUtil.isEmpty(goodsIds)) {
			updateCollectionGoodsList();
			return;
		}
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(this, MainActivity.PROCESSDIALOG_ID_MY_COLLECTIONS));
		myrequest.setAlertDialogConfig(new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_MY_COLLECTIONS));
		myrequest.setSql("SELECT * from gsw_goods where goods_id in ("
				+ goodsIds + ")");
		myrequest.send(getGoodsCallback);
	}

	MyRequestCallback getUserCallback = new MyRequestCallback() {
		
		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			getCollections();
		};
	};
	
	MyRequestCallback getCollectionsCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);

			mCollectGoods = new ArrayList<CollectGoodsEntity>();
			ArrayList<JSONObject> jsons = result.getData();
			for (JSONObject json : jsons) {
				mCollectGoods.add(new CollectGoodsEntity(json));
			}
			getGoods();
		};
	};

	MyRequestCallback getGoodsCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);

			Map<String, GoodsEntity> goodsMap = new HashMap<String, GoodsEntity>();
			ArrayList<JSONObject> jsons = result.getData();
			for (JSONObject json : jsons) {
				GoodsEntity goods = new GoodsEntity(json);
				String goodsId = goods.getValueAsString(GoodsEntity.GOODS_ID, "");
				goodsMap.put(goodsId, goods);
			}
			for (int i = 0; i < mCollectGoods.size(); i++) {
				CollectGoodsEntity collection = mCollectGoods.get(i);
				String goodsId = collection.getValueAsString(CollectGoodsEntity.GOODS_ID, "");
				collection.setGoods(goodsMap.get(goodsId));
			}
			updateCollectionGoodsList();
		};
	};
	
	MyRequestCallback deleteCollectionsCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			MyAccount.getUserFromNet(getUserCallback, false);
		};
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		GoodsEntity goods = myCollectionAdapter.getItem(position).getGoods();
		if (goods != null) {
			Intent intent = new Intent(this, goodsDetailActivity.class);
			intent.putExtra(goodsDetailActivity.PARAM_GOODS, goods.toString());
			startActivity(intent);
		}
	}
}
