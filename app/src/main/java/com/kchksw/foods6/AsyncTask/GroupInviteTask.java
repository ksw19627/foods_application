package com.kchksw.foods6.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.kchksw.foods6.Activity.GroupInviteActivity;
import com.kchksw.foods6.Activity.MainActivity;
import com.kchksw.foods6.etc.Group;
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

/**
 * 로그인 업무를 수행하는 AsyncTask 클래스
 * AsyncTask 클래스는 doInBackground(자식 쓰레드) -> onPostExecute(메인UI 쓰레드)순으로 순차적 실행된다.
 * 안드로이드에서 UI는 메인쓰레드에서만 접근가능하고, 메인쓰레드에서는 웹서버와 통신이 불가능하므로 AsynTask를 쓰면 적절하다.
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */

public class GroupInviteTask extends AsyncTask<Void, Void, Boolean> {


    Group group;
    String friendsId;
    String friendsName;
    GroupInviteActivity context;


    // Http 통신을 이용하기위한 변수
    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;

    String result = "";

    // 생성자를 통해 id,pw,액티비티를 가져온다.
    public GroupInviteTask(Group group, String friendsId, String friendsName, GroupInviteActivity context) {
        this.group = group;
        this.friendsId = friendsId;
        this.friendsName = friendsName;
        this.context = context;


    }


    // 백그라운드에서 자식쓰레드가 수행되는 함수
    // 이 함수가 끝나고 리턴시 onPostExecute함수가 메인쓰레드에서 수행된다.
    // 이 함수에서는 웹서버에 접근하여 id, pw 일치 여부를 확인한다.
    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            Log.d("check ", "check load");
            // http방식을 통한 서버주소 + php파일의 경로를 설정
            // Simulate network access.
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(Util.SERVER_ADDRESS + "group_invite.php");

            // 로그인입력값인 id와 pw를 input으로 설정하여 ArrayList에 set한다.
            nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("group_name", group.getGroups_name()));
            nameValuePairs.add(new BasicNameValuePair("group_id", group.getGroups_id()));
            nameValuePairs.add(new BasicNameValuePair("friendsId", friendsId));

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


            Log.e("response:", result);

            // 2초 delay
            // Thread.sleep(2000);

            // 여기서 리턴되는 값에따라 onPostExecute의 인자값으로 들어간다.
            // response의 결과가 User Found라면 유저가 입력한 정보가 일치하는 것이므로 true반환한다.
            // 그렇지 않은경우 유저가 입력한 정보가 틀렸을 경우(No such User Found)이므로 false를 반환한다.

            if (result.equals("")) {
                return false;
            } else
                return true;


        } catch (Exception e) {
            Log.d("Exception : ", e.toString());
            return false;
        }


    }

    // 자식쓰레드가 doInBackground 함수를 마치면 호출되는 메인쓰레드의 콜백함수
    // 이 함수에서 doInBackground함수의 성공여부에 따라 실행시키는 것이 다르다.
    @Override
    protected void onPostExecute(final Boolean success) {

        if (success) {
            MainActivity mainActivity = MainActivity.getInstance();

            for (int i = 0; i < context.firstFragment.arrayListGroup.size(); i++) {
                if (context.firstFragment.arrayListGroup.get(i).getGroups_id().equals(group.getGroups_id())) {
                    int member_count = Integer.parseInt(context.firstFragment.arrayListGroup.get(i).getGroups_member_count()) + Integer.parseInt(result);

                    context.firstFragment.arrayListGroup.get(i).setGroups_member_count(member_count+"");
                    context.firstFragment.arrayListGroup.get(i).setGroups_members_id(context.firstFragment.arrayListGroup.get(i).getGroups_members_id() + ", "+ friendsId);
                    context.firstFragment.arrayListGroup.get(i).setGroups_members(context.firstFragment.arrayListGroup.get(i).getGroups_members() + ", "+ friendsName);


                    break;
                }

            }
            context.firstFragment.groupAdapter.notifyDataSetChanged();
            context.firstFragment.mToast.setText("그룹초대 완료");
            context.firstFragment.mToast.show();
            context.finish();


        } else {

        }


    }

    // 도중에 취소된 경우
    @Override
    protected void onCancelled() {

    }
}