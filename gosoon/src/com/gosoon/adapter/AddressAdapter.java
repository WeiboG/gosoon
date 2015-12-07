package com.gosoon.adapter;

import java.util.ArrayList;
import com.gosoon.R;
import com.gosoon.entity.UserAddressEntity;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class AddressAdapter extends BaseListAdapter<UserAddressEntity> {
	
	String mSelectedId = null;
	public static int checkItem = 0;
	public boolean fromUser;
	public AddressAdapter(Activity activity,
			ArrayList<UserAddressEntity> list) {
		super(activity, list);
	}

	public void setSelectedId(String id) {
		this.mSelectedId = id;
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return list.get(position).getValueAsLong(UserAddressEntity.ADDRESS_ID, 0);
	}
	public String getAddressSummery(int position){
		UserAddressEntity entity = getItem(position);;
		String addressName = entity.getValueAsString(UserAddressEntity.PROVINCE_NAME, "");
		String address = entity.getValueAsString(UserAddressEntity.CITY_NAME, "");
		String mobile = entity.getValueAsString(UserAddressEntity.DISTRICT_NAME, "");
		return addressName + " " + address + " " + mobile;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (convertView == null){
			convertView = activity.getLayoutInflater().inflate(R.layout.item_address, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv_address_recipients.setText(getAddressSummery(position));
		UserAddressEntity userAddress = getItem(position);
		viewHolder.tv_address_recipients.setText(userAddress.getValueAsString(UserAddressEntity.CONSIGNEE, ""));
//		viewHolder.tv_address_name.setText(userAddress.getValueAsString(UserAddressEntity.ADDRESS_NAME, ""));
		viewHolder.tv_address_province.setText(getAddressSummery(position));
		viewHolder.tv_address_street.setText(userAddress.getValueAsString(UserAddressEntity.ADDRESS, ""));
		viewHolder.tv_address_phone.setText(userAddress.getValueAsString(UserAddressEntity.MOBILE, ""));
		viewHolder.cb_address_check.setVisibility(fromUser ? View.GONE : View.VISIBLE);
		viewHolder.cb_address_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton view, boolean checked) {
					if (checked) {
						checkItem = position;
					}
					notifyDataSetChanged();
			}
		});
		viewHolder.cb_address_check.setChecked(position == checkItem);
//		if (mSelectedId != null && userAddress.getValueAsString(UserAddressEntity.ADDRESS_ID, "").equals(mSelectedId)) {
//			viewHolder.btn_edit_address.setVisibility(View.VISIBLE);
//		} else {
//			viewHolder.btn_edit_address.setVisibility(View.GONE);
//		}
		return convertView;
	}
	protected class ViewHolder {
		
		public TextView tv_address_recipients;
//		public TextView tv_address_name;
		public TextView tv_address_province;
		public TextView tv_address_street;
		public TextView tv_address_phone;
		public CheckBox cb_address_check;

		public ViewHolder(View view) {
			tv_address_recipients = (TextView) view.findViewById(R.id.tv_address_recipients);
//			tv_address_name = (TextView) view.findViewById(R.id.tv_address_name);
			tv_address_province = (TextView) view.findViewById(R.id.tv_address_province);
			tv_address_street = (TextView) view.findViewById(R.id.tv_address_street);
			tv_address_phone = (TextView) view.findViewById(R.id.tv_address_phone);
			cb_address_check = (CheckBox) view.findViewById(R.id.cb_address_check);
		}	
	}

}
