package com.kchksw.foods6.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kchksw.foods6.Adapter.ShareRequestAdapter;
import com.kchksw.foods6.AsyncTask.RequestShareAcceptTask;
import com.kchksw.foods6.AsyncTask.ShareRequestCheckTask;
import com.kchksw.foods6.R;
import com.kchksw.foods6.etc.User;

import java.util.ArrayList;



public class
ThirdFragment extends Fragment
{
    public ShareRequestAdapter shareRequestAdapter;
    public ArrayList<User> shareRequestArrayListFollower;
    public ArrayList<User> shareRequestArrayListFollowing;

    public TextView textViewShareRequest;
    public ListView shareRequestListView;

    Button btnFollowingRequest;
    Button btnFollowerRequest;

    public final int SELECTED_FOLLOWING = 0;
    public final int SELECTED_FOLLOWER = 1;

    public int selectBtn;

    View view;

    public static ThirdFragment thirdFragment;
    public ThirdFragment()
    {
        this.thirdFragment = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_third, container, false);

        shareRequestArrayListFollowing = new ArrayList<User>();
        shareRequestArrayListFollower = new ArrayList<User>();

        textViewShareRequest = (TextView)view.findViewById(R.id.TextViewRequestShare);


        ShareRequestCheckTask shareRequestCheckTask = new ShareRequestCheckTask(this);
        shareRequestCheckTask.execute();


        btnFollowingRequest = (Button) view.findViewById(R.id.btnFollowingRequest);
        btnFollowerRequest = (Button) view.findViewById(R.id.btnFollowerRequest);


        btnFollowingRequest.setTextColor(Color.RED);
        selectBtn = SELECTED_FOLLOWING;

        btnFollowingRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFollowingRequest.setTextColor(Color.RED);
                btnFollowerRequest.setTextColor(Color.WHITE);
                selectBtn = SELECTED_FOLLOWING;

                shareRequestAdapter = new ShareRequestAdapter(view.getContext(), shareRequestArrayListFollowing, selectBtn); // simple textview for list item
                shareRequestListView = (ListView) view.findViewById(R.id.listViewRequestShare);
                shareRequestListView.setAdapter(shareRequestAdapter);
                shareRequestAdapter.notifyDataSetChanged();

                if(shareRequestArrayListFollowing.size() != 0){
                    shareRequestListView.setVisibility(View.VISIBLE);
                    textViewShareRequest.setVisibility(View.GONE);
                }else{
                    shareRequestListView.setVisibility(View.GONE);
                    textViewShareRequest.setVisibility(View.VISIBLE);
                }
            }
        });

        btnFollowerRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFollowingRequest.setTextColor(Color.WHITE);
                btnFollowerRequest.setTextColor(Color.RED);
                selectBtn = SELECTED_FOLLOWER;

                shareRequestAdapter = new ShareRequestAdapter(view.getContext(), shareRequestArrayListFollower, selectBtn); // simple textview for list item
                shareRequestListView = (ListView) view.findViewById(R.id.listViewRequestShare);
                shareRequestListView.setAdapter(shareRequestAdapter);
                shareRequestAdapter.notifyDataSetChanged();

                if(shareRequestArrayListFollower.size() != 0){
                    shareRequestListView.setVisibility(View.VISIBLE);
                    textViewShareRequest.setVisibility(View.GONE);
                }else{
                    shareRequestListView.setVisibility(View.GONE);
                    textViewShareRequest.setVisibility(View.VISIBLE);
                }
            }
        });


        shareRequestAdapter = new ShareRequestAdapter(view.getContext(), shareRequestArrayListFollowing, selectBtn); // simple textview for list item
        shareRequestListView = (ListView) view.findViewById(R.id.listViewRequestShare);
        shareRequestListView.setAdapter(shareRequestAdapter);

        shareRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if(selectBtn == SELECTED_FOLLOWER) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());

                    // 기본 dialog를 선언한다.
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("공유 요청을 수락하시겠습니까?");
                    builder.setCancelable(false); // Cancel 버튼 없이 Yes, No 버튼만을 사용한다.

                    // Yes버튼 눌렀을 경우 리스너 등록
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                        public void onClick(DialogInterface dialog, int id) {


                            RequestShareAcceptTask requestShareAcceptTask = new RequestShareAcceptTask(thirdFragment, position);
                            requestShareAcceptTask.execute();

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
                    alert.setTitle("Title");
                    // Icon을 지정한다.
                    alert.setIcon(R.mipmap.ic_launcher);
                    // dialog를 보여준다.
                    alert.show();
                }
            }
        });
        return view;
    }
}
