package com.gosoon.entity;

import org.json.JSONObject;

public class CollectGoodsEntity extends BaseEntity{

	public static final String REC_ID = "rec_id";
	public static final String USER_ID = "user_id";
	public static final String GOODS_ID = "goods_id";
	public static final String ADD_TIME = "add_time";
	public static final String IS_ATTENTION = "is_attention";

	public CollectGoodsEntity(JSONObject json) {
		super(json);
	}

	public CollectGoodsEntity() {
        super();
	}

	static tableConfig mTable = null;
	@Override
	public tableConfig getTable() {
		if (mTable == null) {
			mTable = new tableConfig("gsw_collect_goods");
		}
		return mTable;
	}
	
	GoodsEntity mGoods = null;
	public GoodsEntity getGoods(){
	    return mGoods;
	}
	public void setGoods(GoodsEntity goods){
		mGoods = goods;
	}
}
