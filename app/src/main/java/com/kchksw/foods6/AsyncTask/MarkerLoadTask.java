package com.kchksw.foods6.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kchksw.foods6.Model.Model;
import com.kchksw.foods6.etc.MyMarkerItem;
import com.kchksw.foods6.etc.Util;
import com.kchksw.foods6.parsing.JsonParsing;

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

// 구글맵을 띄워 보여줄때 웹서버에 있는 DB로부터 마커의 정보를 불러오는 클래스
// 마커의정보를 불러와 저장한 뒤 구글맵위에 마커를 그린다.
public class MarkerLoadTask extends AsyncTask<Void, Integer, String[][]> {


    GoogleMap mMap; // 구글맵

    private String facebook_or_group_id; // 자신의 이메일을 키로하여 db에서 정보를 찾아옴
    int callType;

    String[][] parserdData; // json형식을 파싱하여 저장할 2차원배열

    // http통신에 필요한 변수
    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;

    // 생성자
    public MarkerLoadTask(GoogleMap mMap, String id, int callType) {

        this.mMap = mMap;
        this.facebook_or_group_id = id;
        this.callType = callType;

    }

    // 자식쓰레드에서 웹서버에 통신하여 내 id로 저장되어있는 정보를 json형식으로 받아온다.
    @Override
    protected String[][] doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.

        // Simulate network access.
        // 웹서버에 접속할 경로등록
        httpclient = new DefaultHttpClient();

        if (callType == Util.CALL_BY_MYINFO || callType == Util.CALL_BY_MYFRINED) {

            httppost = new HttpPost(Util.SERVER_ADDRESS + "mapMarker_user.php");

        } else if (callType == Util.CALL_BY_MYGROUP) {
            httppost = new HttpPost(Util.SERVER_ADDRESS + "mapMarker_group.php");
        }
        try {
            Log.e("callType!!!!!!!", callType+"");

            // 내 id로 인자값을 set하여 내가 저장한 정보만 가져올수 있게함
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("facebook_or_group_id", facebook_or_group_id));

            // utf8로 인코딩하고 실행하여 json형식을 받아옴
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            response = httpclient.execute(httppost);

            // 받아온 json형식의 데이터를 버퍼리더로 변환
            BufferedReader bufreader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(),
                            "utf-8"));

            // 버퍼리더를 string으로 변환하기위한 변수
            String line = null;
            String result = "";

            // 버퍼리더로부터 한줄씩 읽어와 읽을게 더이상 없을때까지 string형식 뒤에 붙여 저장한다.
            while ((line = bufreader.readLine()) != null) {
                result += line;
            }

            // json형식의 string 데이터를 파싱하여 string 배열로 저장한다.
            parserdData = JsonParsing.jsonParserList(result);

            return parserdData;


        } catch (Exception e) {
            Log.d("Exception : ", e.toString());
            return null;

        }


    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        // 파일 다운로드 퍼센티지 표시 작업
        // setProgressPercent(progress[0]);
    }

    // doInBackground 함수가 끝나고 실행되는 메인쓰레드(콜백메서드)
    // 여기에서 파싱된 배열 데이터를 가지고 모델의 arrayList에 저장하며 지도위에 마커를 그린다.
    @Override
    protected void onPostExecute(String[][] parserdData) {

        Model.myMarkerItems.clear();
        Model.modelMaxId = -1;

        // 파싱된 데이터가 존재한다면
        if (parserdData != null) {



            // 첫번째 레코드부터 길이까지
            for (int i = 0; i < parserdData.length; i++) {

                // i번째 정보의 위도와 경도(3,4)
                double latitude = Double.parseDouble(parserdData[i][3]);
                double longitude = Double.parseDouble(parserdData[i][4]);

                // 마커의 위치를 지정하고 title은 i번째의 정보 0번으로 한다.
                // 마커가 이동될수 있도록 draggable을 true로 한다.
                LatLng position = new LatLng(latitude, longitude);
                MarkerOptions m;
                if(callType == Util.CALL_BY_MYINFO || callType == Util.CALL_BY_MYGROUP) {
                    m = new MarkerOptions().position(position).title(parserdData[i][0]).draggable(true);
                }
                else{
                    m = new MarkerOptions().position(position).title(parserdData[i][0]).draggable(false);
                }
                // marker를 맵위에 그린다.
                mMap.addMarker(m);

                // 맵을 켰을때 카메라는 가장 마지막에 그려진 marker를 기준으로한다. 줌포인트는 12
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12));

                // 파싱된 데이터로부터 모델의 arrayList에 저장한다.
                MyMarkerItem myMarkerItem = new MyMarkerItem();
                myMarkerItem.setId(i); // marker의 id
                myMarkerItem.setLat(latitude); // 위도
                myMarkerItem.setLon(longitude); // 경도
                myMarkerItem.setStore(parserdData[i][0]); // 음식점이름
                myMarkerItem.setFoodname(parserdData[i][1]); // 음식이름
                myMarkerItem.setComment(parserdData[i][2]); // 평가글
                myMarkerItem.setStringImage(parserdData[i][5]); // 이미지의 파일이름 (pk)
                myMarkerItem.setRating(Float.parseFloat(parserdData[i][6])); // 평점
                myMarkerItem.setCategory(parserdData[i][7]); // category 값
                myMarkerItem.setAddress(parserdData[i][8]); // category 값

                Model.modelMaxId = i;
                Model.myMarkerItems.add(myMarkerItem);
            }

            //finish();
        } else {

        }
    }

    @Override
    protected void onCancelled() {

    }

}