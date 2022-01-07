package com.kchksw.foods6.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kchksw.foods6.AsyncTask.GroupsLoadTask;
import com.kchksw.foods6.AsyncTask.UserRegistTask;
import com.kchksw.foods6.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    int needRequest = 0;
    int currentRequest = 0;
    int requestCount = 0;
    private static final int REQUEST_PERMISSION = 0;

    public static String[] PERMISSIONS_ARRAY = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    public boolean[] needPermission = {
            false, false, false
    };

    private CallbackManager callbackManager;

    public boolean load_info_me = false;
    boolean load_info_friends = false;
    public boolean load_info_groups = false;
    boolean activityCallComplete = false;

    JSONObject me;

    LoginActivity context;
    public Intent intent;

    LoginButton loginButton;


    public static Toast mToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); // SDK 초기화 (setContentView 보다 먼저 실행되어야합니다. 안그럼 에러납니다.)
        setContentView(R.layout.activity_login);

        mToast = Toast.makeText(this, "null", Toast.LENGTH_LONG);

        context = this;
        intent = new Intent(LoginActivity.this, MainActivity.class);

        callbackManager = CallbackManager.Factory.create();  //로그인 응답을 처리할 콜백 관리자

        loginButton = (LoginButton) findViewById(R.id.login_button); //페이스북 로그인 버튼
        //유저 정보, 친구정보, 이메일 정보등을 수집하기 위해서는 허가(퍼미션)를 받아야 합니다.
        loginButton.clearPermissions();
        loginButton.setReadPermissions("public_profile", "user_friends");
        //버튼에 바로 콜백을 등록하는 경우 LoginManager에 콜백을 등록하지 않아도됩니다.
        //반면에 커스텀으로 만든 버튼을 사용할 경우 아래보면 CustomloginButton OnClickListener안에 LoginManager를 이용해서
        //로그인 처리를 해주어야 합니다.

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
//                  context.login_result = login_result;

                GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                        login_result.getAccessToken(),
                        //AccessToken.getCurrentAccessToken(),
                        "/me",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {

                                me = response.getJSONObject();
                                Log.e("me", me + "");
                                intent.putExtra("jsonMe", me.toString());

                                UserRegistTask userRegistTask = null;
                                GroupsLoadTask groupsLoadTask = null;
                                try {
                                    userRegistTask = new UserRegistTask(me.getString("id"), me.getString("name"), context);
                                    groupsLoadTask = new GroupsLoadTask(me.getString("id"), context);
                                    userRegistTask.execute();
                                    groupsLoadTask.execute();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                ).executeAsync();

                GraphRequestAsyncTask graphRequestAsyncTask2 = new GraphRequest(
                        login_result.getAccessToken(),
                        //AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {

                                try {
                                    JSONArray jsonFriends = response.getJSONObject().getJSONArray("data");
                                    intent.putExtra("jsonFriends", jsonFriends.toString());

                                    load_info_friends = true;
                                    loadingComplete();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();
            }


            @Override
            public void onError(FacebookException error) {
            }

            @Override
            public void onCancel() {
            }
        });


        RequestPermission();



        if (needRequest == 0) {
            //  startActivity(new Intent(getApplication(), LoginActivity.class)); // 로딩이 끝난후 이동할 Activity
            loginButton.performClick();
            // LoginActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
        }


    }

    public void loadingComplete() {
        Log.e("친구 정보 로딩완료?", load_info_friends + "");
        Log.e("내 정보 로딩완료?", load_info_me + "");
        Log.e("내 그룹 로딩완료?", load_info_groups + "");
        if (!activityCallComplete) {
            if (load_info_friends && load_info_me && load_info_groups) {
                activityCallComplete = true;
                startActivity(intent);
                finish();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void RequestPermission() {


        for (int i = 0; i < PERMISSIONS_ARRAY.length; i++) {
            if (checkSelfPermission(PERMISSIONS_ARRAY[i]) != PackageManager.PERMISSION_GRANTED) {
                needRequest++;
                needPermission[i] = true;
            }
        }

        String[] needPermissions = new String[needRequest];

        int index = 0;
        for(int i =0; i< PERMISSIONS_ARRAY.length; i++){
            if(needPermission[i]){
                Log.e("index", index+"");
                Log.e("i", i+"");
                Log.e("length", PERMISSIONS_ARRAY.length+"");
                needPermissions[index++] = PERMISSIONS_ARRAY[i];
            }
        }
        if(index != 0){
            ActivityCompat.requestPermissions(this, needPermissions, REQUEST_PERMISSION);
        }



    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("aaa", "Aaa");
        if (requestCode == REQUEST_PERMISSION) {

            for(int i=0; i< permissions.length; i++){
                if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    currentRequest++;
                }
            }

        }

        Log.d("NeedRequest", needRequest + "");
        Log.d("CurrentRequest", currentRequest + "");


        if (needRequest == currentRequest) {
            //startActivity(new Intent(getApplication(), LoginActivity.class)); // 로딩이 끝난후 이동할 Activity
            loginButton.performClick();
        }

        else {
            mToast.setText("권한 허가가 필요합니다. 2초 뒤 자동 종료됩니다.");
            mToast.show();
            Handler hd = new Handler();
            hd.postDelayed(new delayhandler(), 2000); // 2초 후에 hd Handler 실행
        }

        //LoginActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
    }

    private class delayhandler implements Runnable {
        public void run() {
            mToast.cancel();
            finish();
        }
    }

}
