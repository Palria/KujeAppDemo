package com.palria.kujeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.palria.kujeapp.models.ProductOrderDataModel;

public class SingleProductOrderActivity extends AppCompatActivity {
    AllOrdersFragment.OrderCallback orderCallback;

    FirebaseFirestore firebaseFirestore;
    String userId;
    String orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product_order);

        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchIntentData();
        initUI();
        orderCallback = new AllOrdersFragment.OrderCallback() {
            @Override
            public void onSuccess(ProductOrderDataModel productOrderDataModel) {

            }

            @Override
            public void onFailed(String errorMessage) {

            }
        };
        getOrder();

    }

    void initUI(){

    }

    void fetchIntentData(){
        Intent intent = getIntent();
        orderId = intent.getStringExtra(GlobalValue.ORDER_ID);
    }

    private void getOrder(){
            firebaseFirestore.collection(GlobalValue.ALL_ORDERS).document(orderId).get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot){

                    String dateOrdered  = documentSnapshot.get(GlobalValue.DATE_ORDERED_TIME_STAMP)!=null?documentSnapshot.getTimestamp(GlobalValue.DATE_ORDERED_TIME_STAMP).toDate()+"":"Undefined";
                    if(dateOrdered.length()>0){
                        dateOrdered = dateOrdered.substring(0,10);
                    }
                    final String dateOrdered2 = dateOrdered;
                    String productId  = ""+documentSnapshot.get(GlobalValue.PRODUCT_ID);
                    String productOwnerId  = ""+documentSnapshot.get(GlobalValue.PRODUCT_OWNER_USER_ID);
                    String productName  = ""+documentSnapshot.get(GlobalValue.PRODUCT_TITLE);
                    String productImageDownloadUrl  = ""+documentSnapshot.get(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL);
                    String customerId  = ""+documentSnapshot.get(GlobalValue.CUSTOMER_ID);
                    String customerContactPhoneNumber =  ""+documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_PHONE_NUMBER);
                    String customerContactEmail =  ""+documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_EMAIL);
                    String customerContactAddress =  ""+documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_ADDRESS);
                    String customerContactLocation =  ""+documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_LOCATION);
                    String orderDescription  =  ""+documentSnapshot.get(GlobalValue.ORDER_DESCRIPTION);
                    boolean isResolved  = documentSnapshot.get(GlobalValue.ORDER_DESCRIPTION)!=null?documentSnapshot.getBoolean(GlobalValue.IS_ORDER_RESOLVED) :false;
                    boolean isOwnerSeen  = documentSnapshot.get(GlobalValue.IS_OWNER_SEEN)!=null?documentSnapshot.getBoolean(GlobalValue.IS_OWNER_SEEN) :false;


                    orderCallback.onSuccess(new ProductOrderDataModel(
                            productId,
                            productOwnerId,
                            productName,
                            productImageDownloadUrl,
                            orderId,
                            customerId,
                            dateOrdered2,
                            orderDescription,
                            customerContactPhoneNumber,
                            customerContactEmail,
                            customerContactAddress,
                            customerContactLocation,
                            isResolved,
                            isOwnerSeen
                    ));


                }

            });


    }


    interface OrderCallback{
        void onSuccess(ProductOrderDataModel orderDataModel);
        void onFailed(String errorMessage);
    }
}