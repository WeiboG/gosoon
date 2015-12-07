package com.gosoon.util;

import com.gosoon.BaseActivity;
import com.gosoon.MainActivity;
import com.gosoon.MyApplication;
import com.gosoon.R;

import android.app.Activity;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;

public class ProgressDialogConfig {
	public int id;
	public int title = R.string.load_data_from_net_title;
	public int message = R.string.load_data_from_net_message;
	public boolean indeterminate = true; 
	public boolean cancelable = true;
	public OnCancelListener onCalcelListener = null;
	public OnDismissListener onDismissListener = null;
	public Activity activity;

	public ProgressDialogConfig(Activity activity, int id) {
		this.activity = activity;
		this.id = id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ProgressDialogConfig) {
			return id == ((ProgressDialogConfig) o).id;
		}
		return false;
	}
	
	public void show() {
		try {
			if (activity != null) {
				if (activity instanceof MainActivity) {
					((MainActivity) activity).showProcessDialog(this);
				} else if (MyApplication.TopActivity instanceof BaseActivity) {
					((BaseActivity) activity).showProcessDialog(this);
				}
			} else if (MyApplication.TopActivity != null) {
				if (MyApplication.TopActivity instanceof MainActivity) {
					((MainActivity) MyApplication.TopActivity).showProcessDialog(this);
				} else if (MyApplication.TopActivity instanceof BaseActivity) {
					((BaseActivity) MyApplication.TopActivity).showProcessDialog(this);
				}
			}
		} catch (Exception e) {
		}
		
	}
	
	public void close() {
		try {
			if (activity != null) {
				if (activity instanceof MainActivity) {
					((MainActivity) activity).closeProcessDialog(this);
				} else if (MyApplication.TopActivity instanceof BaseActivity) {
					((BaseActivity) activity).closeProcessDialog(this);
				}
			} else if (MyApplication.TopActivity != null) {
				if (MyApplication.TopActivity instanceof MainActivity){
					((MainActivity) MyApplication.TopActivity).closeProcessDialog(this);
				} else if (MyApplication.TopActivity instanceof BaseActivity) {
					((BaseActivity) MyApplication.TopActivity).closeProcessDialog(this);
				}
			}
		} catch (Exception e) {
		}
		
	}
}
