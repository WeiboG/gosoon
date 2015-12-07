package com.gosoon.entity;

import org.json.JSONObject;

public class IntegralEntity extends BaseEntity{
	public static final String PAY_POINTS="pay_points";
	public static final String CHANGE_TIME="change_time";
	public static final String CHANGE_DESC="change_desc";
	public static final String CHANGE_TYPE="change_type";
	public IntegralEntity(JSONObject json)
	{
		super(json);
	}

	@Override
	public tableConfig getTable() {
		return null;
	}

	

}
