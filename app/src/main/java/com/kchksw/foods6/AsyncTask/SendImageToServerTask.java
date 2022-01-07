package com.kchksw.foods6.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kchksw.foods6.Activity.FirstUploadActivity;
import com.kchksw.foods6.Activity.MainActivity;
import com.kchksw.foods6.Activity.SecondUploadActivity;
import com.kchksw.foods6.Model.Model;
import com.kchksw.foods6.etc.Group;
import com.kchksw.foods6.etc.MyMarkerItem;
import com.kchksw.foods6.etc.User;
import com.kchksw.foods6.etc.Util;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import static com.kchksw.foods6.Activity.MapLoadActivity.mapLoadActivity;
import static com.kchksw.foods6.Model.Model.modelMaxId;

/**
 * Created by user on 2017-01-06.
 */

public class SendImageToServerTask extends AsyncTask<Void, Void, Boolean> {

    String fileTotalPathName;
    SecondUploadActivity context;
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
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;

    String store;
    String food;
    String comment;
    String latitude;
    String longitude;
    String rating;
    String facebook_or_group_id;
    String image_name;
    String category;
    String address;

    int callType;
    User user;
    Group group;

    MainActivity mainActivity;

    boolean success;
    String response;
    public String result;

    public SendImageToServerTask(SecondUploadActivity context, int callType, User user) {

        this.context = context;
        mainActivity = MainActivity.getInstance();

        this.callType = callType;
        this.user = user;

        fileTotalPathName = context.imageUri.getPath();
        store = context.place_name; //
        food = context.food_name; //
        comment = context.comments; //
        latitude = context.latitude; //
        longitude = context.longitude; //
        rating = context.rating_value;//
        facebook_or_group_id = mainActivity.loginUser.getId(); //
        image_name = context.imageFileName; //
        category = context.food_category;
        address = context.address;


    }

    public SendImageToServerTask(SecondUploadActivity context, int callType, Group group) {

        this.context = context;
        mainActivity = MainActivity.getInstance();

        this.callType = callType;
        this.group = group;

        fileTotalPathName = context.imageUri.getPath();
        store = context.place_name; //
        food = context.food_name; //
        comment = context.comments; //
        latitude = context.latitude; //
        longitude = context.longitude; //
        rating = context.rating_value;//
        facebook_or_group_id = group.getGroups_id();
        image_name = context.imageFileName; //
        category = context.food_category;
        address = context.address;


    }

    @Override
    protected Boolean doInBackground(Void... params) {


        try {
            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(fileTotalPathName);

            Log.e("callType", callType + "");
            Log.e("id", facebook_or_group_id);


             URL url = new URL(Util.SERVER_ADDRESS + "imageUploadToServer.php");

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
            dos.writeBytes("Content-Disposition: form-data; name=\"food\";" + lineEnd);
            dos.writeBytes(lineEnd + URLEncoder.encode(food, "utf-8"));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"store\";" + lineEnd);
            dos.writeBytes(lineEnd + URLEncoder.encode(store,"UTF-8"));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"comment\";" + lineEnd);
            dos.writeBytes(lineEnd + URLEncoder.encode(comment,"UTF-8"));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"latitude\";" + lineEnd);
            dos.writeBytes(lineEnd + URLEncoder.encode(latitude,"UTF-8"));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"longitude\";" + lineEnd);
            dos.writeBytes(lineEnd + URLEncoder.encode(longitude,"UTF-8"));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"image_name\";" + lineEnd);
            dos.writeBytes(lineEnd + image_name);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"rating\";" + lineEnd);
            dos.writeBytes(lineEnd + URLEncoder.encode(rating,"UTF-8"));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"facebook_or_group_id\";" + lineEnd);
            dos.writeBytes(lineEnd + URLEncoder.encode(facebook_or_group_id,"UTF-8"));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"category\";" + lineEnd);
            dos.writeBytes(lineEnd + URLEncoder.encode(category,"UTF-8"));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"address\";" + lineEnd);
            dos.writeBytes(lineEnd + URLEncoder.encode(address,"UTF-8"));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"callType\";" + lineEnd);
            dos.writeBytes(lineEnd + URLEncoder.encode(callType+"","UTF-8"));
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

                    if (result.equals("upload OK"))
                        success = true;
                    else
                        success = false;
                }


            }

            //close the streams //
            fileInputStream.close();
//            dos.flush();
            //          dos.close();


            Log.d("success6", result + "");

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
            Toast.makeText(context, "성공적으로 저장되었습니다.",
                    Toast.LENGTH_SHORT).show();

            modelMaxId++;

            MyMarkerItem myMarkerItem = new MyMarkerItem();
            myMarkerItem.setId(modelMaxId); // marker의 id
            myMarkerItem.setLat(Double.parseDouble(latitude)); // 위도
            myMarkerItem.setLon(Double.parseDouble(longitude)); // 경도
            myMarkerItem.setStore(store); // 음식점이름
            myMarkerItem.setFoodname(food); // 음식이름
            myMarkerItem.setComment(comment); // 평가글
            myMarkerItem.setStringImage(image_name); // 이미지의 파일이름
            myMarkerItem.setRating(Float.parseFloat(rating)); // 평점
            myMarkerItem.setCategory(category); // category 값
            myMarkerItem.setAddress(address);

            Model.myMarkerItems.add(myMarkerItem);


            // ImageLoadingTask imageLoadingTask = new ImageLoadingTask(modelMaxId);
            // imageLoadingTask.execute();

            LatLng position = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            MarkerOptions m = new MarkerOptions().position(position).title(store).draggable(true);
            mapLoadActivity.mMap.addMarker(m);
            mapLoadActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12));




            FirstUploadActivity.firstUploadActivity.finish();
            context.finish();


        } else {
            Toast.makeText(context, "업로드 실패",
                    Toast.LENGTH_SHORT).show();

        }
        context.upLoadProgressBar.setVisibility(ProgressBar.GONE);
        Util.setViewAndChildrenEnabled(context.upLoadLinearView,true);

        context.upLoadComplete.setEnabled(true);

    }
}
