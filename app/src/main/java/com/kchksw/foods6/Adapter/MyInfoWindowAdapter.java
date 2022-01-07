package com.kchksw.foods6.Adapter;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.kchksw.foods6.Activity.MapLoadActivity;
import com.kchksw.foods6.Model.Model;
import com.kchksw.foods6.R;
import com.kchksw.foods6.Refresher.InfoWindowRefresher;
import com.kchksw.foods6.etc.Util;
import com.squareup.picasso.Picasso;

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    MapLoadActivity mapLoadActivity;
    public static MyInfoWindowAdapter myInfoWindowAdapter;

    private View myContentsView;

    RatingBar rbRatingbar;
    TextView tvStore;
    public ImageView ivImage;

    public MyInfoWindowAdapter(MapLoadActivity mapLoadActivity) {
        myInfoWindowAdapter = this;
        this.mapLoadActivity = mapLoadActivity;


        myContentsView = mapLoadActivity.getLayoutInflater().inflate(R.layout.custom_marker_ballon, null);

        rbRatingbar = ((RatingBar) myContentsView.findViewById(R.id.ratingbar));
        tvStore = ((TextView) myContentsView.findViewById(R.id.store));
        ivImage = ((ImageView) myContentsView.findViewById(R.id.image));

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

              Log.d("Check", "getInfoContents 진입");
        int index = -1;
        for (int i = 0; i < Model.myMarkerItems.size(); i++) {
            if (("m" + Model.myMarkerItems.get(i).getId()).equals(marker.getId())) {
                index = i;

                break;
            }
        }
        Log.e("마커아이템 크기",Model.myMarkerItems.size()+"");

        rbRatingbar.setRating(Model.myMarkerItems.get(index).getRating());
        tvStore.setText(Model.myMarkerItems.get(index).getStore());


        Picasso.with(mapLoadActivity).load(Util.SERVER_ADDRESS + "image/" + Model.myMarkerItems.get(index).getStringImage())
                .placeholder(R.drawable.ic_default_image)
                .resize(400, 400)
                .into(ivImage, new InfoWindowRefresher(marker, ivImage, index));






        return myContentsView;

    }



}