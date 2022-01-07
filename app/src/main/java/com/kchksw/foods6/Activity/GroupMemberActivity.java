package com.kchksw.foods6.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.kchksw.foods6.Adapter.UserFriendAdapter;
import com.kchksw.foods6.R;
import com.kchksw.foods6.etc.Group;
import com.kchksw.foods6.etc.User;
import com.kchksw.foods6.fragment.FirstFragment;
import com.kchksw.foods6.fragment.SecondFragment;

import java.util.ArrayList;

public class GroupMemberActivity extends AppCompatActivity {




    public ArrayList<User> arrayListFriendsInGroup;
    Group sendGroupInfo;

    public FirstFragment firstFragment;
    public SecondFragment secondFragment;

    public UserFriendAdapter userFriendAdapter;
    public android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar =  getSupportActionBar();


        setContentView(R.layout.activity_group_member);

        Bundle bundle = getIntent().getExtras();

        sendGroupInfo = bundle.getParcelable("sendGroupInfo");
        actionBar.setTitle(sendGroupInfo.getGroups_name()+" - 구성원");


        firstFragment = FirstFragment.getInstance();
        secondFragment = SecondFragment.getInstance();

        arrayListFriendsInGroup = new ArrayList<User>();
        arrayListFriendsInGroup.add(firstFragment.meInfo.get(0));

        String[] friendToken;

        friendToken = sendGroupInfo.getGroups_members_id().split(", ");

        for (int i = 0; i < friendToken.length; i++) {
            Log.e("token" + i, friendToken[i]);
        }

        for (int i = 0; i < secondFragment.arrayListFriendsAll.size(); i++) {

            boolean isGroup = false;// 내가 선택한 그룹에 포함되어있는지 체크하는 변수

            for (int j = 0; j < friendToken.length; j++) {
                if (secondFragment.arrayListFriendsAll.get(i).getId().equals(friendToken[j])) {
                    isGroup = true;
                    break;
                }
            }
            if (isGroup == true) {
                arrayListFriendsInGroup.add(secondFragment.arrayListFriendsAll.get(i));
            }

        }



        userFriendAdapter = new UserFriendAdapter(getApplication(), arrayListFriendsInGroup); // simple textview for list item
        ListView listViewGroupMember = (ListView) findViewById(R.id.listViewGroupMember);
        listViewGroupMember.setAdapter(userFriendAdapter);

        TextView textViewGroupName = (TextView)findViewById(R.id.textViewGroupName);
        textViewGroupName.setText(sendGroupInfo.getGroups_name());

    }
}
