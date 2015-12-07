package com.gosoon.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gosoon.R;
import com.gosoon.entity.GoodsEntity;
import com.gosoon.entity.OrderInfoEntity;
import com.gosoon.util.DateUtils;
import com.gosoon.util.Utils;

public class MyOrdersAdapter extends BaseListAdapter<OrderInfoEntity> {

	public MyOrdersAdapter(Activity activity,
			List<? extends OrderInfoEntity> list) {
		super(activity, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.item_my_orders, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		OrderInfoEntity order = getItem(position);

		if (order.getOrderGoods() != null && order.getOrderGoods().size() > 0) {
			GoodsEntity firstGoods = order.getOrderGoods().get(0).getGoods();
			if (firstGoods != null) {
				Utils.getDefaultBitmapUtils().display(viewHolder.mImage,
						firstGoods.getUrlAsString(GoodsEntity.GOODS_IMG, ""),
						Utils.getConfig(activity, R.drawable.lost_goods_list));
			}
		}
		viewHolder.mCode.setText("订单编号："
				+ order.getValueAsString(OrderInfoEntity.ORDER_SN, ""));
		viewHolder.mDate.setText(DateUtils.getDateString(order.getValueAsLong(
				OrderInfoEntity.ADD_TIME, 0) * 1000l));
		viewHolder.mAmount.setText(order.getValueAsString(
				OrderInfoEntity.GOODS_AMOUNT, ""));
		viewHolder.mStatus.setText(order.getOrderStatus());

		return convertView;
	}

	protected class ViewHolder {

		public ImageView mImage;
		public TextView mCode;
		public TextView mDate;
		public TextView mAmount;
		public TextView mStatus;

		public ViewHolder(View view) {
			mImage = (ImageView) view.findViewById(R.id.iv_products_item);
			mCode = (TextView) view.findViewById(R.id.tv_order_code);
			mDate = (TextView) view.findViewById(R.id.tv_order_date);
			mAmount = (TextView) view.findViewById(R.id.tv_order_amount);
			mStatus = (TextView) view.findViewById(R.id.tv_order_status);
		}
	}
}
