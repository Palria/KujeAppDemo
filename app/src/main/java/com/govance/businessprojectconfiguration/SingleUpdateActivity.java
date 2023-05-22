package com.govance.businessprojectconfiguration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SingleUpdateActivity extends AppCompatActivity {
    UpdateFetchListener updateFetchListener;
    FirebaseFirestore firebaseFirestore;
    String updateId;
    UpdateDataModel updateDataModel;
    AlertDialog alertDialog;
    TextView updateDisplayNameTextView;
    TextView updateDescriptionTextView;
    TextView viewCountTextView;
    TextView dateAddedTextView;
    ImageButton backButton;
    LinearLayout descLinearLayout;
    LinearLayout mediaLinearLayout;
    String updateImageDownloadUrl;
    TextView dummyTextView;
    ShimmerFrameLayout shimmerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_update);
        firebaseFirestore = FirebaseFirestore.getInstance();
        initUI();
        fetchIntentData();

        getWindow().setNavigationBarColor(getColor(R.color.secondary_app_color));
        getWindow().setStatusBarColor(getColor(R.color.secondary_app_color));

        alertDialog = new AlertDialog.Builder(SingleUpdateActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();
        updateFetchListener = new UpdateFetchListener() {
            @Override
            public void onFailed(String errorMessage) {
                toggleProgress(false);

            }

            @Override
            public void onSuccess(UpdateDataModel updateDataModel) {
                GlobalValue.incrementUpdateViews(updateId);
                toggleProgress(false);
                updateDisplayNameTextView.setText(updateDataModel.getTitle());
                updateDescriptionTextView.setText(updateDataModel.getDescription());
                if(!updateDataModel.getDescription().isEmpty()){
                    descLinearLayout.setVisibility(View.VISIBLE);
                }else{
                    descLinearLayout.setVisibility(View.GONE);

                }
                viewCountTextView.setText(updateDataModel.getNumOfViews()+ " Views");
                dateAddedTextView.setText(updateDataModel.getDatePosted());
                updateImageDownloadUrl = updateDataModel.getImageUrlList().get(0);

                for(int i=0; i<updateDataModel.getImageUrlList().size(); i++){
                    int finalI = i;
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    ImageView imageView = new ImageView(SingleUpdateActivity.this);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    imageView.setBackgroundResource(R.drawable.agg_logo);
                    imageView.setLayoutParams(layoutParams);
                    try{
                        Picasso.get()
                                .load(updateDataModel.getImageUrlList().get(i))
                                .error(R.drawable.agg_logo)
                                .into(imageView);
                    }catch (Exception e){}
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GlobalValue.viewImagePreview(SingleUpdateActivity.this,imageView, updateDataModel.getImageUrlList().get(finalI));
                        }
                    });
                    View dummyView = new View(getApplicationContext());
                    dummyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,5));
                    mediaLinearLayout.addView(imageView);
                    mediaLinearLayout.addView(dummyView);
                }
                stopShimmer();
            }
        };

//        productFetchListener.onSuccess(productDataModel);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingleUpdateActivity.this.onBackPressed();

            }
        });
        toggleProgress(true);
        getUpdateData();

    }

    private void initUI(){
        updateDisplayNameTextView = findViewById(R.id.updateDisplayNameTextViewId);
        updateDescriptionTextView = findViewById(R.id.updateDescriptionTextViewId);
        viewCountTextView = findViewById(R.id.numberOfViewsTextViewId);
        dateAddedTextView = findViewById(R.id.datePostedTextViewId);
        backButton = findViewById(R.id.backButton);
        descLinearLayout = findViewById(R.id.descLinearLayoutId);
        mediaLinearLayout = findViewById(R.id.mediaLinearLayoutId);
        dummyTextView = findViewById(R.id.dummyTextViewId);
        shimmerLayout = findViewById(R.id.shimmerLayout);

    }
    void fetchIntentData(){
        Intent intent = getIntent();
        updateId = intent.getStringExtra(GlobalValue.UPDATE_ID);
        updateDataModel = (UpdateDataModel) intent.getSerializableExtra(GlobalValue.UPDATE_DATA_MODEL);
    }

    private void toggleProgress(boolean show) {
        if (true) {
            return;
        }
        if(show){
            alertDialog.show();
        }else{
            alertDialog.cancel();
        }
    }


    private void getUpdateData(){
        toggleProgress(true);
        firebaseFirestore.collection(GlobalValue.PLATFORM_UPDATES).document(updateId).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateFetchListener.onFailed(e.getMessage());
            }
        })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        shimmerLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);

                        String updateId = documentSnapshot.getId();
                        String updateTitle =""+ documentSnapshot.get(GlobalValue.UPDATE_TITLE);
                        String updateDescription =""+ documentSnapshot.get(GlobalValue.UPDATE_DESCRIPTION);
                        String datePosted = documentSnapshot.get(GlobalValue.DATE_POSTED_TIME_STAMP)!=null ? documentSnapshot.getTimestamp(GlobalValue.DATE_POSTED_TIME_STAMP).toDate()+""  : "Moment ago";
                        if(datePosted.length()>10){
                            datePosted = datePosted.substring(0,10);
                        }
                        long updateViewCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_VIEWS)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_VIEWS) : 0L;
                        ArrayList<String> imageUrlList = documentSnapshot.get(GlobalValue.UPDATE_IMAGE_DOWNLOAD_URL_ARRAY_LIST)!=null ? (ArrayList<String>) documentSnapshot.get(GlobalValue.UPDATE_IMAGE_DOWNLOAD_URL_ARRAY_LIST) : new ArrayList<>();
                        boolean isPrivate = documentSnapshot.get(GlobalValue.IS_PRIVATE)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_PRIVATE) : false;


                        updateFetchListener.onSuccess(new UpdateDataModel(updateId,updateTitle,updateDescription,datePosted,imageUrlList, (int) updateViewCount,isPrivate));

                    }
                });
    }
    void stopShimmer(){
        dummyTextView.setVisibility(View.GONE);
    }
    interface UpdateFetchListener{
        void onFailed(String errorMessage);
        void onSuccess(UpdateDataModel updateDataModel);
    }

}