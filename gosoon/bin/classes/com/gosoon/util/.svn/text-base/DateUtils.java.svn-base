package com.gosoon.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class DateUtils {
	public static String getDateString(long time) {
		Log.d("DateUtil.getDateString", "debug" + time + "\t");
		if (time < 1) {
			return "";
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			String result = sdf.format(new Date(time));
			Log.d("DateUtil.getDateString", "debugresult" + result + "\t");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
