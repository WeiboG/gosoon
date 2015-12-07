package com.gosoon.view;


import com.gosoon.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NumberSelectButton extends LinearLayout{

	public NumberSelectButton(Context context,AttributeSet attrs,int defStyle){
		super(context,attrs,defStyle);
		setMyView(context,attrs);
		setMyAttribute(context,attrs);
	}

	public NumberSelectButton(Context context,AttributeSet attrs){
		super(context,attrs);

		setMyView(context,attrs);
		setMyAttribute(context,attrs);
	}

	public NumberSelectButton(Context context){
		super(context);
		setMyView(context,null);
	}

	private TextView textView;

	private String text;


	private View leftButton;

	private View rightButton;
    
	private OnNumberChangedListner mumberChangedCallback = null;
	
	private void setMyView(Context context,AttributeSet attrs){
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) context
		        .getSystemService(infService);
		li.inflate(R.layout.number_select,this);
		textView = (TextView) findViewById(R.id.numberselectbutton_textView);
		leftButton = findViewById(R.id.numberselectbutton_leftButton);
		leftButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){
				int number = Integer.valueOf(getText());
				if(number > 1){
                    if (mumberChangedCallback!=null && !mumberChangedCallback.OnNumberChanged(number - 1)) {
                    	return;
					}
					setText(String.valueOf(number - 1));
				}else{
					return;
				}

			}
		});
		rightButton = findViewById(R.id.numberselectbutton_rightButton);
		rightButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){
				int number = Integer.valueOf(getText());
                if (mumberChangedCallback!=null && !mumberChangedCallback.OnNumberChanged(number + 1)) {
                	return;
				}
				setText(String.valueOf(number + 1));
			}
		});

	}

	private void setMyAttribute(Context context,AttributeSet attrs){

		if(textView == null){ throw new RuntimeException("�Ҳ�����Դ�ļ�.....�������趨��Դ�ļ�"); }
		int resourceid;

		resourceid = attrs.getAttributeResourceValue(null,"text",0);
		if(resourceid == 0){
			text = attrs.getAttributeValue(null,"0");
		}else{
			text = getResources().getString(resourceid);
		}
		if(text == null){
			text = "1";// Ĭ��
		}
		if(!text.matches("[\\d+]")){ throw new RuntimeException("��Ч��ֵ"); }
		textView.setText(text);
	}



	TextView getTextView(){
		return textView;
	}

	void setTextView(TextView textView){
		this.textView = textView;
	}

	public String getText(){
		return textView.getText().toString();
	}

	public void setText(String text){
		this.text = text;
		textView.setText(text);
	}
	public void setOnNumberChangedListener(OnNumberChangedListner callback){
		mumberChangedCallback = callback;
	}
	
	public interface OnNumberChangedListner {
		public boolean OnNumberChanged(int num);
	}
}
