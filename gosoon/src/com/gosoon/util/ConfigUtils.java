package com.gosoon.util;

import java.util.ArrayList;

import com.gosoon.entity.CategoryEntity;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class ConfigUtils {

	static ArrayList<CategoryEntity> mCategory = null;
	static ArrayList<MyRequestCallback> mCategoryCallbacks = new ArrayList<MyRequestCallback>();
	public static void getCategorysFromNet(MyRequestCallback callback) {
		if (mCategory == null) {
			if (!getCategoryCallback.getMyResult().isLoading()) {
				MyRequest categorysRequest = new MyRequest(HttpMethod.POST, MyRequest.ACTION_SQL);
				categorysRequest.setSql("SELECT * from gsw_category order by sort_order DESC");
				categorysRequest.send(getCategoryCallback);
			}
			mCategoryCallbacks.add(callback);
		} else {
			callback.onSuccess(getCategoryCallback.getMyResult());
		}
	}
	public static void unRegisterCategoryCallback(MyRequestCallback callback) {
		mCategoryCallbacks.remove(callback);
	}
	public static ArrayList<CategoryEntity> getCategorys() {
		return mCategory;
	}
	public static CategoryEntity getCategoryByCatId(String id) {
		if (mCategory != null) {
			for (CategoryEntity category : mCategory) {
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

	static MyRequestCallback getCategoryCallback = new MyRequestCallback() {
		
		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			mCategory = CategoryEntity.praseCategoryEntities(result.getData(), null);
			for (MyRequestCallback callback : mCategoryCallbacks) {
				callback.onSuccess(result);
			}
			mCategoryCallbacks.clear();
		};

		@Override
		public void onFailure(MyResult result) {
			super.onFailure(result);
			for (MyRequestCallback callback : mCategoryCallbacks) {
				callback.onFailure(result);
			}
			mCategoryCallbacks.clear();
		};
	};
}
