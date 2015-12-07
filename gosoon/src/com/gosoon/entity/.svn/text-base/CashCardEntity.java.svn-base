package com.gosoon.entity;

import org.json.JSONObject;

import com.gosoon.fragment.categoryFragment;
import com.gosoon.util.StringUtil;

public class CashCardEntity extends BaseEntity{
	//[{"id":"32","price":"2","code":"TEST10","use_date":null,"create_date":null,"use_id":"0","name":null}]
	public static final String ID = "id";
	public static final String PRICE = "price";
	public static final String CODE = "code";
	public static final String USE_DATE = "use_date";
	public static final String CREAT_DATE = "create_date";
	public static final String USE_ID = "use_id";
	public static final String NAME = "name";

	public CashCardEntity(JSONObject json) {
        super(json);
	}

	public CashCardEntity() {
	}

	static tableConfig mTable = null;
	@Override
	public tableConfig getTable() {
		if (mTable == null) {
			mTable = new tableConfig("gsw_cashcard");
		}
		return mTable;
	}
}
