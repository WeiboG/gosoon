package com.gosoon.entity;

import org.json.JSONObject;

public class UserAddressEntity extends BaseEntity {

	public static final String ADDRESS_ID = "address_id";
	public static final String ADDRESS_NAME = "address_name";
	public static final String USER_ID = "user_id";
	public static final String CONSIGNEE = "consignee";
	public static final String EMAIL = "email";
	public static final String COUNTRY = "country";
	public static final String PROVINCE = "province";
	public static final String CITY = "city";
	public static final String DISTRICT = "district";
	public static final String TOWN = "town";
	public static final String ADDRESS = "address";
	public static final String ZIPCODE = "zipcode";
	public static final String TEL = "tel";
	public static final String MOBILE = "mobile";
	public static final String SIGN_BUILDING = "sign_building";
	public static final String BEST_TIME = "best_time";
	
	public static final String PROVINCE_NAME = "province_name";
	public static final String CITY_NAME = "city_name";
	public static final String DISTRICT_NAME = "district_name";

	public UserAddressEntity(JSONObject json) {
		super(json);
	}
	
	public UserAddressEntity(String sJson) {
		super();
		fromString(sJson);
	}
	
	static tableConfig mTable = null;
	@Override
	public tableConfig getTable() {
		if (mTable == null) {
			mTable = new tableConfig("gsw_user_address");
		}
		return mTable;
	}
	
}
