package com.gosoon.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gosoon.MainActivity;
import com.gosoon.account.MyAccount;
import com.gosoon.fragment.categoryFragment;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyQueryPlug;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.StringUtil;
import com.gosoon.util.programSetting;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class GoodsEntity extends BaseEntity {

	public static final String GOODS_ID = "goods_id";
	public static final String GOODS_SN = "goods_sn";
	public static final String GOODS_NAME = "goods_name";
	public static final String MARKET_PRICE = "market_price";
	public static final String SHOP_PRICE = "shop_price";
	public static final String PROMOTE_PRICE = "promote_price";
	public static final String GOODS_BRIEF = "goods_brief";
	public static final String GOODS_DESC = "goods_desc";
	public static final String GOODS_THUMB = "goods_thumb";
	public static final String GOODS_IMG = "goods_img";
	public static final String GOODS_WEIGHT = "goods_weight";
	public static final String GOODS_WEIGHT_STR = "goods_weight_str";
	public static final String SORT_ORDER = "sort_order";
	public static final String CAT_ID = "cat_id";
	public static final String IS_REAL = "is_real";
	public static final String EXTENSION_CODE = "extension_code";
	public static final String IS_FAVOURABLE = "is_favourable";   //优惠商品

	public GoodsEntity(JSONObject json) {
		super(json);
	}

	public GoodsEntity() {
	}

	static tableConfig mTable = null;

	@Override
	public tableConfig getTable() {
		if (mTable == null) {
			mTable = new tableConfig("gsw_goods");
		}
		return mTable;
	}

	String weight = null;
	public String getWeight() {
		if (StringUtil.isEmpty(weight)) {
			CategoryEntity category = categoryFragment
					.getCategoryByCatId(getValueAsString(GoodsEntity.CAT_ID, ""));
			String unit = "";
			if (category != null) {
				unit = category.getValueAsString(CategoryEntity.MEASURE_UNIT,
						"");
			}
			weight = getValueAsString(GoodsEntity.GOODS_WEIGHT_STR, "") + unit;
		}
		return weight;
	}
	
	int stock = 999;
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	

	public static void checkGoods(final GoodsEntity goods, final boolean addShoppingCart, 
			final MyRequestCallback callback) {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(1000 * 30);
		MyQueryPlug queryPlug = new MyQueryPlug();
		queryPlug.setQueryCondition("act", "check_goods");
		queryPlug.setQueryCondition("goods_id",
				goods.getValueAsString(GOODS_ID, ""));
		if (MyAccount.mbLogin && MyAccount.getUser() != null) {
			queryPlug.setQueryCondition("user_id", MyAccount.getUser()
					.getValueAsString(UserEntity.USER_ID, ""));
		}
		String url = programSetting.getRequestUrl() + "interface.php?"
				+ queryPlug.toString();
		

		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo responseInfo) {
				MyResult result = new MyResult();

				String response = (String) responseInfo.result;
				try {
					JSONObject json = new JSONObject(response);
					if (json.getInt("result") != 0) {
						if (json.getInt("result") == 1) {
							goods.setStock(1);
						}
						if (addShoppingCart) {
							checkGoodsOnSale(goods, callback);
						} else {
							result.setSuccess();
							callback.onSuccess(result);
						}
						return;
					} else {
						result.setFailInfo(null, "您已购买此特价商品，不能再次购买");
					}
				} catch (JSONException e) {
					e.printStackTrace();
					result.setFailInfo(null, "未知错误");
				}
				callback.onFailure(result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				MyResult result = new MyResult();
				result.setFailInfo(null, "未知错误");
				callback.onFailure(result);
			}
		});
	}

	public static void checkGoodsOnSale(final GoodsEntity goods,
			final MyRequestCallback callback) {
		MyRequest myrequest = new MyRequest(HttpMethod.GET,
				MyRequest.ACTION_SQL);
		myrequest.setSql("SELECT * from gsw_goods where goods_id=" + goods.getValueAsString(GoodsEntity.GOODS_ID, ""));
		myrequest.send(new MyRequestCallback() {
			
			@Override
			public void onSuccess(MyResult result) {
				super.onSuccess(result);
				JSONObject json = result.getFirstData();
				if (json != null) {
					GoodsEntity checkGoods = new GoodsEntity(json);
					if (checkGoods.getValueAsInt("is_on_sale", 0) == 1) {
						result.setSuccess();
						callback.onSuccess(result);
						return;
					} else {
						result.setFailInfo(null, goods.getValueAsString(GoodsEntity.GOODS_NAME, "商品") + "已下架");
						callback.onFailure(result);
						return;
					}
				}
				result.setFailInfo(null, "未知错误");
				callback.onFailure(result);
			}
			
			@Override
			public void onFailure(MyResult result) {
				super.onFailure(result);
				callback.onFailure(result);
			}
		});
		
	}
}
