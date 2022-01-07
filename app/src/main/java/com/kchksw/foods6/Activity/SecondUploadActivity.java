package com.kchksw.foods6.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.kchksw.foods6.AsyncTask.SendImageToServerTask;
import com.kchksw.foods6.Cropper.CropImage;
import com.kchksw.foods6.Cropper.CropImageActivity;
import com.kchksw.foods6.Cropper.CropImageView;
import com.kchksw.foods6.R;
import com.kchksw.foods6.etc.Group;
import com.kchksw.foods6.etc.User;
import com.kchksw.foods6.etc.Util;

import java.util.Arrays;


public class SecondUploadActivity extends AppCompatActivity {
    private GoogleApiClient client;

    private InputMethodManager imm;


    public ProgressBar upLoadProgressBar;
    public LinearLayout upLoadLinearView;
    private EditText place_name_input;
    private EditText address_name;
    private EditText food_name_input;
    private Spinner spin_food_category;
    private TextView rating_points;

    private ImageButton btn_Image_upload;

    private RatingBar rating;

    private EditText comment_text;                   // 평가를 적을 빈 Text

    public MenuItem upLoadComplete;

    public String path;

    public String address;
    public String place_name;
    public String food_name;
    public String food_category;
    public String comments;
    public String latitude;
    public String longitude;
    public Uri imageUri = null;

    public String imageFileName;
    public String rating_value;
    boolean place_modified;

    User receiveUser;
    Group receiveGroup;
    int callType;

    public android.support.v7.app.ActionBar actionBar;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_second);


        Bundle bundle = getIntent().getExtras();
        // 전의 intent를 얻어옴
        place_name = bundle.getString("place");
        address = bundle.getString("address");
        latitude = bundle.getDouble("latitude") + "";
        longitude = bundle.getDouble("longitude") + "";

        callType = bundle.getInt("callType");

        if (callType == Util.CALL_BY_MYINFO) {
            receiveUser = bundle.getParcelable("sendUserInfo");
        }
        if (callType == Util.CALL_BY_MYGROUP) {
            receiveGroup = bundle.getParcelable("sendGroupInfo");
        }

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (callType == Util.CALL_BY_MYINFO || callType == Util.CALL_BY_MYFRINED) {
            actionBar.setTitle(receiveUser.getName() + " - 업로드 2/2");
        }
        if (callType == Util.CALL_BY_MYGROUP) {
            actionBar.setTitle(receiveGroup.getGroups_name() + " - 업로드 2/2");
        }





        upLoadLinearView = (LinearLayout)findViewById(R.id.uploadLinearView);

        upLoadProgressBar = (ProgressBar) findViewById(R.id.upLoadProgressBar);
        upLoadProgressBar.setVisibility(ProgressBar.GONE);


        place_name_input = (EditText) findViewById(R.id.set_restaurant_edit);
        address_name = (EditText) findViewById(R.id.set_address);
        food_name_input = (EditText) findViewById(R.id.set_food_name);
        spin_food_category = (Spinner) findViewById(R.id.spinner);
        btn_Image_upload = (ImageButton) findViewById(R.id.imageButton);
        comment_text = (EditText) findViewById(R.id.comment);



        // 전의 intent에서 받아온 값을 set
        place_name_input.setText(place_name);
        address_name.setText(address);



        String [] category = Util.category;
        Arrays.sort(category, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this,R.layout.spinner_item,category);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);

        spin_food_category.setAdapter(spinnerArrayAdapter);

        spin_food_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                food_category = parent.getItemAtPosition(position).toString();
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView)parent.getChildAt(0)).setTextSize(10);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 이미지 업로드를 위한 버튼 선언
        btn_Image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCropImageActivity(null);
                // 클릭시에 미리 만들어놓은 MyAlertDialog 클래스를 호출시킨다.
            }
        });


        rating_points = (TextView) findViewById(R.id.ratingPoints);
        rating = (RatingBar) findViewById(R.id.ratingBar);
        rating.setRating(3.5f);
        rating.setIsIndicator(false);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                rating_value = String.valueOf(ratingBar.getRating());
                rating_points.setText(rating_value);
            }
        });
        // ratingBar 선언 및 기본값 지정


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
        getMenuInflater().inflate(R.menu.menu_upload_complete, menu);
        upLoadComplete = menu.findItem(R.id.upLoadComplete);
        Log.e("uploadComplete", upLoadComplete+"");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){

        if(menu.getItemId() == R.id.upLoadComplete){
            if (checkallComponents()) {

                SendImageToServerTask sendImageToServerTask = null;

                upLoadComplete.setEnabled(false);

                Util.setViewAndChildrenEnabled(upLoadLinearView,false);

                upLoadProgressBar.setVisibility(ProgressBar.VISIBLE);
                upLoadProgressBar.setIndeterminate(true);
                upLoadProgressBar.setMax(100);

                if (callType == Util.CALL_BY_MYINFO) {
                    sendImageToServerTask = new SendImageToServerTask(SecondUploadActivity.this, callType, receiveUser);

                }
                if (callType == Util.CALL_BY_MYGROUP) {
                    sendImageToServerTask = new SendImageToServerTask(SecondUploadActivity.this, callType, receiveGroup);

                }


                sendImageToServerTask.execute();
            }
        }

        return super.onOptionsItemSelected(menu);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();

                CropImageActivity cropImageActivity = CropImageActivity.getInstance();
                imageFileName = cropImageActivity.strFileName;

                btn_Image_upload.setImageURI(imageUri);
              //  Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
              //  Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Start crop image activity for the given image.
     */


    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    /**
     * 키보드를 가림.
     */

    private void hideKeyboard(EditText ed) {
        imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed.getWindowToken(), 0);
    }

    /**
     * 키보드를 보여줌.
     */

    private void showKeyboard(EditText ed) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(ed, InputMethodManager.SHOW_IMPLICIT);
    }

    private boolean checkallComponents() {

        place_name_input.setError(null);
        address_name.setError(null);
        comment_text.setError(null);
        boolean result = true;
        View[] focusView = new View[4];

        place_name = place_name_input.getText().toString();
        address = address_name.getText().toString();
        food_name = food_name_input.getText().toString();
        comments = comment_text.getText().toString();

        rating_value = rating.getRating() + "";

        if (TextUtils.isEmpty(place_name)) {
            place_name_input.setError("음식점을 입력해주세요");
            focusView[0] = place_name_input;
            result = false;
        } if (TextUtils.isEmpty(address)) {
            address_name.setError("주소를 입력해주세요");
            focusView[1] = address_name;
            result = false;
        } if (TextUtils.isEmpty(food_name)) {
            food_name_input.setError("음식이름을 입력해주세요");
            focusView[2] = food_name_input;
            result = false;
        } if (TextUtils.isEmpty(comments)) {
            comment_text.setError("평가를 작성해주셔야 해요");
            focusView[3] = comment_text;
            result = false;
        }  if (imageUri == null) {
            Toast.makeText(this, "이미지를 업로드 해주세요", Toast.LENGTH_LONG).show();
            result = false;
        }

        if (!result) {
            for (int i = 0; i < focusView.length; i++) {
                if (focusView[i] != null)
                    focusView[i].requestFocus();
            }

        }

        return result;
    }
}






