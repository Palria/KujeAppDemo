
package com.palria.kujeapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ApplyJobActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    String userId;
    String jobOwnerId = "";
    String applicationId;
    String jobId;
    String applicationDescription;
    String jobName;
    String contactAddress;
    String contactEmail;
    String contactPhone;
    boolean isNewApplication = true;
    ApplicationCallback applicationCallback;
    String jobImageDownloadUrl = "";
    TextView jobDisplayNameTextView;
    ImageView jobImageView;
    TextInputEditText emailTextEdit;
    TextInputEditText phoneTextEdit;
    TextInputEditText contactAddressTextEdit;
    TextInputEditText descriptionTextEdit;
    ImageButton backButton;
    Button applyButton;
    AlertDialog confirmApplicationDialog;
    AlertDialog successApplicationDialog;
    AlertDialog alertProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setNavigationBarColor(getResources().getColor(R.color.teal_200,getTheme()));
        setContentView(R.layout.activity_apply_job);
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchIntentData();
        initUI();

        if(isNewApplication){
            applicationId = GlobalValue.getRandomString(60);
        }
       applicationCallback = new ApplicationCallback() {
           @Override
           public void onSuccess() {
               toggleProgress(false);
               successApplicationDialog.show();
           }

           @Override
           public void onFailed(String errorMessage) {
               applyButton.setEnabled(true);
               toggleProgress(false);
               initConfirmApplicationDialog(true,errorMessage);


           }
       };
        try {
            Picasso.get()
                    .load(jobImageDownloadUrl)
                    .error(R.drawable.agg_logo)
                    .placeholder(R.drawable.agg_logo)
                    .into(jobImageView);
        }catch (Exception ignored){}

        jobDisplayNameTextView.setText("Apply for : "+jobName);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ApplyJobActivity.super.onBackPressed();
            }
        });
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applicationDescription = descriptionTextEdit.getText()+"";
                contactEmail = emailTextEdit.getText()+"";
                contactPhone = phoneTextEdit.getText()+"";
                contactAddress = contactAddressTextEdit.getText()+"";
                if(!contactPhone.isEmpty()) {
                    confirmApplicationDialog.show();
                }else{
                GlobalValue.createSnackBar(ApplyJobActivity.this,jobDisplayNameTextView,"Please enter your phone number to enable the poster reach you", Snackbar.LENGTH_INDEFINITE);
                }
            }
        });
    }

