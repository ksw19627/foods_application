// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"
package com.kchksw.foods6.Activity;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import com.kchksw.foods6.AsyncTask.MarkerInfoModifyTask;
import com.kchksw.foods6.AsyncTask.MarkerInfoRemoveTask;
import com.kchksw.foods6.AsyncTask.MarkerInfoTakeTask;
import com.kchksw.foods6.Cropper.CropImage;
import com.kchksw.foods6.Cropper.CropImageActivity;
import com.kchksw.foods6.Cropper.CropImageView;
import com.kchksw.foods6.Model.Model;
import com.kchksw.foods6.R;
import com.kchksw.foods6.etc.Group;
import com.kchksw.foods6.etc.User;
import com.kchksw.foods6.etc.Util;

import java.util.Arrays;


public class ShowDetailActivity extends AppCompatActivity {

    int index;
    EditText edStore;
    EditText edAddr;
    Spinner spinnerCategory;
    EditText edFood;
    ImageButton imgView;
    RatingBar ratingBar;
    EditText edComment;

    TextView rating_points;

    public LinearLayout infoModifyLinearLayout;

    public String place_name;
    public String food_name;
    public String comments;
    public String addr;
    public String rating_value;
    public Uri imageUri = null;
    public String imageFileName;
    public String category;

    public boolean imageFileChanged = false;

    public ProgressBar Bar;

    public User receiveUser;
    public Group receiveGroup;
    public int callType;

    public boolean isModify = false;

    public MenuItem infoModifyComplete;

    public static Toast mToast;

    public static ShowDetailActivity showDetailActivity;

    public ShowDetailActivity() {
        showDetailActivity = this;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);

        mToast = Toast.makeText(this, "null", Toast.LENGTH_SHORT);

        Bundle bundle = getIntent().getExtras();

        index = bundle.getInt("index");
        callType = bundle.getInt("callType");

        if (callType == Util.CALL_BY_MYINFO || callType == Util.CALL_BY_MYFRINED) {
            receiveUser = bundle.getParcelable("sendUserInfo");
        }
        if (callType == Util.CALL_BY_MYGROUP) {
            receiveGroup = bundle.getParcelable("sendGroupInfo");
        }

        Bar = (ProgressBar) findViewById(R.id.upLoadProgressBar2);
        infoModifyLinearLayout =(LinearLayout)findViewById(R.id.infoModifyLinearLayout);

        edStore = (EditText) findViewById(R.id.edStore);
        edAddr = (EditText) findViewById(R.id.edAddr);
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        edFood = (EditText) findViewById(R.id.edFood);
        imgView = (ImageButton) findViewById(R.id.imgView);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        edComment = (EditText) findViewById(R.id.edComment);





        // 해당하는 변수에 각각의 값을 모델에서 가져와 set한다.
        edStore.setText(Model.myMarkerItems.get(index).getStore());
        edAddr.setText(Model.myMarkerItems.get(index).getAddress());
        edFood.setText(Model.myMarkerItems.get(index).getFoodname());
        edComment.setText(Model.myMarkerItems.get(index).getComment());
        ratingBar.setRating(Model.myMarkerItems.get(index).getRating());
        //spinnerCategory.setPrompt(Model.myMarkerItems.get(index).getCategory());
        imgView.setImageBitmap(Model.myMarkerItems.get(index).getBitmap());


        Util.setViewAndChildrenEnabled(infoModifyLinearLayout, false);
        ratingBar.setEnabled(true);
        ratingBar.setIsIndicator(true);


        imageFileName = Model.myMarkerItems.get(index).getStringImage();
        category = Model.myMarkerItems.get(index).getCategory();


