package com.palria.kujeapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SingleJobActivity extends AppCompatActivity {
    JobFetchListener jobFetchListener;
    FirebaseFirestore firebaseFirestore;
    String jobId;
    JobDataModel jobDataModel;
    AlertDialog alertDialog;
    TextView jobDisplayNameTextView;
    TextView jobDescriptionTextView;
    TextView viewCountTextView;
    TextView applicantCountTextView;
    TextView dateAddedTextView;
    TextView jobApplicantsTextView;
    ExtendedFloatingActionButton applyActionButton;
    ImageButton backButton;
    LinearLayout descLinearLayout;
    LinearLayout mediaLinearLayout;
    FrameLayout applicantsFrameLayout;
    String jobImageDownloadUrl;
    TextView dummyTextView;
    ShimmerFrameLayout shimmerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_job);
        firebaseFirestore = FirebaseFirestore.getInstance();
        initUI();
        fetchIntentData();

        getWindow().setNavigationBarColor(getColor(R.color.secondary_app_color));
        getWindow().setStatusBarColor(getColor(R.color.secondary_app_color));

        alertDialog = new AlertDialog.Builder(SingleJobActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();
        jobFetchListener = new JobFetchListener() {
            @Override
            public void onFailed(String errorMessage) {
                toggleProgress(false);

            }

            @Override
            public void onSuccess(JobDataModel jobDataModel) {
                GlobalValue.incrementJobViews(jobId);
                toggleProgress(false);
                jobDisplayNameTextView.setText(jobDataModel.getJobTitle());
                jobDescriptionTextView.setText(jobDataModel.getJobDescription());
                if(!jobDataModel.getJobDescription().isEmpty()){
                    descLinearLayout.setVisibility(View.VISIBLE);
                }else{
                    descLinearLayout.setVisibility(View.GONE);

                }
                viewCountTextView.setText(jobDataModel.getJobViewCount()+ " Views");
                applicantCountTextView.setText(jobDataModel.getJobApplicantCount() + " Applicants");
                dateAddedTextView.setText(jobDataModel.getDatePosted());
                jobImageDownloadUrl = jobDataModel.getImageUrlList().get(0);

                for(int i=0; i<jobDataModel.getImageUrlList().size(); i++){
                    final int finalI = i;
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,350);
                    ImageView imageView = new ImageView(SingleJobActivity.this);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    imageView.setBackgroundResource(R.drawable.agg_logo);
                    imageView.setLayoutParams(layoutParams);
                    try{
                        Picasso.get()
                                .load(jobDataModel.getImageUrlList().get(i))
                                .error(R.drawable.agg_logo)
                                .into(imageView);
                    }catch (Exception e){}
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GlobalValue.viewImagePreview(SingleJobActivity.this,imageView, jobDataModel.getImageUrlList().get(finalI));
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

//        jobFetchListener.onSuccess(jobDataModel);
        applyActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),ApplyJobActivity.class);
                intent.putExtra(GlobalValue.JOB_ID,jobId);
                intent.putExtra(GlobalValue.JOB_DATA_MODEL,jobDataModel);
                intent.putExtra(GlobalValue.JOB_IMAGE_DOWNLOAD_URL,jobImageDownloadUrl);
                intent.putExtra(GlobalValue.JOB_OWNER_USER_ID,jobDataModel.getJobOwnerId());
                intent.putExtra(GlobalValue.JOB_TITLE,jobDataModel.getJobTitle());

                startActivity(intent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingleJobActivity.this.onBackPressed();

            }
        });
        toggleProgress(true);
        getJobData();
        openApplicantsFragment();

    }

    private void initUI(){
        jobDisplayNameTextView = findViewById(R.id.jobDisplayNameTextViewId);
        jobDescriptionTextView = findViewById(R.id.jobDescriptionTextViewId);
        viewCountTextView = findViewById(R.id.numberOfViewsTextViewId);
        applicantCountTextView = findViewById(R.id.numberOfApplicantTextViewId);
        dateAddedTextView = findViewById(R.id.dateAddedTextViewId);
        applyActionButton = findViewById(R.id.applyJobActionButtonId);
        jobApplicantsTextView = findViewById(R.id.jobApplicantsTextViewId);
        backButton = findViewById(R.id.backButton);
        descLinearLayout = findViewById(R.id.descLinearLayoutId);
        mediaLinearLayout = findViewById(R.id.mediaLinearLayoutId);
        applicantsFrameLayout = findViewById(R.id.applicantsFrameLayoutId);
        dummyTextView = findViewById(R.id.dummyTextViewId);
        shimmerLayout = findViewById(R.id.shimmerLayout);

    }
    void fetchIntentData(){
        Intent intent = getIntent();
        jobId = intent.getStringExtra(GlobalValue.JOB_ID);
        jobDataModel = (JobDataModel) intent.getSerializableExtra(GlobalValue.JOB_DATA_MODEL);
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

    private void openApplicantsFragment(){
Bundle bundle = new Bundle();
bundle.putBoolean(GlobalValue.IS_SINGLE_JOB,true);
bundle.putBoolean(GlobalValue.IS_SINGLE_CUSTOMER,false);
bundle.putString(GlobalValue.JOB_ID,jobId);
bundle.putString(GlobalValue.CUSTOMER_ID,GlobalValue.getCurrentUserId());
Fragment fragment = new AllApplicationsFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(applicantsFrameLayout.getId(),fragment)
                .commit();
    }

    private void getJobData(){
        toggleProgress(true);
        firebaseFirestore.collection(GlobalValue.ALL_JOBS).document(jobId).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                jobFetchListener.onFailed(e.getMessage());
            }
        })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        shimmerLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);

                        String jobId = documentSnapshot.getId();
                        String jobTitle =""+ documentSnapshot.get(GlobalValue.JOB_TITLE);
                        String jobOwnerId =""+ documentSnapshot.get(GlobalValue.JOB_OWNER_USER_ID);
                        String jobPrice =""+ documentSnapshot.get(GlobalValue.JOB_SALARY);
                        String jobDescription =""+ documentSnapshot.get(GlobalValue.JOB_DESCRIPTION);
                        String location = documentSnapshot.get(GlobalValue.LOCATION)+"";
                        String phone = documentSnapshot.get(GlobalValue.CONTACT_PHONE_NUMBER) +"";
                        String email = documentSnapshot.get(GlobalValue.CONTACT_EMAIL) +"";
