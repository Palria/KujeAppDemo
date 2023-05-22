package com.govance.businessprojectconfiguration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.govance.businessprojectconfiguration.adapters.NotificationAdapter;
import com.govance.businessprojectconfiguration.adapters.ServiceAdapter;
import com.govance.businessprojectconfiguration.models.NotificationDataModel;
import com.govance.businessprojectconfiguration.models.ServiceDataModel;

import java.util.ArrayList;

public class AllServicesFragment extends Fragment {

    public AllServicesFragment() {
        // Required empty public constructor
    }

    View parentView;
    DocumentSnapshot lastRetrievedServiceSnapshot = null;
    boolean isLoadingMoreServices = false;
    boolean isFirstLoad = true;
    boolean isFromSearchContext = false;
    String searchKeyword = "";
    ShimmerFrameLayout shimmerFrameLayout, progressIndicatorShimmerLayout;
    RecyclerView serviceRecyclerView;
    LinearLayout containerLinearLayout;
    ServiceAdapter serviceAdapter;
    ArrayList<ServiceDataModel> serviceDataModelArrayList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFromSearchContext = getArguments().getBoolean(GlobalValue.IS_FROM_SEARCH_CONTEXT, false);
        searchKeyword = getArguments().getString(GlobalValue.SEARCH_KEYWORD, "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_all_services, container, false);
        initUI();
        initRecyclerView();
        getServices();
        listenToScrollChange();

        return parentView;

    }

    void initUI() {
        serviceRecyclerView = parentView.findViewById(R.id.serviceRecyclerViewId);
        containerLinearLayout = parentView.findViewById(R.id.containerLinearLayoutId);
        shimmerFrameLayout = parentView.findViewById(R.id.servicesShimmerLayoutId);

    }
    void listenToScrollChange(){
//        if(true){
//            return;
//        }

        serviceRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

//                if (oldScrollY > scrollY) {
//                    topFrameLayout.setVisibility(View.VISIBLE);
//
//                } else {
////                  topFrameLayout.animate();
//                    topFrameLayout.setVisibility(View.GONE);
//
//                }
                if(!serviceRecyclerView.canScrollVertically(View.LAYOUT_DIRECTION_RTL)){
//                           Toast.makeText(getContext(), "end", Toast.LENGTH_SHORT).show();
                    if(!isLoadingMoreServices){
                        getServices();
                    }
                }

            }
        });
//        productRecyclerView.getViewTreeObserver()
//                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        productRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//                            @Override
//                            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//
//                                    if (oldScrollY > scrollY) {
//                                        topFrameLayout.setVisibility(View.VISIBLE);
//
//                                    } else {
////                                        topFrameLayout.animate();
//                                        topFrameLayout.setVisibility(View.GONE);
//
//                                    }
//                            }
//                        });
//                    }
//                });
    }

    void initRecyclerView() {
        serviceAdapter = new ServiceAdapter(getContext(), serviceDataModelArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        serviceRecyclerView.setLayoutManager(linearLayoutManager);
        serviceRecyclerView.setAdapter(serviceAdapter);
    }

    void getServices() {
        Query query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_SERVICES);

        if (!isLoadingMoreServices) {
            if(isFirstLoad) {

                serviceDataModelArrayList.clear();
                serviceAdapter.notifyDataSetChanged();
                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
            } else {
                progressIndicatorShimmerLayout = GlobalValue.showShimmerLayout(getContext(), containerLinearLayout);

            }
        if (isFromSearchContext) {
            if(isFirstLoad) {
                query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_SERVICES).whereArrayContains(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchKeyword).limit(20);

            }
                query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_SERVICES).whereArrayContains(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchKeyword).startAfter(lastRetrievedServiceSnapshot).limit(20);
        }else{
            if(isFirstLoad){
                 query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_SERVICES).limit(20);

            }else{
                 query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_SERVICES).limit(20L).startAfter(lastRetrievedServiceSnapshot);

            }
        }
        }

        isLoadingMoreServices = true;

        query.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isLoadingMoreServices = false;

            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String serviceId = documentSnapshot.getId();
                    String serviceOwnerId = "" + documentSnapshot.get(GlobalValue.SERVICE_OWNER_USER_ID);
                    String title = "" + documentSnapshot.get(GlobalValue.SERVICE_TITLE);
                    String description = "" + documentSnapshot.get(GlobalValue.SERVICE_DESCRIPTION);
                    long totalRequests = documentSnapshot.get(GlobalValue.TOTAL_SERVICE_REQUESTS) != null ? documentSnapshot.getLong(GlobalValue.TOTAL_SERVICE_REQUESTS) : 0L;
                    long numberOfNewRequests = documentSnapshot.get(GlobalValue.TOTAL_NEW_SERVICE_REQUESTS) != null ? documentSnapshot.getLong(GlobalValue.TOTAL_NEW_SERVICE_REQUESTS) : 0L;
                    String dateAdded = documentSnapshot.get(GlobalValue.DATE_ADDED_TIME_STAMP) != null ? documentSnapshot.getTimestamp(GlobalValue.DATE_ADDED_TIME_STAMP).toDate() + "" : "Undefined";
                    if (dateAdded.length() > 10) {
                        dateAdded = dateAdded.substring(0, 10);
                    }
                    serviceDataModelArrayList.add(new ServiceDataModel(serviceId, serviceOwnerId, title, description, dateAdded, totalRequests, numberOfNewRequests));
                    serviceAdapter.notifyItemChanged(serviceDataModelArrayList.size());

                }

                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.INVISIBLE);
                GlobalValue.removeShimmerLayout(containerLinearLayout,progressIndicatorShimmerLayout);
                if (queryDocumentSnapshots.size() == 0) {
                    if(isFirstLoad) {

                    }
                }else{
                    lastRetrievedServiceSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                }
                isFirstLoad = false;
                isLoadingMoreServices = false;
            }
        });

    }

}