package com.gosoon.entity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class CategoryEntity extends BaseEntity {

	public static final String CATEGORY_ID = "cat_id";
	public static final String CATEGORY_NAME = "cat_name";
	public static final String CATEGORY_DESC = "cat_desc";
	public static final String PARENT_ID = "parent_id";
	public static final String MEASURE_UNIT = "measure_unit";
	
	public static ArrayList<CategoryEntity> praseCategoryEntities(ArrayList<JSONObject> jsons, CategoryEntity parent) {
		String parentId = "0";
		if (parent != null) {
			parentId = parent.getValueAsString(CATEGORY_ID, null);
			if (parentId == null) {
				return null;
			}
		}
		ArrayList<CategoryEntity> entities = new ArrayList<CategoryEntity>();
		if (jsons != null) {
			for (JSONObject json : jsons) {
				if (json.has(PARENT_ID)) {
					try {
						if (parentId.equals(json.getString(PARENT_ID))) {
							CategoryEntity child = new CategoryEntity(json);
							entities.add(child);
							child.mChildren = praseCategoryEntities(jsons, child);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return entities;
	}
	
	ArrayList<CategoryEntity> mChildren = null;
	public CategoryEntity(JSONObject json){
		super(json);
	}

	public ArrayList<CategoryEntity> getChildren(){
	    return mChildren;
	}
	
	public CategoryEntity getChildByCatId(String id) {
		if (mChildren != null) {
			for (CategoryEntity category : mChildren) {
				if (category.getValueAsString(CategoryEntity.CATEGORY_ID, "").equals(id)) {
		    		return category;
		    	} else {
		    		category = category.getChildByCatId(id);
		    		if (category != null) {
		    			return category;
		    		}
		    	}
			}
		}
		return null;
	}

	public String getDesc() {
		String desc = "";
		if (mChildren != null) {
			for (CategoryEntity child : mChildren) {
				if (!desc.isEmpty()) {
					desc += "/";
				}
				desc += child.getValueAsString(CATEGORY_NAME, "");
			}
		}
		return desc;
	}

	static tableConfig mTable = null;
	@Override
	public tableConfig getTable() {
		if (mTable == null) {
			mTable = new tableConfig("gsw_category");
		}
		return mTable;
	}
}
