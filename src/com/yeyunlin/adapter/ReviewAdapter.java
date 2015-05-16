package com.yeyunlin.adapter;

import java.util.List;

import com.yeyunlin.info.ReviewInfo;
import com.yeyunlin.wirelessorder.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ReviewAdapter extends BaseAdapter {
	private List<ReviewInfo> mReviewInfos;
	private LayoutInflater mInflater;
	
	public ReviewAdapter(Context context,List<ReviewInfo> reviewInfos) {
		mReviewInfos = reviewInfos;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mReviewInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHodler viewHodler;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.review_list_item, null);
			viewHodler = new ViewHodler();
			viewHodler.nameView = (TextView) convertView.findViewById(R.id.review_name);
			viewHodler.timeView = (TextView) convertView.findViewById(R.id.review_time);
			viewHodler.contentView = (TextView) convertView.findViewById(R.id.review_content);
			convertView.setTag(viewHodler);
		}else {
			viewHodler = (ViewHodler) convertView.getTag();
		}
		viewHodler.nameView.setText(mReviewInfos.get(position).getName());
		viewHodler.timeView.setText(mReviewInfos.get(position).getTime());
		viewHodler.contentView.setText(mReviewInfos.get(position).getContent());
		return convertView;
	}

	public class ViewHodler{
		public TextView nameView;
		public TextView timeView;
		public TextView contentView;
	}
}
