package com.gosoon.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.gosoon.LoginActivity;
import com.gosoon.MainActivity;
import com.gosoon.MyApplication;
import com.gosoon.R;
import com.gosoon.settleAccountsActivity;
import com.gosoon.account.MyAccount;
import com.gosoon.adapter.ShoppingcartAdapter;
import com.gosoon.adapter.ShoppingcartAdapter.OnGoodsChecked;
import com.gosoon.entity.CartEntity;
import com.gosoon.entity.GoodsEntity;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.ToastUtil;
import com.gosoon.util.Utils;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

@SuppressLint("ValidFragment")
public class shoppingCartFragment extends baseFragment implements
		OnClickListener {

	ShoppingcartAdapter mShoppingcartAdapter;
	ListView mShoppingCartListView;
	CheckBox mCheckAll;
	static TextView mTotalPrice;
	View mSettleAccounts;
	View btn_rignt, ly_no_goods, ly_shoppingcart_total, mViewGoods;
	private OnViewGoods onViewGoods = null;
	@SuppressLint("ValidFragment")
	public shoppingCartFragment(Bundle params){
		super(params);
	}
	
	public interface OnViewGoods{
	      public void OnViewGoodsClick();  
	} 
	public void setOnViewGoodsClick(OnViewGoods onViewGoods){
		this.onViewGoods = onViewGoods;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View main = inflater.inflate(R.layout.fragment_shoppingcart, container,
				false);
		setTitle(main, getResources().getString(R.string.view_shoppingcart));
		hideLeftButton(main, true);
		setRightButton(main, R.drawable.btn_delete_shoppingcart);
		
		mShoppingCartListView = (ListView) main
				.findViewById(R.id.lv_shoppingcart);
		ly_no_goods = main.findViewById(R.id.ly_no_goods);
		mViewGoods = main.findViewById(R.id.btn_add_address);
		ly_shoppingcart_total = main.findViewById(R.id.ly_shoppingcart_total);
		mShoppingCartListView.setDividerHeight(0);
		mShoppingCartListView.setAdapter(mShoppingcartAdapter);
		mCheckAll = (CheckBox) main
				.findViewById(R.id.cb_shoppingcart_check_all);
		mSettleAccounts = main.findViewById(R.id.btn_settle_accounts);

		mCheckAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(!doNothing){
					for (CartEntity cartEntity : mCarts) {
						cartEntity.setValueAsBoolean(CartEntity.CART_CHECK,
								isChecked);
					}
					saveShoppingCarts();
					totalPrice();
					updateShoppingcart();
				}
				doNothing = false;
			}
		});
		mSettleAccounts.setOnClickListener(this);
		mViewGoods.setOnClickListener(this);

		mTotalPrice = (TextView) main
				.findViewById(R.id.tv_shoppingcart_total_label);
		totalPrice();
		updateShoppingcart();
		return main;
	}
	@Override
	public void onResume() {
		updateShoppingcart();
		super.onResume();
	}

	protected void updateShoppingcart() {
		if (mShoppingcartAdapter == null ) {
			mShoppingcartAdapter = new ShoppingcartAdapter(this, mCarts);
			mShoppingcartAdapter.setOnGoodsChecked(onGoodsChecked);
			mShoppingCartListView.setAdapter(mShoppingcartAdapter);
		} else {
			mShoppingcartAdapter.changeData(mCarts);
		}
		if(mCarts.size() > 0){
			ly_no_goods.setVisibility(View.GONE);
			ly_shoppingcart_total.setVisibility(View.VISIBLE);
		}else{
			ly_no_goods.setVisibility(View.VISIBLE);
			ly_shoppingcart_total.setVisibility(View.GONE);
		}
		isCheckAll();
	}
	private OnGoodsChecked onGoodsChecked = new OnGoodsChecked(){
		@Override
		public void onChecked() {
			isCheckAll();
		}
	};
	public static final String SHAREDPREFERENCE_SHOPPINGCART = "shoppingcart";
	private static ArrayList<CartEntity> mCarts = new ArrayList<CartEntity>();
	public static void initShoppingCarts(){
		mCarts.clear();
		SharedPreferences sharedPreferences = Utils
				.getDefaultSharedPreferences();
		Set<String> cartSet = sharedPreferences.getStringSet(
				SHAREDPREFERENCE_SHOPPINGCART, null);
		if (cartSet != null) {
			Iterator<String> it = cartSet.iterator();
			while (it.hasNext()) {
				String jsonstr = it.next();
				CartEntity cart = new CartEntity();
				cart.fromString(jsonstr);
				mCarts.add(cart);
			}
		}
	}

	public static void saveShoppingCarts() {
		SharedPreferences sharedPreferences = Utils
				.getDefaultSharedPreferences();
		Set<String> cartSet = new HashSet<String>();
		for (CartEntity cart : mCarts) {
			cartSet.add(cart.toString());
		}
		Editor editor = sharedPreferences.edit();
		editor.putStringSet(SHAREDPREFERENCE_SHOPPINGCART, cartSet);
		editor.commit();
		totalPrice();
		MainActivity.updateShoppingCartSize();
	}

	public static ArrayList<CartEntity> getShoppingCart() {
		return mCarts;
	}

	public static CartEntity getCartFromShoppingCart(String goodsId) {
		for (CartEntity cart : mCarts) {
			GoodsEntity goods = cart.getGoodsEntity();
			if (goods != null
					&& goods.getValueAsString(GoodsEntity.GOODS_ID, "invalid")
							.equals(goodsId)) {
				return cart;
			}
		}
		return null;
	}

	public static void addShoppingCart(final GoodsEntity goods, final int num) {
		if (goods == null) {
			return;
		}
		GoodsEntity.checkGoods(goods, true, new MyRequestCallback(){
			@Override
			public void onSuccess(MyResult result) {
				super.onSuccess(result);
				doAddShoppingCart(goods, num);
				ToastUtil.show(MyApplication.getContext(), goods.getValueAsString(GoodsEntity.GOODS_NAME, "") + "加入购物车成功");
			}

			@Override
			public void onFailure(MyResult result) {
				super.onFailure(result);
				ToastUtil.show(MyApplication.getContext(), result.mFailReason);
			}
		});
	}
	
	public static void doAddShoppingCart(GoodsEntity goods, int num) {
		CartEntity cart = getCartFromShoppingCart(goods.getValueAsString(
				GoodsEntity.GOODS_ID, ""));
		if (cart == null) {
			cart = new CartEntity();
			cart.setGoodsEntity(goods);
			cart.setValueAsInt(CartEntity.CART_STOCK, goods.getStock());
			mCarts.add(cart);
			getGoodsAttr(cart);
		}
		int amount = cart.getValueAsInt(CartEntity.CART_AMOUNT, 0);
		amount += num;
		if (amount > goods.getStock()) {
			amount = goods.getStock();
		}
		cart.setValueAsInt(CartEntity.CART_AMOUNT, amount);
		saveShoppingCarts();
	}

	public static void getGoodsAttr(final CartEntity cart) {
		MyRequest myrequest = new MyRequest(HttpMethod.GET,
				MyRequest.ACTION_SQL);
		myrequest
				.setSql("SELECT a.attr_name, g.attr_value FROM gsw_goods_attr AS g LEFT JOIN gsw_attribute AS a ON a.attr_id=g.attr_id WHERE a.attr_id=211 AND g.goods_id="
						+ cart.getGoodsEntity().getValueAsString(GoodsEntity.GOODS_ID, "")
						+ " GROUP BY a.sort_order, g.attr_price, g.goods_attr_id");
		myrequest.send(new MyRequestCallback() {
			@Override
			public void onSuccess(MyResult result) {
				super.onSuccess(result);
				JSONObject json = result.getFirstData();
				if (json != null) {
					try {
						cart.setValueAsString(CartEntity.CART_GOODS_ATTR_NAME, json.getString("attr_name"));
						cart.setValueAsString(CartEntity.CART_GOODS_ATTR_VALUE, json.getString("attr_value"));
						saveShoppingCarts();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static void modifyShoppingCart(GoodsEntity goods, String name,
			String value) {
		if (goods == null) {
			return;
		}
		CartEntity cart = getCartFromShoppingCart(goods.getValueAsString(
				GoodsEntity.GOODS_ID, ""));
		if (cart == null) {
			cart = new CartEntity();
			cart.setGoodsEntity(goods);			
			cart.setValueAsInt(CartEntity.CART_STOCK, goods.getStock());
			mCarts.add(cart);
			getGoodsAttr(cart);
		}
		if (CartEntity.CART_AMOUNT.equals(name)) {
		    int num = Integer.parseInt(value);
		    if (num > cart.getValueAsInt(CartEntity.CART_STOCK, 999)) {
		    	ToastUtil.show(MyApplication.getContext(), "超过商品可购买数量");
				saveShoppingCarts();
		    	return;
		    }
		}
		cart.setValueAsString(name, value);
		saveShoppingCarts();
	}
	boolean doNothing = false;
	private void isCheckAll(){
		boolean isCheckAll = true;
		for (int i = 0; i < mCarts.size(); i++) {
			isCheckAll = mCarts.get(i).getValueAsBoolean(CartEntity.CART_CHECK, false);
			if(!isCheckAll){
				break;
			}
		}
		//doNothing = !isCheckAll;
		doNothing = true;
		mCheckAll.setChecked(isCheckAll);
		doNothing = false;
	}
	public static void delSelectedShoppingCart() {
		ArrayList<CartEntity> carts = getCheckedCartEntitys();
		mCarts.removeAll(carts);
		saveShoppingCarts();
	}

	// 计算总价
	public static void totalPrice() {
		double totalPrice = 0;
		for (CartEntity cart : mCarts) {
			if (cart.getValueAsBoolean(CartEntity.CART_CHECK, false)) {
				double price = Double
						.parseDouble(cart.getGoodsEntity().getValueAsString(
								GoodsEntity.SHOP_PRICE, "0.00"));
				totalPrice += (cart.getValueAsInt(CartEntity.CART_AMOUNT, 1) * price);
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		Message msg = new Message();
		msg.obj = df.format(totalPrice);
		mHandler.dispatchMessage(msg);
	}
	
	public static String getTotalPrice() {
		double totalPrice = 0;
		for (CartEntity cart : mCarts) {
			if (cart.getValueAsBoolean(CartEntity.CART_CHECK, false)) {
				double price = Double
						.parseDouble(cart.getGoodsEntity().getValueAsString(
								GoodsEntity.SHOP_PRICE, "0.00"));
				totalPrice += (cart.getValueAsInt(CartEntity.CART_AMOUNT, 1) * price);
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
        return  df.format(totalPrice);
	}

	// 获取选中的CartEntity 列表
	public static ArrayList<CartEntity> getCheckedCartEntitys(){
		ArrayList<CartEntity> cartEntitys = null;
		for (CartEntity cart : mCarts) {
			if (cart.getValueAsBoolean(CartEntity.CART_CHECK, false)) {
				if(cartEntitys == null){
					cartEntitys = new ArrayList<CartEntity>();
				}
				cartEntitys.add(cart);
			}
		}
		return cartEntitys;
	}
	

	static Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (mTotalPrice != null) {
				mTotalPrice.setText(msg.obj.toString());
			}
		};
	};

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_settle_accounts:
			settleAccounts();
			break;
		case R.id.btn_rignt:
			showDelAlert();
			break;
		case R.id.btn_add_address:
			if(onViewGoods != null){
				onViewGoods.OnViewGoodsClick();
			}
			break;
		default:
			break;
		}
	}

	private void showDelAlert() {
		ArrayList<CartEntity> carts = getCheckedCartEntitys();
		if(carts == null || carts.size() == 0){
			return;
		}
		
		AlertDialogConfig alertDialogConfig = new AlertDialogConfig(getActivity(), MainActivity.ALERTDIALOG_ID_DEL_SHOPPINGCART);
		alertDialogConfig.message = "确定要删除所选商品吗？";
		alertDialogConfig.title = R.string.prompt;
		alertDialogConfig.showNegative = true;
		alertDialogConfig.onPositiveListener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				delSelectedShoppingCart();
//				mShoppingcartAdapter.changeData(mCarts);
				updateShoppingcart();
			}
		};
		alertDialogConfig.show();
	}

	protected void settleAccounts() {
		if (MyAccount.mbLogin) {
			ArrayList<CartEntity> cartEntitys = getCheckedCartEntitys();
			if (cartEntitys != null && cartEntitys.size() > 0){
				index = 0;
				checkGoods();
			} else {
				AlertDialogConfig config = new AlertDialogConfig(getActivity(), MainActivity.ALERTDIALOG_ID_SELECT_SHOPPING_CART);
				config.message = "请先选择商品";
				((MainActivity) getActivity()).showAlertDialog(config);	
			}
		} else {
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
		}
	}
	
	int index = 0;
	ProgressDialogConfig mProgressDialogConfig;
	protected void checkGoods() {
		if (index == 0) {
//			Log.e("TAG", "index--->0");
			mProgressDialogConfig = new ProgressDialogConfig(getActivity(), 999);
			mProgressDialogConfig.cancelable = false;
			((MainActivity) getActivity()).showProcessDialog(mProgressDialogConfig);
		}
		ArrayList<CartEntity> cartEntitys = getCheckedCartEntitys();
		if (index == cartEntitys.size()) {
			
			((MainActivity) getActivity()).closeProcessDialog(mProgressDialogConfig);
			
			Intent intent = new Intent(getActivity(), settleAccountsActivity.class);
			startActivity(intent);
		} else {
			GoodsEntity goods = cartEntitys.get(index).getGoodsEntity();
			GoodsEntity.checkGoods(goods, false, new MyRequestCallback(){
				@Override
				public void onSuccess(MyResult result){
					super.onSuccess(result);
                    index++;
                    checkGoods();
				}

				@Override
				public void onFailure(MyResult result) {
					super.onFailure(result);
					((MainActivity) getActivity()).closeProcessDialog(mProgressDialogConfig);
					
					AlertDialogConfig config = new AlertDialogConfig(getActivity(), 999);
					config.title = R.string.prompt;
					config.message = result.mFailReason;
					((MainActivity) getActivity()).showAlertDialog(config);
				}
			});
		}
	}

	// 获取购物车商品数量
	public static int getShoppingCartSize() {
		return mCarts.size();
	}
}
