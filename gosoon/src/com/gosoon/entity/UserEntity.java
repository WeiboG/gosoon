package com.gosoon.entity;

import org.json.JSONObject;

import com.gosoon.util.DateUtils;

public class UserEntity extends BaseEntity{

	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String MOBILE_PHONE = "mobile_phone";
	public static final String REG_TIME = "reg_time";
	/**用户现有资金**/
	public static final String USER_MONEY = "user_money";
	/*用户当前积分*/
	public static final String PAY_POINTS = "pay_points";
	
	public UserEntity(JSONObject json) {
		super(json);
	}

	static tableConfig mTable = null;
	@Override
	public tableConfig getTable() {
		if (mTable == null) {
			mTable = new tableConfig("gsw_users");
		}
		return mTable;
	}

	public String getRegTime(){
		return DateUtils.getDateString(getValueAsLong(REG_TIME, 0) * 1000l);
	}
}