        rating_points = (TextView) findViewById(R.id.ratingPoints);
        rating_points.setText(String.valueOf(ratingBar.getRating()));


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                rating_value = String.valueOf(ratingBar.getRating());
                rating_points.setText(rating_value);
            }
        });
        // ratingBar 선언 및 기본값 지정

        String[] categoryArray = Util.category;
        Arrays.sort(categoryArray, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, R.layout.spinner_item, categoryArray);
        //spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);

        spinnerCategory.setAdapter(spinnerArrayAdapter);

        int spinnerPosition = spinnerArrayAdapter.getPosition(Model.myMarkerItems.get(index).getCategory());
        spinnerCategory.setSelection(spinnerPosition);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(10);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCropImageActivity(null);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isModify) {
            getMenuInflater().inflate(R.menu.menu_show_detail, menu);


        } else {
            getMenuInflater().inflate(R.menu.menu_info_modify_complete, menu);
            infoModifyComplete = menu.findItem(R.id.infoModifyComplete);
        }
        isModify = !isModify;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.dialog_remove) {

            if (callType == Util.CALL_BY_MYINFO || callType == Util.CALL_BY_MYGROUP) {
                MarkerInfoRemoveTask markerInfoRemoveTask = new MarkerInfoRemoveTask(ShowDetailActivity.this, index);
                markerInfoRemoveTask.execute();

            } else {
                mToast.setText("친구 정보를 삭제할 수 없습니다.");
                mToast.show();
            }
            return true;
        }
        if (item.getItemId() == R.id.dialog_fix) {

            if (callType == Util.CALL_BY_MYINFO || callType == Util.CALL_BY_MYGROUP) {
                invalidateOptionsMenu();

                Util.setViewAndChildrenEnabled(infoModifyLinearLayout, true);
                ratingBar.setIsIndicator(false);
            }else {
                mToast.setText("수정 권한이 없습니다.");
                mToast.show();
            }
            return true;
        }
        if (item.getItemId() == R.id.infoModifyComplete) {

            if(checkComponents()) {
                Util.setViewAndChildrenEnabled(infoModifyLinearLayout, false);
                ratingBar.setEnabled(true);
                ratingBar.setIsIndicator(true);
                infoModifyComplete.setEnabled(false);

                Bar.setVisibility(ProgressBar.VISIBLE);
                  Bar.setIndeterminate(true);
                  Bar.setMax(100);

                   MarkerInfoModifyTask markerInfoModifyTask = new MarkerInfoModifyTask(ShowDetailActivity.this, index);
                   markerInfoModifyTask.execute();


            }
        }
        if (item.getItemId() == R.id.dialog_take) {

            MarkerInfoTakeTask markerInfoTakeTask = null;

            if (callType == Util.CALL_BY_MYINFO) {
                mToast.setText("내 정보는 가져올 수 없습니다.");
                mToast.show();
                return true;
            }

            markerInfoTakeTask = new MarkerInfoTakeTask(ShowDetailActivity.this, index);
            markerInfoTakeTask.execute();

            return true;
        }
        return super.onOptionsItemSelected(item);
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

                imageFileChanged = true;

                imgView.setImageURI(imageUri);
          //      Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
          //      Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }



    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    private boolean checkComponents() {


        edStore.setError(null);
        edFood.setError(null);
        edAddr.setError(null);
        edComment.setError(null);

        boolean result = true;
        View[] focusView = new View[4];

        place_name = edStore.getText().toString();
        food_name = edFood.getText().toString();
        comments = edComment.getText().toString();
        addr = edAddr.getText().toString();
        rating_value = ratingBar.getRating() + "";

        if (TextUtils.isEmpty(place_name)) {
            edStore.setError("음식점을 입력해주세요");
            focusView[0] = edStore;
            result = false;
        }
        if (TextUtils.isEmpty(addr)) {
            edAddr.setError("위치를 입력해주세요");
            focusView[1] = edAddr;
            result = false;
        }
        if (TextUtils.isEmpty(food_name)) {
            edFood.setError("음식이름을 입력해주세요");
            focusView[2] = edFood;
            result = false;
        }
        if (TextUtils.isEmpty(comments)) {
            edComment.setError("평가를 작성해주셔야 해요");
            focusView[3] = edComment;
            result = false;
        }
        if (imgView.getDrawable() == null) {
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

