package com.kchksw.foods6.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kchksw.foods6.Activity.MainActivity;
import com.kchksw.foods6.Activity.MapLoadActivity;
import com.kchksw.foods6.Activity.ShowDetailActivity;
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
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017-01-06.
 */

public class MarkerInfoModifyTask extends AsyncTask<Void, Void, Boolean> {

    String fileTotalPathName;
    ShowDetailActivity context;
    int serverResponseCode = 0;

    HttpURLConnection conn = null;
    DataOutputStream dos = null;
    OutputStream os = null;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;

    HttpPost httppost;
    HttpResponse response2;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;

    String store;
    String food;
    String comment;
    String rating;
    String image_name;
    String category;
    String address;

    String facebook_or_group_id;
    int callType;

    MainActivity mainActivity;

    boolean success;
    String response;
    public String result;

    int index;

    public MarkerInfoModifyTask(ShowDetailActivity context, int index) {

        this.context = context;
        mainActivity = MainActivity.getInstance();

        if (context.imageFileChanged) {
            fileTotalPathName = context.imageUri.getPath();
        }
        store = context.place_name; //
        food = context.food_name; //
        comment = context.comments; //
        address = context.addr;
        rating = context.rating_value;//

        image_name = context.imageFileName; //
        category = context.category;

        callType = context.callType;
        if(callType == Util.CALL_BY_MYINFO){
            facebook_or_group_id = context.receiveUser.getId();
        }
        if(callType == Util.CALL_BY_MYGROUP){
            facebook_or_group_id = context.receiveGroup.getGroups_id();
        }


        this.index = index;


    }

