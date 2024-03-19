package com.palria.kujeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.palria.kujeapp.models.ServiceDataModel;

public class SingleServiceActivity extends AppCompatActivity {
    ServiceDataModel serviceDataModel;
    String serviceId;
    TextView titleTextView;
    TextView descriptionTextView;
    TextView newRequestsTextView;
    TextView totalRequestsTextView;
    TabLayout tabLayout;
    FrameLayout requestFrameLayout;
    FrameLayout catalogFrameLayout;

    ExtendedFloatingActionButton addNewCatalogActionButton;
    static OnAddNewCatalogButtonClickListener onAddNewCatalogButtonClickListener;

    boolean isRequestsFragmentOpened = false;
    boolean isCatalogFragmentOpened = false;

    MaterialButton declineAdvertButton,approveAdvertButton,boostButton,requestServiceButton;
    long numberOfRequestedViews = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_service);
        initUI();
        fetchIntentData();
        getService();
//        initFragment();
        initTabLayout();
        addNewCatalogActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddNewCatalogButtonClickListener.onClick(addNewCatalogActionButton);
            }
        });
    }

    void initUI(){
        titleTextView = findViewById(R.id.serviceTitleTextViewId);
        descriptionTextView = findViewById(R.id.serviceDescriptionTextViewId);
        newRequestsTextView = findViewById(R.id.newRequestTextViewId);
        totalRequestsTextView = findViewById(R.id.totalRequestTextViewId);
        requestFrameLayout = findViewById(R.id.requestFrameLayoutId);
        catalogFrameLayout = findViewById(R.id.catalogFrameLayoutId);
        tabLayout = findViewById(R.id.tabLayoutId);
        addNewCatalogActionButton = findViewById(R.id.addNewCatalogActionButtonId);

        declineAdvertButton = findViewById(R.id.declineAdvertButtonId);
        approveAdvertButton = findViewById(R.id.approveAdvertButtonId);
        boostButton = findViewById(R.id.boostButtonId);
        requestServiceButton = findViewById(R.id.requestServiceButtonId);


    }

    void fetchIntentData(){
        Intent intent = getIntent();
//        serviceOwnerId = intent.getStringExtra(GlobalValue.PAGE_OWNER_USER_ID);
        serviceDataModel = (ServiceDataModel) intent.getSerializableExtra(GlobalValue.PAGE_DATA_MODEL);
        serviceId = intent.getStringExtra(GlobalValue.PAGE_ID);
//        serviceTitle = intent.getStringExtra(GlobalValue.PAGE_TITLE);

    }

    void getService(){
        GlobalValue.getFirebaseFirestoreInstance()
                .collection(GlobalValue.PAGES)
                .document(serviceId)
                .get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String serviceId = documentSnapshot.getId();
                        String serviceOwnerId = ""+ documentSnapshot.get(GlobalValue.PAGE_OWNER_USER_ID);
                        String title = ""+ documentSnapshot.get(GlobalValue.PAGE_TITLE);
                        String description = ""+ documentSnapshot.get(GlobalValue.PAGE_DESCRIPTION);
                        long totalRequests =  documentSnapshot.get(GlobalValue.TOTAL_PAGE_REQUESTS)!=null? documentSnapshot.getLong(GlobalValue.TOTAL_PAGE_REQUESTS): 0L;
                        long numberOfNewRequests =  documentSnapshot.get(GlobalValue.TOTAL_NEW_PAGE_REQUESTS)!=null? documentSnapshot.getLong(GlobalValue.TOTAL_NEW_PAGE_REQUESTS): 0L;
                        String dateAdded =  documentSnapshot.get(GlobalValue.DATE_ADDED_TIME_STAMP)!=null? documentSnapshot.getTimestamp(GlobalValue.DATE_ADDED_TIME_STAMP).toDate()+"": "Undefined";
                        if(dateAdded.length()>10){
                            dateAdded = dateAdded.substring(0,10);
                        }

                        titleTextView.setText(title);
                        descriptionTextView.setText(description);
                        newRequestsTextView.setText(numberOfNewRequests+" new requests");
                        totalRequestsTextView.setText(totalRequests+ " total requests");

                        boolean isAdvertRequested =  documentSnapshot.get(GlobalValue.IS_ADVERT_REQUESTED)!=null? documentSnapshot.getBoolean(GlobalValue.IS_ADVERT_REQUESTED): false;
                        boolean isAdvertRunning =  documentSnapshot.get(GlobalValue.IS_ADVERT_RUNNING)!=null? documentSnapshot.getBoolean(GlobalValue.IS_ADVERT_RUNNING): false;

                        numberOfRequestedViews = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_REQUESTED_ADVERT_VIEW)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_REQUESTED_ADVERT_VIEW) : 0L;

                        if(isAdvertRequested && GlobalValue.isPlatformAccount()){
                            declineAdvertButton.setVisibility(View.VISIBLE);
                            approveAdvertButton.setVisibility(View.VISIBLE);
                            boostButton.setVisibility(View.GONE);
                            declineAdvertButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    declineAdvertButton.setText("Declining...");
                                    declineAdvertButton.setEnabled(false);
                                    approveAdvertButton.setEnabled(false);
                                    GlobalValue.declineAdvert(serviceOwnerId,serviceId,false,false,true,false, new GlobalValue.ActionCallback() {
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
                            approveAdvertButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    approveAdvertButton.setText("Approving...");
                                    declineAdvertButton.setEnabled(false);
                                    approveAdvertButton.setEnabled(false);
                                    GlobalValue.approveAdvert(serviceOwnerId,serviceId,numberOfRequestedViews,false,false,true,false, new GlobalValue.ActionCallback() {
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
                        else if(GlobalValue.getCurrentUserId().equals(serviceOwnerId+"")){
                            //it's the owner's account
                            declineAdvertButton.setVisibility(View.GONE);
                            approveAdvertButton.setVisibility(View.GONE);
                            if(isAdvertRequested ){
                                boostButton.setEnabled(false);
                                boostButton.setText("Boost requested");
                            }
                            if(isAdvertRunning){
                                boostButton.setEnabled(false);
                                boostButton.setText("Boost running");
                            }else{
                                boostButton.setVisibility(View.VISIBLE);
                            }
                            boostButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    boostButton.setEnabled(false);
                                    GlobalValue.requestAdvert(SingleServiceActivity.this,serviceOwnerId,serviceId,false,false,true,false, new GlobalValue.ActionCallback() {
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

                        else{
                            //this is customer account
                            declineAdvertButton.setVisibility(View.GONE);
                            approveAdvertButton.setVisibility(View.GONE);
                            boostButton.setVisibility(View.GONE);
                            requestServiceButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }


    private void  initTabLayout(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tab.getText().toString().equalsIgnoreCase("REQUESTS")){
                    addNewCatalogActionButton.setVisibility(View.GONE);

                    if(isRequestsFragmentOpened){
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(requestFrameLayout);
                    }else {
                        isRequestsFragmentOpened =true;
                        setFrameLayoutVisibility(requestFrameLayout);
                        ServiceRequestsFragment allOrdersFragment = new ServiceRequestsFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString(GlobalValue.USER_ID,"");
                        bundle.putBoolean(GlobalValue.IS_SINGLE_SERVICE,true);
                        bundle.putBoolean(GlobalValue.IS_SINGLE_CUSTOMER,false);
                        bundle.putString(GlobalValue.PAGE_ID,serviceId);
                        bundle.putString(GlobalValue.CUSTOMER_ID,"");
                        allOrdersFragment.setArguments(bundle);

                        initFragment(allOrdersFragment, requestFrameLayout);
                    }

                }
                if(tab.getText().toString().equalsIgnoreCase("CATALOG")){
                    addNewCatalogActionButton.setVisibility(View.VISIBLE);
                    if(isCatalogFragmentOpened){
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(catalogFrameLayout);
                    }else {
                        isCatalogFragmentOpened =true;
                        setFrameLayoutVisibility(catalogFrameLayout);
                        ServiceCatalogFragment serviceCatalogFragment = new ServiceCatalogFragment(onAddNewCatalogButtonClickListener);

                        Bundle bundle = new Bundle();
                        bundle.putString(GlobalValue.PAGE_ID,serviceId);
                        serviceCatalogFragment.setArguments(bundle);

                        initFragment(serviceCatalogFragment, catalogFrameLayout);

                    }

                }
//                else if(tab.getPosition()==1)
//                {
//                    if(isMessagesFragmentOpened){
//                        //Just set the frame layout visibility
//                        setFrameLayoutVisibility(messagesFrameLayout);
//                    }else {
//                        isMessagesFragmentOpened =true;
//                        setFrameLayoutVisibility(messagesFrameLayout);
//                        MessagesFragment messagesFragment = new MessagesFragment();
//
//                        initFragment(messagesFragment, messagesFrameLayout);
//                    }
//                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
if(GlobalValue.isBusinessOwner()) {
    TabLayout.Tab requestsItem = tabLayout.newTab();
    requestsItem.setText("Requests");
    tabLayout.addTab(requestsItem, 0);

    TabLayout.Tab catalogItem = tabLayout.newTab();
    catalogItem.setText("Catalog");
    tabLayout.addTab(catalogItem, 1);
}else{

    TabLayout.Tab catalogItem = tabLayout.newTab();
    catalogItem.setText("Catalog");
    tabLayout.addTab(catalogItem, 0);
}
//
//        TabLayout.Tab messagesItem = tabLayout.newTab();
//        ordersItem.setText("Messages");
//        tabLayout.addTab(ordersItem,1);

    }


    private void initFragment(Fragment fragment, FrameLayout frameLayout){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(), fragment)
                .commit();


    }

    private void setFrameLayoutVisibility(FrameLayout frameLayoutToSetVisible){
        requestFrameLayout.setVisibility(View.GONE);
        catalogFrameLayout.setVisibility(View.GONE);
        frameLayoutToSetVisible.setVisibility(View.VISIBLE);
    }

public interface OnAddNewCatalogButtonClickListener{
        void onClick(View button);
}
}