package com.gosoon.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.gosoon.R;
import com.gosoon.entity.CartEntity;
import com.gosoon.entity.CollectGoodsEntity;
import com.gosoon.entity.GoodsEntity;
import com.gosoon.fragment.shoppingCartFragment;
import com.gosoon.util.Utils;

public class MyCollectionAdapter extends BaseListAdapter<CollectGoodsEntity> {

	public MyCollectionAdapter(Activity activity,
			List<? extends CollectGoodsEntity> list) {
		super(activity, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.item_collection, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final CollectGoodsEntity collectGoods = getItem(position);
		GoodsEntity goods = collectGoods.getGoods();

		if (goods != null) {
			Utils.getDefaultBitmapUtils().display(viewHolder.mImage,
					goods.getUrlAsString(GoodsEntity.GOODS_IMG, ""), Utils.getConfig(activity, R.drawable.lost_goods_list));
			viewHolder.mName.setText(goods.getValueAsString(GoodsEntity.GOODS_NAME,
					""));
			viewHolder.mShopPrice.setText(goods.getValueAsString(
					GoodsEntity.SHOP_PRICE, ""));
			viewHolder.mPriceMarket.setText("￥ "
					+ goods.getValueAsString(GoodsEntity.MARKET_PRICE, ""));
			viewHolder.mSize.setText(goods.getWeight());
		}

		viewHolder.mCheckBox.setOnCheckedChangeListener(null);
		viewHolder.mCheckBox.setChecked(mSelectedCollections.contains(collectGoods));
		viewHolder.mCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							mSelectedCollections.add(collectGoods);
						} else {
							mSelectedCollections.remove(collectGoods);
						}
					}
				});
		return convertView;
	}

	ArrayList<CollectGoodsEntity> mSelectedCollections = new ArrayList<CollectGoodsEntity>();
	public ArrayList<CollectGoodsEntity> getSelectedItems() {
		return mSelectedCollections;
	}

	protected class ViewHolder {

		public ImageView mImage;
		public TextView mName;
		public TextView mSize;
		public TextView mPromote;
		public TextView mPriceMarket;
		public TextView mShopPrice;
		public CheckBox mCheckBox;

		public ViewHolder(View view) {
			mCheckBox = (CheckBox) view
					.findViewById(R.id.cb_collection_check);
			mImage = (ImageView) view.findViewById(R.id.iv_products_item);
			mName = (TextView) view.findViewById(R.id.tv_products_name_item);
			mSize = (TextView) view.findViewById(R.id.tv_products_size);
			mPromote = (TextView) view.findViewById(R.id.tv_products_promote);
			mPriceMarket = (TextView) view
					.findViewById(R.id.tv_products_price_market);
			mShopPrice = (TextView) view
					.findViewById(R.id.tv_products_price_promote);

			mPriceMarket.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		}
	}
}
