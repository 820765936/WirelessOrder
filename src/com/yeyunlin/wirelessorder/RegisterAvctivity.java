package com.yeyunlin.wirelessorder;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yeyunlin.util.HttpUtil;

public class RegisterAvctivity extends Activity implements OnClickListener {
	private EditText mNameView;
	private EditText mAccountView;
	private EditText mPasswordView;
	private ImageView mBackImageView;
	private Button mRegisterButton;
	private boolean isNameExist = false;
	private boolean isAccountExist = false;
	private String mName;
	private String mAccount;
	private String mPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		findView();
	}

	private void findView() {
		mNameView = (EditText) findViewById(R.id.register_name);
		mAccountView = (EditText) findViewById(R.id.register_account);
		mPasswordView = (EditText) findViewById(R.id.register_password);
		mRegisterButton = (Button) findViewById(R.id.register_land);
		mBackImageView = (ImageView) findViewById(R.id.top_back);
		mRegisterButton.setOnClickListener(this);
		mBackImageView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.register_land:
			mName = mNameView.getText().toString().trim();
			mAccount = mAccountView.getText().toString().trim();
			mPassword = mPasswordView.getText().toString().trim();
			if (mName.equals("") || mAccount.equals("") || mPassword.equals("")) {
				showToast("不能为空！");
			} else {
				new Thread(checkExistRunnable).start();
			}
			break;
		case R.id.top_back:
			finish();
			break;
		default:
			break;
		}
	}

	private void refreshOnFail() {
		mNameView.setText("");
		mAccountView.setText("");
		mPasswordView.setText("");
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	private void showToast(String content) {
		Toast.makeText(RegisterAvctivity.this, content, Toast.LENGTH_SHORT)
				.show();
	}

	Runnable checkExistRunnable = new Runnable() {

		@Override
		public void run() {
			String queryString = "name=" + mName;
			String url = HttpUtil.BASE_URL + "TestNameServlet?" + queryString;
			if (HttpUtil.queryStringForPost(url).equals("1")) {
				isNameExist = true;
			}

			queryString = "account=" + mAccount;
			url = HttpUtil.BASE_URL + "TestAccountServlet?" + queryString;
			if (HttpUtil.queryStringForPost(url).equals("1")) {
				isAccountExist = true;
			}
			Message message = handler.obtainMessage();
			handler.sendMessage(message);
		}
	};

	Runnable registerRunnable = new Runnable() {

		@Override
		public void run() {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", mName));
			params.add(new BasicNameValuePair("account", mAccount));
			params.add(new BasicNameValuePair("password", mPassword));
			params.add(new BasicNameValuePair("integral", "0"));
			params.add(new BasicNameValuePair("icon", "default"));

			UrlEncodedFormEntity entity1 = null;
			try {
				entity1 = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			String url = HttpUtil.BASE_URL + "RegisterServlet";
			HttpPost request = HttpUtil.getHttpPost(url);
			request.setEntity(entity1);
			String result = HttpUtil.queryStringForPost(request);
			Log.v("food", "--- " + result);
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (isNameExist) {
				showToast("该昵称已存在！");
			} else if (isAccountExist) {
				showToast("该账号已存在！");
			} else {
				new Thread(registerRunnable).start();
				showToast("注册成功！");
			}
			refreshOnFail();
		};
	};
}
