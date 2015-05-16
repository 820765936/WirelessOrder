package com.yeyunlin.wirelessorder;

import com.yeyunlin.info.UserInfo;
import com.yeyunlin.util.HttpUtil;
import com.yeyunlin.util.JsonUtil;
import com.yeyunlin.util.Md5Util;
import com.yeyunlin.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private ImageView mBackView;
	private TextView mRegisterView;
	private EditText mAccountText;
	private EditText mPasswordText;
	private Button mLandButton;
	private String mAccount;
	private String mPassword;
	private SharedPreferences mSharedPreferences;

	Handler loginHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String result = (String) msg.obj;
			if (result.equals("0")) {
				mAccountText.setText("");
				mPasswordText.setText("");
				Toast.makeText(LoginActivity.this, "账号或密码错误！", Toast.LENGTH_SHORT).show();
			} else {
				UserInfo userInfo = JsonUtil.getUserFromJson(result);
				Log.v("food", " --- :  "+userInfo.getName());
				Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
				saveUserInformation(userInfo);
				finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mSharedPreferences = getSharedPreferences(Util.SHARE_NAME, Context.MODE_WORLD_READABLE);

		findView();
		bindListener();
	}

	private void findView() {
		mBackView = (ImageView) findViewById(R.id.top_back);
		mRegisterView = (TextView) findViewById(R.id.top_register);
		mAccountText = (EditText) findViewById(R.id.login_account);
		mPasswordText = (EditText) findViewById(R.id.login_password);
		mLandButton = (Button) findViewById(R.id.login_land);
	}

	private void bindListener() {
		Listener listener = new Listener();
		mBackView.setOnClickListener(listener);
		mRegisterView.setOnClickListener(listener);
		mLandButton.setOnClickListener(listener);
	}

	private class Listener implements OnClickListener {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.top_back:
				finish();
				break;
			case R.id.top_register:
				Intent registerIntent = new Intent(LoginActivity.this,RegisterAvctivity.class);
				startActivity(registerIntent);
				break;
			case R.id.login_land:
				checkPassword();
				break;
			default:
				break;
			}

		}
	}

	private void checkPassword() {
		mAccount = mAccountText.getText().toString().trim();
		mPassword = mPasswordText.getText().toString().trim();
		if ("".equals(mAccount) || "".equals(mPassword)) {
			Toast.makeText(this, "账号和密码不能为空！", Toast.LENGTH_SHORT).show();
		} else {
			new Thread(runnable).start();
		}
	}
	
	private void saveUserInformation(UserInfo userInfo){
		Editor editor = mSharedPreferences.edit();
		editor.putString(Util.USER_NAME, userInfo.getName());
		editor.putString(Util.USER_ACCOUNT, userInfo.getAccount());
		editor.putString(Util.USER_PASSWORD, Md5Util.stringToMD5(userInfo.getPassword()));
		editor.putInt(Util.USER_INTEGRAL, userInfo.getIntegral());
		editor.putString(Util.USER_ICON,userInfo.getIcon());
		editor.commit();
	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			String queryString = "account=" + mAccount + "&password="
					+ mPassword;
			String url = HttpUtil.BASE_URL + "LoginServlet?" + queryString;
			String result = HttpUtil.queryStringForPost(url);
			Log.v("food", "   ----   " + result);
			Message message = loginHandler.obtainMessage();
			message.obj = result;
			loginHandler.sendMessage(message);
		}
	};
}
