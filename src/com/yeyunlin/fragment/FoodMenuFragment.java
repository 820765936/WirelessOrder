package com.yeyunlin.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.yeyunlin.adapter.FoodChooseAdapter;
import com.yeyunlin.dao.DBManager;
import com.yeyunlin.info.FoodInfo;
import com.yeyunlin.util.HttpUtil;
import com.yeyunlin.util.Md5Util;
import com.yeyunlin.util.Util;
import com.yeyunlin.wirelessorder.DataApplication;
import com.yeyunlin.wirelessorder.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FoodMenuFragment extends Fragment implements OnClickListener {
	private TextView mNoOrderView;
	private ListView mFoodMenuListView;
	private Button mContainButton;
	private List<FoodInfo> mFoodInfos;
	private DataApplication mDataApp;
	private SharedPreferences mSharepreference;
	private SharedPreferences mPreference;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mSharepreference = getActivity().getSharedPreferences(
				Util.SHARE_DESK_NAME, Context.MODE_WORLD_READABLE);
		mPreference = getActivity().getSharedPreferences(Util.SHARE_NAME,
				Context.MODE_WORLD_READABLE);
		View view = inflater.inflate(R.layout.content_food, null);
		findView(view);
		initUI();
		return view;
	}

	private void initUI() {
		mDataApp = (DataApplication) getActivity().getApplication();
		mFoodInfos = mDataApp.getAllFood();
		if (mFoodInfos.size() > 0) {
			mNoOrderView.setVisibility(View.GONE);
			mContainButton.setClickable(true);
			FoodChooseAdapter adapter = new FoodChooseAdapter(getActivity(),
					mFoodInfos);
			mFoodMenuListView.setAdapter(adapter);
		}else {
			mNoOrderView.setVisibility(View.VISIBLE);
			mContainButton.setClickable(false);
		}
	}

	private void findView(View view) {
		mNoOrderView = (TextView) view.findViewById(R.id.food_Menu_noorder);
		mFoodMenuListView = (ListView) view.findViewById(R.id.food_menu_list);
		mContainButton = (Button) view.findViewById(R.id.contain);
		mContainButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.contain:
			DBManager dbManager = new DBManager(getActivity());
			if (dbManager.queryFoods().size() == 0) {
				List<FoodInfo> foodInfos = mDataApp.getAllFood();
				int totalPrice = 0;
				for (FoodInfo foodInfo : foodInfos) {
					Log.v("food", " :   " + foodInfo.getName());
					totalPrice += foodInfo.getPrice();
				}
				showContainDialog(totalPrice);
			}
			dbManager.closeDB();
			break;
		default:
			break;
		}
	}

	private void saveTotalPrice(int totalPrice) {
		Editor editor = mSharepreference.edit();
		editor.putInt(Util.PAY_PRICE, totalPrice);
		editor.commit();
	}
	
	private void saveToSqlite(){
		DBManager dbManager = new DBManager(getActivity());
		dbManager.addfoodlist(mFoodInfos);
		dbManager.closeDB();
	}

	private void showContainDialog(final int totalPrice) {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle("下单").setMessage("总价：" + totalPrice + "元，确认下单么？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						saveTotalPrice(totalPrice);
						saveToSqlite();
						mContainButton.setClickable(false);
						new Thread(runnable).start();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			String orderId = Md5Util.stringToMD5(mSharepreference.getString(
					Util.DESK_TIME, "1900"));
			String userName = mPreference.getString(Util.USER_NAME, "unknow");
			String deskId = mSharepreference.getInt(Util.DESK_NUMBER, 1) + "";
			String time = mSharepreference.getString(Util.DESK_TIME, "1900");
			String paytype = mSharepreference.getString(Util.PAY_TYPE, "0");
			for (FoodInfo foodInfo : mFoodInfos) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("orderId", orderId));
				params.add(new BasicNameValuePair("userName", userName));
				params.add(new BasicNameValuePair("foodId", foodInfo
						.getNumber() + ""));
				params.add(new BasicNameValuePair("deskId", deskId));
				params.add(new BasicNameValuePair("time", time));
				params.add(new BasicNameValuePair("paytype", paytype));

				UrlEncodedFormEntity entity1 = null;
				try {
					entity1 = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				String url = HttpUtil.BASE_URL + "OrderServlet";
				HttpPost request = HttpUtil.getHttpPost(url);
				request.setEntity(entity1);
				String result = HttpUtil.queryStringForPost(request);
				Log.v("food", "--- " + result);
			}
		}
	};
}
