package com.kchksw.foods6.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kchksw.foods6.Activity.GroupInviteActivity;
import com.kchksw.foods6.Activity.GroupMemberActivity;
import com.kchksw.foods6.Activity.MainActivity;
import com.kchksw.foods6.Activity.MapLoadActivity;
import com.kchksw.foods6.Adapter.GroupAdapter;
import com.kchksw.foods6.Adapter.UserMeAdapter;
import com.kchksw.foods6.AsyncTask.GroupExitTask;
import com.kchksw.foods6.AsyncTask.GroupNameChangeTask;
import com.kchksw.foods6.R;
import com.kchksw.foods6.etc.Group;
import com.kchksw.foods6.etc.User;
import com.kchksw.foods6.etc.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FirstFragment extends Fragment {
    String jsonGroups;
    String jsonMe;
    String facebook_id;

    public ArrayList<User> meInfo;
    public ArrayList<Group> arrayListGroup;

    public ListView meListView;
    public UserMeAdapter meAdapter;

    public GroupAdapter groupAdapter;
    public ListView groupsListView;

    User sendUserInfo;
    Group sendGroupInfo;

    public TextView textGroupName;

    public static Toast mToast;

    public static FirstFragment context;

    public AlertDialog mAlertDialog;

    public FirstFragment(String jsonMe, String jsonGroups) {
        this.jsonGroups = jsonGroups;
        this.jsonMe = jsonMe;
        context = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        mToast = Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT);

        JSONObject me;
        meInfo = new ArrayList<User>();

        meAdapter = new UserMeAdapter(view.getContext(), meInfo); // simple textview for list item
        meListView = (ListView) view.findViewById(R.id.listViewMe);


        meListView.setAdapter(meAdapter);



        try {
            me = new JSONObject(jsonMe);
            ((MainActivity)getActivity()).loginUser.setName(me.getString("name"));
            ((MainActivity)getActivity()).loginUser.setId(me.getString("id"));
            meInfo.add(((MainActivity)getActivity()).loginUser);


        } catch (JSONException e) {
            e.printStackTrace();
        }


      /*  try {
            me = new JSONObject(jsonMe);
            MyProfilePictureLoadTask myProfilePictureLoadTask = new MyProfilePictureLoadTask(me, this);
            myProfilePictureLoadTask.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/



        meListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                Log.d("login Info", ((MainActivity) getActivity()).loginUser.getName());
                sendUserInfo = meInfo.get(position);

                sendUserInfo.setImage(null);

                Intent intent = new Intent(getActivity(),
                        MapLoadActivity.class);
                    /*
                     * ComponentName componentName = new
					 * ComponentName("activity", "activity.BookInfoActivity");
					 * intent.setComponent(componentName);
					 */
                intent.putExtra("sendUserInfo", sendUserInfo);
                intent.putExtra("callType", Util.CALL_BY_MYINFO);


                startActivity(intent);
            }
        });


        JSONObject group_json_object;
        JSONArray group_json_array;
        arrayListGroup = new ArrayList<Group>();

        try {
            group_json_object = new JSONObject(jsonGroups);
            group_json_array = group_json_object.getJSONArray("groups");


            for (int l = 0; l < group_json_array.length(); l++) {
                Group group = new Group();
                group.setGroups_name(group_json_array.getJSONObject(l).getString("groups_name"));
                group.setGroups_id(group_json_array.getJSONObject(l).getString("groups_id"));
                group.setGroups_member_count(group_json_array.getJSONObject(l).getString("count(*)"));

                String groupMember = "";
                String groupMemberId = "";
                for (int k = 0; k < Integer.parseInt(group.getGroups_member_count()); k++) {
                    String jsonUserName_Id = group_json_array.getJSONObject(l).getString(k + "");

                    JSONObject json = new JSONObject(jsonUserName_Id);
                    String user_name = json.getString("user_name");
                    String user_id = json.getString("facebook_id");
                    groupMember = groupMember + user_name + ", ";
                    groupMemberId = groupMemberId + user_id + ", ";
                }
                String finalGroupMember = groupMember.substring(0, groupMember.length() - 2);
                String finalGroupMemberId = groupMemberId.substring(0, groupMemberId.length() - 2);

                Log.e("유저이름 ! ", finalGroupMember);
                group.setGroups_members(finalGroupMember);
                group.setGroups_members_id(finalGroupMemberId);

                arrayListGroup.add(group);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        groupAdapter = new GroupAdapter(view.getContext(), arrayListGroup); // simple textview for list item
        groupsListView = (ListView) view.findViewById(R.id.listViewGroups);
        groupsListView.setAdapter(groupAdapter);

        Util.groupArrayListSort(arrayListGroup);


        groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                sendGroupInfo = arrayListGroup.get(position);

                Intent intent = new Intent(getActivity(),
                        MapLoadActivity.class);
                    /*
                     * ComponentName componentName = new
					 * ComponentName("activity", "activity.BookInfoActivity");
					 * intent.setComponent(componentName);
					 */
                intent.putExtra("sendGroupInfo", sendGroupInfo);
                intent.putExtra("callType", Util.CALL_BY_MYGROUP);

                startActivity(intent);
            }
        });

        groupsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());

                // 기본 dialog를 선언한다.

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(null);

                // custom으로 만들어놓은 dialog를 할당한다.
                final View customDialogView = inflater.inflate(R.layout.dialog_group_management, null, false);

                textGroupName = (TextView) customDialogView.findViewById(R.id.textGroupName);
                TextView textMapShow = (TextView) customDialogView.findViewById(R.id.textMapShow);
                TextView textGroupMemberShow = (TextView) customDialogView.findViewById(R.id.textGroupMemberShow);
                TextView textGroupNameChange = (TextView) customDialogView.findViewById(R.id.textGroupNameChange);
                TextView textFriendInvite = (TextView) customDialogView.findViewById(R.id.textFriendInvite);
                TextView textGroupExit = (TextView) customDialogView.findViewById(R.id.textGroupExit);


                textGroupName.setText(arrayListGroup.get(position).getGroups_name());


                builder.setView(customDialogView);

                // 정의한 dialog를 생성하고 보여준다.
                mAlertDialog = builder.create();
                mAlertDialog.show();


                textMapShow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendGroupInfo = arrayListGroup.get(position);

                        Intent intent = new Intent(getActivity(),
                                MapLoadActivity.class);
                    /*
                     * ComponentName componentName = new
					 * ComponentName("activity", "activity.BookInfoActivity");
					 * intent.setComponent(componentName);
					 */
                        intent.putExtra("sendGroupInfo", sendGroupInfo);
                        intent.putExtra("callType", Util.CALL_BY_MYGROUP);

                        startActivity(intent);
                        mAlertDialog.cancel();
                    }
                });


                textGroupMemberShow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendGroupInfo = arrayListGroup.get(position);

                        Intent intent = new Intent(getActivity(),
                                GroupMemberActivity.class);
                    /*
                     * ComponentName componentName = new
					 * ComponentName("activity", "activity.BookInfoActivity");
					 * intent.setComponent(componentName);
					 */
                        intent.putExtra("sendGroupInfo", sendGroupInfo);

                        startActivity(intent);
                        mAlertDialog.cancel();
                    }
                });

                textGroupNameChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        sendGroupInfo = arrayListGroup.get(position);

                        final EditText etEdit = new EditText(getActivity());

                        LayoutInflater inflater = LayoutInflater.from(getActivity());

                        // 기본 dialog를 선언한다.
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("변경할 그룹이름을 입력하세요.");
                        builder.setView(etEdit);
                        builder.setCancelable(false); // Cancel 버튼 없이 Yes, No 버튼만을 사용한다.


                        // Yes버튼 눌렀을 경우 리스너 등록
                        builder.setPositiveButton("변경", new DialogInterface.OnClickListener() {

                            Group sendGroupInfo = arrayListGroup.get(position);

                            public void onClick(DialogInterface dialog, int id) {

                                if (!etEdit.getText().toString().equals(null)) {
                                    GroupNameChangeTask groupNameChangeTask = new GroupNameChangeTask(sendGroupInfo, etEdit.getText().toString(), ((MainActivity) getActivity()).loginUser.getId());
                                    groupNameChangeTask.execute();

                                }


                            }
                            // No버튼을 눌렀을 경우 리스너 등록
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                        // 위에서 정의한 dialog를 생성한다.
                        AlertDialog alert = builder.create();
                        // Title을 지정한다.
                        alert.setTitle("그룹명 변경");
                        // Icon을 지정한다.
                        alert.setIcon(R.drawable.foods_launcher);
                        // dialog를 보여준다.
                        alert.show();
                        mAlertDialog.cancel();

                    }
                });

                textFriendInvite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendGroupInfo = arrayListGroup.get(position);

                        Intent intent = new Intent(getActivity(),
                                GroupInviteActivity.class);
                    /*
                     * ComponentName componentName = new
					 * ComponentName("activity", "activity.BookInfoActivity");
					 * intent.setComponent(componentName);
					 */
                        intent.putExtra("sendGroupInfo", sendGroupInfo);
                        startActivity(intent);
                        mAlertDialog.cancel();
                    }
                });


                textGroupExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendGroupInfo = arrayListGroup.get(position);


                        LayoutInflater inflater = LayoutInflater.from(getActivity());

                        // 기본 dialog를 선언한다.
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("정말 그룹에서 나가시나요?");

                        builder.setCancelable(false); // Cancel 버튼 없이 Yes, No 버튼만을 사용한다.


                        // Yes버튼 눌렀을 경우 리스너 등록
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                            public void onClick(DialogInterface dialog, int id) {

                                GroupExitTask groupExitTask = new GroupExitTask(sendGroupInfo);
                                groupExitTask.execute();
                            }
                            // No버튼을 눌렀을 경우 리스너 등록
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                        // 위에서 정의한 dialog를 생성한다.
                        AlertDialog alert = builder.create();
                        // Title을 지정한다.
                        alert.setTitle("그룹 나가기");
                        // Icon을 지정한다.
                        alert.setIcon(R.drawable.foods_launcher);
                        // dialog를 보여준다.
                        alert.show();
                        mAlertDialog.cancel();


                    }
                });


                return true;
            }
        });

        return view;
    }

    public static FirstFragment getInstance() {

        return context;
    }
}