package com.kchksw.foods6.etc;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KimChanHyup on 2016-08-03.
 */
public class User implements Parcelable {
    private String name;
    private String id;
    private Bitmap image;
    private boolean following;
    private boolean follower;

    public User() {

    }
    public User(String name, String id, Bitmap image) {
        this.name = name;
        this.id = id;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean isFollowing() {
        return following;
    }
    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isFollower() {
        return follower;
    }

    public void setFollower(boolean follower) {
        this.follower = follower;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

        dest.writeString(name);
        dest.writeString(id);
        dest.writeParcelable(image, flags);

    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel(Parcel src) {
            ClassLoader loader = Bitmap.class.getClassLoader();

            String name = src.readString();
            String id = src.readString();
            Bitmap image = src.readParcelable(loader);


            // rentCount,
            return new User(name, id, image);

        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }

    };
}
