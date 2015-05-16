package com.yeyunlin.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yeyunlin.info.DeskInfo;
import com.yeyunlin.info.FoodInfo;
import com.yeyunlin.info.ReviewInfo;
import com.yeyunlin.info.UserInfo;

public class JsonUtil {
	public static UserInfo getUserFromJson(String result){
		UserInfo userInfo = new UserInfo();
		try {
			JSONObject jsonObject = new JSONObject(result);
			userInfo.setName(jsonObject.getString("name"));
			userInfo.setAccount(jsonObject.getString("account"));
			userInfo.setPassword(jsonObject.getString("password"));
			userInfo.setIntegral(jsonObject.getInt("integral"));
			userInfo.setIcon(jsonObject.getString("icon"));
			return userInfo;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public static List<FoodInfo> getFoodFromJson(String result){
		List<FoodInfo> foodInfos = new ArrayList<FoodInfo>();
		try {
			JSONArray jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				FoodInfo foodInfo = new FoodInfo();
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				foodInfo.setNumber(jsonObject.getInt(Util.FOOD_NUMBER));
				foodInfo.setName(jsonObject.getString(Util.FOOD_NAME));
				foodInfo.setPrice(jsonObject.getInt(Util.FOOD_PRICE));
				foodInfo.setType(jsonObject.getString(Util.FOOD_TYPE));
				foodInfos.add(foodInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return foodInfos;
	}
	
	public static List<ReviewInfo> getReviewFromJson(String result){
		List<ReviewInfo> reviewInfos = new ArrayList<ReviewInfo>();
		try {
			JSONArray jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				ReviewInfo reviewInfo = new ReviewInfo();
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				reviewInfo.setName(jsonObject.getString("name"));
				reviewInfo.setTime(jsonObject.getString("time"));
				reviewInfo.setContent(jsonObject.getString("content"));
				reviewInfos.add(reviewInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reviewInfos;
	}
	
	public static List<DeskInfo> getdeskFromJson(String result){
		List<DeskInfo> deskInfos = new ArrayList<DeskInfo>();
		try {
			JSONArray jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				DeskInfo deskInfo = new DeskInfo();
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				deskInfo.setId(jsonObject.getInt("id"));
				deskInfo.setFlag(jsonObject.getInt("flag"));;
				deskInfos.add(deskInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deskInfos;
	}
}
