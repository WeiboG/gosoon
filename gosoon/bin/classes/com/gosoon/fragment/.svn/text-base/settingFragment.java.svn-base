package com.gosoon.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gosoon.MainActivity;
import com.gosoon.R;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.ToastUtil;
import com.gosoon.util.Utils;
import com.lidroid.xutils.BitmapUtils;

@SuppressLint("ValidFragment")
public class settingFragment extends baseFragment{
	View ly_clear_data, ly_system_out;
	@SuppressLint("ValidFragment")
	public settingFragment(Bundle params) {
		super(params);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_setting, container, false);
		setTitle(root, "设置");
		hideLeftButton(root, true);
		
		ly_clear_data = root.findViewById(R.id.ly_clear_data);
		ly_clear_data.setOnClickListener(this);
		ly_system_out = root.findViewById(R.id.ly_system_out);
		ly_system_out.setOnClickListener(this);
		return root;
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ly_clear_data:
			celarData();
			break;
		case R.id.ly_system_out:
			showSystemOutAlert();
			break;

		default:
			break;
		}
	}
	private void showSystemOutAlert() {
		AlertDialogConfig alertDialogConfig = new AlertDialogConfig(getActivity(), MainActivity.ALERTDIALOG_ID_DEL_SHOPPINGCART);
		alertDialogConfig.message = "确定要退出程序吗？";
		alertDialogConfig.title = R.string.prompt;
		alertDialogConfig.showNegative = true;
		alertDialogConfig.onPositiveListener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				getActivity().finish();
			}
		};
		alertDialogConfig.show();
	}
	private void celarData() {
		BitmapUtils bitmapUtils = Utils.getDefaultBitmapUtils();
		bitmapUtils.clearCache();
		bitmapUtils.clearDiskCache();
		bitmapUtils.clearMemoryCache();
		ToastUtil.show(getActivity(), "已清除本地数据");
	}
}
