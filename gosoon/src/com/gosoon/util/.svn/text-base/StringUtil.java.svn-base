package com.gosoon.util;

public class StringUtil {
	public static boolean isEmpty(String input){
		if(input == null)
			return true;
		if(input.trim().length() == 0)
			return true;
		return false;
	}
	/**
	 * 判断是否有空格
	 * @param str
	 * @return
	 */
	public static boolean hasSpace(String str){
		if(str.trim().length() != str.length()
			|| str.replaceAll("\\t|\\\\t|\u0020|\\u3000", "").length() != str.length() ){
			return true;
		}
		return false;
	}
}
