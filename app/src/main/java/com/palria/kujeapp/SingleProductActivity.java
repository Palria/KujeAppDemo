package com.palria.kujeapp;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SingleProductActivity extends AppCompatActivity {
    ProductFetchListener productFetchListener;
    FirebaseFirestore firebaseFirestore;
    String productId;
    ProductDataModel productDataModel;
    AlertDialog alertDialog;
    TextView productDisplayNameTextView;
    TextView productDescriptionTextView;
    TextView viewCountTextView;
    TextView orderCountTextView;
    TextView dateAddedTextView;
    TextView productOrdersTextView;
    ExtendedFloatingActionButton orderActionButton;
    ImageButton backButton;
    LinearLayout descLinearLayout;
    LinearLayout mediaLinearLayout;
    FrameLayout ordersFrameLayout;
    String productImageDownloadUrl;
    TextView dummyTextView;
    ShimmerFrameLayout shimmerLayout;
    MaterialButton declineAdvertButton,approveAdvertButton,boostButton;
    long numberOfRequestedViews = 0L;
    boolean isAdvertRequested = false;
    boolean isAdvertRunning = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product);
        firebaseFirestore = FirebaseFirestore.getInstance();
        initUI();
        fetchIntentData();

        getWindow().setNavigationBarColor(getColor(R.color.secondary_app_color));
        getWindow().setStatusBarColor(getColor(R.color.secondary_app_color));

        alertDialog = new AlertDialog.Builder(SingleProductActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();
        productFetchListener = new ProductFetchListener() {
            @Override
            public void onFailed(String errorMessage) {
                toggleProgress(false);

            }

            @Override
            public void onSuccess(ProductDataModel productDataModel) {
                GlobalValue.incrementProductViews(productId);
                toggleProgress(false);
                productDisplayNameTextView.setText(productDataModel.getProductTitle());
                productDescriptionTextView.setText(productDataModel.getProductDescription());
                if(!productDataModel.getProductDescription().isEmpty()){
                    descLinearLayout.setVisibility(View.VISIBLE);
                }else{
                    descLinearLayout.setVisibility(View.GONE);

                }
                viewCountTextView.setText(productDataModel.getProductViewCount()+ " Views");
                orderCountTextView.setText(productDataModel.getProductOrderCount() + " Orders");
                dateAddedTextView.setText(productDataModel.getDatePosted());
                productImageDownloadUrl = productDataModel.getImageUrlList().get(0);

                for(int i=0; i<productDataModel.getImageUrlList().size(); i++){
                    final int finalI = i;
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    ImageView imageView = new ImageView(SingleProductActivity.this);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    imageView.setBackgroundResource(R.drawable.agg_logo);
                    imageView.setLayoutParams(layoutParams);
                    try{
                        Picasso.get()
                                .load(productDataModel.getImageUrlList().get(i))
                                .error(R.drawable.agg_logo)
                                .into(imageView);
                    }catch (Exception e){}
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GlobalValue.viewImagePreview(SingleProductActivity.this,imageView, productDataModel.getImageUrlList().get(finalI));
                        }
                    });
                    View dummyView = new View(getApplicationContext());
                    dummyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,5));
                    mediaLinearLayout.addView(imageView);
                    mediaLinearLayout.addView(dummyView);
                }

                //render advert components

                if(productDataModel.isAdvertRequested() && GlobalValue.isPlatformAccount()){
                    declineAdvertButton.setVisibility(View.VISIBLE);
                    approveAdvertButton.setVisibility(View.VISIBLE);
                    boostButton.setVisibility(View.GONE);
                    declineAdvertButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            declineAdvertButton.setText("Declining...");
                            declineAdvertButton.setEnabled(false);
                            approveAdvertButton.setEnabled(false);
                            GlobalValue.declineAdvert(productDataModel.getProductOwnerId(),productDataModel.getProductId(),false,true,false,false, new GlobalValue.ActionCallback() {
                                @Override
                                public void onSuccess() {

                                    declineAdvertButton.setText("Declined");
                                    approveAdvertButton.setVisibility(View.GONE);
                                }

                                @Override
                                public void onFailed(String errorMessage) {

                                    declineAdvertButton.setText("Retry decline");
                                    declineAdvertButton.setEnabled(true);
                                    approveAdvertButton.setEnabled(true);
                                }
                            });
                        }
                    });
                    approveAdvertButton.setText("Approve "+numberOfRequestedViews+" views boost");
                    approveAdvertButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            approveAdvertButton.setText("Approving...");
                            declineAdvertButton.setEnabled(false);
                            approveAdvertButton.setEnabled(false);
                            GlobalValue.approveAdvert(productDataModel.getProductOwnerId(),productDataModel.getProductId(),numberOfRequestedViews,false,false,true,false, new GlobalValue.ActionCallback() {
                                @Override
                                public void onSuccess() {

                                    approveAdvertButton.setText("Approved");
                                    declineAdvertButton.setVisibility(View.GONE);
                                }

                                @Override
                                public void onFailed(String errorMessage) {

                                    approveAdvertButton.setText("Retry approve");
                                    declineAdvertButton.setEnabled(true);
                                    approveAdvertButton.setEnabled(true);
                                }
                            });
                        }
                    });
                }
                else if(GlobalValue.getCurrentUserId().equals(productDataModel.getProductOwnerId()+"")){
                    //it's the owner's account
                    declineAdvertButton.setVisibility(View.GONE);
                    approveAdvertButton.setVisibility(View.GONE);
                    if(isAdvertRequested || isAdvertRunning){
                        boostButton.setVisibility(View.GONE);
                    }else{
                        boostButton.setVisibility(View.VISIBLE);
                    }
                    boostButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boostButton.setEnabled(false);
                            GlobalValue.requestAdvert(SingleProductActivity.this,productDataModel.getProductOwnerId(),productDataModel.getProductId(),false,true,false,false, new GlobalValue.ActionCallback() {
                                @Override
                                public void onSuccess() {

                                    boostButton.setText("Boost requested");
                                }

                                @Override
                                public void onFailed(String errorMessage) {

                                    approveAdvertButton.setText("Retry boost");
                                    boostButton.setEnabled(true);

                                }
                            });
                        }
                    });
                }
                stopShimmer();
            }
        };

