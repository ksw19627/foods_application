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
            // parent???? ???????????? ???????????? ???????????????? ???????????? ????????????.
            // ???????????? ???????? ???????????? ???????? ????????????, parent???? Listview ????????.
            // 2) View v
            // view???? ???????????????? ???????????? ???????????? ???????????????? ????????????.
            // 3) int position
            // Listview???? ???????????? ???????????? ????????
            // 4) long id
            // id???? position ???? ???????????????????? ????????????.
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO ???????????? ???????????????? ???????????? ???????????? ????????????.
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

                Log.e("???????????? ! ", finalGroupMember);
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
            // parent???? ???????????? ???????????? ???????????????? ???????????? ????????????.
            // ???????????? ???????? ???????????? ???????? ????????????, parent???? Listview ????????.
            // 2) View v
            // view???? ???????????????? ???????????? ???????????? ???????????????? ????????????.
            // 3) int position
            // Listview???? ???????????? ???????????? ????????
            // 4) long id
            // id???? position ???? ???????????????????? ????????????.
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO ???????????? ???????????????? ???????????? ???????????? ????????????.
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

                // ?????? dialog??? ????????????.

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(null);

                // custom?????? ??????????????? dialog??? ????????????.
                final View customDialogView = inflater.inflate(R.layout.dialog_group_management, null, false);

                textGroupName = (TextView) customDialogView.findViewById(R.id.textGroupName);
                TextView textMapShow = (TextView) customDialogView.findViewById(R.id.textMapShow);
                TextView textGroupMemberShow = (TextView) customDialogView.findViewById(R.id.textGroupMemberShow);
                TextView textGroupNameChange = (TextView) customDialogView.findViewById(R.id.textGroupNameChange);
                TextView textFriendInvite = (TextView) customDialogView.findViewById(R.id.textFriendInvite);
                TextView textGroupExit = (TextView) customDialogView.findViewById(R.id.textGroupExit);


                textGroupName.setText(arrayListGroup.get(position).getGroups_name());


                builder.setView(customDialogView);

                // ????????? dialog??? ???????????? ????????????.
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

                        // ?????? dialog??? ????????????.
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("????????? ??????????????? ???????????????.");
                        builder.setView(etEdit);
                        builder.setCancelable(false); // Cancel ?????? ?????? Yes, No ???????????? ????????????.


                        // Yes?????? ????????? ?????? ????????? ??????
                        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {

                            Group sendGroupInfo = arrayListGroup.get(position);

                            public void onClick(DialogInterface dialog, int id) {

                                if (!etEdit.getText().toString().equals(null)) {
                                    GroupNameChangeTask groupNameChangeTask = new GroupNameChangeTask(sendGroupInfo, etEdit.getText().toString(), ((MainActivity) getActivity()).loginUser.getId());
                                    groupNameChangeTask.execute();

                                }


                            }
                            // No????????? ????????? ?????? ????????? ??????
                        }).setNegativeButton("??????", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                        // ????????? ????????? dialog??? ????????????.
                        AlertDialog alert = builder.create();
                        // Title??? ????????????.
                        alert.setTitle("????????? ??????");
                        // Icon??? ????????????.
                        alert.setIcon(R.drawable.foods_launcher);
                        // dialog??? ????????????.
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

                        // ?????? dialog??? ????????????.
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("?????? ???????????? ????????????????");

                        builder.setCancelable(false); // Cancel ?????? ?????? Yes, No ???????????? ????????????.


                        // Yes?????? ????????? ?????? ????????? ??????
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                            public void onClick(DialogInterface dialog, int id) {

                                GroupExitTask groupExitTask = new GroupExitTask(sendGroupInfo);
                                groupExitTask.execute();
                            }
                            // No????????? ????????? ?????? ????????? ??????
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                        // ????????? ????????? dialog??? ????????????.
                        AlertDialog alert = builder.create();
                        // Title??? ????????????.
                        alert.setTitle("?????? ?????????");
                        // Icon??? ????????????.
                        alert.setIcon(R.drawable.foods_launcher);
                        // dialog??? ????????????.
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