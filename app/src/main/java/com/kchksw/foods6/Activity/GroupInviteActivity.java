package com.kchksw.foods6.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kchksw.foods6.Adapter.CustomChoiceListViewAdapter;
import com.kchksw.foods6.AsyncTask.GroupInviteTask;
import com.kchksw.foods6.R;
import com.kchksw.foods6.etc.Group;
import com.kchksw.foods6.etc.User;
import com.kchksw.foods6.fragment.FirstFragment;
import com.kchksw.foods6.fragment.SecondFragment;

import java.util.ArrayList;


public class GroupInviteActivity extends AppCompatActivity {




    public ArrayList<User> arrayListFriendsNotGroup;
    public CustomChoiceListViewAdapter adapter;
    public ListView listViewInvite;

    public FirstFragment firstFragment;
    public SecondFragment secondFragment;

    Group group;

    Boolean isGroup;

    EditText editsearch;
    public static Toast mToast;
    public android.support.v7.app.ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar =  getSupportActionBar();


        setContentView(R.layout.activity_group_invite);

        mToast = Toast.makeText(this, "null", Toast.LENGTH_SHORT);



        firstFragment = FirstFragment.getInstance();
        secondFragment = SecondFragment.getInstance();

        arrayListFriendsNotGroup = new ArrayList<User>();

        Bundle bundle = getIntent().getExtras();
        group = bundle.getParcelable("sendGroupInfo");
        actionBar.setTitle(group.getGroups_name()+" - 그룹초대");

        String[] friendToken;

        friendToken = group.getGroups_members_id().split(", ");

        for (int i = 0; i < friendToken.length; i++) {
            Log.e("token" + i, friendToken[i]);
        }

        for (int i = 0; i < secondFragment.arrayListFriendsAll.size(); i++) {

            isGroup = false;// 내가 선택한 그룹에 포함되어있는지 체크하는 변수

            for (int j = 0; j < friendToken.length; j++) {
                if (secondFragment.arrayListFriendsAll.get(i).getId().equals(friendToken[j])) {
                    isGroup = true;
                    break;
                }
            }
            if (isGroup == false) {
                arrayListFriendsNotGroup.add(secondFragment.arrayListFriendsAll.get(i));


            }

        }


        adapter = new CustomChoiceListViewAdapter(this, arrayListFriendsNotGroup);
        listViewInvite = (ListView) findViewById(R.id.listViewInvite);
        listViewInvite.setAdapter(adapter);


        editsearch = (EditText) findViewById(R.id.friendSearch);



        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable edit) {

                String filterText = edit.toString();
                ((CustomChoiceListViewAdapter) listViewInvite.getAdapter()).getFilter().filter(filterText);

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        editsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        // 완료 동작
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }


        });


        TextView textView = (TextView) findViewById(R.id.textView1);
        if (arrayListFriendsNotGroup.size() == 0) {
            textView.setVisibility(View.VISIBLE);
            listViewInvite.setVisibility(View.GONE);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // res 폴더 내의 menu_main.xml이 있는 경우 실행 가능한 함수다.
        getMenuInflater().inflate(R.menu.menu_invite_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {

        if (menu.getItemId() == R.id.complete) {
            SparseBooleanArray checkedItems = listViewInvite.getCheckedItemPositions();
            int count = adapter.getCount();

            boolean isSelected = false;

            String friendsId = "";
            String friendsName = "";
            for (int i = count - 1; i >= 0; i--) {
                if (checkedItems.get(i)) {
                    friendsId = friendsId + adapter.getItem(i).getId() + ", ";
                    friendsName = friendsName + adapter.getItem(i).getName() + ", ";
                    isSelected = true;
                }
            }
            if (isSelected) {
                final String friendsId2 = friendsId.substring(0, friendsId.length() - 2);
                final String friendsName2 = friendsName.substring(0, friendsName.length() - 2);

                LayoutInflater inflater = LayoutInflater.from(GroupInviteActivity.this);

                // 기본 dialog를 선언한다.
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupInviteActivity.this);
                builder.setMessage("정말 그룹에 초대할까요?");

                builder.setCancelable(false); // Cancel 버튼 없이 Yes, No 버튼만을 사용한다.


                // Yes버튼 눌렀을 경우 리스너 등록
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int id) {

                        GroupInviteTask groupInviteTask = new GroupInviteTask(group, friendsId2, friendsName2, GroupInviteActivity.this);
                        groupInviteTask.execute();

                    }
                    // No버튼을 눌렀을 경우 리스너 등록
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
                // 위에서 정의한 dialog를 생성한다.
                AlertDialog alert = builder.create();
                // Title을 지정한다.
                alert.setTitle("그룹초대");
                // Icon을 지정한다.
                alert.setIcon(R.drawable.foods_launcher);
                // dialog를 보여준다.
                alert.show();

            } else {
                mToast.setText("하나 이상의 인원을 선택해 주세요.");
                mToast.show();
            }
        }
        return super.onOptionsItemSelected(menu);
    }


}
