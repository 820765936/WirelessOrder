package com.yeyunlin.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yeyunlin.util.Util;
import com.yeyunlin.wirelessorder.DataApplication;
import com.yeyunlin.wirelessorder.LoginActivity;
import com.yeyunlin.wirelessorder.MainActivity;
import com.yeyunlin.wirelessorder.R;

/**
 * @date 2015/5/6
 * @author yeyunlin
 * @description 侧边栏菜单
 */
public class LeftFragment extends Fragment implements OnClickListener {
	private SharedPreferences mSharedPreferences;

	private RelativeLayout mUserLayout;
	private TextView mUserNameView;
	private View mOrderView;
	private View mChangeDeskView;
	private View mCollectView;
	private View mBillView;
	private View mReviewsView;
	private View mMemberView;
	private DataApplication mDataApplication;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mSharedPreferences = getActivity().getSharedPreferences(
				Util.SHARE_NAME, Context.MODE_WORLD_READABLE);
		mDataApplication = (DataApplication) getActivity().getApplication();
		View view = inflater.inflate(R.layout.layout_menu, null);
		findViews(view);
		return view;
	}

	private void initUi() {
		String userName = mSharedPreferences.getString(Util.USER_NAME, "未登录");
		mUserNameView.setText(userName);
		if (!Util.isLanded(getActivity()) && mDataApplication.isFirstLogin()) {
			mDataApplication.setFirstLogin(false);
			showLandDialog("确认登录吗？","登录享优惠");
		}
	}

	@Override
	public void onResume() {
		initUi();
		super.onResume();
	}

	public void findViews(View view) {
		mUserLayout = (RelativeLayout) view.findViewById(R.id.menu_user);
		mUserNameView = (TextView) view.findViewById(R.id.menu_user_name);
		mOrderView = view.findViewById(R.id.menu_title_order);
		mChangeDeskView = view.findViewById(R.id.menu_title_changedesk);
		mCollectView = view.findViewById(R.id.menu_title_collect);
		mBillView = view.findViewById(R.id.menu_title_bill);
		mReviewsView = view.findViewById(R.id.menu_title_reviews);
		mMemberView = view.findViewById(R.id.menu_title_member);

		mUserLayout.setOnClickListener(this);
		mOrderView.setOnClickListener(this);
		mChangeDeskView.setOnClickListener(this);
		mCollectView.setOnClickListener(this);
		mBillView.setOnClickListener(this);
		mReviewsView.setOnClickListener(this);
		mMemberView.setOnClickListener(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		Fragment newContent = null;
		String title = null;
		switch (v.getId()) {
		case R.id.menu_user:
			onLand();
			break;
		case R.id.menu_title_order:
			newContent = new OrderFragment();
			title = getString(R.string.menu_title_order);
			break;
		case R.id.menu_title_changedesk:
			newContent = new DeskChangeFragment();
			title = getString(R.string.menu_title_changedesk);
			break;
		case R.id.menu_title_collect:
			newContent = new FoodMenuFragment();
			title = getString(R.string.menu_title_collect);
			break;
		case R.id.menu_title_bill:
			newContent = new BillMenuFragment();
			title = getString(R.string.menu_title_bill);
			break;
		case R.id.menu_title_reviews:
			newContent = new ReviewFragment();
			title = getString(R.string.menu_title_reviews);
			break;
		case R.id.menu_title_member:
			newContent = new MemberFragment();
			title = getString(R.string.menu_title_member);
			break;
		default:
			break;
		}
		if (newContent != null) {
			switchFragment(newContent, title);
		}
	}

	/**
	 * 切换fragment
	 * 
	 * @param fragment
	 */
	private void switchFragment(Fragment fragment, String title) {
		if (getActivity() == null) {
			return;
		}
		if (getActivity() instanceof MainActivity) {
			MainActivity fca = (MainActivity) getActivity();
			fca.switchConent(fragment, title);
		}
	}

	private void onLand() {
		String userAccount = mSharedPreferences
				.getString(Util.USER_ACCOUNT, "");
		if (userAccount.equals("")) {
			swichActivity();
		} else {
			showLandDialog("重新登录吗？","重新登录");
		}
	}

	private void showLandDialog(String message , String title) {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setMessage(message).setTitle(title);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Util.clearSharepreference(mSharedPreferences);
				swichActivity();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private void swichActivity() {
		Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
		startActivity(loginIntent);
	}
}
