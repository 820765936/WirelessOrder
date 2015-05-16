package com.yeyunlin.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yeyunlin.wirelessorder.R;

public class MemberFragment extends Fragment {
	private GridView mMemberGridView;
	private TextView mContentTextView;
	private int[] mIcons = { R.drawable.transport, R.drawable.exchage,
			R.drawable.review, R.drawable.sale, R.drawable.birthday,
			R.drawable.gift };
	private int[] mContent = {R.string.member_transport, R.string.member_exchage,
			R.string.member_review, R.string.member_sale, R.string.member_birthday,
			R.string.member_gift};
	private String[] mInformations = { "谢绝运费", "积分兑换", "评论积分",
			"会员折扣", "生日礼物", "神秘礼物"};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.content_member, null);
		iniUi(view);
		return view;
		
	}

	private void iniUi(View view) {
		mMemberGridView = (GridView) view.findViewById(R.id.menber_gridview);
		mContentTextView = (TextView) view.findViewById(R.id.member_content);
		mContentTextView.setText(getActivity().getResources().getString(mContent[0]));
		List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < mIcons.length; i++) {
			Map<String, Object> item = new HashMap();
			item.put("icon", mIcons[i]);
			item.put("information", mInformations[i]);
			contents.add(item);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),
				contents, R.layout.menber_list_item, new String[] { "icon",
						"information" }, new int[] { R.id.member_icon,
						R.id.member_information });
		mMemberGridView.setAdapter(simpleAdapter);
		mMemberGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long arg3) {
				mContentTextView.setText(getActivity().getResources().getString(mContent[position]));
			}
		});
	}
}
