package com.kchksw.foods6.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kchksw.foods6.R;
import com.kchksw.foods6.etc.Group;

import java.util.ArrayList;


public class GroupAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Group> arrData;
	private LayoutInflater inflater;


	// private Typeface mTypeface;

	public GroupAdapter(Context c, ArrayList<Group> arr) {
		// mTypeface = Typeface.createFromAsset(c.getAssets(),
		// "fonts/BMHANNA_11yrs_ttf.ttf");
		this.context = c;
		this.arrData = arr;
		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return arrData.size();
	}

	public Object getItem(int position) {
		return arrData.get(position).getGroups_name();
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.layout_listview_info_group, parent, false);
		}

	//	ImageView image = (ImageView) convertView.findViewById(R.id.image);
	//	image.setImageBitmap(arrData.get(position).getImage());

		TextView groupName = (TextView) convertView.findViewById(R.id.name);
		groupName.setText(arrData.get(position).getGroups_name());
		// name.setTypeface(mTypeface);

		TextView numberOfPeople = (TextView) convertView.findViewById(R.id.numberOfPeople);
		numberOfPeople.setText(arrData.get(position).getGroups_member_count()+"명");
		// text.setTypeface(mTypeface);

		TextView groupMembers = (TextView) convertView.findViewById(R.id.groupMember);
		groupMembers.setText(arrData.get(position).getGroups_members());

		return convertView;
	}

}