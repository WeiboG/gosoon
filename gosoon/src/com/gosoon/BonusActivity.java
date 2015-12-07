package com.gosoon;

import java.util.List;

import com.gosoon.account.MyAccount;
import com.gosoon.adapter.BonusListAdapter;
import com.gosoon.entity.BonusEntity;
import com.gosoon.util.AlertDialogConfig;
import com.gosoon.util.MyRequest;
import com.gosoon.util.MyRequestCallback;
import com.gosoon.util.MyResult;
import com.gosoon.util.ProgressDialogConfig;
import com.gosoon.util.ToastUtil;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class BonusActivity extends BaseActivity implements android.view.View.OnClickListener{
	
	List<BonusEntity> mList;
	private ListView mBonusListView;
	View view_top_detail;
	ImageButton mAddBonusBtn;
	BonusListAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bonus);
		mBonusListView = (ListView) findViewById(R.id.bonus_list);
		setTitle("我的优惠券");
		view_top_detail = findViewById(R.id.bonus_top_detail);
		mAddBonusBtn = (ImageButton) view_top_detail.findViewById(R.id.btn_rignt);
		mAddBonusBtn.setVisibility(View.VISIBLE);
		mAddBonusBtn.setImageResource(R.drawable.add_bonus);
		mAddBonusBtn.setOnClickListener(this);
		
		 // 查询我的优惠券并显示
		BonusEntity.getMyAllBonusFromNet(getBonusFromNetCallback);
	}
	
	MyRequestCallback getBonusFromNetCallback = new MyRequestCallback(){

		@Override
		public void onSuccess(MyResult result) {
			super.onSuccess(result);
			//得到优惠券
			if(BonusEntity.getMyAllBonusEntities().size()<=0){
				ToastUtil.show(BonusActivity.this, "您还有没优惠券");
			}else{
				mList = BonusEntity.getMyAllBonusEntities();
				mAdapter = new BonusListAdapter(BonusActivity.this, mList);
				mBonusListView.setAdapter(mAdapter);
			}
		}
	};
	

	public void onClick(View v) {
		if(v.getId()==R.id.btn_rignt){
			Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("添加优惠券");
			final EditText editText = new EditText(this);
			editText.setHint("请输入优惠券序列号");
			editText.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			dialog.setView(editText);
			dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which){
					dialog.dismiss();
				}
			});
			dialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String bonus_sn = editText.getText().toString();
					if(bonus_sn.equals("")){
						ToastUtil.show(BonusActivity.this, "请输入优惠券序列号");
						return;
					}
					String username = MyAccount.mUserName;
					MyRequest myRequest = new MyRequest(MyRequest.ACTION_ACT_ADD_BONUS, username, bonus_sn, 0);
					myRequest.setProcessDialogConfig(new ProgressDialogConfig(BonusActivity.this, MainActivity.ALERTDIALOG_ID_LOGIN));
					myRequest.setAlertDialogConfig(new AlertDialogConfig(BonusActivity.this, MainActivity.ALERTDIALOG_ID_LOGIN));
					myRequest.send(addBonusCallBack);
				}
			});
			
			dialog.create().show();
		}
	}
	
	MyRequestCallback addBonusCallBack = new MyRequestCallback(){
		
		public void onSuccess(MyResult result){
			ToastUtil.show(BonusActivity.this, result.mMsg);
			BonusEntity.getMyAllBonusFromNet(getBonusFromNetCallback);
			
		};
		
		public void onFailure(MyResult result) {
		};
	};
}
