package com.kchksw.foods6.Refresher;

import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.google.android.gms.maps.model.Marker;
import com.kchksw.foods6.Model.Model;
import com.squareup.picasso.Callback;

/**
 * Created by 226 on 2017-05-18.
 */

public class InfoWindowRefresher implements Callback {
    private Marker marker;
    private int index;
    private ImageView ivImage;

    public InfoWindowRefresher(Marker marker, ImageView ivImage, int index) {
        this.marker = marker;
        this.index =index;
        this.ivImage = ivImage;
    }

    @Override
    public void onSuccess() {

        Model.myMarkerItems.get(index).setBitmap(((BitmapDrawable)ivImage.getDrawable()).getBitmap());

        if (marker != null && marker.isInfoWindowShown()) {

            marker.hideInfoWindow();
            marker.showInfoWindow();
        }
    }

    @Override
    public void onError() {}
}
