package com.kchksw.foods6.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kchksw.foods6.R;
import com.kchksw.foods6.Refresher.UserRefresher;
import com.kchksw.foods6.etc.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ShareRequestAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<User> arrData;
	private LayoutInflater inflater;
	private int selectBtn;


	// private Typeface mTypeface;

	public ShareRequestAdapter(Context c, ArrayList<User> arr, int selectBtn) {
		// mTypeface = Typeface.createFromAsset(c.getAssets(),
		// "fonts/BMHANNA_11yrs_ttf.ttf");
		this.context = c;
		this.arrData = arr;
		this.selectBtn = selectBtn;

		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return arrData.size();
	}

	public Object getItem(int position) {
		return arrData.get(position).getName();
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.layout_listview_request, parent, false);
		}

		ImageView image = (ImageView) convertView.findViewById(R.id.imageView);
		Picasso.with(context).load("https://graph.facebook.com/" + arrData.get(position).getId() + "/picture?width=500&height=500&type=normal")
				.placeholder(R.drawable.img_user_default)
				.resize(400, 400)
				.into(image, new UserRefresher(arrData.get(position), image));




		TextView name = (TextView) convertView.findViewById(R.id.name);
		if(selectBtn == 0) {
			name.setText(arrData.get(position).getName() + "님에게\n 공유 요청을 보낸 상태입니다.");
		}
		if(selectBtn == 1) {
			name.setText(arrData.get(position).getName() + "님의\n 공유 요청입니다.");
		}




		
		return convertView;
	}

}