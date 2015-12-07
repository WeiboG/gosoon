package com.gosoon.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gosoon.R;
import com.gosoon.entity.GoodsEntity;
import com.gosoon.util.Utils;

public class GoodsAdapter extends BaseListAdapter<GoodsEntity> {

	public GoodsAdapter(Activity activity, List<? extends GoodsEntity> list) {
		super(activity, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.item_products, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		GoodsEntity goods = getItem(position);

		Utils.getDefaultBitmapUtils().display(viewHolder.mImage,
				goods.getUrlAsString(GoodsEntity.GOODS_IMG, ""), Utils.getConfig(activity, R.drawable.lost_goods_list));
		viewHolder.mName.setText(goods.getValueAsString(GoodsEntity.GOODS_NAME,
				""));
		viewHolder.mShopPrice.setText(goods.getValueAsString(
				GoodsEntity.SHOP_PRICE, ""));
		viewHolder.mPriceMarket.setText("￥ "
				+ goods.getValueAsString(GoodsEntity.MARKET_PRICE, ""));
		viewHolder.mSize.setText(goods.getWeight());

		return convertView;
	}

	protected class ViewHolder {

		public ImageView mImage;
		public TextView mName;
		public TextView mSize;
		public TextView mPromote;
		public TextView mPriceMarket;
		public TextView mShopPrice;

		public ViewHolder(View view) {
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
