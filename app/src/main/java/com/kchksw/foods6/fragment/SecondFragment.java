package com.kchksw.foods6.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kchksw.foods6.Adapter.UserFriendAdapter;
import com.kchksw.foods6.AsyncTask.FriendPermissionCheckTask;
import com.kchksw.foods6.AsyncTask.FriendsProfileLoadTask;
import com.kchksw.foods6.AsyncTask.RequestSharePermissionTask;
import com.kchksw.foods6.R;
import com.kchksw.foods6.etc.User;

import org.json.JSONArray;

import java.util.ArrayList;

public class SecondFragment extends Fragment {


    String jsonFriends;

    public ArrayList<User> arrayListFriendsAll;
    public ArrayList<User> arrayListFriendsFollowing;
    public ArrayList<User> arrayListFriendsNotFollowing;
    public ArrayList<User> arrayListFriendsFollower;

    public UserFriendAdapter userFriendAdapter;

    public User sendUserInfo;

    public static SecondFragment context;

    public static Toast mToast;


    Button btnShowAll;
    Button btnShowFollowing;
    Button btnShowNotFollowing;
    Button btnShowFollower;

    View view;

    ListView friendsListView;
    EditText editsearch;

    public User longClickedfrined = null;

    public SecondFragment(String jsonFriends) {
        this.jsonFriends = jsonFriends;
        context = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_second, container, false);

        mToast = Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT);

        JSONArray friendslist;

        arrayListFriendsAll = new ArrayList<User>();
        arrayListFriendsFollowing = new ArrayList<User>();
        arrayListFriendsNotFollowing = new ArrayList<User>();
        arrayListFriendsFollower = new ArrayList<User>();


        btnShowAll = (Button) view.findViewById(R.id.btnShowAll);
        btnShowFollowing = (Button) view.findViewById(R.id.btnShowFollowing);
        btnShowNotFollowing = (Button) view.findViewById(R.id.btnShowNotFollowing);
        btnShowFollower = (Button) view.findViewById(R.id.btnShowFollower);


        btnShowAll.setTextColor(Color.RED);


        userFriendAdapter = new UserFriendAdapter(view.getContext(), arrayListFriendsAll); // simple textview for list item
        friendsListView = (ListView) view.findViewById(R.id.listViewFriends);
        friendsListView.setAdapter(userFriendAdapter);


        editsearch = (EditText) view.findViewById(R.id.friendSearch);
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable edit) {
                btnShowAll.performClick();
                String filterText = edit.toString();
                ((UserFriendAdapter) friendsListView.getAdapter()).getFilter().filter(filterText);

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
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }


        });


        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShowAll.setTextColor(Color.RED);
                btnShowFollowing.setTextColor(Color.WHITE);
                btnShowNotFollowing.setTextColor(Color.WHITE);
                btnShowFollower.setTextColor(Color.WHITE);

                userFriendAdapter = new UserFriendAdapter(view.getContext(), arrayListFriendsAll); // simple textview for list item
                friendsListView = (ListView) view.findViewById(R.id.listViewFriends);
                friendsListView.setAdapter(userFriendAdapter);
                userFriendAdapter.notifyDataSetChanged();
            }
        });

        btnShowFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShowAll.setTextColor(Color.WHITE);
                btnShowFollowing.setTextColor(Color.RED);
                btnShowNotFollowing.setTextColor(Color.WHITE);
                btnShowFollower.setTextColor(Color.WHITE);

                userFriendAdapter = new UserFriendAdapter(view.getContext(), arrayListFriendsFollowing); // simple textview for list item
                friendsListView = (ListView) view.findViewById(R.id.listViewFriends);
                friendsListView.setAdapter(userFriendAdapter);
                userFriendAdapter.notifyDataSetChanged();
            }
        });

        btnShowNotFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShowAll.setTextColor(Color.WHITE);
                btnShowFollowing.setTextColor(Color.WHITE);
                btnShowNotFollowing.setTextColor(Color.RED);
                btnShowFollower.setTextColor(Color.WHITE);

                userFriendAdapter = new UserFriendAdapter(view.getContext(), arrayListFriendsNotFollowing); // simple textview for list item
                friendsListView = (ListView) view.findViewById(R.id.listViewFriends);
                friendsListView.setAdapter(userFriendAdapter);
                userFriendAdapter.notifyDataSetChanged();
            }
        });

        btnShowFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShowAll.setTextColor(Color.WHITE);
                btnShowFollowing.setTextColor(Color.WHITE);
                btnShowNotFollowing.setTextColor(Color.WHITE);
                btnShowFollower.setTextColor(Color.RED);

                userFriendAdapter = new UserFriendAdapter(view.getContext(), arrayListFriendsFollower); // simple textview for list item
                friendsListView = (ListView) view.findViewById(R.id.listViewFriends);
                friendsListView.setAdapter(userFriendAdapter);
                userFriendAdapter.notifyDataSetChanged();
            }
        });


        FriendsProfileLoadTask friendsProfileLoadTask = new FriendsProfileLoadTask(jsonFriends, this);
        friendsProfileLoadTask.execute();


        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // 1) AdapterView<?> parentView
            // parentґВ Е¬ёЇµИ ЗЧёсАЗ єОёрєдАО ѕоґрЕН єдАМґЩ.
            // ё®ЅєЖ® єдАЗ ЗЧёсА» Е¬ёЇ ЗЯґЩёй, parentґВ Listview АМґЩ.
            // 2) View v
            // viewґВ »зїлАЪ°Ў Е¬ёЇЗС ЗЧёсїЎ ЗШґзЗПґВ єдАМґЩ.
            // 3) int position
            // ListviewАЗ ј±ЕГµИ ЗЧёсАЗ А§ДЎ
            // 4) long id
            // idґВ position °ъ µїАПЗПґЩ°н єёёйµК.
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO ѕЖАМЕЫ Е¬ёЇЅГїЎ ±ёЗцЗТ і»їлАє ї©±вїЎ.

                sendUserInfo = new User();

                sendUserInfo.setId(userFriendAdapter.getItem(position).getId());
                sendUserInfo.setName(userFriendAdapter.getItem(position).getName());


                FriendPermissionCheckTask friendPermissionCheckTask = new FriendPermissionCheckTask(context, sendUserInfo);
                friendPermissionCheckTask.execute();

            }
        });
        friendsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()

        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
                                           long id) {


                longClickedfrined = userFriendAdapter.getItem(position);


                if (longClickedfrined.isFollowing()) {
                    mToast.setText("이미 " + longClickedfrined.getName() + "(을)를 Following 할 수 있습니다.");
                    mToast.show();

                    // 클릭과 Long클릭 중복 사용하기~! (return ture 설정하면 Long클릭 후 클릭은 처리 안됨)
                    return true; // 다음 이벤트 계속 진행 false, 이벤트 완료 true


                } else {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());

                    // 기본 dialog를 선언한다.
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("공유 요청을 보내시겠습니까?");
                    builder.setCancelable(false); // Cancel 버튼 없이 Yes, No 버튼만을 사용한다.


                    // Yes버튼 눌렀을 경우 리스너 등록
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                        public void onClick(DialogInterface dialog, int id) {


                            RequestSharePermissionTask requestSharePermissionTask = new RequestSharePermissionTask(context, longClickedfrined);
                            requestSharePermissionTask.execute();


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
                    alert.setTitle("Following 요청");
                    // Icon을 지정한다.
                    alert.setIcon(R.drawable.foods_launcher);
                    // dialog를 보여준다.
                    alert.show();
                }

                return false;
            }
        });

        return view;
    }


    public static SecondFragment getInstance() {

        return context;
    }


}
