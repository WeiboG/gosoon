package com.gosoon.entity;

import org.json.JSONObject;

public class GoodsGalleryEntity extends BaseEntity{

	public static final String GOODSGALLERY_IMG_ID = "img_id";
	public static final String GOODSGALLERY_GOODS_ID = "goods_id";
	public static final String GOODSGALLERY_IMG_URL = "img_url";
	public static final String GOODSGALLERY_IMG_DECS = "img_desc";
	public static final String GOODSGALLERY_THUMB_URL = "thumb_url";
	public static final String GOODSGALLERY_IMG_ORIGINAL = "img_original";
	
	public GoodsGalleryEntity(JSONObject json) {
		super(json);
	}

	static tableConfig mTable = null;
	@Override
	public tableConfig getTable() {
		if (mTable == null) {
			mTable = new tableConfig("gsw_goods_gallery");
		}
		return mTable;
	}
}
