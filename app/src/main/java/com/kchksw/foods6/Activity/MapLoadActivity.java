package com.kchksw.foods6.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.kchksw.foods6.Adapter.MyInfoWindowAdapter;
import com.kchksw.foods6.AsyncTask.MarkerLoadTask;
import com.kchksw.foods6.AsyncTask.ModifyLocationTask;
import com.kchksw.foods6.Model.Model;
import com.kchksw.foods6.R;
import com.kchksw.foods6.etc.Group;
import com.kchksw.foods6.etc.User;
import com.kchksw.foods6.etc.Util;

import java.util.List;

import static com.kchksw.foods6.R.id.map;


// 네비게이션 액티비티의 하위 프래그먼트 중 하나
// 구글맵지도를 띄워 내가 등록한 업체들을 마커로 표시해주는 클래스
public class MapLoadActivity extends AppCompatActivity implements OnMapReadyCallback, OnInfoWindowClickListener {

    MapView mMapView; // 맵뷰 선언
    public GoogleMap mMap; // 구글맵 선언

    public User receiveUser;
    public Group receiveGroup;
    public int callType;


    FloatingActionMenu fab_Menu;
    FloatingActionButton fab_upload, fab_button;


    public ImageView image;
    public TextView tvStore;
    public TextView tvFood;
    public RatingBar ratingBar;
    public TextView tvComment;
    public TextView tvCategory;
    public Button btnModify;

    double curLatitude;
    double curLongitude;

    public Marker clickedMarker;

    public android.support.v7.app.ActionBar actionBar;

    LocationManager mLocationManager;

    public static MapLoadActivity mapLoadActivity;

    public MapLoadActivity() {
        mapLoadActivity = this;
    }


    //ArrayList<MyMarkerItem> model.myMarkerItems = new ArrayList<MyMarkerItem>();

    // 인포윈도우(말풍선) 클릭시 호출되는 함수
    // showDetailInfo함수로 등록된 세부 정보를 보여줌
    @Override
    public void onInfoWindowClick(Marker marker) {
        // Toast.makeText(this, "Info Window clicked@" + marker.getId(), Toast.LENGTH_SHORT).show();

        clickedMarker = marker;
        // 클릭된 마커의 정보를 모델에서 찾아 웹서버로부터 이미지를 load해 온다.
        int index = -1;
        for (int i = 0; i < Model.myMarkerItems.size(); i++) {
            if (("m" + Model.myMarkerItems.get(i).getId()).equals(marker.getId())) {
                index = i;
                break;
            }
        }


        Intent intent = new Intent(getApplication(), ShowDetailActivity.class); // 회원가입 화면으로 가기위한 화면을 intent에 담아 생성

        if (callType == Util.CALL_BY_MYINFO) {
            intent.putExtra("sendUserInfo", receiveUser);
            intent.putExtra("callType", Util.CALL_BY_MYINFO);

        } else if (callType == Util.CALL_BY_MYFRINED) {
            intent.putExtra("sendUserInfo", receiveUser);
            intent.putExtra("callType", Util.CALL_BY_MYFRINED);

        } else if (callType == Util.CALL_BY_MYGROUP) {
            intent.putExtra("sendGroupInfo", receiveGroup);
            intent.putExtra("callType", Util.CALL_BY_MYGROUP);
        }


        intent.putExtra("index", index);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_maps);


        // 구글맵을 띄움
        mMapView = (MapView) findViewById(map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);


    }


    @Override
    public void onResume() {
        super.onResume();

        // destroy all menu and re-call onCreateOptionsMenu
        this.invalidateOptionsMenu();
    }

    /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_map, menu);

    }
