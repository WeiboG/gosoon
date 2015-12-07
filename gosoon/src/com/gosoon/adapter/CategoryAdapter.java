package com.gosoon.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.util.LogUtils;
import com.gosoon.R;
import com.gosoon.entity.CategoryEntity;
import com.gosoon.fragment.baseFragment;

public class CategoryAdapter extends BaseListAdapter<CategoryEntity> {

	static HashMap<String, Integer> mLogos = new HashMap<String, Integer>();
	static {
		mLogos.put("今日抢鲜", R.drawable.list_category1);
		mLogos.put("当季预售", R.drawable.list_category2);
		mLogos.put("时令水果", R.drawable.list_category3);
		mLogos.put("水产海鲜", R.drawable.list_category4);
		mLogos.put("热卖食品", R.drawable.list_category5);
		mLogos.put("肉禽蛋品", R.drawable.list_category6);
		mLogos.put("淘遍南通", R.drawable.list_category7);
		mLogos.put("特价促销", R.drawable.list_category8);
		mLogos.put("手机专享", R.drawable.list_category9);
	}

	public CategoryAdapter(baseFragment fragment,
			ArrayList<CategoryEntity> category) {
		super(fragment, category);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (convertView == null){
			convertView = activity.getLayoutInflater().inflate(R.layout.item_category, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		CategoryEntity category = getItem(position);
		String name = category.getValueAsString(CategoryEntity.CATEGORY_NAME, "");
		if (mLogos.containsKey(name)) {
			viewHolder.mLogo.setImageResource(mLogos.get(name));
		} else {
			LogUtils.e(name + " unknow category");
		}

		viewHolder.mName.setText(name);
		viewHolder.mDesc.setText(category.getDesc());
		return convertView;
	}

	protected class ViewHolder {

		public ImageView mLogo;
		public TextView mName;
		public TextView mDesc;

		public ViewHolder(View view) {
			mLogo = (ImageView) view.findViewById(R.id.iv_category_logo);
			mName = (TextView) view.findViewById(R.id.tv_category_name);
			mDesc = (TextView) view.findViewById(R.id.tv_category_desc);
		}
	}
}
