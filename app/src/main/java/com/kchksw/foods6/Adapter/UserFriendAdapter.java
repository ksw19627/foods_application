package com.kchksw.foods6.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.kchksw.foods6.R;
import com.kchksw.foods6.Refresher.UserRefresher;
import com.kchksw.foods6.etc.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class UserFriendAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<User> arrData;
    private LayoutInflater inflater;


    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    private ArrayList<User> filteredItemList;


    Filter listFilter;
    // private Typeface mTypeface;


    public UserFriendAdapter(Context c, ArrayList<User> arr) {
        // mTypeface = Typeface.createFromAsset(c.getAssets(),
        // "fonts/BMHANNA_11yrs_ttf.ttf");
        this.context = c;
        this.arrData = arr;
        inflater = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        filteredItemList = arrData;
    }

    public int getCount() {
        return filteredItemList.size();
    }


    public User getItem(int position) {
        return filteredItemList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_listview_info_friends, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.imageView);
        Picasso.with(context).load("https://graph.facebook.com/" + filteredItemList.get(position).getId() + "/picture?width=500&height=500&type=normal")
                .placeholder(R.drawable.img_user_default)
                .resize(400, 400)
                .into(image, new UserRefresher(filteredItemList.get(position), image));


        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setText(filteredItemList.get(position).getName());
        //name.setTextColor(Color.WHITE);

        // name.setTypeface(mTypeface);

        ImageView imageFollowing = (ImageView) convertView.findViewById(R.id.imageViewFollowing);
        if (filteredItemList.get(position).isFollowing())
            imageFollowing.setVisibility(View.VISIBLE);
        else
            imageFollowing.setVisibility(View.INVISIBLE);

        ImageView imageFollower = (ImageView) convertView.findViewById(R.id.imageViewFollower);
        if (filteredItemList.get(position).isFollower())
            imageFollower.setVisibility(View.VISIBLE);
        else
            imageFollower.setVisibility(View.INVISIBLE);


        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter();
        }
        return listFilter;
    }

    private class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = arrData;
                results.count = arrData.size();
            } else {
                ArrayList<User> itemList = new ArrayList<User>();
                for (User item : arrData) {
                    if (item.getName().toUpperCase().contains(constraint.toString().toUpperCase())) {
                        itemList.add(item);
                    }
                }
                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) { // update listview by filtered data list.
            filteredItemList = (ArrayList<User>) results.values; // notify
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}