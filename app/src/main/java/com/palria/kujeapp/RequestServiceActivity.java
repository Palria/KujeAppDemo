
package com.palria.kujeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class RequestServiceActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    String userId;
    String serviceOwnerId = "";
    String serviceId;
    String requestId;
    String requestDescription;
    String serviceTitle;
    String contactAddress;
    String contactEmail;
    String contactPhone;
    boolean isNewService = true;
    RequestCallback requestCallback;
    TextView serviceTitleTextView;
//    ImageView productImageView;
    TextInputEditText emailTextEdit;
    TextInputEditText phoneTextEdit;
    TextInputEditText contactAddressTextEdit;
    TextInputEditText descriptionTextEdit;
    ImageButton backButton;
    Button submitRequestButton;
    AlertDialog confirmRequestDialog;
    AlertDialog successRequestDialog;
    AlertDialog alertProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.teal_200,getTheme()));
        setContentView(R.layout.activity_request_service);
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchIntentData();
        initUI();

        requestCallback = new RequestCallback() {
            @Override
            public void onSuccess() {
                toggleProgress(false);
                successRequestDialog.show();
            }

            @Override
            public void onFailed(String errorMessage) {
                submitRequestButton.setEnabled(true);
                toggleProgress(false);
                initConfirmRequestDialog(true,errorMessage);


            }
        };

        serviceTitleTextView.setText("Request: "+serviceTitle);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestServiceActivity.super.onBackPressed();
            }
        });
        submitRequestButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                requestDescription = descriptionTextEdit.getText()+"";
                contactEmail = emailTextEdit.getText()+"";
                contactPhone = phoneTextEdit.getText()+"";
                contactAddress = contactAddressTextEdit.getText()+"";
                if(!contactPhone.isEmpty()) {
                    confirmRequestDialog.show();
                }else{
                    phoneTextEdit.setError("Enter phone number");
                    GlobalValue.createSnackBar(RequestServiceActivity.this,serviceTitleTextView,"Please enter your phone number to enable us reach you", Snackbar.LENGTH_INDEFINITE);
                }
            }
        });
    }

    void initUI(){
        serviceTitleTextView = findViewById(R.id.serviceTitleTextViewId);
//        productImageView = findViewById(R.id.productImageViewId);
        emailTextEdit = findViewById(R.id.emailTextEditId);
        phoneTextEdit = findViewById(R.id.phoneTextEditId);
        descriptionTextEdit = findViewById(R.id.descriptionTextEditId);
        contactAddressTextEdit = findViewById(R.id.contactAddressTextEditId);
        submitRequestButton = findViewById(R.id.submitRequestActionButtonId);
        backButton = findViewById(R.id.backButton);
        initConfirmRequestDialog(false,"");
        initSuccessRequestDialog();
        alertProgressDialog = new AlertDialog.Builder(RequestServiceActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();

    }
    void fetchIntentData(){
        Intent intent = getIntent();
        serviceOwnerId = intent.getStringExtra(GlobalValue.SERVICE_OWNER_USER_ID);
        serviceId = intent.getStringExtra(GlobalValue.SERVICE_ID);
        serviceTitle = intent.getStringExtra(GlobalValue.SERVICE_TITLE);

        if(!isNewService){
            requestId = intent.getStringExtra(GlobalValue.REQUEST_ID);
        }else{
            requestId = GlobalValue.getRandomString(60);
        }

    }
    public void initConfirmRequestDialog(boolean isRetry, String errorMessage){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        String button = "Confirm";
        String message = "Confirm to submit your request";
        if(isRetry){
            button = "Retry";
            message = "Your Request Failed,"+errorMessage+" Please try again";
        }

        builder.setMessage(message);
        builder.setNegativeButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        toggleProgress(true);
                        submitRequest();
                        submitRequestButton.setEnabled(false);

                    }
                });
            }
        }).setNeutralButton("Not yet",null);
        confirmRequestDialog =builder.create();
        if(isRetry){
            confirmRequestDialog.show();
        }


    }
    public void initSuccessRequestDialog(){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);

        builder.setMessage("Your request is successful");
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RequestServiceActivity.super.onBackPressed();

            }
        }).setNeutralButton("Ok",null);
        successRequestDialog =builder.create();


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

    void submitRequest(){

        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        DocumentReference requestDocumentReference =  firebaseFirestore.collection(GlobalValue.ALL_REQUESTS).document(requestId);
        HashMap<String, Object> requestDetails = new HashMap<>();
        requestDetails.put(GlobalValue.SERVICE_ID, serviceId);
        requestDetails.put(GlobalValue.SERVICE_TITLE, serviceTitle);
        requestDetails.put(GlobalValue.SERVICE_OWNER_USER_ID, serviceOwnerId);
        requestDetails.put(GlobalValue.PAGE_POSTER_ID, "AAG_HOMES_ACCOUNT");
        requestDetails.put(GlobalValue.REQUEST_ID, requestId);
        requestDetails.put(GlobalValue.CUSTOMER_ID, GlobalValue.getCurrentUserId());
        requestDetails.put(GlobalValue.CUSTOMER_CONTACT_EMAIL, contactEmail);
        requestDetails.put(GlobalValue.CUSTOMER_CONTACT_PHONE_NUMBER, contactPhone);
        requestDetails.put(GlobalValue.CUSTOMER_CONTACT_ADDRESS, contactAddress);
        requestDetails.put(GlobalValue.REQUEST_DESCRIPTION, requestDescription);
        requestDetails.put(GlobalValue.DATE_REQUESTED_TIME_STAMP, FieldValue.serverTimestamp());
        requestDetails.put(GlobalValue.IS_PRIVATE, false);
        requestDetails.put(GlobalValue.IS_OWNER_SEEN, false);
        writeBatch.set(requestDocumentReference,requestDetails, SetOptions.merge());


        DocumentReference serviceDocumentReference =  firebaseFirestore.collection(GlobalValue.PLATFORM_SERVICES).document(serviceId);
        HashMap<String, Object> serviceDetails = new HashMap<>();
        serviceDetails.put(GlobalValue.TOTAL_SERVICE_REQUESTS, 1L);
        serviceDetails.put(GlobalValue.TOTAL_NEW_SERVICE_REQUESTS, 1L);
        serviceDetails.put(GlobalValue.LAST_REQUEST_ID, requestId);
        serviceDetails.put(GlobalValue.LAST_REQUEST_DATE_TIME_STAMP, FieldValue.serverTimestamp());
        writeBatch.update(serviceDocumentReference,serviceDetails);

        DocumentReference customerDocumentReference =  firebaseFirestore.collection(GlobalValue.ALL_USERS).document(userId);
        HashMap<String, Object> customerDetails = new HashMap<>();
        customerDetails.put(GlobalValue.TOTAL_SERVICE_REQUESTS, 1L);
        customerDetails.put(GlobalValue.LAST_REQUEST_ID, requestId);
        customerDetails.put(GlobalValue.LAST_SERVICE_REQUESTED_ID, serviceId);
        customerDetails.put(GlobalValue.LAST_REQUEST_DATE_TIME_STAMP, FieldValue.serverTimestamp());
        writeBatch.update(customerDocumentReference,customerDetails);

//
//        DocumentReference serviceOwnerDocumentReference =  firebaseFirestore.collection(GlobalValue.ALL_USERS).document(serviceOwnerId);
//        HashMap<String, Object> ownerServiceDetails = new HashMap<>();
//        ownerServiceDetails.put(GlobalValue.TOTAL_SERVICE_REQUESTS, 1L);
//        ownerServiceDetails.put(GlobalValue.TOTAL_NEW_SERVICE_REQUESTS, 1L);
//        ownerServiceDetails.put(GlobalValue.LAST_REQUEST_ID, requestId);
//        ownerServiceDetails.put(GlobalValue.LAST_REQUEST_DATE_TIME_STAMP, FieldValue.serverTimestamp());
//        writeBatch.update(serviceOwnerDocumentReference,ownerServiceDetails);

        writeBatch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                requestCallback.onFailed(e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                requestCallback.onSuccess();
            }
        });
    }
    interface RequestCallback{
        void onSuccess();
        void onFailed(String errorMessage);
    }
}