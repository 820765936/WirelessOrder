package com.yeyunlin.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.LangUtils;

import com.yeyunlin.adapter.ReviewAdapter;
import com.yeyunlin.info.ReviewInfo;
import com.yeyunlin.util.HttpUtil;
import com.yeyunlin.util.JsonUtil;
import com.yeyunlin.util.Util;
import com.yeyunlin.wirelessorder.LoginActivity;
import com.yeyunlin.wirelessorder.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.AlteredCharSequence;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class ReviewFragment extends Fragment implements OnClickListener{
	private SharedPreferences mSharedPreferences;
	private ListView mReviewList;
	private ReviewAdapter mReviewAdapter;
	private EditText mContentView;
	private ImageView mSendImageView;
	List<ReviewInfo> mReviewInfos = new ArrayList<ReviewInfo>();
	private static final int SEND_SUCCESS = 1;
	private static final int GET_SUCCESS = 2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.content_review, null);
		findView(view);
		initUI();
		return view;
	}

	private void findView(View view) {
		mSharedPreferences = getActivity().getSharedPreferences(
				Util.SHARE_NAME, Context.MODE_WORLD_READABLE);
		mReviewList = (ListView) view.findViewById(R.id.review_list);
		mContentView = (EditText) view.findViewById(R.id.review_text);
		mSendImageView = (ImageView) view.findViewById(R.id.send);
		mSendImageView.setOnClickListener(this);
	}
	
	private void initUI() {
		new Thread(getContentRunnale).start();
	}

	@Override
	public void onClick(View view) {
		String reviewContent = mContentView.getText().toString().trim();
		String name = mSharedPreferences.getString(Util.USER_NAME, "");
		if (name.equals("")) {
			mContentView.setText("");
			closeSoftInput();
			showDialog();
			return;
		}
		if (reviewContent.equals("")) {
			Toast.makeText(getActivity(), "请输入评论！", Toast.LENGTH_SHORT).show();
		}else {
			new Thread(runnable).start();
			onReview();
		}
	}
	
	public void showDialog(){
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle("提示信息").setMessage("评论前需要先登录，确认登录吗？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				Intent intent = new Intent(getActivity(),LoginActivity.class);
				startActivity(intent);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		}).show();
	}
	
	private void onReview(){
		String name = mSharedPreferences.getString(Util.USER_NAME, "unknow");
		String reviewContent = mContentView.getText().toString().trim();
		String time = Util.getCurrentTime();
		ReviewInfo reviewInfo = new ReviewInfo();
		reviewInfo.setName(name);
		reviewInfo.setTime(time);
		reviewInfo.setContent(reviewContent);
		mReviewInfos.add(reviewInfo);
		mReviewAdapter.notifyDataSetChanged();
		mContentView.setText("");
		closeSoftInput();
	}
	
	private void closeSoftInput(){
		View view = getActivity().getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
	}
	
	Runnable getContentRunnale = new Runnable() {
		
		@Override
		public void run() {
			String url = HttpUtil.BASE_URL + "GetReviewsServlet";
			String result = HttpUtil.queryStringForGet(url);
			mReviewInfos = JsonUtil.getReviewFromJson(result);
			if (mReviewInfos.size() > 0) {
				Message message = handler.obtainMessage();
				message.what = GET_SUCCESS;
				handler.sendMessage(message);
			}
		}
	};
	
	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			String name = mSharedPreferences.getString(Util.USER_NAME, "unknow");
			String reviewContent = mContentView.getText().toString().trim();
			String time = Util.getCurrentTime();
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userName", name));
			params.add(new BasicNameValuePair("reviewContent", reviewContent));
			params.add(new BasicNameValuePair("time", time));
			
			UrlEncodedFormEntity entity1 = null;
			try {
				entity1 = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			String url = HttpUtil.BASE_URL + "ReviewServlet";
			HttpPost request = HttpUtil.getHttpPost(url);
			request.setEntity(entity1);
			String result = HttpUtil.queryStringForPost(request);
			Message message = handler.obtainMessage();
			message.what = SEND_SUCCESS;
			handler.sendMessage(message);
			Log.v("food", "--- " + result);
		}
	};
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SEND_SUCCESS) {
				
			} else {
				mReviewAdapter = new ReviewAdapter(getActivity(), mReviewInfos);
				mReviewList.setAdapter(mReviewAdapter);
			}
			super.handleMessage(msg);
		}
	};
}
