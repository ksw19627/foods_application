package com.kchksw.foods6.etc;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KimChanHyup on 2016-08-03.
 */
public class Group implements Parcelable {
    private String groups_id;
    private String groups_name;
    private String groups_member_count;
    private String groups_members;
    private String groups_members_id;

    public Group() {

    }

    public Group(String groups_id, String groups_name, String groups_member_count, String groups_members, String groups_members_id) {
        this.groups_id = groups_id;
        this.groups_name = groups_name;
        this.groups_member_count = groups_member_count;
        this.groups_members = groups_members;
        this.groups_members_id = groups_members_id;
    }

    public void setGroups_members_id(String groups_members_id) {
        this.groups_members_id = groups_members_id;
    }

    public String getGroups_members_id() {

        return groups_members_id;
    }

    public void setGroups_id(String groups_id) {
        this.groups_id = groups_id;
    }

    public String getGroups_id() {
        return groups_id;
    }

    public void setGroups_name(String groups_name) {
        this.groups_name = groups_name;
    }

    public String getGroups_name() {
        return groups_name;
    }

    public String getGroups_member_count() {
        return groups_member_count;
    }

    public void setGroups_member_count(String groups_member_count) {
        this.groups_member_count = groups_member_count;
    }

    public String getGroups_members() {
        return groups_members;
    }

    public void setGroups_members(String groups_members) {
        this.groups_members = groups_members;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

        dest.writeString(groups_id);
        dest.writeString(groups_name);
        dest.writeString(groups_member_count);
        dest.writeString(groups_members);
        dest.writeString(groups_members_id);
        // dest.writeParcelable(image, flags);

    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {

        @Override
        public Group createFromParcel(Parcel src) {
            ClassLoader loader = Bitmap.class.getClassLoader();

            String groups_id = src.readString();
            String groups_name = src.readString();
            String groups_member_count = src.readString();
            String groups_members = src.readString();
            String groups_members_id = src.readString();
            //Bitmap image = src.readParcelable(loader);


            // rentCount,
            return new Group(groups_id, groups_name, groups_member_count, groups_members, groups_members_id);

        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }

    };
}
