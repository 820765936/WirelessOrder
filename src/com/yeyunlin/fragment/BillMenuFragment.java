package com.yeyunlin.fragment;

import com.yeyunlin.dao.DBManager;
import com.yeyunlin.util.HttpUtil;
import com.yeyunlin.util.Md5Util;
import com.yeyunlin.util.Util;
import com.yeyunlin.wirelessorder.DataApplication;
import com.yeyunlin.wirelessorder.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BillMenuFragment extends Fragment {
	private LinearLayout mBillInformation;
	private TextView mPriceView;
	private TextView mTimeView;
	private TextView mNoOrderView;
	private Button mCashButton;
	private Button mAlipayButton;
	private SharedPreferences mSharedpreference;
	private DataApplication mData;
	private String mOrderId;
	private int mDeskid;
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			String result = (String) msg.obj;
			Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
			mCashButton.setClickable(false);
			mAlipayButton.setClickable(false);
			super.handleMessage(msg);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.content_bill, null);
		mSharedpreference = getActivity().getSharedPreferences(
				Util.SHARE_DESK_NAME, Context.MODE_WORLD_READABLE);
		mData = (DataApplication) getActivity().getApplication();
		findView(view);
		initView();
		return view;
	}

	private void initView() {
		DBManager dbManager = new DBManager(getActivity());
		if (dbManager.queryFoods().size() > 0) {
			mBillInformation.setVisibility(View.VISIBLE);
			mNoOrderView.setVisibility(View.GONE);
			mCashButton.setClickable(true);
			mAlipayButton.setClickable(true);
			String time = mSharedpreference.getString(Util.DESK_TIME, "1900年1月1日");
			int TotalPricer = mSharedpreference.getInt(Util.PAY_PRICE, 888);
			String timeFormat = getActivity().getResources().getString(
					R.string.bill_time);
			String finalTime = String.format(timeFormat, time);
			mTimeView.setText(finalTime);
			String priceFormat = getActivity().getResources().getString(
					R.string.bill_price);
			String finalPrice = String.format(priceFormat, TotalPricer + "");
			mPriceView.setText(finalPrice);
		}else {
			mBillInformation.setVisibility(View.GONE);
			mNoOrderView.setVisibility(View.VISIBLE);
			mCashButton.setClickable(false);
			mAlipayButton.setClickable(false);
		}
		dbManager.closeDB();
	}

	private void findView(View view) {
		mBillInformation = (LinearLayout) view.findViewById(R.id.bill_information);
		mPriceView = (TextView) view.findViewById(R.id.bill_price);
		mTimeView = (TextView) view.findViewById(R.id.bill_time);
		mNoOrderView = (TextView) view.findViewById(R.id.bill_noorder);
		mCashButton = (Button) view.findViewById(R.id.bill_cash);
		mAlipayButton = (Button) view.findViewById(R.id.bill_alipay);

		ClickListener clickListener = new ClickListener();
		mCashButton.setOnClickListener(clickListener);
		mAlipayButton.setOnClickListener(clickListener);
	}

	class ClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.bill_cash:
				onPayByCash();
				break;
			case R.id.bill_alipay:
				//onPayByAlipay();
				break;
			default:
				break;
			}
		}
	}

	private void onPayByCash() {
		mOrderId = Md5Util.stringToMD5(mSharedpreference.getString(Util.DESK_TIME, "1900"));
		mDeskid = mSharedpreference.getInt(Util.DESK_NUMBER, 1);
		new Thread(runnable).start();
		clearData();
	}
	
	private void clearData(){
		Editor deskEditor = mSharedpreference.edit();
		deskEditor.clear();
		deskEditor.commit();
		mData.removeAllFood();
	}
	
	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			String queryString = "orderId=" + mOrderId + "&deskId="
					+ mDeskid;
			String url = HttpUtil.BASE_URL + "UpdataOrderServlet?" + queryString;
			String result = HttpUtil.queryStringForPost(url);
			Log.v("food", result);
			DBManager dbManager = new DBManager(getActivity());
			dbManager.clearFoods();
			dbManager.closeDB();
			Message message = handler.obtainMessage();
			message.obj = result;
			handler.sendMessage(message);
		}
	};
}
