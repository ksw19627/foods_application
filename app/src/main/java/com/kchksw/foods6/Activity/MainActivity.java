package com.kchksw.foods6.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.firebase.FirebaseApp;
import com.kchksw.foods6.R;
import com.kchksw.foods6.etc.BackPressCloseHandler;
import com.kchksw.foods6.etc.User;
import com.kchksw.foods6.fragment.FirstFragment;
import com.kchksw.foods6.fragment.SecondFragment;
import com.kchksw.foods6.fragment.ThirdFragment;

public class MainActivity extends AppCompatActivity {
    ViewPager vp;
    LinearLayout ll;

    String jsonFriends;
    String jsonMe;
    String jsonGroups;

    private BackPressCloseHandler backPressCloseHandler; // 뒤로가기 버튼을 2번 눌러야 종료되게 해주는 핸들러

    public User loginUser;


    public FirstFragment firstFragment;
    public SecondFragment secondFragment;
    public ThirdFragment thirdFragment;

    public android.support.v7.app.ActionBar actionBar;

    public static MainActivity context;


    public MainActivity(){
        context = this;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        actionBar =  getSupportActionBar();

        setContentView(R.layout.activity_main);


        backPressCloseHandler = new BackPressCloseHandler(this); // 뒤로가기 버튼 핸들러의 객체 생성


        loginUser = new User();

        FirebaseApp.initializeApp(this);

        Intent intent = getIntent();
        jsonFriends = intent.getStringExtra("jsonFriends");
        Log.e("jsonFriends : ", jsonFriends);

        jsonMe = intent.getStringExtra("jsonMe");
        Log.e("jsonMe : ", jsonMe);

        jsonGroups = intent.getStringExtra("jsonGroups");
        Log.e("jsonGroups : ", jsonGroups);


        firstFragment = new FirstFragment(jsonMe, jsonGroups);
        secondFragment = new SecondFragment(jsonFriends);
        thirdFragment = new ThirdFragment();


        vp = (ViewPager) findViewById(R.id.vp);
        ll = (LinearLayout) findViewById(R.id.ll);

        ImageButton tab_first = (ImageButton) findViewById(R.id.tab_first);
        ImageButton tab_second = (ImageButton) findViewById(R.id.tab_second);
        ImageButton tab_third = (ImageButton) findViewById(R.id.tab_third);

        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setOffscreenPageLimit(2);
        vp.setCurrentItem(0);

        tab_first.setOnClickListener(movePageListener);
        tab_first.setTag(0);
        tab_second.setOnClickListener(movePageListener);
        tab_second.setTag(1);
        tab_third.setOnClickListener(movePageListener);
        tab_third.setTag(2);

        tab_first.setSelected(true);
        actionBar.setTitle("나 & 그룹");

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int i = 0;
                while (i < 3) {
                    if (position == i) {
                        ll.findViewWithTag(i).setSelected(true);
                        actionBarModify(i);

                    } else {
                        ll.findViewWithTag(i).setSelected(false);
                    }
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }



    View.OnClickListener movePageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();

            int i = 0;
            while (i < 3) {
                if (tag == i) {
                    ll.findViewWithTag(i).setSelected(true);
                    actionBarModify(i);
                } else {
                    ll.findViewWithTag(i).setSelected(false);
                }
                i++;
            }

            vp.setCurrentItem(tag);








        }
    };




    private class pagerAdapter extends FragmentStatePagerAdapter {
        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return firstFragment;
                case 1:
                    return secondFragment;
                case 2:
                    return thirdFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
    void actionBarModify(int index){
        if(index == 0){
            actionBar.setTitle("나 & 그룹");
        }else if(index == 1){
            actionBar.setTitle("친구");
        }else if(index == 2){
            actionBar.setTitle("공유요청");
        }
    }



    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed(); // 핸들러에 있는 onBackPressed함수를 호출한다.(2번 눌러야 종료)


    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // res 폴더 내의 menu_main.xml이 있는 경우 실행 가능한 함수다.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){

        if(menu.getItemId() == R.id.newGroup){
            Intent intent = new Intent(this, GroupCreateActivity.class);

            intent.putExtra("loginUserID", loginUser.getId());
            startActivity(intent);

        }

        return super.onOptionsItemSelected(menu);
    }


    public static MainActivity getInstance(){

        return context;
    }


}
