package com.gosoon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.pay.Result;
import com.alipay.sdk.pay.SignUtils;
import com.app.tool.logger.Logger;
import com.gosoon.account.MyAccount;
import com.gosoon.adapter.OrderGoodsAdapter;
import com.gosoon.entity.BonusEntity;
import com.gosoon.entity.CartEntity;
import com.gosoon.entity.GoodsEntity;
import com.gosoon.entity.OrderGoodsEntity;
import com.gosoon.entity.OrderInfoEntity;
import com.gosoon.entity.PaymentEntity;
import com.gosoon.entity.ShippingEntity;
import com.gosoon.entity.UserAddressEntity;
import com.gosoon.entity.UserEntity;
import com.gosoon.fragment.shoppingCartFragment;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.StringUtil;
import com.gosoon.util.ToastUtil;
import com.gosoon.util.programSetting;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.ta.utdid2.android.utils.StringUtils;

public class settleAccountsActivity extends BaseActivity implements
		OnClickListener {

	public static final String PARAM_ORDER_INFO = "param_order_info";

	static final int REQUEST_CODE_FOR_USER_ADDRESS = 1;

	UserAddressEntity mUserAddress = null;
	PaymentEntity mPaymentEntity = null;
	ShippingEntity mShippingEntity = null;
	OrderInfoEntity mOrderInfo = null;
	ArrayList<OrderGoodsEntity> mOrderGoods = null;
	ArrayList<OrderGoodsEntity> mSuccessOrderGoods = new ArrayList<OrderGoodsEntity>();

	TextView tv_user_address;
	TextView tv_shipping;
	TextView tv_payment;
	TextView tv_pay_by_balance;
	TextView tv_order_total_amount, shopping_fee;
	View btn_pay, btn_cancel;
	LinearLayout ll_order_goods;
	TextView tv_address_label, tv_deliver_label, tv_pay_label,
			tv_pay_by_balance_label, tv_select_bonus, has_pay, should_pay,
			tv_bonus, tv_select_integral, view_integral;
	View iv_arrow1, iv_arrow2, iv_arrow3, iv_arrow4, iv_arrow5, iv_arrow6,
			ly_has_pay, ly_should_pay;

	private int containsFavourable = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_settle_account);

		// 获取Intent中的信息(如果存在)，否则从购物车获取信息
		initDataFromIntent();
		// 初始化控件
		initViews();
		btn_pay.setOnClickListener(this);
		// 更新视图
		updateView();
		
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.ly_edit_address:
			selectAddress();
			break;
		case R.id.ly_select_deliver_type:
			selectShipping();
			break;
		case R.id.ly_select_pay_type:
			selectPayment();
			break;
		case R.id.ly_pay_by_balance:
			creatSurplusDialog();
			break;
		case R.id.ly_select_bonus:
			selectBonusDialog();
			break;
		case R.id.ly_select_integral:
			selectIntegralDialog();
			break;
		case R.id.btn_pay:

			if (mOrderInfo.isTempOrder()) {
				createOrder();
			} else if (mOrderInfo.isPayByAlipay()) {
				payByAlipay();

			}
			break;
		case R.id.btn_cancel:
			// 取消订单
			orderStatusCode = 2;
			showConfirmAlert();
			break;
		default:
			break;
		}
	}

	private void initViews() {
		tv_user_address = (TextView) findViewById(R.id.tv_user_address);
		tv_shipping = (TextView) findViewById(R.id.tv_shipping);
		tv_payment = (TextView) findViewById(R.id.tv_payment);
		tv_pay_by_balance = (TextView) findViewById(R.id.tv_pay_by_balance);
		tv_select_bonus = (TextView) findViewById(R.id.tv_select_bonus);
		tv_bonus = (TextView) findViewById(R.id.tv_bonus);
		tv_select_integral = (TextView) findViewById(R.id.tv_select_integral);
		shopping_fee = (TextView) findViewById(R.id.shopping_fee);
		tv_order_total_amount = (TextView) findViewById(R.id.tv_order_total_amount);
		view_integral = (TextView) findViewById(R.id.integral);
		btn_pay = findViewById(R.id.btn_pay);
		btn_cancel = findViewById(R.id.btn_cancel);
		ll_order_goods = (LinearLayout) findViewById(R.id.ll_order_goods);
		// cb_shoppingcart_check_all = (CheckBox)
		// findViewById(R.id.cb_shoppingcart_check_all);
		ly_has_pay = findViewById(R.id.ly_has_pay);
		ly_should_pay = findViewById(R.id.ly_should_pay);
		has_pay = (TextView) findViewById(R.id.has_pay);
		should_pay = (TextView) findViewById(R.id.should_pay);

		tv_address_label = (TextView) findViewById(R.id.tv_address_label);
		tv_deliver_label = (TextView) findViewById(R.id.tv_deliver_label);
		tv_pay_label = (TextView) findViewById(R.id.tv_pay_label);
		tv_pay_by_balance_label = (TextView) findViewById(R.id.tv_pay_by_balance_label);
		iv_arrow1 = findViewById(R.id.iv_arrow1);
		iv_arrow2 = findViewById(R.id.iv_arrow2);
		iv_arrow3 = findViewById(R.id.iv_arrow3);
		iv_arrow4 = findViewById(R.id.iv_arrow4);
		iv_arrow5 = findViewById(R.id.iv_arrow5);
		iv_arrow6 = findViewById(R.id.iv_arrow6);
	}

	protected void updateView() {
		if (mOrderInfo.isTempOrder()) {
			findViewById(R.id.ly_edit_address).setOnClickListener(this);
			findViewById(R.id.ly_select_deliver_type).setOnClickListener(this);
			findViewById(R.id.ly_select_pay_type).setOnClickListener(this);
			findViewById(R.id.ly_pay_by_balance).setOnClickListener(this);
			findViewById(R.id.ly_select_bonus).setOnClickListener(this);
			findViewById(R.id.ly_select_integral).setOnClickListener(this);

			setTitle(getResources().getString(R.string.confirm_order));
		} else {
			findViewById(R.id.ly_edit_address).setOnClickListener(null);
			findViewById(R.id.ly_select_deliver_type).setOnClickListener(null);
			findViewById(R.id.ly_select_pay_type).setOnClickListener(null);
			findViewById(R.id.ly_pay_by_balance).setOnClickListener(null);
			tv_address_label.setText("送货地址：");
			tv_deliver_label.setText("配送方式：");
			tv_pay_label.setText("支付方式：");
			tv_pay_by_balance_label.setText("余额支付：");
			tv_select_bonus.setText("优惠券：");

			iv_arrow1.setVisibility(View.GONE);
			iv_arrow2.setVisibility(View.GONE);
			iv_arrow3.setVisibility(View.GONE);
			iv_arrow4.setVisibility(View.GONE);
			iv_arrow5.setVisibility(View.GONE);
			iv_arrow6.setVisibility(View.GONE);

			if (mOrderInfo.getValueAsDouble(OrderInfoEntity.SURPLUS, 0) == 0) {
				findViewById(R.id.ly_pay_by_balance).setVisibility(View.GONE);
			} else {
				updateSurplus();
			}

			findViewById(R.id.ly_select_bonus).setVisibility(View.GONE);
			findViewById(R.id.ly_select_integral).setVisibility(View.GONE);

			// 显示使用的优惠券的金额
			View bonusView = findViewById(R.id.layout_bonus_label);
			double bonus_money = mOrderInfo.getValueAsDouble("type_money", 0);
			if (bonus_money != 0) {
				bonusView.setVisibility(View.VISIBLE);
				updateBonusMoney();

			}

			int integral = mOrderInfo
					.getValueAsInt(OrderInfoEntity.INTEGRAL, 0);
			if (integral > 0) {
				findViewById(R.id.layout_integral).setVisibility(View.VISIBLE);
				view_integral.setText(integral + "");
			}

			// cb_shoppingcart_check_all.setVisibility(View.GONE);
			btn_pay.setVisibility(View.GONE);

			String payName = mOrderInfo.getValueAsString(
					OrderInfoEntity.PAY_NAME, "");
			int payStatus = mOrderInfo.getValueAsInt(
					OrderInfoEntity.PAY_STATUS, -1);
			int orderStatus = mOrderInfo.getValueAsInt(
					OrderInfoEntity.ORDER_STATUS, -1);
			Double surplus = mOrderInfo.getValueAsDouble(
					OrderInfoEntity.SURPLUS, -1d);

			// 可以付款: !货到付款 && 未付款 && (未确认 || 已确认)
			if (!"货到付款".equals(payName) && payStatus == 0
					&& (orderStatus == 0 || orderStatus == 1)) {
				btn_pay.setVisibility(View.VISIBLE);
			} else {
				btn_pay.setVisibility(View.GONE);
			}

			// 可以取消: (未确认 && 未付款) || 未使用余额支付的
			if (orderStatus == 0 && payStatus == 0 && surplus == 0) {
				btn_cancel.setVisibility(View.VISIBLE);
				btn_cancel.setOnClickListener(this);
			}

			// 已付，应付
			ly_has_pay.setVisibility(View.VISIBLE);
			ly_should_pay.setVisibility(View.VISIBLE);
			should_pay.setText(mOrderInfo.getValueAsString(
					OrderInfoEntity.ORDER_AMOUNT, ""));
			has_pay.setText(mOrderInfo.getValueAsString(
					OrderInfoEntity.SURPLUS, "0.0"));
			setTitle("订单详情");
		}
		updateAddress();
		updateShipping();
		updatePayment();
		updateOrderAmount();
		// 显示商品总价
		tv_order_total_amount.setText(mOrderInfo.getValueAsString(
				OrderInfoEntity.GOODS_AMOUNT, ""));
		// 显示运费
		shopping_fee.setText(mOrderInfo.getValueAsString(
				OrderInfoEntity.SHIPPING_FEE, ""));

		ll_order_goods.removeAllViews();
		if (mOrderGoods != null) {
			OrderGoodsAdapter goodsAdapter = new OrderGoodsAdapter(this,
					mOrderGoods);
			for (int i = 0; i < goodsAdapter.getCount(); i++) {
				ll_order_goods.addView(goodsAdapter.getView(i, null, null));
			}
		}
	}

	/**
	 * 刷新应付金额
	 */
	private void updateOrderAmount() {
		double order_amount = mOrderInfo.getValueAsDouble(
				OrderInfoEntity.ORDER_AMOUNT, 0);
		DecimalFormat format = new DecimalFormat("0.00");
		should_pay.setText(format.format(order_amount) + "");
	}

	/**
	 * 订单显示优惠券金额
	 */

	private void updateBonusMoney() {

		tv_bonus.setText(mOrderInfo.getValueAsString(
				OrderInfoEntity.TYPE_MONEY, ""));

	}

	/**
	 * 刷新余额
	 */
	protected void updateSurplus() {
		double surplus = mOrderInfo
				.getValueAsDouble(OrderInfoEntity.SURPLUS, 0);
		if (surplus > -0.01 && surplus <= 0.01) {
			tv_pay_by_balance.setText("请输入");
			return;
		}
		tv_pay_by_balance.setText(mOrderInfo.getValueAsString(
				OrderInfoEntity.SURPLUS, "0.0"));
	}

	protected void updateAddress() {
		tv_user_address.setText(mOrderInfo.getValueAsString(
				OrderInfoEntity.ADDRESS,
				getResources().getString(R.string.label_pls_input)));
	}

	protected void updateShipping() {
		tv_shipping.setText(mOrderInfo.getValueAsString(
				OrderInfoEntity.SHIPPING_NAME,
				getResources().getString(R.string.label_pls_input)));
		String price = null;
		try {
			if (!tv_shipping.getText().toString().equals("请输入")) {
				price = mShippingEntity.getValueAsString(
						ShippingEntity.SHIPPING_PRICE, "0.0");
			} else {
				price = "0.0";
			}
		} catch (Exception e) {
			price = "0.0";
		}
		System.out.println("A  "+price);
		// 新创建的订单
		if (mOrderInfo.isTempOrder()) {
			/**
			 * 计算运费
			 */
			double _shoppingfee = getShoppingFee(price);
			shopping_fee.setText(_shoppingfee + "");

			/**
			 * 处理余额
			 */
			calOrderamountAndUpdateSurplus();
		}
		// 查看订单详情
		else {
			shopping_fee.setText(mOrderInfo.getValueAsDouble(
					OrderInfoEntity.SHIPPING_FEE, 0) + "");
		}
	}

	/**
	 * 计算结算价格并刷新余额
	 */
	private void calOrderamountAndUpdateSurplus() {
		double goodsamount = mOrderInfo.getValueAsDouble(OrderInfoEntity.GOODS_AMOUNT, 0);
		double shoppingfee = mOrderInfo.getValueAsDouble(OrderInfoEntity.SHIPPING_FEE, 0);
		calOrderAmount(goodsamount, shoppingfee);
		double surplus = mOrderInfo.getValueAsDouble(OrderInfoEntity.SURPLUS, 0);
		double orderAmount = mOrderInfo.getValueAsDouble(OrderInfoEntity.ORDER_AMOUNT, 0);
		if (surplus > orderAmount) {
			mOrderInfo.setValueAsString(OrderInfoEntity.SURPLUS, orderAmount
					+ "");
			updateSurplus();
		}
	}

	/**
	 * 计算运费
	 * @param price 
	 * 
	 * @return
	 */
	private double getShoppingFee(String price) {
		double shoppingfee = Double.parseDouble(price);
		double goodsprice = mOrderInfo.getValueAsDouble(OrderInfoEntity.GOODS_AMOUNT, 0);
		if("客户自提".equals(mOrderInfo.getValueAsString(OrderInfoEntity.SHIPPING_NAME, ""))) {
			mOrderInfo.setValueAsString(OrderInfoEntity.SHIPPING_FEE, shoppingfee
					+ "");
			return 0.00;
		}
		if(shoppingfee == 0) {
			shoppingfee = 6.00;
		} else {
			if (mOrderInfo.getValueAsDouble(OrderInfoEntity.GOODS_AMOUNT, 0) >= shoppingfee) {
				shoppingfee = 0.00;
			} else {
				shoppingfee = 6.00;
			}
		}
		mOrderInfo.setValueAsString(OrderInfoEntity.SHIPPING_FEE, shoppingfee
				+ "");
		return shoppingfee;
	}

	/**
	 * 计算结算价格根据物品价格和运费
	 * 
	 * @param goods_fee
	 * @param shoppingfee
	 */
	private void calOrderAmount(double goods_amount, double shoppingfee) {

		double order_amount = goods_amount + shoppingfee;
		mOrderInfo.setValueAsString(OrderInfoEntity.ORDER_AMOUNT, order_amount
				+ "");

	}

	protected void updatePayment() {
		tv_payment.setText(mOrderInfo.getValueAsString(
				OrderInfoEntity.PAY_NAME,
				getResources().getString(R.string.label_pls_input)));
	}

	protected void initDataFromIntent() {
		Intent intent = getIntent();
		/**
		 * 从我的订单调转而来
		 */
		if (intent.hasExtra(PARAM_ORDER_INFO)) {
			mOrderInfo = new OrderInfoEntity();
			mOrderInfo.fromString(intent.getStringExtra(PARAM_ORDER_INFO));
			mOrderGoods = mOrderInfo.getOrderGoods();

		} else {

			/**
			 * 获取商品信息，从购物车页面跳转而来
			 */
			mOrderGoods = new ArrayList<OrderGoodsEntity>();
			ArrayList<CartEntity> carts = shoppingCartFragment
					.getCheckedCartEntitys();
			double goodsAmount = 0;
			double shippingFee = 0;

			/**
			 * 把购物车的实体信息转换成订单商品实体的信息
			 */
			for (CartEntity cart : carts) {
				OrderGoodsEntity orderGoodsEntity = new OrderGoodsEntity();
				if (!orderGoodsEntity.setCart(cart)) {
					// TODO alert user
					continue;
				}
				mOrderGoods.add(orderGoodsEntity);
				double price = Double.parseDouble(cart.getGoodsEntity()
						.getValueAsString(GoodsEntity.SHOP_PRICE, "0.00"));
				goodsAmount += (cart.getValueAsInt(CartEntity.CART_AMOUNT, 1) * price);
				/* 判断是否包含优惠商品 */
				containsFavourable += Integer.parseInt(cart.getGoodsEntity()
						.getValueAsString(GoodsEntity.IS_FAVOURABLE, 0 + ""));
			}

			shippingFee = 0;

			mOrderInfo = new OrderInfoEntity();
			mOrderInfo.initOrderInfo();
			DecimalFormat format = new DecimalFormat("0.00");
			mOrderInfo.setValueAsString(OrderInfoEntity.GOODS_AMOUNT,
					format.format(goodsAmount));
			mOrderInfo.setValueAsString(OrderInfoEntity.SHIPPING_FEE,
					shippingFee + "");
			mOrderInfo.setValueAsString(OrderInfoEntity.ORDER_AMOUNT,
					(goodsAmount + shippingFee) + "");
			mOrderInfo.setValueAsString(OrderInfoEntity.MONEY_PAID, "0");
			mOrderInfo.setValueAsString(OrderInfoEntity.DISCOUNT, "0");

		}

	}

	/**
	 * 弹出选择积分对话框
	 */
	private void selectIntegralDialog() {
		if(isFavourable("积分"))
			return ;
		else{
			MyRequest myRequest = new MyRequest(MyRequest.ACTION_INTEGRAL,
					MyAccount.mUserName, 0);
			myRequest.setProcessDialogConfig(new ProgressDialogConfig(this,
					MainActivity.ALERTDIALOG_ID_LOGIN));
			myRequest.setAlertDialogConfig(new AlertDialogConfig(this,
					MainActivity.ALERTDIALOG_ID_LOGIN));
			myRequest.send(new MyRequestCallback() {
				@Override
				public void onSuccess(MyResult result) {
					super.onSuccess(result);
					try {
						JSONObject object = new JSONObject(
								result.mResponseInfo.result);
						final int integral = Integer.parseInt(object
								.getString("integral"));
						Builder dialog = new AlertDialog.Builder(
								settleAccountsActivity.this);
						dialog.setMessage("当前积分：" + integral);
						final EditText editText = new EditText(
								settleAccountsActivity.this);
						editText.setHint("请输入要使用的积分");
						editText.setInputType(InputType.TYPE_CLASS_NUMBER
								| InputType.TYPE_NUMBER_FLAG_DECIMAL);
						editText.setMaxEms(10);
						dialog.setView(editText);
						dialog.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int arg1) {
										dialog.dismiss();
									}
								});
						dialog.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										String input = editText.getText()
												.toString();
										if (StringUtil.isEmpty(input)) {
											mOrderInfo.setValueAsString(
													OrderInfoEntity.INTEGRAL, "0");
										} else {
											int integral_input = (int) Double
													.parseDouble(input);
											if (integral_input < 0
													|| integral_input > integral) {
												ToastUtil
														.show(settleAccountsActivity.this,
																"积分输入错误");
												return;
											}
											mOrderInfo.setValueAsString(
													OrderInfoEntity.INTEGRAL,
													integral_input + "");
											updateIntegral();
											calOrderAMount();
										}
										dialog.dismiss();
									}
								});
						dialog.create().show();

					} catch (JSONException e) {
						ToastUtil.show(settleAccountsActivity.this, "获取积分失败，请再试一次");
					}
				}

				@Override
				public void onFailure(MyResult result) {
					super.onFailure(result);
					ToastUtil.show(settleAccountsActivity.this, "获取积分失败，请再试一次");
					Log.e("476", result.mResponseInfo.result);
				}
			});
		}
		

	}

	/**
	 * 更新积分显示
	 */
	private void updateIntegral() {
		int integral = mOrderInfo.getValueAsInt(OrderInfoEntity.INTEGRAL, 0);
		if (integral > -0.01 && integral < 0.01) {
			tv_select_integral.setText("请选择");
			return;
		}
		tv_select_integral.setText(mOrderInfo.getValueAsString(
				OrderInfoEntity.INTEGRAL, ""));

	}

	/**
	 * 对点击选择优惠券的响应
	 */
	private void selectBonusDialog() {
		if(isFavourable("优惠券"))
			return ;
		else{
			BonusEntity.setGoodsMoney(mOrderInfo.getValueAsDouble(
					OrderInfoEntity.GOODS_AMOUNT, 0.0));
			BonusEntity.getBonusFromNet(getBonusFromNetCallback);
		}
		
	}

	MyRequestCallback getBonusFromNetCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			ArrayList<BonusEntity> _BonusEntities = BonusEntity
					.getBonusEntities();
			createSelectBonusDialog(_BonusEntities);
		}

	};

	/**
	 * 弹出可用优惠券对话框
	 * 
	 * @param _BonusEntities
	 */
	private void createSelectBonusDialog(
			final ArrayList<BonusEntity> _BonusEntities) {

		String items[] = new String[_BonusEntities.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = _BonusEntities.get(i).getType_name() + "("
					+ _BonusEntities.get(i).getType_money() + "元)";
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				"优惠券").setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				BonusEntity _BonusEntity = _BonusEntities.get(whichButton);
				mOrderInfo.setValueAsString(OrderInfoEntity.BONUS_ID,
						_BonusEntity.getBonus_id());
				mOrderInfo.setValueAsString(OrderInfoEntity.BONUS,
						_BonusEntity.getType_money());
				updateBonus();
				calOrderAMount();
				dialog.dismiss();
			}

		});
		AlertDialog ab = builder.create();
		ab.show();
		if (items.length >= 5) {
			WindowManager manager = getWindowManager();
			Display d = manager.getDefaultDisplay();
			Window window = ab.getWindow();
			WindowManager.LayoutParams params = window.getAttributes();
			params.height = (int) (d.getHeight() * 0.57);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			ab.getWindow().setAttributes(params);
		}
	};

	/**
	 * 计算优惠券和结算价格
	 */
	private void calBonusAndOrderAMount() {
		double surplus = mOrderInfo.getValueAsDouble(OrderInfoEntity.SURPLUS,
				0.0);
		double bonus = mOrderInfo.getValueAsDouble(OrderInfoEntity.BONUS,
				0.0);
		double orderamount = mOrderInfo.getValueAsDouble(
				OrderInfoEntity.ORDER_AMOUNT, 0.0);
		// 如果余额为0
		if (surplus > -0.000001 && surplus < 0.000001) {
			mOrderInfo.setValueAsString(
					OrderInfoEntity.ORDER_AMOUNT,
					(mOrderInfo.getValueAsDouble(OrderInfoEntity.ORDER_AMOUNT,
							0.0) - bonus) + "");
		} else if (surplus + bonus <= orderamount) {
			mOrderInfo.setValueAsString(
					OrderInfoEntity.ORDER_AMOUNT,
					(mOrderInfo.getValueAsDouble(OrderInfoEntity.ORDER_AMOUNT,
							0.0) - bonus) + "");
		} else if (surplus + bonus > orderamount) {
			mOrderInfo.setValueAsString(
					OrderInfoEntity.ORDER_AMOUNT,
					(mOrderInfo.getValueAsDouble(OrderInfoEntity.ORDER_AMOUNT,
							0.0) - bonus) + "");
			mOrderInfo.setValueAsString(OrderInfoEntity.SURPLUS, mOrderInfo
					.getValueAsString(OrderInfoEntity.ORDER_AMOUNT, "0.0"));
		}
	}

	/**
	 * 更新优惠券参数显示
	 */
	private void updateBonus() {
		tv_select_bonus.setText(mOrderInfo.getValueAsString(
				OrderInfoEntity.BONUS, "0.0"));
	}

	protected void selectAddress() {
		Intent intent = new Intent(this, addressListActivity.class);
		if (mUserAddress != null) {
			intent.putExtra(addressListActivity.PARAM_SELECTED_ADDRESS_ID,
					mUserAddress.getValueAsString(UserAddressEntity.ADDRESS_ID,
							null));
		}
		startActivityForResult(intent, REQUEST_CODE_FOR_USER_ADDRESS);
	}

	protected void selectPayment() {
		PaymentEntity.getPaymentsFromNet(getPaymentFromNetCallback);
	}

	MyRequestCallback getPaymentFromNetCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			ArrayList<PaymentEntity> paymentEntities = PaymentEntity
					.getPayments();
			ArrayList<PaymentEntity> rightPaymentEntities = new ArrayList<PaymentEntity>();
			for (PaymentEntity payment : paymentEntities) {
				if ((payment.getValueAsString(PaymentEntity.IS_COD, "").equals(
						"1") || payment.getValueAsString(
						PaymentEntity.PAY_CODE, "").equals(
						PaymentEntity.PAY_CODE_ALIPAY))
						&& payment.getValueAsString(PaymentEntity.ENABLED, "1")
								.equals("1")) {
					rightPaymentEntities.add(payment);
				}
			}
			creatPaymentDialog(rightPaymentEntities);
		};
	};

	protected void selectShipping() {
		ShippingEntity.getShippingsFromNet(getShippingFromNetCallback);
	}

	MyRequestCallback getShippingFromNetCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			ArrayList<ShippingEntity> shippingEntities = ShippingEntity
					.getShippings();
			creatShippingDialog(shippingEntities);
		};
	};

	private void creatSurplusDialog() {
		if(isFavourable("余额"))
			return ;
		else{
			Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("当前余额："
					+ MyAccount.getUser().getValueAsString(UserEntity.USER_MONEY,
							"0.0") + "元");
			final EditText editText = new EditText(this);
			editText.setHint("请输入要使用的余额");
			editText.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			editText.setMaxEms(10);
			dialog.setView(editText);
			dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			});
			dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					String input = editText.getText().toString();
					if (StringUtil.isEmpty(input)) {
						mOrderInfo.setValueAsString(OrderInfoEntity.SURPLUS, "0.0");
						updateSurplus();
						dialog.dismiss();
						return;
					}
					double surplus = Double.parseDouble(input);
					if (surplus < 0
							|| surplus > MyAccount.getUser().getValueAsDouble(
									UserEntity.USER_MONEY, 0)
							|| surplus > mOrderInfo.getValueAsDouble(
									OrderInfoEntity.GOODS_AMOUNT, 0)
									+ mOrderInfo.getValueAsDouble(
											OrderInfoEntity.SHIPPING_FEE, 0)) {
						ToastUtil.show(getApplicationContext(), "金额输入错误");
						return;
					}
					mOrderInfo.setValueAsString(OrderInfoEntity.SURPLUS, surplus
							+ "");

					updateSurplus();
					/* 刷新应付余额 */
					calOrderAMount();
					dialog.dismiss();
				}
			});
			dialog.create().show();
		}
	}

	private boolean isFavourable(String name) {
		if (containsFavourable > 0) {
			ToastUtil.show(this, "优惠商品不能使用" + name);
			return true;
		}
		return false;
	}

	/**
	 * 计算结算价格
	 * 
	 * @return
	 */
	private void calOrderAMount() {
		double good_mount = mOrderInfo.getValueAsDouble(
				OrderInfoEntity.GOODS_AMOUNT, 0.0);
		double shoppingfee = mOrderInfo.getValueAsDouble(
				OrderInfoEntity.SHIPPING_FEE, 0.0);
		double surplus = mOrderInfo.getValueAsDouble(OrderInfoEntity.SURPLUS,
				0.0);
		double bonus = mOrderInfo.getValueAsDouble(OrderInfoEntity.BONUS,
				0.0);
		int integral = mOrderInfo.getValueAsInt(OrderInfoEntity.INTEGRAL, 0);
		double temp_money = good_mount - bonus;
		if (integral > temp_money * 0.2 * 100) {
			integral = (int) (temp_money * 0.2 * 100);
			Log.e("大于", integral + "");
		}
		double order_money = good_mount + shoppingfee - bonus - integral
				/ 100.0;
		mOrderInfo.setValueAsString(OrderInfoEntity.INTEGRAL, integral + "");
		mOrderInfo.setValueAsString(OrderInfoEntity.ORDER_AMOUNT, order_money
				+ "");
		double orderAmount = mOrderInfo.getValueAsDouble(
				OrderInfoEntity.ORDER_AMOUNT, 0);
		double orderSurplus = mOrderInfo.getValueAsDouble(
				OrderInfoEntity.SURPLUS, 0);
		if (orderAmount >= orderSurplus) {
			orderAmount = orderAmount - orderSurplus;
			mOrderInfo.setValueAsString(OrderInfoEntity.ORDER_AMOUNT,
					orderAmount + "");
		} else if (orderAmount < orderSurplus) {
			orderSurplus = orderAmount;
			orderAmount = 0;
			mOrderInfo.setValueAsString(OrderInfoEntity.ORDER_AMOUNT,
					orderAmount + "");
			mOrderInfo.setValueAsString(OrderInfoEntity.SURPLUS, orderSurplus
					+ "");
		}
		updateOrderAmount();
		updateSurplus();
		updateIntegral();
		// update
	}

	/**
	 * 弹出配送方式选择框
	 * 
	 * @param shippingEntities
	 */
	private void creatShippingDialog(
			final ArrayList<ShippingEntity> shippingEntities) {
		String[] items = new String[shippingEntities.size()];
		for (int i = 0; i < shippingEntities.size(); i++) {
			items[i] = shippingEntities.get(i).getValueAsString(
					ShippingEntity.SHIPPING_NAME, "");
		}
		new AlertDialog.Builder(this).setTitle(R.string.title_deliver_type)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mShippingEntity = shippingEntities.get(whichButton);
						mOrderInfo.setShipping(mShippingEntity);
						updateShipping();
						/* 刷新应付金额 */
						calOrderAMount();
						dialog.dismiss();
					}
				}).create().show();
	}

	/**
	 * 弹出付款方式选择框
	 * 
	 * @param shippingEntities
	 */
	private void creatPaymentDialog(
			final ArrayList<PaymentEntity> paymentEntities) {
		String[] items = new String[paymentEntities.size()];
		for (int i = 0; i < paymentEntities.size(); i++) {
			items[i] = paymentEntities.get(i).getValueAsString(
					PaymentEntity.PAY_NAME, "");
		}

		new AlertDialog.Builder(this).setTitle(R.string.title_payment_type)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mPaymentEntity = paymentEntities.get(whichButton);
						mOrderInfo.setPayment(mPaymentEntity);
						updatePayment();
						dialog.dismiss();
					}
				}).create().show();
	}

	boolean createOrderHasFailed = false;

	protected void createOrder() {
		mProgressDialogConfig = new ProgressDialogConfig(
				settleAccountsActivity.this,
				MainActivity.PROCESSDIALOG_ID_CREATE_ORDER);
		mProgressDialogConfig.cancelable = false;
		showProcessDialog(mProgressDialogConfig);

		btn_pay.setEnabled(false);
		createOrderHasFailed = false;
		mSuccessOrderGoods.clear();

		AlertDialogConfig config = new AlertDialogConfig(this,
				MainActivity.ALERTDIALOG_ID_CREATE_ORDER);
		config.title = R.string.prompt;

		if (mUserAddress == null) {
			finishCreateOrder(false, "错误的送货地址");
			return;
		}
		if (mShippingEntity == null) {
			finishCreateOrder(false, "错误的配送方式");
			return;
		}
		if (mPaymentEntity == null) {
			finishCreateOrder(false, "错误的支付方式");
			return;
		}
		doCreateOrder();
	}

	protected void doCreateOrder() {
		// 计算order_Amount
		calOrderAMount();
		double orderAmount = mOrderInfo.getValueAsDouble(
				OrderInfoEntity.ORDER_AMOUNT, 0);
		if (orderAmount < 0.01 && orderAmount > -0.01) {
			mOrderInfo.setValueAsString(OrderInfoEntity.PAY_STATUS, "2");
			mOrderInfo.setValueAsString(OrderInfoEntity.PAY_TIME,
					System.currentTimeMillis() / 1000l + "");
		}
		MyRequest myrequest = new MyRequest(mOrderInfo.getTable().mTableName,
				mOrderInfo.toString());
		myrequest.send(createOrderInfoCallback);
	}

	protected void getCreatedOrder() {
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		myrequest.setSql("SELECT * from gsw_order_info where user_id="
				+ mOrderInfo.getValueAsString(OrderInfoEntity.USER_ID, "")
				+ " AND order_sn="
				+ mOrderInfo.getValueAsString(OrderInfoEntity.ORDER_SN, ""));
		myrequest.send(getOrderInfoCallback);
	}

	protected void doCreateOrderGoods() {
		for (final OrderGoodsEntity ordergoods : mOrderGoods) {
			ordergoods.setOrderInfo(mOrderInfo);
			
			/*Log.e("958", ordergoods.toString());
			try {
				FileWriter eriFileWriter = new FileWriter(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ceshi.txt"));
				eriFileWriter.write(ordergoods.toString());
				eriFileWriter.flush();
				eriFileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			MyRequest myrequest = new MyRequest(
					ordergoods.getTable().mTableName, ordergoods.toString());
			myrequest.send(new MyRequestCallback(){
				@Override
				public void onSuccess(MyResult result) {
					
					
					super.onSuccess(result);
					mSuccessOrderGoods.add(ordergoods);
					finishCreateOrder(true, "");
					
				}

				@Override
				public void onFailure(MyResult result) {
					super.onFailure(result);
					finishCreateOrder(false, result.mFailReason);
					
				}
			});
		}
	}

	protected void delOrder() {
		if (mOrderInfo != null) {
			MyRequest myrequest = new MyRequest(HttpMethod.POST,
					MyRequest.ACTION_SQL);
			/*
			 * myrequest .setSql("DELETE from gsw_order_info where user_id=" +
			 * mOrderInfo.getValueAsString( OrderInfoEntity.USER_ID, "") +
			 * " AND order_sn=" + mOrderInfo.getValueAsString(
			 * OrderInfoEntity.ORDER_SN, ""));
			 */
			myrequest
					.setSql("UPDATE gsw_order_info SET order_status = 3 where user_id="
							+ mOrderInfo.getValueAsString(
									OrderInfoEntity.USER_ID, "")
							+ " AND order_sn="
							+ mOrderInfo.getValueAsString(
									OrderInfoEntity.ORDER_SN, ""));
			// Log.e("951",
			// mOrderInfo.getValueAsString(OrderInfoEntity.ORDER_SN,
			// "00000")+"--"+mOrderInfo.getValueAsString(OrderInfoEntity.ORDER_ID,
			// "00000"));
			myrequest.send(new MyRequestCallback() {
				@Override
				public void onSuccess(MyResult result) {
					super.onSuccess(result);
//					Log.e("success", result.mResponseInfo.result);
				}

				@Override
				public void onFailure(MyResult result) {
					super.onFailure(result);
//					Log.e("Fail", result.mResponseInfo.result);
				}
			});

			if (mOrderInfo.has(OrderInfoEntity.ORDER_ID)){
				myrequest = new MyRequest(HttpMethod.POST, MyRequest.ACTION_SQL);
				myrequest.setSql("DELETE from gsw_order_goods where order_id="
						+ mOrderInfo.getValueAsString(OrderInfoEntity.ORDER_ID,
								""));
				myrequest.send(null);
			}
		}
	}

	protected void finishCreateOrder(boolean success, String msg) {

		if (success) {
			
			if (mSuccessOrderGoods.size() == mOrderGoods.size()) {
				mOrderInfo.setOrderGoods(mSuccessOrderGoods);
				shoppingCartFragment.delSelectedShoppingCart();
				MyAccount.getUserFromNet(null, true);
				if (mOrderInfo.isPayByAlipay()
						&& mOrderInfo.getValueAsDouble(
								OrderInfoEntity.ORDER_AMOUNT, -1) != 0) {
					payByAlipay();
				} else {
					finishProgressDialog(success, msg);
					// 处理优惠券
					dealWithBonus();
					/* 使用积分及返还积分 */
					dealWithIntegral();
				}
			}
		} else {
			finishProgressDialog(success, msg);
			createOrderHasFailed = true;
			delOrder();
			// mOrderInfo.remove(OrderInfoEntity.ORDER_ID);
		}
	}

	/**
	 * 使用积分
	 */
	private void dealWithIntegral() {
		String order_id = mOrderInfo.getValueAsString(OrderInfoEntity.ORDER_ID,
				"");
		int integral = mOrderInfo.getValueAsInt(OrderInfoEntity.INTEGRAL, 0);
		if(integral>0){
			String keys_1[] = new String[] { MyRequest.PARAM_ORDER_ID,
					MyRequest.PARAM_INTEGRAL };
			String values_1[] = new String[] { order_id, integral + "" };
			MyRequest myRequest = new MyRequest(MyRequest.ACTION_USE_INTEGRAL,
					keys_1, values_1);
			myRequest.send(null);
		}
		
		if(order_id.length()>1){
			String keys_2[] = new String[]{MyRequest.PARAM_ORDER_ID};
			String values_2[] = new String[]{order_id};
			MyRequest myRequest2 = new MyRequest(MyRequest.ACTION_GIVE_INTEGRAL,keys_2,values_2);
			myRequest2.send(new MyRequestCallback(){
				@Override
				public void onSuccess(MyResult result) {
					super.onSuccess(result);
				}
				
				@Override
				public void onFailure(MyResult result) {
					super.onFailure(result);
				}
			});
		}
	}

	protected void finishProgressDialog(boolean success, String msg) {
		if (mProgressDialogConfig != null) {
			closeProcessDialog(mProgressDialogConfig);
		}

		if (success) {
			startActivity(new Intent(this, OrderResultActivity.class));
			finish();
		} else {
			AlertDialogConfig config = new AlertDialogConfig(this,
					MainActivity.ALERTDIALOG_ID_CREATE_ORDER);
			config.title = R.string.prompt;
			config.message = msg;
			showAlertDialog(config);
		}
		btn_pay.setEnabled(true);
	}

	ProgressDialogConfig mProgressDialogConfig = null;
	MyRequestCallback createOrderInfoCallback = new MyRequestCallback() {

		@Override
		public void onFailure(MyResult result) {
			finishCreateOrder(false, result.mFailReason);
		};

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			getCreatedOrder();
			queryUserMoney();
			
			
		};
	};

	MyRequestCallback getOrderInfoCallback = new MyRequestCallback() {

		@Override
		public void onFailure(MyResult result) {
			finishCreateOrder(false, result.mFailReason);
		};

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			
			if (result.getFirstData() != null) {
				mOrderInfo = new OrderInfoEntity(result.getFirstData());
				doCreateOrderGoods();
			} else {
				finishCreateOrder(false, "订单查询失败");
			}
			
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_FOR_USER_ADDRESS) {
				String userAddress = data
						.getStringExtra(addressListActivity.PARAM_SELECTED_ITEM);
				mUserAddress = new UserAddressEntity(userAddress);
				mOrderInfo.setUserAddress(mUserAddress);
				updateAddress();
			}
		}
	}

	protected void payByAlipay() {
		try {
			double amount = Double.parseDouble(mOrderInfo.getValueAsString(
					OrderInfoEntity.ORDER_AMOUNT, ""));
			if (amount < 0.000001) {
				alipaySuccess();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		String orderInfo = getOrderInfo();
		
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(settleAccountsActivity.this);
				// 调用支付接口
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	public String getOrderInfo() {
		// 合作者身份ID
		String orderInfo = "partner=" + "\"" + programSetting.getPartner()
				+ "\"";

		// 卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + programSetting.getSeller() + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\""
				+ mOrderInfo.getValueAsString(OrderInfoEntity.ORDER_SN, "")
				+ "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + getGoodsTitle() + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + getGoodsDetails() + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\""
				+ mOrderInfo.getValueAsString(OrderInfoEntity.ORDER_AMOUNT, "")
				+ "\"";

		// 服务器异步通知页面路径
		// orderInfo += "&notify_url=" + "\"" +
		// "http://notify.msp.hk/notify.htm"
		// + "\"";
		
		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" +"http://www.gosoon60.com/alipay_notify_url.php" + "\"";

		// 接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		// orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	protected String getGoodsTitle() {
		String title = "";
		for (OrderGoodsEntity orderGoods : mOrderGoods) {
			if (!StringUtils.isEmpty(title)) {
				title += "/";
			}
			title += orderGoods.getValueAsString(OrderGoodsEntity.GOODS_NAME,
					"");
		}
		title = "菓速网购物-" + title;
		return title;
	}

	protected String getGoodsDetails() {
		String details = "";
		for (OrderGoodsEntity orderGoods : mOrderGoods) {
			if (!StringUtils.isEmpty(details)) {
				details += "/";
			}
			details += orderGoods.getValueAsString(OrderGoodsEntity.GOODS_NAME,
					"");
			details += orderGoods.getValueAsString(
					OrderGoodsEntity.GOODS_NUMBER, "");
			details += "件";
		}
		return details;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, programSetting.getRSAPrivate());
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

	private static final int SDK_PAY_FLAG = 1;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				Result resultObj = new Result((String) msg.obj);
				String resultStatus = resultObj.resultStatus;

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					
					dealWithBonus();
					dealWithIntegral();
//					alipaySuccess();
					
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”
					// 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）

					if (TextUtils.equals(resultStatus, "8000")) {
						finishProgressDialog(false, "支付宝支付结果确认中");
					} else {
						finishProgressDialog(false, "支付宝支付失败");
					}
					Intent intent = new Intent(settleAccountsActivity.this,
							settleAccountsActivity.class);
					intent.putExtra(PARAM_ORDER_INFO, mOrderInfo.toString());
					startActivity(intent);
					finish();
				}
				break;
			}
			default:
				break;
			}
		}

	};

	protected void alipaySuccess(){
		
		OrderInfoEntity updateOrderInfo = new OrderInfoEntity();
		updateOrderInfo.setValueAsString(OrderInfoEntity.ORDER_ID,
				mOrderInfo.getValueAsString(OrderInfoEntity.ORDER_ID, ""));
		// updateOrderInfo.setValueAsString(OrderInfoEntity.PAY_STATUS, "2");
		updateOrderInfo.setValueAsString(OrderInfoEntity.PAY_TIME,
				System.currentTimeMillis() / 1000l + "");
		updateOrderInfo.setValueAsString(OrderInfoEntity.MONEY_PAID,
				mOrderInfo.getValueAsString(OrderInfoEntity.ORDER_AMOUNT, ""));
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		myrequest
				.setSql(updateOrderInfo.getUpdateSql(OrderInfoEntity.ORDER_ID));
		myrequest.send(updateOrderCallback);
		
		/**
		 * 处理优惠券
		 */
		dealWithBonus();
		dealWithIntegral();
	}

	// 发送已使用优惠券的请求
	private void dealWithBonus() {
		int bonus_id = mOrderInfo.getValueAsInt(OrderInfoEntity.BONUS_ID, 0);
		String order_id = mOrderInfo.getValueAsString(OrderInfoEntity.ORDER_ID,
				"");

		if (bonus_id != 0) {
			MyRequest bonusRequest = new MyRequest(MyRequest.ACTION_USER_BONUS,
					bonus_id, order_id);
			bonusRequest.setProcessDialogConfig(new ProgressDialogConfig(this,
					MainActivity.ALERTDIALOG_ID_LOGIN));
			bonusRequest.setAlertDialogConfig(new AlertDialogConfig(this,
					MainActivity.ALERTDIALOG_ID_LOGIN));
			bonusRequest.send(myBonusCallBack);
		}
	}

	MyRequestCallback myBonusCallBack = new MyRequestCallback() {

		public void onSuccess(MyResult result) {
			Log.e("优惠券使用成功", result.mResponseInfo.result.toString());
		};

		public void onFailure(MyResult result) {
			Log.e("优惠券使用失败", result.mResponseInfo.result.toString());
		};
	};

	/**
	 * 更新订单状态
	 * 
	 * @param statusCode
	 */
	int orderStatusCode = -1;

	protected void updateOrderStatus() {
		if (orderStatusCode == 2) {

			String orderid = mOrderInfo.getValueAsString(
					OrderInfoEntity.ORDER_ID, "0");
			dealBonusWhenCancelOrder(orderid);
		} else {
			OrderInfoEntity updateOrderInfo = new OrderInfoEntity();
			updateOrderInfo.setValueAsString(OrderInfoEntity.ORDER_ID,
					mOrderInfo.getValueAsString(OrderInfoEntity.ORDER_ID, ""));
			updateOrderInfo.setValueAsString(OrderInfoEntity.ORDER_STATUS, ""
					+ orderStatusCode);
			MyRequest myrequest = new MyRequest(HttpMethod.POST,
					MyRequest.ACTION_SQL);
			myrequest.setSql(updateOrderInfo
					.getUpdateSql(OrderInfoEntity.ORDER_ID));

			myrequest.setProcessDialogConfig(new ProgressDialogConfig(this,
					MainActivity.PROCESSDIALOG_ID_GETGOODSLIST));
			myrequest.setAlertDialogConfig(new AlertDialogConfig(this,
					MainActivity.ALERTDIALOG_ID_GETGOODSLIST));
			myrequest.send(updateOrderStatusCallback);
		}
	}

	MyRequestCallback updateOrderStatusCallback = new MyRequestCallback() {

		/*
		 * @Override public void onSuccess(MyResult result) { switch
		 * (orderStatusCode) { case 2:
		 * ToastUtil.show(settleAccountsActivity.this, "订单已取消");
		 * btn_cancel.setVisibility(View.GONE);
		 * btn_pay.setVisibility(View.GONE);
		 * mOrderInfo.setValueAsString(OrderInfoEntity.ORDER_STATUS, "2");
		 * //如果使用优惠券则返还给用户 int bonusid =
		 * mOrderInfo.getValueAsInt(OrderInfoEntity.BONUS_ID, 0); String orderid
		 * = mOrderInfo.getValueAsString(OrderInfoEntity.ORDER_ID, "");
		 * if(bonusid!=0){ dealBonusWhenCancelOrder(orderid); } break;
		 * 
		 * default: break; } };
		 */
		@Override
		public void onFailure(MyResult result) {
			finishProgressDialog(false, "更新订单失败，请联系网站人员更改订单状态");
		};
	};

	MyRequestCallback updateOrderCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			finishProgressDialog(true, "支付宝支付成功");
		};

		@Override
		public void onFailure(MyResult result) {
			finishProgressDialog(false, "更新订单失败，请联系网站人员更改订单状态");
		};
	};

	/**
	 * 取消订单之后返还优惠券
	 * 
	 * @since 2015-05-19
	 */
	private void dealBonusWhenCancelOrder(String orderid) {

		Log.e("1183ff", orderid + "");
		MyRequest myrequest = new MyRequest(orderid);
		myrequest.send(backToBonusCallback);

	}

	MyRequestCallback backToBonusCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			ToastUtil.show(settleAccountsActivity.this, "订单已取消");
			btn_cancel.setVisibility(View.GONE);
			btn_pay.setVisibility(View.GONE);
			mOrderInfo.setValueAsString(OrderInfoEntity.ORDER_STATUS, "2");
		};

		@Override
		public void onFailure(MyResult result) {

			Log.e("settleAccount->1195", "取消订单失败");
		};

	};

	private void showConfirmAlert() {
		AlertDialogConfig alertDialogConfig = new AlertDialogConfig(this,
				MainActivity.ALERTDIALOG_ID_CONFIRM_CANCEL_ORDER);
		switch (orderStatusCode) {
		case 2:
			alertDialogConfig.message = "确定要取消订单吗？";
			break;

		default:
			break;
		}
		alertDialogConfig.title = R.string.prompt;
		alertDialogConfig.showNegative = true;
		alertDialogConfig.onPositiveListener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				switch (orderStatusCode) {
				case 2:
					updateOrderStatus();
					break;

				default:
					break;
				}
			}
		};
		alertDialogConfig.show();
	}

	private void queryUserMoney() {
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		myrequest
				.setSql("select user_money, user_id from gsw_users where user_id="
						+ MyAccount.getUser().getValueAsString(
								UserEntity.USER_ID, ""));
		myrequest.send(queryUserMoneyCallback);
	}

	MyRequestCallback queryUserMoneyCallback = new MyRequestCallback() {
		@Override
		public void onSuccess(MyResult result) {
			if (result.getFirstData() != null) {
				try {
					double userMoney = result.getFirstData().getDouble(
							"user_money");
					updateUserMoney(userMoney
							- mOrderInfo.getValueAsDouble(
									OrderInfoEntity.SURPLUS, 0) + "");
				} catch (JSONException e) {
					queryUserMoney();
				}
			} else {
				queryUserMoney();
			}
			
			Log.e("查询用户1152成功",result.mResponseInfo.result);
		};
		
		

		@Override
		public void onFailure(MyResult result) {
			Log.e("查询用户1152失败",result.mResponseInfo.result);
			queryUserMoney();
		};

		public void onCancelled(MyResult result) {
			queryUserMoney();
		};
	};
	String totalMoney = "";

	private void updateUserMoney(String totalMoney) {
		this.totalMoney = totalMoney;
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		String sql = "UPDATE gsw_users SET user_money='" + totalMoney
				+ "' WHERE user_id="
				+ MyAccount.getUser().getValueAsString(UserEntity.USER_ID, "");
		myrequest.setSql(sql);
		myrequest.send(updateUserMoneyCallback);
	}

	MyRequestCallback updateUserMoneyCallback = new MyRequestCallback() {
		@Override
		public void onFailure(MyResult result) {
			updateUserMoney(totalMoney);
		};
	};
}
