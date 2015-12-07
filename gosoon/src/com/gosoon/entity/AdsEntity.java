package com.gosoon.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.gosoon.util.programSetting;

public class AdsEntity extends BaseEntity {

	public static final String IMAGE = "image";
	public static final String TARGET = "target";

	public AdsEntity(JSONObject json) {
		super(json);
	}

	@Override
	public tableConfig getTable() {
		return null;
	}

	@Override
	public String getUrlAsString(String name, String defaultValue) {
		if (mJson != null && mJson.has(name)) {
			try {
				String url = mJson.getString(name);
				if (!url.startsWith("http")) {
					url = programSetting.getADSImageUrl() + url;
				}
				return url;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return defaultValue;
	}

}