//        productFetchListener.onSuccess(productDataModel);
        orderActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),MakeNewProductOrderActivity.class);
                intent.putExtra(GlobalValue.PRODUCT_ID,productId);
                intent.putExtra(GlobalValue.PRODUCT_DATA_MODEL,productDataModel);
                intent.putExtra(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL,productImageDownloadUrl);
                intent.putExtra(GlobalValue.PRODUCT_OWNER_USER_ID,productDataModel.getProductOwnerId());
                intent.putExtra(GlobalValue.PRODUCT_TITLE,productDataModel.getProductTitle());

                startActivity(intent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingleProductActivity.this.onBackPressed();

            }
        });
        toggleProgress(true);
        getProductData();
        openOrdersFragment();

    }

    private void initUI(){
        productDisplayNameTextView = findViewById(R.id.productDisplayNameTextViewId);
        productDescriptionTextView = findViewById(R.id.productDescriptionTextViewId);
        viewCountTextView = findViewById(R.id.numberOfViewsTextViewId);
        orderCountTextView = findViewById(R.id.numberOfOrderTextViewId);
        dateAddedTextView = findViewById(R.id.dateAddedTextViewId);
        orderActionButton = findViewById(R.id.orderProductActionButtonId);
        productOrdersTextView = findViewById(R.id.productOrdersTextViewId);
        backButton = findViewById(R.id.backButton);
        descLinearLayout = findViewById(R.id.descLinearLayoutId);
        mediaLinearLayout = findViewById(R.id.mediaLinearLayoutId);
        ordersFrameLayout = findViewById(R.id.ordersFrameLayoutId);
        dummyTextView = findViewById(R.id.dummyTextViewId);
        shimmerLayout = findViewById(R.id.shimmerLayout);

        declineAdvertButton = findViewById(R.id.declineAdvertButtonId);
        approveAdvertButton = findViewById(R.id.approveAdvertButtonId);
        boostButton = findViewById(R.id.boostButtonId);

    }
    void fetchIntentData(){
        Intent intent = getIntent();
        productId = intent.getStringExtra(GlobalValue.PRODUCT_ID);
        if( intent.getSerializableExtra(GlobalValue.PRODUCT_DATA_MODEL) !=null) {
            productDataModel = (ProductDataModel) intent.getSerializableExtra(GlobalValue.PRODUCT_DATA_MODEL);
        }
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

    private void openOrdersFragment(){
Bundle bundle = new Bundle();
bundle.putBoolean(GlobalValue.IS_SINGLE_PRODUCT,true);
bundle.putBoolean(GlobalValue.IS_SINGLE_CUSTOMER,false);
bundle.putString(GlobalValue.PRODUCT_ID,productId);
bundle.putString(GlobalValue.CUSTOMER_ID,GlobalValue.getCurrentUserId());
Fragment fragment = new AllOrdersFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(ordersFrameLayout.getId(),fragment)
                .commit();
    }

    private void getProductData(){
        toggleProgress(true);
        firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).document(productId).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                productFetchListener.onFailed(e.getMessage());
            }
        })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        shimmerLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);

                        String productId = documentSnapshot.getId();
                        String productTitle =""+ documentSnapshot.get(GlobalValue.PRODUCT_TITLE);
                        String productOwnerId =""+ documentSnapshot.get(GlobalValue.PRODUCT_OWNER_USER_ID);
                        String productPrice =""+ documentSnapshot.get(GlobalValue.PRODUCT_PRICE);
                        String productDescription =""+ documentSnapshot.get(GlobalValue.PRODUCT_DESCRIPTION);
                        String location = documentSnapshot.get(GlobalValue.LOCATION)+"";
                        String phone = documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_PHONE_NUMBER) +"";
                        String email = documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_EMAIL) +"";
                        String residentialAddress = documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_ADDRESS) +"";
                        String datePosted = documentSnapshot.get(GlobalValue.DATE_POSTED_TIME_STAMP)!=null ? documentSnapshot.getTimestamp(GlobalValue.DATE_POSTED_TIME_STAMP).toDate()+""  : "Undefined";
                        if(datePosted.length()>10){
                            datePosted = datePosted.substring(0,10);
                        }
                        long productOrderCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_ORDERS)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_ORDERS) : 0L;
                        long productNewOrderCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_NEW_ORDERS)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_NEW_ORDERS) : 0L;
                        long productViewCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_VIEWS)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_VIEWS) : 0L;
                        ArrayList<String> imageUrlList = documentSnapshot.get(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL_ARRAY_LIST)!=null ? (ArrayList<String>) documentSnapshot.get(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL_ARRAY_LIST) : new ArrayList<>();
                        boolean isPrivate = documentSnapshot.get(GlobalValue.IS_PRIVATE)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_PRIVATE) : false;

                        boolean isFromSubmission = documentSnapshot.get(GlobalValue.IS_FROM_SUBMISSION) != null ? documentSnapshot.getBoolean(GlobalValue.IS_FROM_SUBMISSION) : false;
                        boolean isApproved = documentSnapshot.get(GlobalValue.IS_APPROVED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_APPROVED) : false;
                        boolean isSold = documentSnapshot.get(GlobalValue.IS_SOLD) != null ? documentSnapshot.getBoolean(GlobalValue.IS_SOLD) : false;

                        if(productOrderCount<=0){
                            productOrdersTextView.setText("No orders yet");
                        }

                        boolean isAdvertRequested =  documentSnapshot.get(GlobalValue.IS_ADVERT_REQUESTED)!=null? documentSnapshot.getBoolean(GlobalValue.IS_ADVERT_REQUESTED): false;
                        SingleProductActivity.this.isAdvertRequested = isAdvertRequested;
                        isAdvertRunning =  documentSnapshot.get(GlobalValue.IS_ADVERT_RUNNING)!=null? documentSnapshot.getBoolean(GlobalValue.IS_ADVERT_RUNNING): false;
                        numberOfRequestedViews = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_REQUESTED_ADVERT_VIEW)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_REQUESTED_ADVERT_VIEW) : 0L;

                        productFetchListener.onSuccess(new ProductDataModel(productId,productOwnerId,productTitle,productPrice,productDescription,location,phone,email,residentialAddress,isSold,datePosted,productViewCount,productOrderCount,productNewOrderCount,imageUrlList,isPrivate,isFromSubmission,isApproved,isAdvertRequested));

                    }
                });
    }
    void stopShimmer(){
    dummyTextView.setVisibility(View.GONE);
    orderActionButton.setEnabled(true);
}
    interface ProductFetchListener{
        void onFailed(String errorMessage);
        void onSuccess(ProductDataModel productDataModel);
    }

}