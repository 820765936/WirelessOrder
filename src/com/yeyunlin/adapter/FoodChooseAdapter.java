package com.yeyunlin.adapter;

import java.util.List;

import com.yeyunlin.info.FoodInfo;
import com.yeyunlin.wirelessorder.R;

import android.content.Context;
import android.opengl.Visibility;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FoodChooseAdapter extends BaseAdapter {
	private Context context;
	private List<FoodInfo> foodInfos;
	private LayoutInflater mInflater;

	public FoodChooseAdapter(Context context, List<FoodInfo> foodInfos) {
		this.context = context;
		this.foodInfos = foodInfos;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return foodInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.food_list_item, null);
			holder = new ViewHolder();
			holder.nameTextView = (TextView) convertView
					.findViewById(R.id.food_name);
			holder.priceTextView = (TextView) convertView
					.findViewById(R.id.food_price);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.nameTextView.setText(foodInfos.get(position).getName());
		holder.priceTextView.setText(foodInfos.get(position).getPrice()
				+ "");
		return convertView;
	}

	public class ViewHolder {
		public TextView nameTextView;
		public TextView priceTextView;
	}
}
