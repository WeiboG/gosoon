package com.gosoon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.gosoon.account.MyAccount;
import com.gosoon.entity.GoodsEntity;
import com.gosoon.adapter.MyOrdersAdapter;
import com.gosoon.entity.OrderGoodsEntity;
import com.gosoon.entity.OrderInfoEntity;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

public class MyOrderActivity extends BaseActivity implements OnItemClickListener {
	public static final int RESULT_VIEW_GOODS = 1;
	ArrayList<OrderInfoEntity> mOrderInfos = null;
	MyOrdersAdapter ordersAdapter;
	ListView listView;
	View ly_no_order, btn_view_goods,view_top_detail;
	ImageButton mAddBonusBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_order);
		setTitle("我的订单");

		listView = (ListView) findViewById(R.id.lv_orders);
		ly_no_order = findViewById(R.id.ly_no_order);
		listView.setOnItemClickListener(this);
		btn_view_goods = findViewById(R.id.btn_view_goods);
		btn_view_goods.setOnClickListener(onButtonClick);
		
	}
	OnClickListener onButtonClick = new OnClickListener() {
		@Override
		public void onClick(View arg0){
			setResult(RESULT_VIEW_GOODS);
			finish();
		}
	};
	@Override
	protected void onResume() {
		MyAccount.getUserFromNet(getUserCallback, false);
		updateOrderInfoList();
		super.onResume();
	}

	protected void updateOrderInfoList(){
		if(mOrderInfos != null && mOrderInfos.size() > 0){
			ordersAdapter = new MyOrdersAdapter(this, mOrderInfos);
			listView.setAdapter(ordersAdapter);
			ly_no_order.setVisibility(View.GONE);
		}else{
			ly_no_order.setVisibility(View.VISIBLE);
		}
	}

	protected void getOrderInfos(){
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(this, MainActivity.ALERTDIALOG_ID_LOGIN));
		myrequest.setAlertDialogConfig(new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_LOGIN));
		myrequest.setSql("SELECT gsw_order_info.*,gsw_bonus_type.* from gsw_order_info "
				+ "LEFT JOIN gsw_user_bonus ON gsw_order_info.order_id=gsw_user_bonus.order_id "
				+ "LEFT JOIN gsw_bonus_type ON gsw_user_bonus.bonus_type_id = gsw_bonus_type.type_id "
				+ "where gsw_order_info.user_id="
				+ MyAccount.getUser().getValueAsString(UserEntity.USER_ID, "") + " GROUP BY gsw_order_info.order_id order by add_time desc " );
		myrequest.send(getOrderInfoCallback);
	}
	protected void getOrderGoods() {
		String orderIds = "";
		for (int i = 0; i < mOrderInfos.size(); i++) {
			if (!StringUtil.isEmpty(orderIds)){
				orderIds += ", ";
			}
			orderIds += mOrderInfos.get(i).getValueAsString(
					OrderInfoEntity.ORDER_ID, "");
		}
		if (StringUtil.isEmpty(orderIds)) {
			updateOrderInfoList();
			return;
		}
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(this, MainActivity.PROCESSDIALOG_ID_MY_ORDERS));
		myrequest.setAlertDialogConfig(new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_MY_ORDERS));
		myrequest.setSql("SELECT * from gsw_order_goods where order_id in ("
				+ orderIds + ")");
		myrequest.send(getOrderGoodsCallback);
	}
	protected void getGoods() {
		String goodsIds = "";
		for (int i = 0; i < mOrderInfos.size(); i++) {
			ArrayList<OrderGoodsEntity> orderGoods = mOrderInfos.get(i).getOrderGoods();
			if (orderGoods != null && orderGoods.size() > 0) {
				for (int j = 0; j < orderGoods.size(); j++) {
					if (!StringUtil.isEmpty(goodsIds)) {
						goodsIds += ", ";
					}
					goodsIds += orderGoods.get(j).getValueAsString(
							OrderGoodsEntity.GOODS_ID, "");
				}
			}
		}
		if (StringUtil.isEmpty(goodsIds)) {
			updateOrderInfoList();
			return;
		}
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		myrequest.setProcessDialogConfig(new ProgressDialogConfig(this, MainActivity.PROCESSDIALOG_ID_MY_ORDERS));
		myrequest.setAlertDialogConfig(new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_MY_ORDERS));
		myrequest.setSql("SELECT * from gsw_goods where goods_id in ("
				+ goodsIds + ")");
		myrequest.send(getGoodsCallback);
	}

	MyRequestCallback getUserCallback = new MyRequestCallback() {
		
		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			getDataFromNet();
		};
	};
	@Override
	protected void getDataFromNet() {
		super.getDataFromNet();
		if(isNetworkConnected()){
			getOrderInfos();
		}
	}
	
	MyRequestCallback getOrderInfoCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);

			mOrderInfos = new ArrayList<OrderInfoEntity>();
			ArrayList<JSONObject> jsons = result.getData();
			for (JSONObject json : jsons) {
				mOrderInfos.add(new OrderInfoEntity(json));
			}
			getOrderGoods();
		};
	};

	MyRequestCallback getOrderGoodsCallback = new MyRequestCallback(){

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);

			Map<String, ArrayList<OrderGoodsEntity>> orderGoodsMap = new HashMap<String, ArrayList<OrderGoodsEntity>>();
			ArrayList<JSONObject> jsons = result.getData();
			for (JSONObject json : jsons) {
				OrderGoodsEntity orderGoods = new OrderGoodsEntity(json);
				String orderId = orderGoods.getValueAsString(OrderGoodsEntity.ORDER_ID, "");
				if (!orderGoodsMap.containsKey(orderId)) {
					orderGoodsMap.put(orderId, new ArrayList<OrderGoodsEntity>());
				}
				orderGoodsMap.get(orderId).add(orderGoods);
			}
			for (int i = 0; i < mOrderInfos.size(); i++) {
				OrderInfoEntity orderInfo = mOrderInfos.get(i);
				String orderId = orderInfo.getValueAsString(OrderInfoEntity.ORDER_ID, "");
				ArrayList<OrderGoodsEntity> orderGoods = orderGoodsMap.get(orderId);
				orderInfo.setOrderGoods(orderGoods);
			}
			getGoods();
		};
	};

	MyRequestCallback getGoodsCallback = new MyRequestCallback(){

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
			for (int i = 0; i < mOrderInfos.size(); i++) {
				ArrayList<OrderGoodsEntity> orderGoods = mOrderInfos.get(i).getOrderGoods();
				if (orderGoods != null && orderGoods.size() > 0) {
					for (int j = 0; j < orderGoods.size(); j++) {
						String goodsId = orderGoods.get(j).getValueAsString(
								OrderGoodsEntity.GOODS_ID, "");
						orderGoods.get(j).setGoods(goodsMap.get(goodsId));
					}
				}
			}
			updateOrderInfoList();
		};
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3){
		Intent intent = new Intent(this, settleAccountsActivity.class);
		intent.putExtra(settleAccountsActivity.PARAM_ORDER_INFO, ordersAdapter.getItem(position).toString());
		startActivity(intent);
	}
}
