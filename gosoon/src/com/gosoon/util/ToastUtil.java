package com.gosoon.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastUtil
{
	static boolean isToastShow=false;
	private static  Toast toast = null;
	private static Handler handler;
	public static void show(final Context context, final String info)
	{
		try {
			if(handler == null){
				handler = new Handler();
			}
			handler.post(new Runnable()
			{

				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					
					if(toast!=null){
						
					    toast.cancel();
					}
					toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
					toast.show();
				}

			});
		} catch (Exception e) {
		}
	}

	public static void show(final Context context, final int info)
	{
		if(handler == null){
			handler = new Handler();
		}
		handler.post(new Runnable()
		{

			@Override
			public void run()
			{
				
				if(toast!=null){
					
				    toast.cancel();
				}
				toast = Toast.makeText(context, info + "", Toast.LENGTH_SHORT);
				toast.show();	
			}
		});
	}
}
