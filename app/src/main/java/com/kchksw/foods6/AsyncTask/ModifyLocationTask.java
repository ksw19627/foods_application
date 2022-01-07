package com.kchksw.foods6.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.kchksw.foods6.Activity.MapLoadActivity;
import com.kchksw.foods6.Model.Model;
import com.kchksw.foods6.etc.Util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// 마커 드래그이벤트시 웹 서버 DB에 위도와 경도를 바꾸어 주는 클래스
public class ModifyLocationTask extends AsyncTask<Void, Void, Boolean> {

    // 변수 선언

    int index; // 현재 선택된 마커의 id 인덱스 값
    double latitude; // 위도
    double longitude; // 경도

    String facebook_or_group_id;
    int callType;

    public MapLoadActivity context = MapLoadActivity.mapLoadActivity;

    String result = "";

    public ModifyLocationTask(int index, double latitude, double longitude) {
        Log.d("Check", "생성자 진입");

        this.index = index;
        this.latitude = latitude;
        this.longitude = longitude;

        callType = context.callType;
        if(callType == Util.CALL_BY_MYINFO){
            facebook_or_group_id = context.receiveUser.getId();
        }
        if(callType == Util.CALL_BY_MYGROUP){
            facebook_or_group_id = context.receiveGroup.getGroups_id();
        }

        Log.e("f_or_g_id", facebook_or_group_id);

    }

    // 자식쓰레드가 웹서버에 위도와 경도값을 주어 DB를 수정할수 있도록 통신한다.
    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        Log.d("Check", "doInBackground 진입");

        // http통신에 필요한 변수
        HttpPost httppost;
        HttpResponse response;
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;

        try {
            // Simulate network access.
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(Util.SERVER_ADDRESS + "modifyLocation.php");

            // pk인 db_id와 위도, 경도값을 set한다.
            nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("key_category", Model.myMarkerItems.get(index).getCategory()));
            nameValuePairs.add(new BasicNameValuePair("key_store", Model.myMarkerItems.get(index).getStore()));
            nameValuePairs.add(new BasicNameValuePair("key_foodname", Model.myMarkerItems.get(index).getFoodname()));
            nameValuePairs.add(new BasicNameValuePair("key_comment", Model.myMarkerItems.get(index).getComment()));
            nameValuePairs.add(new BasicNameValuePair("key_latitude", Model.myMarkerItems.get(index).getLat()+""));
            nameValuePairs.add(new BasicNameValuePair("key_longitude", Model.myMarkerItems.get(index).getLon()+""));
            nameValuePairs.add(new BasicNameValuePair("key_address", Model.myMarkerItems.get(index).getAddress()));
            nameValuePairs.add(new BasicNameValuePair("key_string_image", Model.myMarkerItems.get(index).getStringImage()));
            nameValuePairs.add(new BasicNameValuePair("key_rating", Model.myMarkerItems.get(index).getRating()+""));
            nameValuePairs.add(new BasicNameValuePair("key_facebook_or_group_id", facebook_or_group_id));
            nameValuePairs.add(new BasicNameValuePair("callType", callType+""));

            nameValuePairs.add(new BasicNameValuePair("latitude", latitude + ""));
            nameValuePairs.add(new BasicNameValuePair("longitude", longitude + ""));

            // utf-8로 인코딩하여 웹서버로 전송한다.
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            // http를 execute시키고 그 결과값을 reponse로 받아온다.
            response = null;
            response = httpclient.execute(httppost);


            // 받아온 json형식의 데이터를 버퍼리더로 변환
            BufferedReader bufreader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(),
                            "utf-8"));

            // 버퍼리더를 string으로 변환하기위한 변수
            String line = null;


            // 버퍼리더로부터 한줄씩 읽어와 읽을게 더이상 없을때까지 string형식 뒤에 붙여 저장한다.
            while ((line = bufreader.readLine()) != null) {
                result += line;
            }


            Log.e("response:", result);





            Log.e("latitude",latitude + "");
            Log.e("longitude",longitude + "");
        } catch (Exception e) {
            Log.d("Exception : ", e.toString());

        }

        return true;
    }

    // doInBackground 함수 뒤에 실행되는 콜백 메인쓰레드
    @Override
    protected void onPostExecute(final Boolean success) {
        Log.d("Check", "onPostExcute 진입");
        // mAuthTask = null;

        if (success) {
            // 이동된 마커의 위도와 경도를 모델에 저장한다.
            Model.myMarkerItems.get(index).setLat(latitude);
            Model.myMarkerItems.get(index).setLon(longitude);

            // 완료된 후 카메라를 이동된 위치로 이동시킨다.
            context.mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude)));
        } else {
        }
    }

    // 도중 취소되었을때 실행되는 메소드
    @Override
    protected void onCancelled() {
        Log.d("Check", "onCancelled 진입");
        // mAuthTask = null;
    }
}