*/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true); // zoom을 컨트롤할수 있는 버튼을 활성화
        mMap.getUiSettings().setCompassEnabled(true); // 나침반 활성화

        mMap.getUiSettings().setMapToolbarEnabled(false); // 구글앱 연결 아이콘 비활성화
        mMap.getUiSettings().setRotateGesturesEnabled(false); // 구글맵 회전 비활성화
        mMap.getUiSettings().setMyLocationButtonEnabled(false); // 버튼클릭 비활성화
        mMap.setMyLocationEnabled(true); // 내 위치 보기 활성화
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this)); // infoWindow 어댑터를 등록
        mMap.setOnInfoWindowClickListener(this); // 인포윈도우 클릭 이벤트를 처리할 리스너 등록


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                Log.e("전", marker.getPosition().latitude + "");
                int zoom = (int) mMap.getCameraPosition().zoom;
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude + (double) 160 / Math.pow(2, zoom), marker.getPosition().longitude), zoom);


                Log.e("후", marker.getPosition().latitude + (double) 90 / Math.pow(2, zoom) + "");
                mMap.animateCamera(cu);

                marker.showInfoWindow();


                return true;
            }
        });

        // 마커를 드래그했을때의 이벤트 처리
        mMap.setOnMarkerDragListener(new OnMarkerDragListener() {

            double prev_latitude; // 드래그 되기 전 위도
            double prev_longitude; // 드래그 되기 전 경도

            // 드래그를 시작할 때 불려지는 함수
            @Override
            public void onMarkerDragStart(Marker marker) {

                // 현재 선택된 마커의 id와 일치하는 정보를 모델에서 찾아
                // 그 모델의 위도와 경도를 저장해 놓는다.(드래그 취소시 사용)
                for (int i = 0; i < Model.myMarkerItems.size(); i++) {
                    if (("m" + Model.myMarkerItems.get(i).getId()).equals(marker.getId())) {
                        prev_latitude = Model.myMarkerItems.get(i).getLat();
                        prev_longitude = Model.myMarkerItems.get(i).getLon();
                        Log.d("Prev_latitude", prev_latitude + "");
                        Log.d("Prev_longitude", prev_longitude + "");
                        break;
                    }
                }
            }

            // 드래그를 마쳐 손이 떼질 때 호출되는 함수
            // DialogLocationModify 함수를 통해 dialog를 띄워 유저에게 한번더 물어본다.
            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker marker) {
                // TODO Auto-generated method stub
                DialogLocationModify(marker, prev_latitude, prev_longitude);
            }

            // 드래그 중일 때 호출되는 함수
            @Override
            public void onMarkerDrag(Marker marker) {
                // TODO Auto-generated method stub

            }
        });

        // 로그인시 intent로 가져온 email 주소를 string 변수에 저장한다.
        Bundle bundle = getIntent().getExtras();
        callType = bundle.getInt("callType");

        if (callType == Util.CALL_BY_MYINFO || callType == Util.CALL_BY_MYFRINED) {
            receiveUser = bundle.getParcelable("sendUserInfo");
        }
        if (callType == Util.CALL_BY_MYGROUP) {
            receiveGroup = bundle.getParcelable("sendGroupInfo");
        }
        actionBar = getSupportActionBar();
        if (callType == Util.CALL_BY_MYINFO || callType == Util.CALL_BY_MYFRINED) {
            actionBar.setTitle(receiveUser.getName() + " - Map");
        }
        if (callType == Util.CALL_BY_MYGROUP) {
            actionBar.setTitle(receiveGroup.getGroups_name() + " - Map");
        }


        //  setHasOptionsMenu(true);
        fab_Menu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        fab_upload = (FloatingActionButton) findViewById(R.id.fab1);
        fab_button = (FloatingActionButton) findViewById(R.id.fab2);


        fab_upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                Intent intent = new Intent(MapLoadActivity.this, FirstUploadActivity.class);

                if (callType == Util.CALL_BY_MYINFO) {
                    intent.putExtra("sendUserInfo", receiveUser);
                    intent.putExtra("callType", Util.CALL_BY_MYINFO);

                } else if (callType == Util.CALL_BY_MYGROUP) {
                    intent.putExtra("sendGroupInfo", receiveGroup);
                    intent.putExtra("callType", Util.CALL_BY_MYGROUP);

                }

                intent.putExtra("callType", callType);
                startActivity(intent);

            }
        });
        fab_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked


                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location myLocation = getLastKnownLocation();

                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    curLatitude = myLocation.getLatitude();
                    curLongitude = myLocation.getLongitude();


                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLatitude, curLongitude), 14), 1500, null);


                    // double lat = mMap.getMyLocation().getLatitude();
                    //    double lon = mMap.getMyLocation().getLatitude();
                } else {
                    Toast.makeText(mapLoadActivity, "GPS를 켜주세요.", Toast.LENGTH_SHORT).show();
                }


            }
        });


        if (callType == Util.CALL_BY_MYINFO || callType == Util.CALL_BY_MYFRINED) {

            if (callType == Util.CALL_BY_MYFRINED) {
                fab_Menu.setVisibility(View.GONE);

            }

            String facebook_id = receiveUser.getId();

            // 지도에 내가 등록한 업체의 marker를 생성한다.
            MarkerLoadTask mMarkerLoadTask;
            mMarkerLoadTask = new MarkerLoadTask(mMap, facebook_id, callType);
            mMarkerLoadTask.execute();

        } else if (callType == Util.CALL_BY_MYGROUP) {
            String group_id = receiveGroup.getGroups_id();

            // 지도에 내가 등록한 업체의 marker를 생성한다.
            MarkerLoadTask mMarkerLoadTask;
            mMarkerLoadTask = new MarkerLoadTask(mMap, group_id, callType);
            mMarkerLoadTask.execute();
        }


    }

    // 마커를 드래그했을시 드래그에서 떼어졌을 때, 위치를 변경하는것이 맞는지 확인하는 함수
    private void DialogLocationModify(final Marker marker, final double prev_latitude, final double prev_longitude) {


        // dialog를 정의한다.
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("위치를 변경 하시겠습니까?");
        alt_bld.setCancelable(false); // Cancel 버튼 없이 Yes, No 버튼만을 사용한다.

        // Yes버튼 눌렀을 경우 리스너 등록
        alt_bld.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                // 현재 이동된 마커와 일치하는 모델을 찾는다.
                for (int i = 0; i < Model.myMarkerItems.size(); i++) {
                    if (("m" + Model.myMarkerItems.get(i).getId()).equals(marker.getId())) {


                        // AsynTask 클래스를 실행시켜 현재 이동된 마커의 정보로 웹 서버 DB를 업데이트 시킨다.
                        ModifyLocationTask mAuthTask;
                        mAuthTask = new ModifyLocationTask(i, marker.getPosition().latitude, marker.getPosition().longitude);
                        mAuthTask.execute((Void) null);

                        break;
                    }
                }


            }
            // No버튼을 눌렀을 경우 리스너 등록
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                // 마커의 위치를 드래그시작에 저장된 값으로 다시 이동시킨다.
                marker.setPosition(new LatLng(prev_latitude, prev_longitude));
                dialog.cancel();
            }
        });
        // 위에서 정의한 dialog를 생성한다.
        AlertDialog alert = alt_bld.create();
        // Title을 지정한다.
        alert.setTitle("장소변경");
        // Icon을 지정한다.
        alert.setIcon(R.drawable.foods_launcher);
        // dialog를 보여준다.
        alert.show();
    }


    // 윈도우인포(말풍선)을 클릭시 dialog를 보여주는 함수
    public void showDetailInfo(Marker marker) {

        LayoutInflater inflater = LayoutInflater.from(this);

        // 기본 dialog를 선언한다.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(null);

        // custom으로 만들어놓은 dialog를 할당한다.
        final View customDialogView = inflater.inflate(R.layout.dialog_info, null, false);


        // 해당하는 xml의 UI에 변수를 연결시킨다.
        image = (ImageView) customDialogView.findViewById(R.id.imgView);
        tvStore = (TextView) customDialogView.findViewById(R.id.tvStore);
        tvFood = (TextView) customDialogView.findViewById(R.id.tvFood);
        ratingBar = (RatingBar) customDialogView.findViewById(R.id.ratingBar);
        tvComment = (TextView) customDialogView.findViewById(R.id.comment);
        tvCategory = (TextView) customDialogView.findViewById(R.id.tvCategory);
        btnModify = (Button) customDialogView.findViewById(R.id.btnModify);


        // 클릭된 마커의 정보를 모델에서 찾아 웹서버로부터 이미지를 load해 온다.
        int index = -1;
        for (int i = 0; i < Model.myMarkerItems.size(); i++) {
            if (("m" + Model.myMarkerItems.get(i).getId()).equals(marker.getId())) {
                index = i;

                break;
            }
        }

        // 해당하는 변수에 각각의 값을 모델에서 가져와 set한다.
        tvStore.setText(Model.myMarkerItems.get(index).getStore());
        tvFood.setText(Model.myMarkerItems.get(index).getFoodname());
        tvComment.setText(Model.myMarkerItems.get(index).getComment());
        ratingBar.setRating(Model.myMarkerItems.get(index).getRating());
        tvCategory.setText(Model.myMarkerItems.get(index).getCategory());
        image.setImageBitmap(Model.myMarkerItems.get(index).getBitmap());
        ratingBar.setIsIndicator(true); // 사용자가 별점 변경불가

        // 위에서 이미지로딩한 것을 받아와 이미지를 set한다.


        // 수정하기 버튼을 눌렀을 시
        btnModify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // TODO : 수정화면 액티비티
                /*
                // 키보드 올리기
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                // 키보드 내리기
                InputMethodManager immhide = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                // UI 키보드에따라 재조정하기
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                */


            }
        });
        builder.setView(customDialogView);

        // 정의한 dialog를 생성하고 보여준다.
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.show();
    }


    private Location getLastKnownLocation() {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {

                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

}




