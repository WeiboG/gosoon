package com.gosoon.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ListView;

import com.gosoon.MainActivity;
import com.gosoon.R;
import com.gosoon.goodslistActivity;
import com.gosoon.adapter.CategoryAdapter;
import com.gosoon.entity.CategoryEntity;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.StringUtil;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

@SuppressLint("ValidFragment")
public class categoryFragment extends baseFragment implements OnItemClickListener{

	ListView mCategoryListView = null;
	CategoryAdapter mCategoryAdapter = null;
	EditText mSearchText;
	
	@SuppressLint("ValidFragment")
	public categoryFragment(Bundle params) {
		super(params);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View main = inflater.inflate(R.layout.fragment_category, container, false);
		mCategoryListView = (ListView) main.findViewById(R.id.lv_category);
		mCategoryListView.setDividerHeight(0);
		mCategoryListView.setOnItemClickListener(this);
		mCategoryListView.setAdapter(mCategoryAdapter);
		
		mSearchText = (EditText) main.findViewById(R.id.et_search);
		mSearchText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					searchGoods(v.getText().toString());
				}
				return false;
			}
		});
		
		updateCategorys();
		return main;
	}

	private void searchGoods(String goodsName) {
		if (!StringUtil.isEmpty(goodsName)) {    		
    		Intent intent = new Intent(getActivity(), goodslistActivity.class);
			intent.putExtra(goodslistActivity.PARAM_GOODS_NAME, goodsName);
			startActivity(intent);
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		getCategorysFromNet(getCategoryFromNetCallback);
	}

    MyRequestCallback getCategoryFromNetCallback = new MyRequestCallback() {
    	
		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			updateCategorys();
		};
	};

	protected void updateCategorys() {
		if (mCategoryListView != null) {
			if (mCategoryAdapter == null) {
				mCategoryAdapter = new CategoryAdapter(this, getCategorys());
				mCategoryListView.setAdapter(mCategoryAdapter);
			} else {
				mCategoryAdapter.changeData(getCategorys());
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		CategoryEntity category = mCategoryAdapter.getItem(position);
        if (category != null) {    		
    		Intent intent = new Intent(getActivity(), goodslistActivity.class);
			intent.putExtra(goodslistActivity.PARAM_CATEGORY, category.toString());
			startActivity(intent);
        }
	}

	static ArrayList<CategoryEntity> mCategory = null;
	static ArrayList<MyRequestCallback> mCategoryCallbacks = new ArrayList<MyRequestCallback>();
	public static void getCategorysFromNet(MyRequestCallback callback) {
		if (mCategory == null) {
			if (!getCategoryCallback.getMyResult().isLoading()) {
				MyRequest categorysRequest = new MyRequest(HttpMethod.POST, MyRequest.ACTION_SQL);
				categorysRequest.setSql("SELECT * from gsw_category order by sort_order ASC");
				categorysRequest.setProcessDialogConfig(new ProgressDialogConfig(null, MainActivity.PROCESSDIALOG_ID_GETCATEGORY));
				categorysRequest.setAlertDialogConfig(new AlertDialogConfig(null, MainActivity.ALERTDIALOG_ID_GETCATEGORY));
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
