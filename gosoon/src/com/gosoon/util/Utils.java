package com.gosoon.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gosoon.fragment.shoppingCartFragment;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.AlphaAnimation;

public class Utils {

	public static void initUtils(Context context) {
		if (mBitmapUtils == null) {
			mBitmapUtils = new BitmapUtils(context);
		}
		if (mSharedPreferences == null) {
			mSharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			shoppingCartFragment.initShoppingCarts();
		}
	}

	public static void logd(String tag, String msg) {
		if (programSetting.debug) {
			Log.d(tag, msg);
		}
	}

	public static void loge(String tag, String msg) {
		Log.e(tag, msg);
	}

	private static BitmapUtils mBitmapUtils;

	public static BitmapUtils getDefaultBitmapUtils() {
		return mBitmapUtils;
	}

	public static BitmapDisplayConfig getConfig(Context context, int resId) {
		BitmapDisplayConfig config = new BitmapDisplayConfig();

		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(300);
		config.setAnimation(alphaAnimation);
		if (resId != 0) {
			if(context != null){
				Drawable drawable = context.getResources().getDrawable(resId);
				config.setLoadingDrawable(drawable);
				config.setLoadFailedDrawable(drawable);
			}
		} else {

		}
		config.setShowOriginal(false);
		return config;
	}

	private static SharedPreferences mSharedPreferences;

	public static SharedPreferences getDefaultSharedPreferences() {
		return mSharedPreferences;
	}

	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	public static String randVerifyCode() {
		long code = (long) (Math.random() * 999999l);
		DecimalFormat df = new DecimalFormat("000000");
		return df.format(code);
	}

	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^\\d{11}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
}