    @Override
    protected Boolean doInBackground(Void... params) {

        Log.e("imagefile 변경?", context.imageFileChanged+"");
        try {
            if (context.imageFileChanged) {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(fileTotalPathName);

                URL url = new URL(Util.SERVER_ADDRESS + "info_modify_changed_image.php");

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCType", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileTotalPathName);


                dos = new DataOutputStream(conn.getOutputStream());

                /// --*****\r\n
                /// Content-Disposition: form-data; name=\"변수명1\"\r\n변수값1\r\n
                /// --*****\r\n
                /// Content-Disposition: form-data; name=\"변수명2\"\r\n변수값2\r\n
                /// --*****\r\n
                /// Content-Disposition: form-data; name=\"변수명3\"\r\n변수값3\r\n


                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"key_category\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(Model.myMarkerItems.get(index).getCategory(), "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"key_store\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(Model.myMarkerItems.get(index).getStore(), "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"key_foodname\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(Model.myMarkerItems.get(index).getFoodname(), "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"key_comment\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(Model.myMarkerItems.get(index).getComment(), "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"key_latitude\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(Model.myMarkerItems.get(index).getLat()+"", "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"key_longitude\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(Model.myMarkerItems.get(index).getLon()+"", "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"key_address\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(Model.myMarkerItems.get(index).getAddress(), "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"key_string_image\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(Model.myMarkerItems.get(index).getStringImage(), "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"key_rating\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(Model.myMarkerItems.get(index).getRating()+"", "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"key_facebook_or_group_id\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(facebook_or_group_id, "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"callType\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(callType+"", "UTF-8"));
                dos.writeBytes(lineEnd);




                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"food\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(food, "utf-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"store\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(store, "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"comment\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(comment, "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"image_name\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(image_name, "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"rating\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(rating, "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"category\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(category, "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
                dos.writeBytes("Content-Disposition: form-data; name=\"address\";" + lineEnd);
                dos.writeBytes(lineEnd + URLEncoder.encode(address, "UTF-8"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileTotalPathName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);


                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }


                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();


                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);


                if (serverResponseCode == 200) {
                    // 응답 내용(BODY) 구하기

                    try (InputStream in = conn.getInputStream();
                         ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        byte[] buf = new byte[1024 * 8];
                        int length = 0;
                        while ((length = in.read(buf)) != -1) {
                            out.write(buf, 0, length);
                        }
                        System.out.println(new String(out.toByteArray(), "UTF-8"));
                        result = new String(out.toByteArray(), "UTF-8");

                        if (result.equals("update OK"))
                            success = true;
                        else
                            success = false;

                        Log.d("success6", result + "");
                    }


                }

                //close the streams //
                fileInputStream.close();
                //dos.flush();
                //dos.close();
            } else{


                httpclient = new DefaultHttpClient();
                httppost = new HttpPost(Util.SERVER_ADDRESS + "info_modify_not_changed_image.php");

                // 로그인입력값인 id와 pw를 input으로 설정하여 ArrayList에 set한다.
                nameValuePairs = new ArrayList<NameValuePair>(2);


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


                nameValuePairs.add(new BasicNameValuePair("store", store));
                nameValuePairs.add(new BasicNameValuePair("food", food));
                nameValuePairs.add(new BasicNameValuePair("comment", comment));
                nameValuePairs.add(new BasicNameValuePair("image_name", image_name));
                nameValuePairs.add(new BasicNameValuePair("rating", rating));
                nameValuePairs.add(new BasicNameValuePair("category", category));
                nameValuePairs.add(new BasicNameValuePair("address", address));

                // ArrayList를 httpPost에 utf-8로 인코딩하여 set 시켜 보낼준비를 마친다.
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

                // http를 execute시키고 그 결과값을 reponse로 받아온다.
                response2 = null;
                response2 = httpclient.execute(httppost);


                // 받아온 json형식의 데이터를 버퍼리더로 변환
                BufferedReader bufreader = new BufferedReader(
                        new InputStreamReader(response2.getEntity().getContent(),
                                "utf-8"));

                // 버퍼리더를 string으로 변환하기위한 변수
                String line = null;

                String result1 = "";
                // 버퍼리더로부터 한줄씩 읽어와 읽을게 더이상 없을때까지 string형식 뒤에 붙여 저장한다.
                while ((line = bufreader.readLine()) != null) {
                    result1 += line;
                }
                if (result1.equals("update OK"))
                    success = true;
                else
                    success = false;

                Log.d("success6", result1 + "");

            }



            return success;


        } catch (MalformedURLException ex) {

            //     context.dialog.dismiss();
            ex.printStackTrace();

            //context.messageText.setText("MalformedURLException Exception : check script url.");
            //  Toast.makeText(context, "MalformedURLException",
            //         Toast.LENGTH_SHORT).show();


            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            return false;
        } catch (Exception e) {

            //    context.dialog.dismiss();
            e.printStackTrace();


            // messageText.setText("Got Exception : see logcat ");
            // Toast.makeText(context, "Got Exception : see logcat ",
            //               Toast.LENGTH_SHORT).show();

            Log.e("Upload file Exception", "Exception : "
                    + e.getMessage(), e);
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean success) {

        if (success) {
            Toast.makeText(context, "정상적으로 수정되었습니다.",
                    Toast.LENGTH_SHORT).show();

            Model.myMarkerItems.get(index).setStore(store); // 음식점이름
            Model.myMarkerItems.get(index).setFoodname(food); // 음식이름
            Model.myMarkerItems.get(index).setComment(comment); // 평가글
            Model.myMarkerItems.get(index).setStringImage(image_name); // 이미지의 파일이름
            Model.myMarkerItems.get(index).setRating(Float.parseFloat(rating)); // 평점
            Model.myMarkerItems.get(index).setCategory(category); // category 값
            Model.myMarkerItems.get(index).setAddress(address);


        } else {
            Toast.makeText(context, "수정 실패",
                    Toast.LENGTH_SHORT).show();

        }
        context.imageFileChanged = false;
        context.imageFileName = Model.myMarkerItems.get(index).getStringImage();


        MapLoadActivity.mapLoadActivity.clickedMarker.showInfoWindow();
        context.Bar.setVisibility(View.GONE);
        context.invalidateOptionsMenu();
    }
}
