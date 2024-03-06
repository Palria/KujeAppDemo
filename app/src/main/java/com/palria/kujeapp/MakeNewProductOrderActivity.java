
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

import java.util.ArrayList;
import java.util.HashMap;

public class MakeNewProductOrderActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    String userId;
    String productOwnerId = "";
    String orderId;
    String productId;
    String orderDescription;
    String productName;
    String contactAddress;
    String contactEmail;
    String contactPhone;
    boolean isNewOrder = true;
    OrderCallback orderCallback;
    String productImageDownloadUrl = "";
    TextView productDisplayNameTextView;
    ImageView productImageView;
    TextInputEditText emailTextEdit;
    TextInputEditText phoneTextEdit;
    TextInputEditText contactAddressTextEdit;
    TextInputEditText descriptionTextEdit;
    ImageButton backButton;
    Button orderButton;
    AlertDialog confirmOrderDialog;
    AlertDialog successOrderDialog;
    AlertDialog alertProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setNavigationBarColor(getResources().getColor(R.color.teal_200,getTheme()));
        setContentView(R.layout.activity_make_new_product_order);
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchIntentData();
        initUI();

        if(isNewOrder){
            orderId = GlobalValue.getRandomString(60);
        }
       orderCallback = new OrderCallback() {
           @Override
           public void onSuccess() {
               //TODO : SEND NOTIFICATION TO PRODUCT OWNER
               //carries the info about the quiz
               ArrayList<String> modelInfo = new ArrayList<>();
               modelInfo.add(productId);

               ArrayList<String> recipientIds = new ArrayList<>();
               recipientIds.add(productOwnerId);

               //fires out the notification
               GlobalValue.sendNotificationToUsers(GlobalValue.NOTIFICATION_TYPE_PRODUCT_ORDERED,GlobalValue.getRandomString(60),recipientIds,modelInfo,productDisplayNameTextView.getText()+"","New order has been made on your product",null);

               toggleProgress(false);
               successOrderDialog.show();

           }

           @Override
           public void onFailed(String errorMessage) {
               orderButton.setEnabled(true);
               toggleProgress(false);
               initConfirmOrderDialog(true,errorMessage);


           }
       };
        try {
            Picasso.get()
                    .load(productImageDownloadUrl)
                    .error(R.drawable.agg_logo)
                    .placeholder(R.drawable.agg_logo)
                    .into(productImageView);
        }catch (Exception ignored){}

        productDisplayNameTextView.setText("Order: "+productName);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               MakeNewProductOrderActivity.super.onBackPressed();
            }
        });
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderDescription = descriptionTextEdit.getText()+"";
                contactEmail = emailTextEdit.getText()+"";
                contactPhone = phoneTextEdit.getText()+"";
                contactAddress = contactAddressTextEdit.getText()+"";
                if(!contactPhone.isEmpty()) {
                    confirmOrderDialog.show();
                }else{
                GlobalValue.createSnackBar(MakeNewProductOrderActivity.this,productDisplayNameTextView,"Please enter your phone number to enable us reach you", Snackbar.LENGTH_INDEFINITE);
                }
            }
        });
    }

void initUI(){
        productDisplayNameTextView = findViewById(R.id.productDisplayNameTextViewId);
        productImageView = findViewById(R.id.productImageViewId);
        emailTextEdit = findViewById(R.id.emailTextEditId);
        phoneTextEdit = findViewById(R.id.phoneTextEditId);
        descriptionTextEdit = findViewById(R.id.descriptionTextEditId);
        contactAddressTextEdit = findViewById(R.id.contactAddressTextEditId);
        orderButton = findViewById(R.id.orderProductActionButtonId);
        backButton = findViewById(R.id.backButton);
        initConfirmOrderDialog(false,"");
        initSuccessOrderDialog();
        alertProgressDialog = new AlertDialog.Builder(MakeNewProductOrderActivity.this)
            .setCancelable(false)
            .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
            .create();

}
void fetchIntentData(){
        Intent intent = getIntent();
    productOwnerId = intent.getStringExtra(GlobalValue.PRODUCT_OWNER_USER_ID);
    productId = intent.getStringExtra(GlobalValue.PRODUCT_ID);
    productName = intent.getStringExtra(GlobalValue.PRODUCT_TITLE);
    productImageDownloadUrl = intent.getStringExtra(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL);

    if(!isNewOrder){
        orderId = intent.getStringExtra(GlobalValue.ORDER_ID);
    }

}
    public void initConfirmOrderDialog(boolean isRetry, String errorMessage){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        String button = "Confirm";
        String message = "Confirm to make the order";
        if(isRetry){
            button = "Retry";
            message = "Your Order Failed,"+errorMessage+" Please try again";
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
                        orderButton.setEnabled(false);

                    }
                });
            }
        }).setNeutralButton("Not yet",null);
        confirmOrderDialog =builder.create();
        if(isRetry){
            confirmOrderDialog.show();
        }


    }
    public void initSuccessOrderDialog(){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);

        builder.setMessage("Your order is successful");
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MakeNewProductOrderActivity.super.onBackPressed();

            }
        }).setNeutralButton("Ok",null);
        successOrderDialog =builder.create();


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
        DocumentReference orderDocumentReference =  firebaseFirestore.collection(GlobalValue.ALL_ORDERS).document(orderId);
        HashMap<String, Object> orderDetails = new HashMap<>();
        orderDetails.put(GlobalValue.PRODUCT_ID, productId);
        orderDetails.put(GlobalValue.PRODUCT_TITLE, productName);
        orderDetails.put(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL, productImageDownloadUrl);
        orderDetails.put(GlobalValue.PRODUCT_OWNER_USER_ID, productOwnerId);
