package com.kchksw.foods6.etc;

import android.graphics.Bitmap;

/**
 * Created by KimChanHyup on 2016-08-03.
 */
public class MyMarkerItem {
    private int id;
    private double lat;
    private double lon;
    private String store;
    private String foodname;
    private String comment;
    private String stringImage;
    private float rating;
    private String category;
    private Bitmap bitmap;
    private String address;



    public MyMarkerItem() {

    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {

        return address;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {

        return bitmap;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {

        return category;
    }


    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public String getFoodname() {
        return foodname;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getComment() {
        return comment;
    }

    public String getStringImage() {
        return stringImage;
    }

    public String getStore() {
        return store;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setStringImage(String stringImage) {
        this.stringImage = stringImage;
    }

    public void setStore(String store) {
        this.store = store;
    }
}