void initUI(){
        jobDisplayNameTextView = findViewById(R.id.jobDisplayNameTextViewId);
        jobImageView = findViewById(R.id.jobImageViewId);
        emailTextEdit = findViewById(R.id.emailTextEditId);
        phoneTextEdit = findViewById(R.id.phoneTextEditId);
        descriptionTextEdit = findViewById(R.id.descriptionTextEditId);
        contactAddressTextEdit = findViewById(R.id.contactAddressTextEditId);
        applyButton = findViewById(R.id.applyJobActionButtonId);
        backButton = findViewById(R.id.backButton);
        initConfirmApplicationDialog(false,"");
        initSuccessApplicationDialog();
        alertProgressDialog = new AlertDialog.Builder(ApplyJobActivity.this)
            .setCancelable(false)
            .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
            .create();

}
void fetchIntentData(){
        Intent intent = getIntent();
    jobOwnerId = intent.getStringExtra(GlobalValue.JOB_OWNER_USER_ID);
    jobId = intent.getStringExtra(GlobalValue.JOB_ID);
    jobName = intent.getStringExtra(GlobalValue.JOB_TITLE);
    jobImageDownloadUrl = intent.getStringExtra(GlobalValue.JOB_IMAGE_DOWNLOAD_URL);

    if(!isNewApplication){
        applicationId = intent.getStringExtra(GlobalValue.APPLICATION_ID);
    }

}
    public void initConfirmApplicationDialog(boolean isRetry, String errorMessage){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        String button = "Confirm";
        String message = "Confirm to apply";
        if(isRetry){
            button = "Retry";
            message = "Your Application Failed,"+errorMessage+" Please try again";
        }

        builder.setMessage(message);
        builder.setNegativeButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        toggleProgress(true);
                       order();
                        applyButton.setEnabled(false);

                    }
                });
            }
        }).setNeutralButton("Not yet",null);
        confirmApplicationDialog =builder.create();
        if(isRetry){
            confirmApplicationDialog.show();
        }


    }
    public void initSuccessApplicationDialog(){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);

        builder.setMessage("Your application is successful");
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApplyJobActivity.super.onBackPressed();

            }
        }).setNeutralButton("Ok",null);
        successApplicationDialog =builder.create();


    }

    private void toggleProgress(boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    alertProgressDialog.show();
                }else{
                    alertProgressDialog.cancel();
                }
            }
        });

    }


    void order(){

        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        DocumentReference applicationDocumentReference =  firebaseFirestore.collection(GlobalValue.ALL_APPLICATIONS).document(applicationId);
        HashMap<String, Object> applicationDetails = new HashMap<>();
        applicationDetails.put(GlobalValue.JOB_ID, jobId);
        applicationDetails.put(GlobalValue.JOB_TITLE, jobName);
        applicationDetails.put(GlobalValue.JOB_IMAGE_DOWNLOAD_URL, jobImageDownloadUrl);
        applicationDetails.put(GlobalValue.JOB_OWNER_USER_ID, jobOwnerId);
//        applicationDetails.put(GlobalValue.POSTER_ID, "NORMAL");
        applicationDetails.put(GlobalValue.APPLICATION_ID, applicationId);
        applicationDetails.put(GlobalValue.APPLICANT_ID, GlobalValue.getCurrentUserId());
        applicationDetails.put(GlobalValue.APPLICANT_CONTACT_EMAIL, contactEmail);
        applicationDetails.put(GlobalValue.APPLICANT_CONTACT_PHONE_NUMBER, contactPhone);
        applicationDetails.put(GlobalValue.APPLICANT_CONTACT_ADDRESS, contactAddress);
        applicationDetails.put(GlobalValue.APPLICATION_DESCRIPTION, applicationDescription);
        applicationDetails.put(GlobalValue.DATE_APPLIED_TIME_STAMP, FieldValue.serverTimestamp());
        applicationDetails.put(GlobalValue.IS_PRIVATE, false);
        applicationDetails.put(GlobalValue.IS_OWNER_SEEN, false);
        writeBatch.set(applicationDocumentReference,applicationDetails, SetOptions.merge());


        DocumentReference jobDocumentReference =  firebaseFirestore.collection(GlobalValue.ALL_JOBS).document(jobId);
        HashMap<String, Object> jobApplicationDetails = new HashMap<>();
        jobApplicationDetails.put(GlobalValue.TOTAL_NUMBER_OF_APPLICANTS, FieldValue.increment(1L));
        jobApplicationDetails.put(GlobalValue.TOTAL_NUMBER_OF_NEW_APPLICANTS, FieldValue.increment(1L));
        jobApplicationDetails.put(GlobalValue.LAST_APPLICATION_ID, applicationId);
        jobApplicationDetails.put(GlobalValue.LAST_APPLICATION_DATE_TIME_STAMP, FieldValue.serverTimestamp());
        writeBatch.update(jobDocumentReference,jobApplicationDetails);

        DocumentReference orderOwnerDocumentReference =  firebaseFirestore.collection(GlobalValue.ALL_USERS).document(userId);
        HashMap<String, Object> orderOwnerApplicationDetails = new HashMap<>();
        orderOwnerApplicationDetails.put(GlobalValue.TOTAL_NUMBER_OF_MY_APPLICATIONS, FieldValue.increment(1L));
        orderOwnerApplicationDetails.put(GlobalValue.LAST_APPLICATION_ID, applicationId);
        orderOwnerApplicationDetails.put(GlobalValue.LAST_JOB_APPLIED_ID, jobId);
        orderOwnerApplicationDetails.put(GlobalValue.LAST_APPLICATION_DATE_TIME_STAMP, FieldValue.serverTimestamp());
        writeBatch.update(orderOwnerDocumentReference,orderOwnerApplicationDetails);

        DocumentReference jobOwnerDocumentReference =  firebaseFirestore.collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentBusinessId());
        HashMap<String, Object> ownerApplicationDetails = new HashMap<>();
        ownerApplicationDetails.put(GlobalValue.TOTAL_NUMBER_OF_APPLICANTS, FieldValue.increment(1L));
        orderOwnerApplicationDetails.put(GlobalValue.TOTAL_NUMBER_OF_NEW_APPLICANTS, FieldValue.increment(1L));
        ownerApplicationDetails.put(GlobalValue.LAST_APPLICATION_ID, applicationId);
        ownerApplicationDetails.put(GlobalValue.LAST_JOB_APPLIED_ID, jobId);
        ownerApplicationDetails.put(GlobalValue.LAST_APPLICATION_DATE_TIME_STAMP, FieldValue.serverTimestamp());
        writeBatch.update(jobOwnerDocumentReference,ownerApplicationDetails);

        writeBatch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                applicationCallback.onFailed(e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                applicationCallback.onSuccess();
            }
        });
    }

    interface ApplicationCallback{
        void onSuccess();
        void onFailed(String errorMessage);
    }
}