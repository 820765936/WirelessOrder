package com.yeyunlin.wirelessorder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.yeyunlin.fragment.LeftFragment;
import com.yeyunlin.fragment.OrderFragment;

/**
 * @date 2014/5/6
 * @author yeyunlin
 * @description 主界面
 */
public class MainActivity extends SlidingFragmentActivity implements
		OnClickListener {
	private ImageView mTopButton;
	private TextView mTopTextView;
	private Fragment mContentFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById();
		setClickListener();
		initSlidingMenu(savedInstanceState);
	}

	private void findViewById() {
		mTopButton = (ImageView) findViewById(R.id.top_button);
		mTopTextView = (TextView) findViewById(R.id.top_textview);
	}

	private void setClickListener() {
		mTopButton.setOnClickListener(this);
	}

	private void initSlidingMenu(Bundle savedInstanceState) {
		mContentFragment = savedInstanceState != null ? getSupportFragmentManager()
				.getFragment(savedInstanceState, "mContentFragment")
				: new OrderFragment();

		// 设置左侧滑动菜单
		setBehindContentView(R.layout.menu_frame_left);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new LeftFragment()).commit();
		switchConent(mContentFragment, "点餐");
		
		// 实例化滑动菜单对象
		SlidingMenu sm = getSlidingMenu();
		// 设置可以左右滑动的菜单
		sm.setMode(SlidingMenu.LEFT);
		// 设置滑动阴影的宽度
		sm.setShadowWidthRes(R.dimen.shadow_width);
		// 设置滑动菜单阴影的图像资源
		sm.setShadowDrawable(null);
		// 设置滑动菜单视图的宽度
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		sm.setFadeDegree(0.35f);
		// 设置触摸屏幕的模式,这里设置为全屏
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// 设置下方视图的在滚动时的缩放比例
		sm.setBehindScrollScale(0.0f);
	}

	@Override
	public void onClick(View arg0) {
		toggle();
	}

	public void switchConent(Fragment fragment, String title) {
		mContentFragment = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
		mTopTextView.setText(title);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContentFragment);
		
	}
}
