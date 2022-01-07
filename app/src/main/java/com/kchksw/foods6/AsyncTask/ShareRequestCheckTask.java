

package com.kchksw.foods6.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.kchksw.foods6.Activity.MainActivity;
import com.kchksw.foods6.etc.User;
import com.kchksw.foods6.etc.Util;
import com.kchksw.foods6.fragment.ThirdFragment;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class ShareRequestCheckTask extends AsyncTask<Void, Void, Boolean> {

    User user;

    ThirdFragment context;

    // Http 통신을 이용하기위한 변수
    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;

    String result = "";


    public ShareRequestCheckTask(ThirdFragment context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        Log.d("ShareRequestCheckTask", "doInBackground 진입");

        try {


            // http방식을 통한 서버주소 + php파일의 경로를 설정
            // Simulate network access.
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(Util.SERVER_ADDRESS + "share_request_check.php");

            // 로그인입력값인 id와 pw를 input으로 설정하여 ArrayList에 set한다.
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("login_id", ((MainActivity)context.getActivity()).loginUser.getId()));

            // ArrayList를 httpPost에 utf-8로 인코딩하여 set 시켜 보낼준비를 마친다.
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

            Log.d("response:",result);

            // 2초 delay
            // Thread.sleep(2000);

            // 여기서 리턴되는 값에따라 onPostExecute의 인자값으로 들어간다.
            // response의 결과가 User Found라면 유저가 입력한 정보가 일치하는 것이므로 true반환한다.
            // 그렇지 않은경우 유저가 입력한 정보가 틀렸을 경우(No such User Found)이므로 false를 반환한다.

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArrayFollower = (JSONArray)jsonObject.get("follower");
            JSONArray jsonArrayFollowing = (JSONArray)jsonObject.get("following");

            for(int i=0; i<jsonArrayFollower.length(); i++){
                JSONObject jsonObject2 = (JSONObject)jsonArrayFollower.get(i);
                String to_id = (String)jsonObject2.get("to_id");
                String user_name = (String)jsonObject2.get("user_name");
                Log.e("to_id", to_id);
                Log.e("user_name",user_name);

                user = new User();
                user.setId(to_id);
                user.setName(user_name);

                /*
                URL url = new URL("https://graph.facebook.com/" + user.getId() + "/picture?width=500&height=500&type=normal");
                URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                Bitmap bm = BitmapFactory.decodeStream(bis);


                bis.close();
                user.setImage(bm);
*/
                context.shareRequestArrayListFollower.add(user);



            }
            for(int i=0; i<jsonArrayFollowing.length(); i++){
                JSONObject jsonObject2 = (JSONObject)jsonArrayFollowing.get(i);
                String from_id = (String)jsonObject2.get("from_id");
                String user_name = (String)jsonObject2.get("user_name");
                Log.e("from_id", from_id);
                Log.e("user_name",user_name);

                user = new User();
                user.setId(from_id);
                user.setName(user_name);
/*
                URL url = new URL("https://graph.facebook.com/" + user.getId() + "/picture?width=500&height=500&type=normal");
                URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                Bitmap bm = BitmapFactory.decodeStream(bis);


                bis.close();
                user.setImage(bm);
*/
                context.shareRequestArrayListFollowing.add(user);



            }







        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        Log.d("Check", "onPostExcute 진입");
        //mAuthTask = null;

        if (success) {
            Log.d("image load ", "success");
            Util.userArrayListSort(context.shareRequestArrayListFollowing);
            Util.userArrayListSort(context.shareRequestArrayListFollower);

            context.shareRequestAdapter.notifyDataSetChanged();
            if(context.shareRequestArrayListFollowing.size() != 0 &&  context.selectBtn == context.SELECTED_FOLLOWING){
                context.shareRequestListView.setVisibility(View.VISIBLE);
                context.textViewShareRequest.setVisibility(View.GONE);
            }
            if(context.shareRequestArrayListFollower.size() != 0 &&  context.selectBtn == context.SELECTED_FOLLOWER){
                context.shareRequestListView.setVisibility(View.VISIBLE);
                context.textViewShareRequest.setVisibility(View.GONE);
            }
        } else {
            Log.d("image load ", "failed");
        }

    }

    @Override
    protected void onCancelled() {
        Log.d("Check", "onCancelled 진입");
        // mAuthTask = null;
        Log.d("Check", "onCancelled 실패");
    }
}