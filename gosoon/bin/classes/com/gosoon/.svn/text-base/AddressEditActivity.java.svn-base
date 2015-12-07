package com.gosoon;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gosoon.account.MyAccount;
import com.gosoon.entity.RegionEntity;
import com.gosoon.entity.UserAddressEntity;
import com.gosoon.entity.UserEntity;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.StringUtil;
import com.gosoon.util.ToastUtil;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class AddressEditActivity extends BaseActivity {
	public static final String EXTRA_ADD = "add";
	public static final String EXTRA_ENTITY = "entity";
	/** true是添加地址、false是编辑地址 **/
	private static boolean add = true;
	private UserAddressEntity addressEntity;

	// UI
	private EditText et_address_name, et_address_street, et_address_recipients,
			et_address_phone;
	private RelativeLayout ly_address_province, ly_address_city,
			ly_address_area;
	private TextView tv_address_province, tv_address_city, tv_address_area;
    private View btn_commit;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getExtra();
		setContentView(R.layout.fragment_address_edit);
		setTitle(getResources().getString(
				add ? R.string.add_address : R.string.edit_address));
		findView();
		if (!add) {
			updateView();
		}
	}

	private void saveAddress() {
//		String mAddressName = et_address_name.getText().toString();
		String mAddress = et_address_street.getText().toString();
		String mConsignee = et_address_recipients.getText().toString();
		String mMobile = et_address_phone.getText().toString();
		
		if (/*StringUtil.isEmpty(mAddressName) ||*/
			StringUtil.isEmpty(mAddress) ||
			StringUtil.isEmpty(mConsignee) ||
			StringUtil.isEmpty(mMobile)
			) {
			AlertDialogConfig config = new AlertDialogConfig(
					AddressEditActivity.this,
					MainActivity.ALERTDIALOG_ID_ADDRESS_EDIT);
			config.message = "信息不完整";
			showAlertDialog(config);
			return;
		}
		if(add && (mCountry == null || mProvince == null || mArea == null)){
			AlertDialogConfig config = new AlertDialogConfig(
					AddressEditActivity.this,
					MainActivity.ALERTDIALOG_ID_ADDRESS_EDIT);
			config.message = "信息不完整";
			showAlertDialog(config);
			return;
		}
		
		addressEntity.setValueAsString(UserAddressEntity.USER_ID, MyAccount.getUser().getValueAsString(UserEntity.USER_ID, ""));
		
//		addressEntity.setValueAsString(UserAddressEntity.ADDRESS_NAME, mAddressName);
		addressEntity.setValueAsString(UserAddressEntity.ADDRESS, mAddress);
		addressEntity.setValueAsString(UserAddressEntity.CONSIGNEE, mConsignee);
		addressEntity.setValueAsString(UserAddressEntity.MOBILE, mMobile);
		addressEntity.setValueAsString(UserAddressEntity.TEL, mMobile);
		
		if(add || (mArea != null && mProvince != null && mCity != null && mCountry != null)){
			addressEntity.setValueAsString(UserAddressEntity.COUNTRY, mCountry.getValueAsString(RegionEntity.REGION_ID, ""));
			addressEntity.setValueAsString(UserAddressEntity.PROVINCE, mProvince.getValueAsString(RegionEntity.REGION_ID, ""));
			addressEntity.setValueAsString(UserAddressEntity.CITY, mCity.getValueAsString(RegionEntity.REGION_ID, ""));
			addressEntity.setValueAsString(UserAddressEntity.DISTRICT, mArea.getValueAsString(RegionEntity.REGION_ID, ""));
		}
		
		if (add) {
			
			MyRequest myrequest = new MyRequest(addressEntity.getTable().mTableName, addressEntity.toString());
			myrequest.setProcessDialogConfig(new ProgressDialogConfig(this, MainActivity.PROCESSDIALOG_ID_ADDRESS_EDIT));
			myrequest.setAlertDialogConfig(new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_ADDRESS_EDIT));
			myrequest.send(createUserAddressCallback);
		} else {
			addressEntity.remove(UserAddressEntity.CITY_NAME);
			addressEntity.remove(UserAddressEntity.PROVINCE_NAME);
			addressEntity.remove(UserAddressEntity.DISTRICT_NAME);
			MyRequest myrequest = new MyRequest(HttpMethod.GET, MyRequest.ACTION_SQL);
			myrequest.setSql(addressEntity.getUpdateSql(UserAddressEntity.ADDRESS_ID));
			myrequest.send(createUserAddressCallback);
		}
	}
	
	MyRequestCallback createUserAddressCallback = new MyRequestCallback() {
		
		@Override
		public void onSuccess(MyResult result) {
			AddressEditActivity.this.setResult(Activity.RESULT_OK);
			ToastUtil.show(getApplicationContext(), add ? "新建收货地址成功" : "修改收货地址成功");
			finish();
		};
		
		@Override
		public void onFailure(MyResult result) {
			ToastUtil.show(getApplicationContext(), add ? "新建收货地址失败" : "修改收货地址失败");
		};
	};
	private void findView() {
//		et_address_name = (EditText) findViewById(R.id.et_address_name);
		et_address_street = (EditText) findViewById(R.id.et_address_street);
		et_address_recipients = (EditText) findViewById(R.id.et_address_recipients);
		et_address_phone = (EditText) findViewById(R.id.et_address_phone);

		ly_address_province = (RelativeLayout) findViewById(R.id.ly_address_province);
		ly_address_city = (RelativeLayout) findViewById(R.id.ly_address_city);
		ly_address_area = (RelativeLayout) findViewById(R.id.ly_address_area);

		tv_address_province = (TextView) findViewById(R.id.tv_address_province);
		tv_address_city = (TextView) findViewById(R.id.tv_address_city);
		tv_address_area = (TextView) findViewById(R.id.tv_address_area);

		btn_commit = findViewById(R.id.btn_commit);
		
		ly_address_province.setOnClickListener(myClickListener);
		ly_address_city.setOnClickListener(myClickListener);
		ly_address_area.setOnClickListener(myClickListener);
		btn_commit.setOnClickListener(myClickListener);
		if(!add){
			setRightButton(this, R.drawable.btn_delete_shoppingcart);
			rightButton.setOnClickListener(myClickListener);
		}
	}

	/**
	 * 加载地址数据
	 */
	private void updateView() {
//		et_address_name.setText(addressEntity.getValueAsString(
//				UserAddressEntity.ADDRESS_NAME, ""));
		tv_address_province.setText(addressEntity.getValueAsString(
				UserAddressEntity.PROVINCE_NAME, ""));
		tv_address_city.setText(addressEntity.getValueAsString(
				UserAddressEntity.CITY_NAME, ""));
		tv_address_area.setText(addressEntity.getValueAsString(
				UserAddressEntity.DISTRICT_NAME, ""));
		et_address_street.setText(addressEntity.getValueAsString(
				UserAddressEntity.ADDRESS, ""));
		et_address_recipients.setText(addressEntity.getValueAsString(
				UserAddressEntity.CONSIGNEE, ""));
		et_address_phone.setText(addressEntity.getValueAsString(
				UserAddressEntity.MOBILE, ""));
		
	}

	/**
	 * 获取Intent传递过来的数据
	 */
	private void getExtra() {
		add = getIntent().getBooleanExtra(EXTRA_ADD, true);
		String sEntity = getIntent().getStringExtra(EXTRA_ENTITY);
		addressEntity = new UserAddressEntity(sEntity);
	}

	OnClickListener myClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ly_address_province:
				selectrRegion(SELECT_PROVINCE);
				break;
			case R.id.ly_address_city:
				selectrRegion(SELECT_CITY);
				break;
			case R.id.ly_address_area:
				selectrRegion(SELECT_AREA);
				break;
			case R.id.btn_commit:
				saveAddress();
				break;
			case R.id.btn_rignt:
				showConfirmAlert();
				break;
			default:
				break;
			}
		}
	};

	int mSelectRegionType;

	protected void selectrRegion(int type) {
		mSelectRegionType = type;
		RegionEntity.getRegionsFromNet(getRegionsCallback);
	}

	MyRequestCallback getRegionsCallback = new MyRequestCallback() {

		@Override
		public void onSuccess(MyResult result) {
			if (mCountry == null && RegionEntity.getRegions().size() > 0) {
				mCountry = RegionEntity.getRegions().get(0);
			}

			switch (mSelectRegionType) {
			case SELECT_PROVINCE:
				if (mCountry != null) {
					createRegionDialog(mCountry.getChildren(),
							mSelectRegionType);
				} else {
					AlertDialogConfig config = new AlertDialogConfig(
							AddressEditActivity.this,
							MainActivity.ALERTDIALOG_ID_ADDRESS_EDIT);
					config.message = "没有可用地区信息";
					showAlertDialog(config);
				}
				break;
			case SELECT_CITY:
				if (mProvince != null) {
					createRegionDialog(mProvince.getChildren(),
							mSelectRegionType);
				} else {
					AlertDialogConfig config = new AlertDialogConfig(
							AddressEditActivity.this,
							MainActivity.ALERTDIALOG_ID_ADDRESS_EDIT);
					config.message = "没有可用地区信息";
					showAlertDialog(config);
				}
				break;
			case SELECT_AREA:
				if (mCity != null) {
					createRegionDialog(mCity.getChildren(), mSelectRegionType);
				} else {
					AlertDialogConfig config = new AlertDialogConfig(
							AddressEditActivity.this,
							MainActivity.ALERTDIALOG_ID_ADDRESS_EDIT);
					config.message = "没有可用地区信息";
					showAlertDialog(config);
				}
				break;
			default:
				break;
			}
		};

		@Override
		public void onFailure(MyResult result) {

		};
	};

	static final int SELECT_PROVINCE = 1;
	static final int SELECT_CITY = 2;
	static final int SELECT_AREA = 3;
	RegionEntity mCountry = null;
	RegionEntity mProvince = null;
	RegionEntity mCity = null;
	RegionEntity mArea = null;

	protected void createRegionDialog(final ArrayList<RegionEntity> regions,
			final int type) {
        if (regions == null || regions.size() == 0) {
        	AlertDialogConfig config = new AlertDialogConfig(
					AddressEditActivity.this,
					MainActivity.ALERTDIALOG_ID_ADDRESS_EDIT);
			config.message = "没有可用地区信息";
			showAlertDialog(config);
			return;
        }
		
		String[] items = new String[regions.size()];
		for (int i = 0; i < regions.size(); i++) {
			items[i] = regions.get(i).getValueAsString(
					RegionEntity.REGION_NAME, "");
		}

		int titleRes = R.string.title_select_province;
		switch (type) {
		case SELECT_PROVINCE:
			titleRes = R.string.title_select_province;
			break;
		case SELECT_CITY:
			titleRes = R.string.title_select_city;
			break;
		case SELECT_AREA:
			titleRes = R.string.title_select_district;
			break;
		default:
			break;
		}

		new AlertDialog.Builder(this).setTitle(titleRes)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						switch (type) {
						case SELECT_PROVINCE:
							mProvince = regions.get(whichButton);
							mCity = null;
							mArea = null;
							break;
						case SELECT_CITY:
							mCity = regions.get(whichButton);
							mArea = null;
							break;
						case SELECT_AREA:
							mArea = regions.get(whichButton);
							break;
						default:
							break;
						}
						updateRegion(type);
						dialog.dismiss();
					}
				}).create().show();
	}

	protected void updateRegion(int type) {
		switch (type) {
		case SELECT_PROVINCE:
			if (mProvince != null) {
				tv_address_province.setText(mProvince.getValueAsString(
						RegionEntity.REGION_NAME, ""));
			} else {
				tv_address_province.setText(R.string.label_pls_select);
			}
		case SELECT_CITY:
			if (mCity != null) {
				tv_address_city.setText(mCity.getValueAsString(
						RegionEntity.REGION_NAME, ""));
			} else {
				tv_address_city.setText(R.string.label_pls_select);
			}
		case SELECT_AREA:
			if (mArea != null) {
				tv_address_area.setText(mArea.getValueAsString(
						RegionEntity.REGION_NAME, ""));
			} else {
				tv_address_area.setText(R.string.label_pls_select);
			}
			break;
		default:
			break;
		}
	}
	private void showConfirmAlert() {
		AlertDialogConfig alertDialogConfig = new AlertDialogConfig(this, MainActivity.ALERTDIALOG_ID_DEL_SHOPPINGCART);
		alertDialogConfig.message = "确定要删除该地址吗？";
		alertDialogConfig.title = R.string.prompt;
		alertDialogConfig.showNegative = true;
		alertDialogConfig.onPositiveListener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				deleteAddress();
			}
		};
		alertDialogConfig.show();
	}
	
	protected void deleteAddress() {
		MyRequest myrequest = new MyRequest(HttpMethod.POST,
				MyRequest.ACTION_SQL);
		myrequest.setSql("DELETE from gsw_user_address where address_id="
				+ addressEntity.getValueAsString(UserAddressEntity.ADDRESS_ID, "0"));
		myrequest.send(deleteAddressCallback);
	}
	MyRequestCallback deleteAddressCallback = new MyRequestCallback() {

		@Override
		public void onFailure(MyResult result) {
			ToastUtil.show(getApplicationContext(), "操作失败，请重试");
		};

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			ToastUtil.show(getApplicationContext(), "该地址已删除");
			AddressEditActivity.this.setResult(Activity.RESULT_OK);
			finish();
			
		};
	};
}
