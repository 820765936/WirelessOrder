package com.yeyunlin.wirelessorder;

import java.util.ArrayList;
import java.util.List;

import com.yeyunlin.info.FoodInfo;

import android.app.Application;

public class DataApplication extends Application {
	private boolean isFirstLogin = true;
	private List<FoodInfo> foodInfos = new ArrayList<FoodInfo>();

	public boolean isFirstLogin() {
		return isFirstLogin;
	}

	public void setFirstLogin(boolean isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}
	
	public void addFood(FoodInfo foodInfo){
		foodInfos.add(foodInfo);
	}
	
	public boolean isFoodExist(FoodInfo foodInfo){
		for (FoodInfo food : foodInfos) {
			if (food.getName().equals(foodInfo.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public List<FoodInfo> getAllFood(){
		return foodInfos;
	}
	
	public void removeAllFood(){
		foodInfos.clear();
	}
	
	public int foodsSize(){
		return foodInfos.size();
	}
}
