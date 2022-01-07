package com.kchksw.foods6.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kchksw.foods6.R;
import com.kchksw.foods6.etc.Group;
import com.kchksw.foods6.etc.User;
import com.kchksw.foods6.etc.Util;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class FirstUploadActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleApiClient client;

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    // 최소 GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    protected LocationManager locationManager;
    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;
    // 네트워크 사용유무
    boolean isNetworkEnabled = false;
    // GPS 상태값
    boolean isGetLocation = false;

    Location location;
    double lat; // 위도
    double lon; // 경도

    double latitude;
    double longitude;

    private TextView addr;
    private MapFragment mapView;
    private ButtonBarLayout get_place;
    private GoogleMap mMap;
    private Button cancel;
    private Button next;
    private Place place;

    private String address;
    private String place_name;


    User receiveUser;
    Group receiveGroup;
    int callType;

    public android.support.v7.app.ActionBar actionBar;

    public static FirstUploadActivity firstUploadActivity;

    public FirstUploadActivity(){
        firstUploadActivity = this;
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_upload_first);


        mapView = (MapFragment)getFragmentManager().findFragmentById(R.id.mapView);
        mapView.getMapAsync(this);


        get_place = (ButtonBarLayout)findViewById(R.id.Get_Place);
        get_place.setOnClickListener((new View.OnClickListener(){
            public void onClick(View v){
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(FirstUploadActivity.this);
                    startActivityForResult(intent,PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e){
                    e.printStackTrace();
                }
            }
        }));

        addr = (TextView) findViewById(R.id.Address);



        Bundle bundle = getIntent().getExtras();
        callType = bundle.getInt("callType");

        if(callType == Util.CALL_BY_MYINFO){
            receiveUser = bundle.getParcelable("sendUserInfo");
        }
        if(callType == Util.CALL_BY_MYGROUP) {
            receiveGroup = bundle.getParcelable("sendGroupInfo");
        }

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (callType == Util.CALL_BY_MYINFO || callType == Util.CALL_BY_MYFRINED) {
            actionBar.setTitle(receiveUser.getName() + " - 업로드 1/2");
        }
        if (callType == Util.CALL_BY_MYGROUP) {
            actionBar.setTitle(receiveGroup.getGroups_name() + " - 업로드 1/2");
        }


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }





    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    { // 장소 검색 실행 후 결과 처리

        if(requestCode==PLACE_PICKER_REQUEST)
        {
            if(resultCode == RESULT_OK)
            {
                place = PlacePicker.getPlace(data, this);
                // 해당 Place를 받아온다.

                latitude = place.getLatLng().latitude;
                longitude= place.getLatLng().longitude;
                lat = latitude;
                lon = longitude;
                // 받아온 place에서 위도 경도값을 얻어 저장한다.
                address = String.format("%s", place.getAddress());
                addr.setText(address);
                place_name = place.getName().toString();
                // place에서 주소를 얻어오고 이를 place_name이라는 변수에 저장, addr에 해당 주소를 표시한다.

                mMap.clear();
                Marker marker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place_name));
                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16));
                // 얻어낸 place를 이용하여 MapView상에 Marker를 만든다.
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocation();

        if(isGetLocation()){
            Log.i("위치 얻음","i");
            latitude = getLatitude();
            longitude = getLongitude();
           // Toast.makeText(getApplicationContext(), "위도 : " + latitude + ", 경도 : " + longitude, Toast.LENGTH_SHORT).show();

        }
        else {

            Log.i("위치 얻지 않음","i");
            latitude = 37.497909;
            longitude = 127.027587;
            lat = latitude; lon = longitude;
            Toast.makeText(getApplicationContext(), "현재위치를 보여지게 하려면 GPS를 켜주세요", Toast.LENGTH_SHORT).show();
        }

        address = getAddressFromLocation();
        addr.setText(address);

        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                .title("당신의 현위치"));
        marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                lat = latLng.latitude;
                lon = latLng.longitude;
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("사용자 지정"));
                marker.showInfoWindow();
                address = getAddressFromLocation();
                place_name = null;
                addr.setText(address);
            }
        });
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

    }



    public Location getLocation() {
        try {
            // LocationManager는 위치정보를 받을 수 있도록 도와주는 역할을 하고, 실제 위치정보는 LocationProvider를 통해서 얻어온다.
            // 위치정보를 얻으려면, 위치정보를 담당하는 LocationManager의 인스턴스가 필요하다.
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            // GPS 정보 가져오기
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    //      logView.setText("latitude: "+ lat +", longitude: "+ lng);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    //       logView.setText("onStatusChanged");
                }

                public void onProviderEnabled(String provider) {
                    //     logView.setText("onProviderEnabled");
                }

                public void onProviderDisabled(String provider) {
                    //     logView.setText("onProviderDisabled");
                }
            };

            // 위치정보 사용권한 체크(사용권한이 없을경우 -1)
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없을경우

                // 최초 권한 요청인지, 혹은 사용자에 의한 재요청인지 확인
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        && ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // 사용자가 임의로 권한을 취소시킨 경우

                    // 권한 재요청
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
                    // 최초로 권한을 요청하는 경우(첫실행)
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                // 사용 권한이 있음을 확인한 경우

            }


            if (!isGPSEnabled && !isNetworkEnabled) {
                // GPS 와 네트워크사용이 가능하지 않을때 소스 구현

            } else {
                this.isGetLocation = true;

                // 네트워크 정보로 부터 위치값 가져오기
                if (isNetworkEnabled) {


                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도 경도 저장
                            lat = location.getLatitude();
                            lon = location.getLongitude();

                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public boolean onSupportNavigateUp()

    {
        finish();
        return super.onSupportNavigateUp();

    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // res 폴더 내의 menu_main.xml이 있는 경우 실행 가능한 함수다.
        getMenuInflater().inflate(R.menu.menu_upload_next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){

        if(menu.getItemId() == R.id.next){
            Intent intent = new Intent(getApplication(), SecondUploadActivity.class); // 회원가입 화면으로 가기위한 화면을 intent에 담아 생성
            Log.i("?","진입");
            intent.putExtra("address",address);
            intent.putExtra("place",place_name);
            intent.putExtra("latitude",lat);
            intent.putExtra("longitude",lon);


            if (callType == Util.CALL_BY_MYINFO) {
                intent.putExtra("sendUserInfo", receiveUser);
                intent.putExtra("callType", Util.CALL_BY_MYINFO);

            } else if (callType == Util.CALL_BY_MYGROUP) {
                intent.putExtra("sendGroupInfo", receiveGroup);
                intent.putExtra("callType", Util.CALL_BY_MYGROUP);

            }

            intent.putExtra("callType", callType);

            startActivity(intent); // 위에서 만들어진 intent를 새로운 액티비티로 실행시켜 화면을 띄운다.(기존 액티비티는 stack됨)
        }

        return super.onOptionsItemSelected(menu);
    }
    public String getAddressFromLocation() {
        String address="";
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, lon, 1);

            String AddressLine = addresses.get(0).getAddressLine(0);
            String CityName = addresses.get(0).getLocality();
            String StateName = addresses.get(0).getAdminArea();
            String CountryName = addresses.get(0).getCountryName();
            String pName = addresses.get(0).getSubLocality();
            String lName = addresses.get(0).getFeatureName();
            String PostalCode = addresses.get(0).getPostalCode();
            String KnownName = addresses.get(0).getFeatureName();
            address = AddressLine+", "+CityName+", "+StateName+", "+CountryName;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            //locationManager.removeUpdates(GpsInfo.this // locationListener);
        }
    }

    /**
     * 위도값을 가져옵니다.
     */
    public double getLatitude() {
        if (location != null) {
            lat = location.getLatitude();
        }
        return lat;
    }

    /**
     * 경도값을 가져옵니다.
     */
    public double getLongitude() {
        if (location != null) {
            lon = location.getLongitude();
        }
        return lon;
    }

    /**
     * GPS 나 wife 정보가 켜져있는지 확인합니다.
     */
    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    /**
     * GPS 정보를 가져오지 못했을때
     * 설정값으로 갈지 물어보는 alert 창
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS 사용유무셋팅");
        alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까 ? ");

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MapRegister Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.kchksw.foods6/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MapRegister Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.kchksw.foods6/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    // 권한요청에 대한 사용자의 응답 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}