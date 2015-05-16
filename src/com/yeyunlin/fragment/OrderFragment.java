package com.yeyunlin.fragment;

import com.yeyunlin.wirelessorder.FoodChooseActivtiy;
import com.yeyunlin.wirelessorder.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class OrderFragment extends Fragment{
	private TextView mBasicfoodTextView;
	private TextView mColddishTextView;
	private TextView mCookingTextView;
	private TextView mStewTextView;
	private TextView mSoupTextView;
	private TextView mPanTextView;
	private TextView mSeafoodTextView;
	private TextView mDrinkTextView;
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentOrderView = inflater.inflate(R.layout.content_order, null);
		findView(contentOrderView);
		bindListener();
		return contentOrderView;
	}
	
	private void findView(View view){
		mBasicfoodTextView = (TextView) view.findViewById(R.id.order_basicfood);
		mColddishTextView = (TextView) view.findViewById(R.id.order_colddish);
		mCookingTextView = (TextView) view.findViewById(R.id.order_cooking);
		mStewTextView = (TextView) view.findViewById(R.id.order_stew);
		mSoupTextView = (TextView) view.findViewById(R.id.order_soup);
		mPanTextView = (TextView) view.findViewById(R.id.order_pan);
		mSeafoodTextView = (TextView) view.findViewById(R.id.order_seafood);
		mDrinkTextView = (TextView) view.findViewById(R.id.order_drink);
	}
	
	private void bindListener(){
		Listener clickListener = new Listener();
		mBasicfoodTextView.setOnClickListener(clickListener);
		mColddishTextView.setOnClickListener(clickListener);
		mCookingTextView.setOnClickListener(clickListener);
		mStewTextView.setOnClickListener(clickListener);
		mSoupTextView.setOnClickListener(clickListener);
		mPanTextView.setOnClickListener(clickListener);
		mSeafoodTextView.setOnClickListener(clickListener);
		mDrinkTextView.setOnClickListener(clickListener);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	private class Listener implements OnClickListener{

		@Override
		public void onClick(View view) {
			Intent foodChooseAvtivity = new Intent(getActivity(),FoodChooseActivtiy.class);
			foodChooseAvtivity.putExtra("title", ((TextView) view).getText().toString());
			startActivity(foodChooseAvtivity);
		}
	}
}
