package com.kchksw.foods6.Refresher;

import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.kchksw.foods6.etc.User;
import com.squareup.picasso.Callback;

/**
 * Created by 226 on 2017-05-18.
 */

public class UserRefresher implements Callback {
    private User user;
    private ImageView image;

    public UserRefresher(User user, ImageView image) {
        this.user = user;
        this.image = image;
    }

    @Override
    public void onSuccess() {
        user.setImage(((BitmapDrawable)image.getDrawable()).getBitmap());
    }

    @Override
    public void onError() {}
}