//        orderDetails.put(GlobalValue.PAGE_POSTER_ID, "AAG_HOMES_ACCOUNT");
        orderDetails.put(GlobalValue.ORDER_ID, orderId);
        orderDetails.put(GlobalValue.CUSTOMER_ID, GlobalValue.getCurrentUserId());
        orderDetails.put(GlobalValue.CUSTOMER_CONTACT_EMAIL, contactEmail);
        orderDetails.put(GlobalValue.CUSTOMER_CONTACT_PHONE_NUMBER, contactPhone);
        orderDetails.put(GlobalValue.CUSTOMER_CONTACT_ADDRESS, contactAddress);
        orderDetails.put(GlobalValue.ORDER_DESCRIPTION, orderDescription);
        orderDetails.put(GlobalValue.DATE_ORDERED_TIME_STAMP, FieldValue.serverTimestamp());
        orderDetails.put(GlobalValue.IS_PRIVATE, false);
        orderDetails.put(GlobalValue.IS_OWNER_SEEN, false);
        writeBatch.set(orderDocumentReference,orderDetails, SetOptions.merge());


        DocumentReference productDocumentReference =  firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).document(productId);
        HashMap<String, Object> productOrderDetails = new HashMap<>();
        productOrderDetails.put(GlobalValue.TOTAL_NUMBER_OF_ORDERS, FieldValue.increment(1L));
        productOrderDetails.put(GlobalValue.TOTAL_NUMBER_OF_NEW_ORDERS, FieldValue.increment(1L));
        productOrderDetails.put(GlobalValue.LAST_ORDER_ID, orderId);
        productOrderDetails.put(GlobalValue.LAST_ORDER_DATE_TIME_STAMP, FieldValue.serverTimestamp());
        writeBatch.update(productDocumentReference,productOrderDetails);

        DocumentReference orderOwnerDocumentReference =  firebaseFirestore.collection(GlobalValue.ALL_USERS).document(userId);
        HashMap<String, Object> orderOwnerOrderDetails = new HashMap<>();
        orderOwnerOrderDetails.put(GlobalValue.TOTAL_NUMBER_OF_MY_ORDERS, FieldValue.increment(1L));
        orderOwnerOrderDetails.put(GlobalValue.LAST_ORDER_ID, orderId);
        orderOwnerOrderDetails.put(GlobalValue.LAST_PRODUCT_ORDERED_ID, productId);
        orderOwnerOrderDetails.put(GlobalValue.LAST_ORDER_DATE_TIME_STAMP, FieldValue.serverTimestamp());
        writeBatch.update(orderOwnerDocumentReference,orderOwnerOrderDetails);

        DocumentReference productOwnerDocumentReference =  firebaseFirestore.collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentBusinessId());
        HashMap<String, Object> ownerOrderDetails = new HashMap<>();
        ownerOrderDetails.put(GlobalValue.TOTAL_NUMBER_OF_ORDERS, FieldValue.increment(1L));
        orderOwnerOrderDetails.put(GlobalValue.TOTAL_NUMBER_OF_NEW_ORDERS, FieldValue.increment(1L));
        ownerOrderDetails.put(GlobalValue.LAST_ORDER_ID, orderId);
        ownerOrderDetails.put(GlobalValue.LAST_PRODUCT_ORDERED_ID, productId);
        ownerOrderDetails.put(GlobalValue.LAST_ORDER_DATE_TIME_STAMP, FieldValue.serverTimestamp());
        writeBatch.update(productOwnerDocumentReference,ownerOrderDetails);

        writeBatch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                orderCallback.onFailed(e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                orderCallback.onSuccess();

            }
        });
    }

    interface OrderCallback{
        void onSuccess();
        void onFailed(String errorMessage);
    }
}