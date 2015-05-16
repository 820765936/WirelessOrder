package com.yeyunlin.wirelessorder;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yeyunlin.adapter.FoodChooseAdapter;
import com.yeyunlin.info.FoodInfo;
import com.yeyunlin.util.HttpUtil;
import com.yeyunlin.util.JsonUtil;
import com.yeyunlin.util.Util;

public class FoodChooseActivtiy extends Activity {
	private ImageView mTopButton;
	private TextView mTopTextView;
	private TextView mFoundingTextView;
	private ListView mFoodListView;
	private String mSelectDeskNumber;
	private SharedPreferences mSharedPreferences;
	private List<FoodInfo> mFoodInfos;
	private DataApplication mDataApplication;

	private static final int FOOD_CHOOSE = 660;
	private static final int FOOD_DESK = 661;

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FOOD_CHOOSE:
				mFoodInfos = JsonUtil
						.getFoodFromJson((String) msg.obj);
				FoodChooseAdapter adapter = new FoodChooseAdapter(
						FoodChooseActivtiy.this, mFoodInfos);
				mFoodListView.setAdapter(adapter);
				mFoodListView.setOnItemClickListener(new ItemClick());
				break;
			case FOOD_DESK:
				int deskFlag = Integer.parseInt((String) msg.obj);
				if (deskFlag == 0) {
					showToast("开台失败！");
				}else {
					showToast("开台成功");
					Editor editor = mSharedPreferences.edit();
					editor.putInt(Util.DESK_NUMBER, Integer.parseInt(mSelectDeskNumber));
					editor.putString(Util.DESK_TIME, Util.getCurrentTime());
					editor.commit();
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_foodchoose);

		mSharedPreferences = getSharedPreferences(Util.SHARE_DESK_NAME,
				Context.MODE_WORLD_READABLE);

		findView();
		bindListener();
		initView();
		new Thread(runnable).start();
	}

	private void findView() {
		mDataApplication = (DataApplication) getApplication();
		mTopButton = (ImageView) findViewById(R.id.top_button);
		mTopTextView = (TextView) findViewById(R.id.top_textview);
		mFoundingTextView = (TextView) findViewById(R.id.top_founding);
		mFoodListView = (ListView) findViewById(R.id.food_list);
	}

	private void bindListener() {
		Listener clickListener = new Listener();
		mTopButton.setOnClickListener(clickListener);
		mFoundingTextView.setOnClickListener(clickListener);
	}

	private void initView() {
		String title = getIntent().getStringExtra("title");
		mTopTextView.setText(title);
	}

	private class Listener implements OnClickListener {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.top_button:
				finish();
				break;
			case R.id.top_founding:
				findDesk();
				break;
			default:
				break;
			}
		}
	}

	private void findDesk() {
		if (mSharedPreferences.getInt(Util.DESK_NUMBER, -1) < 0) {
			showfindDeskDialog();
		} else {
			showToast("已开台，无需重复开台！");
		}
	}

	private void showfindDeskDialog() {
		AlertDialog.Builder builder = new Builder(this);
		final EditText editText = new EditText(this);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setTitle("选择桌子编号")
				.setIcon(R.drawable.btn_rating_star_on_focused_holo_dark)
				.setView(editText);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mSelectDeskNumber = editText.getText().toString().trim();
				checkDeskNumber(mSelectDeskNumber);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

	private void checkDeskNumber(String deskNumber) {
		if (deskNumber.equals("")) {
			showToast("请输入桌子编号！");
		} else {
			int number = Integer.parseInt(deskNumber);
			if (number < 1 || number > 15) {
				showToast("请输入正确编号！");
			} else {
				new Thread(findRunnable).start();
			}
		}
	}

	private void showToast(String text) {
		Toast.makeText(FoodChooseActivtiy.this, text, Toast.LENGTH_SHORT)
				.show();
	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			String queryString = "type=" + mTopTextView.getText().toString();
			String url = HttpUtil.BASE_URL + "FoodChooseServlet?" + queryString;
			String result = HttpUtil.queryStringForPost(url);
			Log.v("food", result);
			Message message = handler.obtainMessage();
			message.what = FOOD_CHOOSE;
			message.obj = result;
			handler.sendMessage(message);
		}
	};

	Runnable findRunnable = new Runnable() {

		@Override
		public void run() {
			String queryString = "deskNumber=" + mSelectDeskNumber;
			String url = HttpUtil.BASE_URL + "FindDeskServlet?" + queryString;
			String result = HttpUtil.queryStringForPost(url);
			Log.v("food", result);
			Message message = handler.obtainMessage();
			message.what = FOOD_DESK;
			message.obj = result;
			handler.sendMessage(message);
		}
	};
	
	class ItemClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int postion,
				long arg3) {
			if (mSharedPreferences.getInt(Util.DESK_NUMBER, -1) < 0) {
				showToast("点菜前请先开台");
			} else {
				FoodInfo foodInfo = mFoodInfos.get(postion);
				String foodName = foodInfo.getName();
				showToast(foodName+"已点");
				if (!mDataApplication.isFoodExist(foodInfo)) {
					mDataApplication.addFood(foodInfo);
				}
			}
		}
	}
	
	@Override
	protected void onPause() {
		List<FoodInfo> foodInfos = mDataApplication.getAllFood();
		for (FoodInfo foodInfo : foodInfos) {
			Log.v("food", foodInfo.getName());
		}
		super.onPause();
	}
}
