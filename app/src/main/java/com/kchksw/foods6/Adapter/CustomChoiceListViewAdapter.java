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

/**
 * Created by KimChanHyup on 2017-05-07.
 */
public class CustomChoiceListViewAdapter extends BaseAdapter implements Filterable {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private Context context;
    private ArrayList<User> arrData;
    private LayoutInflater inflater;

    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    private ArrayList<User> filteredItemList;


    Filter listFilter;

    // ListViewAdapter의 생성자
    public CustomChoiceListViewAdapter(Context c, ArrayList<User> arr) {
        // mTypeface = Typeface.createFromAsset(c.getAssets(),
        // "fonts/BMHANNA_11yrs_ttf.ttf");
        this.context = c;
        this.arrData = arr;
        inflater = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        filteredItemList = arrData;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return filteredItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_listview_info_friends_for_group, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView image = (ImageView) convertView.findViewById(R.id.imageView1);
        Picasso.with(context).load("https://graph.facebook.com/" + filteredItemList.get(position).getId() + "/picture?width=500&height=500&type=normal")
                .placeholder(R.drawable.img_user_default)
                .resize(400, 400)
                .into(image, new UserRefresher(filteredItemList.get(position), image));

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;
        TextView textTextView = (TextView) convertView.findViewById(R.id.textView1) ;



        // 아이템 내 각 위젯에 데이터 반영
     //   iconImageView.setImageBitmap(filteredItemList.get(position).getImage());
        textTextView.setText(filteredItemList.get(position).getName());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public User getItem(int position) {
        return filteredItemList.get(position) ;
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
