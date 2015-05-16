package com.yeyunlin.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.yeyunlin.dao.DBManager;
import com.yeyunlin.info.DeskInfo;
import com.yeyunlin.util.HttpUtil;
import com.yeyunlin.util.JsonUtil;
import com.yeyunlin.util.Md5Util;
import com.yeyunlin.util.Util;
import com.yeyunlin.wirelessorder.DataApplication;
import com.yeyunlin.wirelessorder.R;

public class DeskChangeFragment extends Fragment implements OnItemClickListener{
	private DataApplication mDataApplication;
	private SharedPreferences mSharedPreferences;
	private ListView mDeskListView;
	private List<DeskInfo> mDeskInfos = new ArrayList<DeskInfo>();
	private int oldDesk;
	private int newDesk;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.content_changedesk, null);
		initUi(view);
		return view;
	}
	
	private void initUi(View view) {
		mDeskListView = (ListView) view.findViewById(R.id.desk_list);
		mDeskListView.setOnItemClickListener(this);
		mDataApplication = (DataApplication) getActivity().getApplication();
		mSharedPreferences = getActivity().getSharedPreferences(Util.SHARE_DESK_NAME, Context.MODE_WORLD_READABLE);
		if (mSharedPreferences.getInt(Util.DESK_NUMBER, -1) < 0) {
			Toast.makeText(getActivity(), "还没开台，无法转台！", Toast.LENGTH_SHORT).show();
		} else {
			new Thread(runnable).start();
		}
	}

	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			String url = HttpUtil.BASE_URL + "UnuseDeskServley";
			String result = HttpUtil.queryStringForGet(url);
			Message message = handler.obtainMessage();
			message.obj = result;
			handler.sendMessage(message);
		}
	};
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			String result = (String) msg.obj;
			mDeskInfos = JsonUtil.getdeskFromJson(result);
			List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < mDeskInfos.size(); i++) {
				Map<String, Object> item = new HashMap();
				item.put("id", mDeskInfos.get(i).getId()+"");
				item.put("type", getActivity().getResources().getString(R.string.changedesk_unuse));
				contents.add(item);
			}
			SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),
					contents, R.layout.desk_list_item, new String[] { "id",
							"type" }, new int[] { R.id.desk_number, R.id.desk_type });
			mDeskListView.setAdapter(simpleAdapter);
			super.handleMessage(msg);
		}
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		oldDesk = mSharedPreferences.getInt(Util.DESK_NUMBER, -1);
		newDesk = mDeskInfos.get(position).getId();
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle("换台").setMessage("确定将"+oldDesk+"号桌换至"+newDesk+"号桌吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				new Thread(upadateDeskRunnable).start();
				Toast.makeText(getActivity(), "换台成功！", Toast.LENGTH_SHORT).show();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		}).show();
	}
	
	private boolean hasOrdered(){
		DBManager dbManager = new DBManager(getActivity());
		return dbManager.queryFoods().size() > 0 ? true :false;
	}
	
	Runnable upadateDeskRunnable = new Runnable() {
		
		@Override
		public void run() {
			Editor editor = mSharedPreferences.edit();
			editor.putInt(Util.DESK_NUMBER, newDesk);
			editor.commit();
			String queryString = "oldDesk=" + oldDesk + "&newDesk="
					+ newDesk;
			String url = HttpUtil.BASE_URL + "UpdateDeskServlet?" + queryString;
			HttpUtil.queryStringForPost(url);
			if (hasOrdered()) {
				String orderId = Md5Util.stringToMD5(mSharedPreferences.getString(
						Util.DESK_TIME, "1900"));
				queryString = "newDesk=" + newDesk + "&orderId="
						+ orderId;
				url = HttpUtil.BASE_URL + "UpdateOrderDeskServlet?" + queryString;
				HttpUtil.queryStringForPost(url);
			}
		}
	};
}
