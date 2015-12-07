package com.gosoon.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gosoon.R;
import com.gosoon.entity.GoodsEntity;
import com.gosoon.entity.OrderGoodsEntity;
import com.gosoon.util.Utils;
import com.lidroid.xutils.db.sqlite.CursorUtils.FindCacheSequence;

public class OrderGoodsAdapter extends BaseListAdapter<OrderGoodsEntity> {
	public OrderGoodsAdapter(Activity activity, List<? extends OrderGoodsEntity> list) {
		super(activity, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.item_order_goods, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		GoodsEntity goods = getItem(position).getGoods();

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
			viewHolder.tv_buy_num.setText(getItem(position).getValueAsString(OrderGoodsEntity.GOODS_NUMBER, ""));
		}
		return convertView;
	}

	protected class ViewHolder {

		public ImageView mImage;
		public TextView mName;
		public TextView mSize;
		public TextView mPromote;
		public TextView mPriceMarket;
		public TextView mShopPrice;
		public TextView tv_buy_num;
		
		public View ly_buy_num;
		public ViewHolder(View view) {
			mImage = (ImageView) view.findViewById(R.id.iv_products_item);
			mName = (TextView) view.findViewById(R.id.tv_products_name_item);
			mSize = (TextView) view.findViewById(R.id.tv_products_size);
			mPromote = (TextView) view.findViewById(R.id.tv_products_promote);
			tv_buy_num = (TextView) view.findViewById(R.id.tv_buy_num);
			mPriceMarket = (TextView) view
					.findViewById(R.id.tv_products_price_market);
			mShopPrice = (TextView) view
					.findViewById(R.id.tv_products_price_promote);

			mPriceMarket.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			
			ly_buy_num = view.findViewById(R.id.ly_buy_num);
			
		}
	}
}
