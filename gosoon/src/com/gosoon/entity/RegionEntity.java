package com.gosoon.entity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.gosoon.MainActivity;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class RegionEntity extends BaseEntity {

	public static final String REGION_ID = "region_id";
	public static final String PARENT_ID = "parent_id";
	public static final String REGION_NAME = "region_name";
	public static final String REGION_TYPE = "region_type";
	public static final String AGENCY_ID = "agency_id";

	public static ArrayList<RegionEntity> praseRegionEntities(ArrayList<JSONObject> jsons, RegionEntity parent) {
		String parentId = "0";
		if (parent != null) {
			parentId = parent.getValueAsString(REGION_ID, null);
			if (parentId == null) {
				return null;
			}
		}
		ArrayList<RegionEntity> entities = new ArrayList<RegionEntity>();
		if (jsons != null) {
			for (JSONObject json : jsons) {
				if (json.has(PARENT_ID)) {
					try {
						if (parentId.equals(json.getString(PARENT_ID))) {
							RegionEntity child = new RegionEntity(json);
							entities.add(child);
							child.mChildren = praseRegionEntities(jsons, child);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return entities;
	}

	ArrayList<RegionEntity> mChildren = null;
	public RegionEntity(JSONObject json){
		super(json);
	}

	public ArrayList<RegionEntity> getChildren(){
	    return mChildren;
	}
	
	public RegionEntity getChildByRegionId(String id) {
		if (mChildren != null) {
			for (RegionEntity region : mChildren) {
				if (region.getValueAsString(REGION_ID, "").equals(id)) {
		    		return region;
		    	} else {
		    		region = region.getChildByRegionId(id);
		    		if (region != null) {
		    			return region;
		    		}
		    	}
			}
		}
		return null;
	}

	static ArrayList<RegionEntity> mRegions = null;
	static ArrayList<MyRequestCallback> mRegionCallbacks = new ArrayList<MyRequestCallback>();
	public static void getRegionsFromNet(MyRequestCallback callback) {
		if (mRegions == null) {
			if (!getRegionCallback.getMyResult().isLoading()) {
				MyRequest myrequest = new MyRequest(HttpMethod.POST, MyRequest.ACTION_SQL);
				myrequest.setSql("SELECT * from gsw_region");
				myrequest.setProcessDialogConfig(new ProgressDialogConfig(null, MainActivity.PROCESSDIALOG_ID_GETREGION));
				myrequest.setAlertDialogConfig(new AlertDialogConfig(null, MainActivity.ALERTDIALOG_ID_GETREGION));
				myrequest.send(getRegionCallback);
			}
			mRegionCallbacks.add(callback);
		} else {
			callback.onSuccess(getRegionCallback.getMyResult());
		}
	}
	public static void unRegisterRegionCallback(MyRequestCallback callback) {
		mRegionCallbacks.remove(callback);
	}
	public static ArrayList<RegionEntity> getRegions() {
		return mRegions;
	}
	public static RegionEntity getRegionByRegionId(String id) {
		if (mRegions != null) {
			for (RegionEntity region : mRegions) {
		    	if (region.getValueAsString(RegionEntity.REGION_ID, "").equals(id)) {
		    		return region;
		    	} else {
		    		region = region.getChildByRegionId(id);
		    		if (region != null) {
		    			return region;
		    		}
		    	}
		    }
		}
		return null;
	}

	static MyRequestCallback getRegionCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			mRegions = RegionEntity.praseRegionEntities(result.getData(), null);
			for (MyRequestCallback callback : mRegionCallbacks) {
				callback.onSuccess(result);
			}
			mRegionCallbacks.clear();
		};

		@Override
		public void onFailure(MyResult result) {
			super.onFailure(result);
			for (MyRequestCallback callback : mRegionCallbacks) {
				callback.onFailure(result);
			}
			mRegionCallbacks.clear();
		};
	};

	static tableConfig mTable = null;
	@Override
	public tableConfig getTable() {
		if (mTable == null) {
			mTable = new tableConfig("gsw_region");
		}
		return mTable;
	}
}
