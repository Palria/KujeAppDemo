
package com.palria.kujeapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.palria.kujeapp.SingleProductActivity;
import com.palria.kujeapp.adapters.ProductOrderRcvAdapter;
import com.palria.kujeapp.adapters.RequestAdapter;
import com.palria.kujeapp.adapters.RequestAdapter;
import com.palria.kujeapp.models.ProductOrderDataModel;
import com.palria.kujeapp.models.RequestDataModel;
import com.palria.kujeapp.models.ServiceDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ServiceRequestsFragment extends Fragment {



    public ServiceRequestsFragment() {
        // Required empty public constructor
    }
    RequestCallback requestCallback;
    RequestAdapter requestRcvAdapter;
    ArrayList<RequestDataModel> requestDataModelArrayList = new ArrayList<>();
    RecyclerView recyclerView;

    FirebaseFirestore firebaseFirestore;
    String userId;
    LinearLayout containerLinearLayout;
    boolean isSingleService = false;
    boolean isSingleCustomer = false;
    String serviceId = "";
    String customerId = "";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(getArguments() != null){
            serviceId = getArguments().getString(GlobalValue.SERVICE_ID,"");
            customerId = getArguments().getString(GlobalValue.CUSTOMER_ID,"");
            isSingleService = getArguments().getBoolean(GlobalValue.IS_SINGLE_SERVICE,true);
            isSingleCustomer = getArguments().getBoolean(GlobalValue.IS_SINGLE_CUSTOMER,false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_service_requests, container, false);
        initUI(parentView);
        initRecyclerView();

        requestCallback = new RequestCallback() {
            @Override
            public void onSuccess(RequestDataModel requestDataModel) {
                requestDataModelArrayList.add(requestDataModel);
                requestRcvAdapter.notifyItemChanged(requestDataModelArrayList.size());
            }

            @Override
            public void onFailed(String errorMessage) {

            }
        };
        getRequests();

        if(getContext().getClass().equals(SingleProductActivity.class)){
            recyclerView.setPadding(2,2,2,200);
        }
        return parentView;
    }
    private  void initUI(View parentView){
        recyclerView = parentView.findViewById(R.id.requestRecyclerViewId);
//        containerLinearLayout = parentView.findViewById(R.id.containerLinearLayoutId);

    }
    private void initRecyclerView(){
        requestRcvAdapter = new RequestAdapter(requestDataModelArrayList,getContext(),isSingleService);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(requestRcvAdapter);

        /*requestDataModelArrayList.add(new RequestDataModel(
                serviceId,
                "serviceName",
                "requestId",
                "customerId",
                "dateRequest",
                "requestDescription",
                "customerContactPhoneNumber",
                "customerContactEmail",
                "customerContactAddress",
                "customerContactLocation",
                false
        ));
        */

        requestRcvAdapter.notifyItemChanged(requestDataModelArrayList.size());
    }
    private void getRequests(){
        Query requestQuery = firebaseFirestore.collection(GlobalValue.ALL_REQUESTS);
        if(isSingleService && isSingleCustomer){
            requestQuery = firebaseFirestore.collection(GlobalValue.ALL_REQUESTS).whereEqualTo(GlobalValue.SERVICE_ID,serviceId).whereEqualTo(GlobalValue.CUSTOMER_ID,customerId);
        }
        else if(isSingleService){
            requestQuery = firebaseFirestore.collection(GlobalValue.ALL_REQUESTS).whereEqualTo(GlobalValue.SERVICE_ID,serviceId);
        }
        else if(isSingleCustomer){
            requestQuery = firebaseFirestore.collection(GlobalValue.ALL_REQUESTS).whereEqualTo(GlobalValue.CUSTOMER_ID,customerId);
        }
        requestQuery.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    String requestId = documentSnapshot.getId();

                    String daterequested  = documentSnapshot.get(GlobalValue.DATE_REQUESTED_TIME_STAMP)!=null?documentSnapshot.getTimestamp(GlobalValue.DATE_REQUESTED_TIME_STAMP).toDate()+"":"Undefined";
                    if(daterequested.length()>10){
                        daterequested = daterequested.substring(0,10);
                    }
                    String serviceId  = ""+documentSnapshot.get(GlobalValue.SERVICE_ID);
                    String serviceTitle  = ""+documentSnapshot.get(GlobalValue.SERVICE_TITLE);

                    String customerId  = ""+documentSnapshot.get(GlobalValue.CUSTOMER_ID);
                    String customerContactPhoneNumber =  ""+documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_PHONE_NUMBER);
                    String customerContactEmail =  ""+documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_EMAIL);
                    String customerContactAddress =  ""+documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_ADDRESS);
                    String customerContactLocation =  ""+documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_LOCATION);
                    String requestDescription  =  ""+documentSnapshot.get(GlobalValue.REQUEST_DESCRIPTION);
                    boolean isResolved  = documentSnapshot.get(GlobalValue.IS_SERVICE_REQUEST_RESOLVED)!=null?documentSnapshot.getBoolean(GlobalValue.IS_SERVICE_REQUEST_RESOLVED) :false;

                    requestCallback.onSuccess(new RequestDataModel(
                            serviceId,
                            serviceTitle,
                            requestId,
                            customerId,
                            daterequested,
                            requestDescription,
                            customerContactPhoneNumber,
                            customerContactEmail,
                            customerContactAddress,
                            customerContactLocation,
                            isResolved
                    ));



//                    firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(GlobalValue.getBusinessAdminId()).collection(GlobalValue.PAGES).document(GlobalValue.getCurrentBusinessId()).collection(GlobalValue.PRODUCTS).document(productId).collection(GlobalValue.PRODUCT_PROFILE).document(productId).get().addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                            String productImageImageDownloadUrl = ""+documentSnapshot.get(GlobalValue.IMAGE_DOWNLOAD_URL);
//                            String productName  = ""+documentSnapshot.get(GlobalValue.PRODUCT_TITLE);
//                            String productDescription = ""+documentSnapshot.get(GlobalValue.PRODUCT_DESCRIPTION);
//
////                            displayOrderView(getContext(),
////                                    containerLinearLayout,
////                                     productImageImageDownloadUrl,
////                                     productName,
////                                     productId,
////                                     dateOrdered2,
////                                     productDescription,
////                                     customerName,
////                                     customerContactPhoneNumber,
////                                     customerContactEmail,
////                                     customerContactAddress,
////                                     customerContactLocation,
////                                     orderDescription
////                            );
//                        }
//                    });
                }
            }
        });
    }


    interface RequestCallback{
        void onSuccess(RequestDataModel requestDataModel);
        void onFailed(String errorMessage);
    }

}