//                        String residentialAddress = documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_ADDRESS) +"";
                        String datePosted = documentSnapshot.get(GlobalValue.DATE_POSTED_TIME_STAMP)!=null ? documentSnapshot.getTimestamp(GlobalValue.DATE_POSTED_TIME_STAMP).toDate()+""  : "Undefined";
                        if(datePosted.length()>10){
                            datePosted = datePosted.substring(0,10);
                        }
                        long jobApplicantCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_APPLICANTS)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_APPLICANTS) : 0L;
                        long jobNewApplicantCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_NEW_APPLICANTS)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_NEW_APPLICANTS) : 0L;
                        long jobViewCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_VIEWS)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_VIEWS) : 0L;
                        ArrayList<String> imageUrlList = documentSnapshot.get(GlobalValue.JOB_IMAGE_DOWNLOAD_URL_ARRAY_LIST)!=null ? (ArrayList<String>) documentSnapshot.get(GlobalValue.JOB_IMAGE_DOWNLOAD_URL_ARRAY_LIST) : new ArrayList<>();
                        boolean isPrivate = documentSnapshot.get(GlobalValue.IS_PRIVATE)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_PRIVATE) : false;

//                        boolean isFromSubmission = documentSnapshot.get(GlobalValue.IS_FROM_SUBMISSION) != null ? documentSnapshot.getBoolean(GlobalValue.IS_FROM_SUBMISSION) : false;
//                        boolean isApproved = documentSnapshot.get(GlobalValue.IS_APPROVED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_APPROVED) : false;
                        boolean isClosed = documentSnapshot.get(GlobalValue.IS_CLOSED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_CLOSED) : false;

                        if(jobApplicantCount<=0){
                            jobApplicantsTextView.setText("No applicants yet");
                        }


                        jobFetchListener.onSuccess(new JobDataModel(jobId,jobOwnerId,jobTitle,jobPrice,jobDescription,location,phone,email,isClosed,datePosted,jobViewCount,jobApplicantCount,jobNewApplicantCount,imageUrlList,isPrivate));

                    }
                });
    }
    void stopShimmer(){
    dummyTextView.setVisibility(View.GONE);
    applyActionButton.setEnabled(true);
}
    interface JobFetchListener{
        void onFailed(String errorMessage);
        void onSuccess(JobDataModel jobDataModel);
    }

}