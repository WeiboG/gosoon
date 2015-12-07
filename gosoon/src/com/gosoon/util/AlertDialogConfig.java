package com.gosoon.util;

import android.app.Activity;

import com.gosoon.BaseActivity;
import com.gosoon.MainActivity;
import com.gosoon.MyApplication;
import com.gosoon.R;

public class AlertDialogConfig {
	public int id;
	public int title = R.string.load_data_from_net_error;
	public String message;
	public int positiveButton = R.string.confirm;
	public int negativeButton = R.string.cancel;
	public boolean showNegative = false;
	public android.content.DialogInterface.OnClickListener onPositiveListener = null;
	public android.content.DialogInterface.OnClickListener onNegativeListener = null;
	public Activity activity;

	public AlertDialogConfig(Activity activity, int id) {
		this.activity = activity;
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AlertDialogConfig) {
			return id == ((AlertDialogConfig) o).id;
		}
		return false;
	}

	public void show() {
		if (activity != null) {
			if (activity instanceof MainActivity) {
				((MainActivity) activity).showAlertDialog(this);
			} else if (MyApplication.TopActivity instanceof BaseActivity){
				((BaseActivity) activity).showAlertDialog(this);
			}
		} else if (MyApplication.TopActivity != null) {
			if (MyApplication.TopActivity instanceof MainActivity){
				((MainActivity) MyApplication.TopActivity).showAlertDialog(this);
			} else if (MyApplication.TopActivity instanceof BaseActivity){
				((BaseActivity) MyApplication.TopActivity).showAlertDialog(this);
			}
		}
	}
}