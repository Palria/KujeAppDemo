package com.palria.kujeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.palria.kujeapp.adapters.AdvertsAdapter;
import com.palria.kujeapp.adapters.AdvertsDataModel;

import java.util.ArrayList;

public class AdvertsFragment extends Fragment {
    public AdvertsFragment() {
        // Required empty public constructor
    }
    public AdvertsFragment(BottomAppBar bar) {
        // Required empty public constructor
    }
    AdvertsFetchListener updateFetchListener;
    SwipeRefreshLayout refreshLayout;
    ShimmerFrameLayout shimmerLayout,progressIndicatorShimmerLayout;

    AdvertsAdapter adapter;
    ArrayList<AdvertsDataModel> updateDataModels = new ArrayList<>();
    DocumentSnapshot lastRetrievedAdvertsSnapshot = null;
    LinearLayout containerLinearLayout;
    boolean isLoadingMoreAdvertss = false;
    boolean isFirstLoad = true;
    boolean isFromSearchContext = false;
    String searchKeyword = "";
    RecyclerView advertsRecyclerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            isFromSearchContext = getArguments().getBoolean(GlobalValue.IS_FROM_SEARCH_CONTEXT,false);
            searchKeyword = getArguments().getString(GlobalValue.SEARCH_KEYWORD,"");

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_adverts, container, false);
        adapter = new AdvertsAdapter(updateDataModels,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);

        shimmerLayout = parentView.findViewById(R.id.advertsShimmerLayoutId);
        containerLinearLayout = parentView.findViewById(R.id.containerLinearLayoutId);
        refreshLayout = parentView.findViewById(R.id.allAdvertsRefreshLayoutId);
        advertsRecyclerView = parentView.findViewById(R.id.advertsRecyclerViewId);
        advertsRecyclerView.setLayoutManager(linearLayoutManager);
        advertsRecyclerView.setAdapter(adapter);

        updateFetchListener = new AdvertsFetchListener() {
            @Override
            public void onFailed(String errorMessage) {

            }

            @Override
            public void onSuccess(AdvertsDataModel updateDataModel) {
//                GlobalValue.incrementAdvertsViews(updateDataModel.getAdvertsId());

                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);

                updateDataModels.add(updateDataModel);
//                productDataModels.add(productDataModel);
//                productDataModels.add(productDataModel);
//                productDataModels.add(productDataModel);
//                productDataModels.add(productDataModel);

                adapter.notifyItemChanged(updateDataModels.size());
            }
        };
        getAdvertsData();
        manageRefreshLayout();
        listenToScrollChange();

        return parentView;
    }

    void manageRefreshLayout(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
                refreshLayout.setRefreshing(false);

                isFirstLoad = true;
                isLoadingMoreAdvertss = false;
                getAdvertsData();

            }
        });
    }
    private void getAdvertsData() {

        Query query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_ADVERTS).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(20);

        if (!isLoadingMoreAdvertss) {

            if (isFirstLoad) {

                updateDataModels.clear();
                adapter.notifyDataSetChanged();
                shimmerLayout.startShimmer();
                shimmerLayout.setVisibility(View.VISIBLE);
            } else {
                progressIndicatorShimmerLayout = GlobalValue.showShimmerLayout(getContext(), containerLinearLayout);

            }

            if (isFromSearchContext) {
               if(isFirstLoad){
                   query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_ADVERTS).whereArrayContains(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchKeyword).whereEqualTo(GlobalValue.IS_VIEW_EXCEEDED,false).limit(20);
            }else {
                query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_ADVERTS).whereArrayContains(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchKeyword).whereEqualTo(GlobalValue.IS_VIEW_EXCEEDED,false).startAfter(lastRetrievedAdvertsSnapshot).limit(20);
            }
            }else {
                if(isFirstLoad){
                     query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_ADVERTS).whereEqualTo(GlobalValue.IS_VIEW_EXCEEDED,false).limit(20);
//                     query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_ADVERTS).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(20);

                }else{
                     query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_ADVERTS).whereEqualTo(GlobalValue.IS_VIEW_EXCEEDED,false).startAfter(lastRetrievedAdvertsSnapshot).limit(20);
