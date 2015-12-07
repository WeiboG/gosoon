package com.gosoon.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gosoon.MyApplication;
import com.gosoon.R;
import com.gosoon.entity.CartEntity;
import com.gosoon.entity.GoodsEntity;
import com.gosoon.fragment.baseFragment;
import com.gosoon.fragment.shoppingCartFragment;
import com.gosoon.util.ToastUtil;
import com.gosoon.util.Utils;
import com.gosoon.view.NumberSelectButton;
import com.gosoon.view.NumberSelectButton.OnNumberChangedListner;

public class ShoppingcartAdapter extends BaseListAdapter<CartEntity> {

	public ShoppingcartAdapter(baseFragment fragment,
			List<? extends CartEntity> list) {
		super(fragment, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.item_shoppingcart, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final CartEntity cart = getItem(position);
		final GoodsEntity goods = cart.getGoodsEntity();
		Utils.getDefaultBitmapUtils().display(viewHolder.mImageView,
				goods.getUrlAsString(GoodsEntity.GOODS_THUMB, ""), Utils.getConfig(activity, R.drawable.lost_shoppingcart));
		viewHolder.mName.setText(goods.getValueAsString(GoodsEntity.GOODS_NAME,
				""));
		viewHolder.mPrice.setText(goods.getValueAsString(GoodsEntity.SHOP_PRICE,
				""));
		viewHolder.mSize.setText(goods.getWeight());
		viewHolder.mNumberSelectButton.setText(cart.getValueAsString(
				CartEntity.CART_AMOUNT, "1"));
		viewHolder.mNumberSelectButton
				.setOnNumberChangedListener(new OnNumberChangedListner() {

					@Override
					public boolean OnNumberChanged(int num) {
						if (num > cart.getValueAsInt(CartEntity.CART_STOCK, 999)) {
					    	ToastUtil.show(MyApplication.getContext(), "超过商品可购买数量");
							return false;
						}
						shoppingCartFragment.modifyShoppingCart(goods,
								CartEntity.CART_AMOUNT, num + "");
						return true;
					}
				});

		viewHolder.mCheckBox.setOnCheckedChangeListener(null);
		viewHolder.mCheckBox.setChecked(cart.getValueAsBoolean(
				CartEntity.CART_CHECK, false));
		viewHolder.mCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						shoppingCartFragment.modifyShoppingCart(goods,
								CartEntity.CART_CHECK, isChecked + "");
						if(onGoodsChecked != null){
							onGoodsChecked.onChecked();
						}
					}
				});

		return convertView;
	}

	protected class ViewHolder {

		CheckBox mCheckBox;
		ImageView mImageView;
		TextView mName, mPrice, mSize;
		NumberSelectButton mNumberSelectButton;

		public ViewHolder(View view) {
			mCheckBox = (CheckBox) view
					.findViewById(R.id.cb_shoppingcart_check);
			mImageView = (ImageView) view.findViewById(R.id.iv_products_item);
			mName = (TextView) view.findViewById(R.id.tv_products_name_item);
			mNumberSelectButton = (NumberSelectButton) view
					.findViewById(R.id.nsb_products_amount);
			mPrice = (TextView) view
					.findViewById(R.id.tv_products_price_promote);
			mSize =  (TextView) view
					.findViewById(R.id.tv_products_size);
		}
	}
	private OnGoodsChecked onGoodsChecked;
	public interface OnGoodsChecked{
		public void onChecked();
	}
	public void setOnGoodsChecked(OnGoodsChecked onGoodsChecked){
		this.onGoodsChecked = onGoodsChecked;
	}
}
