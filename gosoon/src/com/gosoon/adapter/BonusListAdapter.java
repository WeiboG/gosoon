package com.gosoon.adapter;

import java.util.List;

import u.aly.bo;

import com.gosoon.R;
import com.gosoon.entity.BonusEntity;
import com.lidroid.xutils.db.sqlite.CursorUtils.FindCacheSequence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BonusListAdapter extends BaseAdapter{

	private Context mContext;
	private List<BonusEntity> mBonusEntityList;
	public BonusListAdapter(Context p_Context,List<BonusEntity> p_List){
		mContext = p_Context;
		mBonusEntityList = p_List;
	}
	
	@Override
	public int getCount() {
		
		return mBonusEntityList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return mBonusEntityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		BonusEntity bonusEntity = mBonusEntityList.get(position);
		ViewHolder holder;
		if(convertView==null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bonus_list, null);
			holder = new ViewHolder();
			holder.mBonusMoneyView = (TextView) convertView.findViewById(R.id.tv_bonus_money);
			holder.mBonusLimitOrderMoneyView = (TextView) convertView.findViewById(R.id.tv_bonus_limit_order_money);
			holder.mBonusEndTimeView = (TextView) convertView.findViewById(R.id.tv_bonus_end_use_time);
			holder.mBonusStateView = (TextView) convertView.findViewById(R.id.tv_bonus_use_state);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mBonusMoneyView.setText(bonusEntity.getType_money());
		holder.mBonusLimitOrderMoneyView.setText(bonusEntity.getMin_goods_amount());
		holder.mBonusEndTimeView.setText(bonusEntity.getUse_end_date());
		holder.mBonusStateView.setText(bonusEntity.getStatus());
		return convertView;
	}
	
	private class ViewHolder{
		TextView mBonusMoneyView;
		TextView mBonusLimitOrderMoneyView;
		TextView mBonusEndTimeView;
		TextView mBonusStateView;
	} 

}
