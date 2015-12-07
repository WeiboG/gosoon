package com.gosoon.entity;

import org.json.JSONObject;

public class AccountLogEntity extends BaseEntity{
	public static final String USER_ID = "user_id";
	public static final String USER_MONEY = "user_money";
	public static final String FROZEN_MONEY = "frozen_money";
	public static final String RANK_POINTS = "rank_points";
	public static final String PAY_POINTS = "pay_points";
	public static final String CHANGE_TIME = "change_time";
	public static final String CHANGE_DESC = "change_desc";
	public static final String CHANGE_TYPE = "change_type";

	public AccountLogEntity(JSONObject json) {
        super(json);
	}

	public AccountLogEntity() {
	}

	static tableConfig mTable = null;
	@Override
	public tableConfig getTable() {
		if (mTable == null) {
			mTable = new tableConfig("gsw_account_log");
		}
		return mTable;
	}
}
