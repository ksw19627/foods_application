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
        actionBar.setTitle(group.getGroups_name()+" - ????????????");

        String[] friendToken;

        friendToken = group.getGroups_members_id().split(", ");

        for (int i = 0; i < friendToken.length; i++) {
            Log.e("token" + i, friendToken[i]);
        }

        for (int i = 0; i < secondFragment.arrayListFriendsAll.size(); i++) {

            isGroup = false;// ?????? ????????? ????????? ????????????????????? ???????????? ??????

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
                        // ?????? ??????
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        break;
                    default:
                        // ?????? ????????? ??????
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
        // res ?????? ?????? menu_main.xml??? ?????? ?????? ?????? ????????? ?????????.
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

                // ?????? dialog??? ????????????.
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupInviteActivity.this);
                builder.setMessage("?????? ????????? ????????????????");

                builder.setCancelable(false); // Cancel ?????? ?????? Yes, No ???????????? ????????????.


                // Yes?????? ????????? ?????? ????????? ??????
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int id) {

                        GroupInviteTask groupInviteTask = new GroupInviteTask(group, friendsId2, friendsName2, GroupInviteActivity.this);
                        groupInviteTask.execute();

                    }
                    // No????????? ????????? ?????? ????????? ??????
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
                // ????????? ????????? dialog??? ????????????.
                AlertDialog alert = builder.create();
                // Title??? ????????????.
                alert.setTitle("????????????");
                // Icon??? ????????????.
                alert.setIcon(R.drawable.foods_launcher);
                // dialog??? ????????????.
                alert.show();

            } else {
                mToast.setText("?????? ????????? ????????? ????????? ?????????.");
                mToast.show();
            }
        }
        return super.onOptionsItemSelected(menu);
    }


}
