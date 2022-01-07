package com.kchksw.foods6.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kchksw.foods6.Adapter.CustomChoiceListViewAdapter;
import com.kchksw.foods6.AsyncTask.GroupRegistTask;
import com.kchksw.foods6.R;
import com.kchksw.foods6.etc.User;
import com.kchksw.foods6.fragment.SecondFragment;

import java.util.ArrayList;

public class GroupCreateActivity extends AppCompatActivity {


    public String loginUserID;

    Button btnGroupRequest;

    public ArrayList<User> arrayListFriendsAll;
    public CustomChoiceListViewAdapter adapter;
    public ListView friendsListView;


    public SecondFragment secondFragment;

    EditText editsearch;
    public static Toast mToast;

    public android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar =  getSupportActionBar();
        actionBar.setTitle("그룹 생성");
        setContentView(R.layout.activity_new_group);

        mToast = Toast.makeText(this, "null", Toast.LENGTH_SHORT);


        Intent intent = getIntent();

        loginUserID = intent.getStringExtra("loginUserID");


        secondFragment = SecondFragment.getInstance();

        arrayListFriendsAll = secondFragment.arrayListFriendsAll;

        adapter = new CustomChoiceListViewAdapter(this, arrayListFriendsAll);
        friendsListView = (ListView) findViewById(R.id.listViewFriends);
        friendsListView.setAdapter(adapter);


        editsearch = (EditText) findViewById(R.id.friendSearch);


        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable edit) {

                String filterText = edit.toString();
                ((CustomChoiceListViewAdapter) friendsListView.getAdapter()).getFilter().filter(filterText);

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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // res 폴더 내의 menu_main.xml이 있는 경우 실행 가능한 함수다.
        getMenuInflater().inflate(R.menu.menu_new_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {

        if (menu.getItemId() == R.id.complete) {
            SparseBooleanArray checkedItems = friendsListView.getCheckedItemPositions();
            int count = adapter.getCount();

            boolean isSelected = false;

            String friendsId = "";
            for (int i = count - 1; i >= 0; i--) {
                if (checkedItems.get(i)) {
                    friendsId = friendsId + adapter.getItem(i).getId() + ", ";
                    isSelected = true;
                }
            }
            if(isSelected) {
                final String friendsId2 = friendsId.substring(0, friendsId.length() - 2);

                final EditText etEdit = new EditText(GroupCreateActivity.this);

                LayoutInflater inflater = LayoutInflater.from(GroupCreateActivity.this);

                // 기본 dialog를 선언한다.
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupCreateActivity.this);
                builder.setMessage("그룹이름을 입력하세요.");
                builder.setView(etEdit);
                builder.setCancelable(false); // Cancel 버튼 없이 Yes, No 버튼만을 사용한다.


                // Yes버튼 눌렀을 경우 리스너 등록
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int id) {
                        Log.e("editText : ", etEdit.getText().toString());
                        if (!etEdit.getText().toString().equals(null)) {

                            GroupRegistTask groupRegistTask = new GroupRegistTask(GroupCreateActivity.this, etEdit.getText().toString(), friendsId2);
                            groupRegistTask.execute();
                        }


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
                alert.setTitle("그룹 생성");
                // Icon을 지정한다.
                alert.setIcon(R.mipmap.ic_launcher);
                // dialog를 보여준다.
                alert.show();
            }
            else{
                mToast.setText("하나 이상의 인원을 선택해 주세요.");
                mToast.show();
            }


        }

        return super.onOptionsItemSelected(menu);
    }

}