//                     query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_ADVERTS).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).startAfter(lastRetrievedAdvertsSnapshot).limit(20);

                }
//            if (!categorySelected.equalsIgnoreCase("ALL")) {
//                query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_ADVERTS).whereEqualTo(GlobalValue.PRODUCT_CATEGORY, categorySelected).whereNotEqualTo(GlobalValue.PRODUCT_CATEGORY, "categorySelected").orderBy(GlobalValue.PRODUCT_CATEGORY, Query.Direction.DESCENDING).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING);
//            }
            }

            isLoadingMoreAdvertss = true;

            query.get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    updateFetchListener.onFailed(e.getMessage());
                    isLoadingMoreAdvertss = false;

                }
            }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        String updateId = documentSnapshot.getId();
//                    String productOwnerId = "" + documentSnapshot.get(GlobalValue.PRODUCT_OWNER_USER_ID);
                        String updateTitle = "" + documentSnapshot.get(GlobalValue.ADVERT_TITLE);
                        String updateDescription = "" + documentSnapshot.get(GlobalValue.ADVERT_DESCRIPTION);
                        String datePosted = documentSnapshot.get(GlobalValue.DATE_POSTED_TIME_STAMP) != null ? documentSnapshot.getTimestamp(GlobalValue.DATE_POSTED_TIME_STAMP).toDate() + "" : "Undefined";
                        if (datePosted.length() > 10) {
                            datePosted = datePosted.substring(0, 10);
                        }
                        long viewCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_VIEWS) != null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_VIEWS) : 0L;
                        long viewLimit = documentSnapshot.get(GlobalValue.ADVERT_VIEW_LIMIT) != null ? documentSnapshot.getLong(GlobalValue.ADVERT_VIEW_LIMIT) : 0L;
                        ArrayList<String> imageUrlList = documentSnapshot.get(GlobalValue.ADVERT_IMAGE_DOWNLOAD_URL_ARRAY_LIST) != null ? (ArrayList<String>) documentSnapshot.get(GlobalValue.ADVERT_IMAGE_DOWNLOAD_URL_ARRAY_LIST) : new ArrayList<>();
//                    ArrayList<String> viewersIdList = documentSnapshot.get(GlobalValue.ADVERT_VIEWERS_ID_ARRAY_LIST) != null ? (ArrayList<String>) documentSnapshot.get(GlobalValue.ADVERT_VIEWERS_ID_ARRAY_LIST) : new ArrayList<>();
                        boolean isPrivate = documentSnapshot.get(GlobalValue.IS_PRIVATE) != null ? documentSnapshot.getBoolean(GlobalValue.IS_PRIVATE) : false;
                        boolean isViewLimitExceeded = documentSnapshot.get(GlobalValue.IS_VIEW_EXCEEDED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_VIEW_EXCEEDED) : false;

                        updateFetchListener.onSuccess(new AdvertsDataModel(updateId, updateTitle, updateDescription, datePosted, imageUrlList, (int) viewCount,(int) viewLimit,isViewLimitExceeded, isPrivate));
                    }
                    GlobalValue.removeShimmerLayout(containerLinearLayout,progressIndicatorShimmerLayout);
                    if (queryDocumentSnapshots.size() == 0) {
                        if(isFirstLoad) {

                        }
                    }else{
                        lastRetrievedAdvertsSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                    }
                    isFirstLoad = false;
                    isLoadingMoreAdvertss = false;

                }
            });
        }
    }


    void listenToScrollChange(){
//        if(true){
//            return;
//        }

        advertsRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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
                if(!advertsRecyclerView.canScrollVertically(View.LAYOUT_DIRECTION_RTL)){
//                           Toast.makeText(getContext(), "end", Toast.LENGTH_SHORT).show();
                    if(!isLoadingMoreAdvertss){
                        getAdvertsData();
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

    interface AdvertsFetchListener{
        void onFailed(String errorMessage);
        void onSuccess(AdvertsDataModel updateDataModel);
    }

}