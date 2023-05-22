package com.govance.businessprojectconfiguration;

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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.govance.businessprojectconfiguration.models.ServiceDataModel;

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
    }

    void fetchIntentData(){
        Intent intent = getIntent();
//        serviceOwnerId = intent.getStringExtra(GlobalValue.SERVICE_OWNER_USER_ID);
        serviceDataModel = (ServiceDataModel) intent.getSerializableExtra(GlobalValue.SERVICE_DATA_MODEL);
        serviceId = intent.getStringExtra(GlobalValue.SERVICE_ID);
//        serviceTitle = intent.getStringExtra(GlobalValue.SERVICE_TITLE);

    }

    void getService(){
        GlobalValue.getFirebaseFirestoreInstance()
                .collection(GlobalValue.PLATFORM_SERVICES)
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
                        String serviceOwnerId = ""+ documentSnapshot.get(GlobalValue.SERVICE_OWNER_USER_ID);
                        String title = ""+ documentSnapshot.get(GlobalValue.SERVICE_TITLE);
                        String description = ""+ documentSnapshot.get(GlobalValue.SERVICE_DESCRIPTION);
                        long totalRequests =  documentSnapshot.get(GlobalValue.TOTAL_SERVICE_REQUESTS)!=null? documentSnapshot.getLong(GlobalValue.TOTAL_SERVICE_REQUESTS): 0L;
                        long numberOfNewRequests =  documentSnapshot.get(GlobalValue.TOTAL_NEW_SERVICE_REQUESTS)!=null? documentSnapshot.getLong(GlobalValue.TOTAL_NEW_SERVICE_REQUESTS): 0L;
                        String dateAdded =  documentSnapshot.get(GlobalValue.DATE_ADDED_TIME_STAMP)!=null? documentSnapshot.getTimestamp(GlobalValue.DATE_ADDED_TIME_STAMP).toDate()+"": "Undefined";
                        if(dateAdded.length()>10){
                            dateAdded = dateAdded.substring(0,10);
                        }

                        titleTextView.setText(title);
                        descriptionTextView.setText(description);
                        newRequestsTextView.setText(numberOfNewRequests+" new requests");
                        totalRequestsTextView.setText(totalRequests+ " total requests");
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
                        bundle.putString(GlobalValue.SERVICE_ID,serviceId);
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
                        bundle.putString(GlobalValue.SERVICE_ID,serviceId);
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