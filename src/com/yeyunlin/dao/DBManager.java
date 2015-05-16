package com.yeyunlin.dao;

import java.util.ArrayList;
import java.util.List;

import com.yeyunlin.info.FoodInfo;
import com.yeyunlin.util.Util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private DatabaseHelper dHelper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		dHelper = new DatabaseHelper(context);
		db = dHelper.getWritableDatabase();
	}

	public void addfoodlist(List<FoodInfo> foodInfos) {
		db.beginTransaction();
		String sql = "insert into food values (?,?,?,?)";
		try {
			for (FoodInfo foodInfo : foodInfos) {
				db.execSQL(sql,
						new Object[] { foodInfo.getNumber(),
								foodInfo.getName(), foodInfo.getPrice(),
								foodInfo.getType() });
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	public void addfood(FoodInfo foodInfo) {
		String sql = "insert into food values (?,?,?,?)";
		db.execSQL(sql, new Object[] { foodInfo.getNumber(),
				foodInfo.getName(), foodInfo.getPrice(), foodInfo.getType() });
	}
	
	public boolean queryFood(int number){
		String sql = "select * from food where number = "+number+"";
		Cursor cursor = db.rawQuery(sql, null);
		return cursor.moveToNext();
	}

	public List<FoodInfo> queryFoods() {
		List<FoodInfo> foodInfos = new ArrayList<FoodInfo>();
		String sql = "select * from food";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			FoodInfo foodInfo = new FoodInfo();
			foodInfo.setNumber(cursor.getInt(cursor
					.getColumnIndex(Util.FOOD_NUMBER)));
			foodInfo.setName(cursor.getString(cursor
					.getColumnIndex(Util.FOOD_NAME)));
			foodInfo.setPrice(cursor.getInt(cursor
					.getColumnIndex(Util.FOOD_PRICE)));
			foodInfo.setType(cursor.getString(cursor
					.getColumnIndex(Util.FOOD_TYPE)));
			foodInfos.add(foodInfo);
		}
		return foodInfos;
	}
	
	public void clearFoods(){
		String sql = " delete from food ";
		db.execSQL(sql);
	}
	
	public void closeDB(){
		db.close();
	}
}
