package com.gosoon.adapter;

import java.util.List;

import u.aly.bo;

import com.gosoon.R;
import com.lidroid.xutils.db.sqlite.CursorUtils.FindCacheSequence;
import com.gosoon.entity.IntegralEntity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IntegralAdapter extends BaseAdapter{

	private Context mContext;
	private List<IntegralEntity> mBonusEntityList;
	public IntegralAdapter(Context p_Context,List<IntegralEntity> p_List){
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
		IntegralEntity integralEntity = mBonusEntityList.get(position);
		ViewHolder holder;
		if(convertView==null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_integral, null);
			holder = new ViewHolder();
			holder.mIntegraldescView = (TextView) convertView.findViewById(R.id.integral_item_ordernumber);
			holder.mIntegraltimeView = (TextView) convertView.findViewById(R.id.integral_item_time);
			holder.mIntegralnumberView = (TextView) convertView.findViewById(R.id.integral_item_number);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mIntegraldescView.setText(integralEntity.getValueAsString(IntegralEntity.CHANGE_DESC, ""));
		holder.mIntegraltimeView.setText(integralEntity.getValueAsString(IntegralEntity.CHANGE_TIME, ""));
		holder.mIntegralnumberView.setText(integralEntity.getValueAsString(IntegralEntity.PAY_POINTS, ""));
		return convertView;
	}
	
	private class ViewHolder{
		TextView mIntegraldescView;
		TextView mIntegraltimeView;
		TextView mIntegralnumberView;
	} 

}
