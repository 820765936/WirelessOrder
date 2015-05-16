package com.yeyunlin.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Util {
	public static final String SHARE_NAME = "userinformation";
	public static final String USER_NAME = "name";
	public static final String USER_ACCOUNT = "account";
	public static final String USER_PASSWORD = "password";
	public static final String USER_INTEGRAL = "integral";
	public static final String USER_ICON = "icon";
	
	public static final String FOOD_NUMBER = "number";
	public static final String FOOD_NAME = "name";
	public static final String FOOD_PRICE = "price";
	public static final String FOOD_TYPE = "type";
	public static final String FOOD_ICON = "icon";
	public static final String FOOD_DESCRIPTION = "description";
	
	public static final String SHARE_DESK_NAME = "deskinformation";
	public static final String DESK_NUMBER = "desknumber";
	public static final String DESK_TIME = "desktime";
	public static final int DESK_using = 0;
	public static final int DESK_UNUSE = 1;
	public static final int DESK_UNKNOW = 2;
	
	public static final String PAY_TYPE = "pay_type";
	public static final String PAY_PRICE = "pay_price";
	
	public static boolean isLanded(Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_NAME, Context.MODE_WORLD_READABLE);
		String userName = sharedPreferences.getString(USER_NAME, "");
		return !userName.equals("");
	}
	
	public static void clearSharepreference(SharedPreferences sharedPreferences){
		Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
	}
	
	public static String getCurrentTime(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss");
		Date    curDate    =   new    Date(System.currentTimeMillis());
		return simpleDateFormat.format(curDate);
	}
}
