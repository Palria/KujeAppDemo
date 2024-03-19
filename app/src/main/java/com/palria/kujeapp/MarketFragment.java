package com.palria.kujeapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.palria.kujeapp.GlobalValue;
import com.palria.kujeapp.ProductAdapter;
import com.palria.kujeapp.ProductDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MarketFragment extends Fragment {

    FirebaseFirestore firebaseFirestore;
    String userId;
    String pageId;
    String productOwnerUserId;
    boolean isFromSingleOwner;
//    Chip productOptionChip;
//    Chip serviceOptionChip;
    LinearLayout containerLinearLayout;
//    LinearLayout orderSymbolLinearLayout;
    ProductFetchListener productFetchListener;
    RecyclerView productRecyclerView;
    SwipeRefreshLayout topRefreshLayout;
    boolean isFromSearchContext = false;
    String searchKeyword = "";
    boolean isFirstLoad = true;
    boolean isLoadingMoreProducts = false;
    DocumentSnapshot lastRetrievedProductSnapshot = null;

    FrameLayout topFrameLayout;
    ProductAdapter adapter;
    ArrayList<ProductDataModel> productDataModels = new ArrayList<>();
    TabLayout productTabLayout;
    TabLayout serviceTabLayout;
    TextView allProductNewOrderCountTextView;
    String pageName;
    String categorySelected = "ALL";
    ArrayList<ListenerRegistration> documentReferenceAttachedListenerRegistrationArrayList = new ArrayList<>();
    ArrayList<ExoPlayer> activeExoPlayerArrayList = new ArrayList<>();
    ShimmerFrameLayout shimmerLayout,progressIndicatorShimmerLayout;

    boolean isAdvertApproval = false;
    public MarketFragment() {
        // Required empty public constructor
    }

    public MarketFragment(BottomAppBar bar) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            isFromSearchContext = getArguments().getBoolean(GlobalValue.IS_FROM_SEARCH_CONTEXT,false);
            searchKeyword = getArguments().getString(GlobalValue.SEARCH_KEYWORD,"");
            productOwnerUserId = getArguments().getString(GlobalValue.PRODUCT_OWNER_USER_ID,"0");
            isFromSingleOwner = getArguments().getBoolean(GlobalValue.IS_FROM_SINGLE_OWNER,false);
            isAdvertApproval = getArguments().getBoolean(GlobalValue.IS_FOR_APPROVAL,false);

        }


        firebaseFirestore = FirebaseFirestore.getInstance();
        userId =FirebaseAuth.getInstance().getCurrentUser().getUid();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_market, container, false);
//        addCommentEditText = parentView.findViewById(R.id.addCommentEditTextId);
        topRefreshLayout = parentView.findViewById(R.id.topRefreshLayoutId);
        containerLinearLayout = parentView.findViewById(R.id.containerLinearLayoutId);
        topFrameLayout = parentView.findViewById(R.id.topFrameLayoutId);
//        productOptionChip = parentView.findViewById(R.id.productChipId);
//        serviceOptionChip = parentView.findViewById(R.id.serviceChipId);
        productTabLayout = parentView.findViewById(R.id.productCategoryTabLayoutId);
//        orderSymbolLinearLayout = parentView.findViewById(R.id.orderSymbolLinearLayoutId);
        shimmerLayout = parentView.findViewById(R.id.shimmerLayout);


        allProductNewOrderCountTextView = parentView.findViewById(R.id.allProductNewOrderCountTextViewId);
        productRecyclerView = parentView.findViewById(R.id.productRecyclerViewId);
        adapter = new ProductAdapter(productDataModels,getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2, GridLayoutManager.VERTICAL,false);
        productRecyclerView.setLayoutManager(gridLayoutManager);
        productRecyclerView.setAdapter(adapter);
        if(isFromSearchContext || isFromSingleOwner){
//            topFrameLayout.setVisibility(View.GONE);
            productTabLayout.setVisibility(View.GONE);
        }

        manageRefreshLayout();
        productFetchListener = new ProductFetchListener() {
            @Override
            public void onFailed(String errorMessage) {

            }

            @Override
            public void onSuccess(ProductDataModel productDataModel) {

                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);

                productDataModels.add(productDataModel);
//                productDataModels.add(productDataModel);
//                productDataModels.add(productDataModel);
//                productDataModels.add(productDataModel);
//                productDataModels.add(productDataModel);

                adapter.notifyItemChanged(productDataModels.size());
//                progressIndicatorShimmerLayout = GlobalValue.showShimmerLayout(getContext(), containerLinearLayout);

            }
        };
        ArrayList urlList =new ArrayList<>();
        urlList.add("url");
//      productDataModels.add( new ProductDataModel("productId","Chair6","390","For sitting7","2008",urlList,false));
      /*  if(GlobalValue.isBusinessOwner()) {
            orderSymbolLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(GlobalValue.getHostActivityIntent(getContext(), null, GlobalValue.ORDER_FRAGMENT_TYPE, null));

                }
            });
            getOwnerProfileData();

        }

       */
        adapter.notifyItemChanged(productDataModels.size());
        createTabLayout(new OnNewCategorySelectedListener() {
            @Override
            public void onSelected(String categoryName) {
                categorySelected = categoryName;

//                productDataModels.clear();
                adapter.notifyDataSetChanged();

                isFirstLoad = true;
                isLoadingMoreProducts = false;
                getProductData();
            }
        });

        getProductData();
        listenToScrollChange();
//        manageChipSelection();
        return parentView;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(documentReferenceAttachedListenerRegistrationArrayList.size() != 0) {
            for (int i = 0; i < documentReferenceAttachedListenerRegistrationArrayList.size(); i++) {
                documentReferenceAttachedListenerRegistrationArrayList.get(i).remove();
            }
        }
        if(activeExoPlayerArrayList.size() != 0){
            for (int i = 0; i < activeExoPlayerArrayList.size(); i++) {
                activeExoPlayerArrayList.get(i).release();
            }

        }
    }
    /*
        @Override
        public void onResume() {
            super.onResume();
            if(GlobalValue.isOwner()) {
                if (allProductNewOrderCountTextView != null) {
                    if (GlobalValue.ALL_PRODUCT_NEW_ORDER_COUNT <= 0) {
                        allProductNewOrderCountTextView.setVisibility(View.GONE);
                    } else {
                        allProductNewOrderCountTextView.setText("" + GlobalValue.ALL_PRODUCT_NEW_ORDER_COUNT);
                    }

                }
            }
        }
    */
    void listenToScrollChange(){
//        if(true){
//            return;
//        }

        productRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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
                if(!productRecyclerView.canScrollVertically(View.LAYOUT_DIRECTION_RTL)){
//                           Toast.makeText(getContext(), "end", Toast.LENGTH_SHORT).show();
                    if(!isLoadingMoreProducts){
                        getProductData();
//                        Toast.makeText(getContext(), "Scrolled", Toast.LENGTH_SHORT).show();
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
    public void createTabLayout(OnNewCategorySelectedListener onNewCategorySelectedListener){
        productTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //change categories here
//                toggleContentsVisibility(false);
                onNewCategorySelectedListener.onSelected(tab.getText().toString());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        TabLayout.Tab allTabItem= productTabLayout.newTab();
        allTabItem.setText("ALL");
        productTabLayout.addTab(allTabItem,0,true);



        TabLayout.Tab devices =productTabLayout.newTab();
        devices.setText("DEVICES & TECHS");
        productTabLayout.addTab(devices,1);

        TabLayout.Tab propertyTabItem= productTabLayout.newTab();
        propertyTabItem.setText("PROPERTIES");
        productTabLayout.addTab(propertyTabItem,2);


        TabLayout.Tab furnitureTabItem = productTabLayout.newTab();
        furnitureTabItem.setText("HOMES & FURNITURE");
        productTabLayout.addTab(furnitureTabItem, 3);


        TabLayout.Tab autoMobiles =productTabLayout.newTab();
        autoMobiles.setText("AUTO MOBILES");
        productTabLayout.addTab(autoMobiles,4);


        TabLayout.Tab fashion= productTabLayout.newTab();
        fashion.setText("FASHION & DESIGNING");
        productTabLayout.addTab(fashion,5);

        TabLayout.Tab foods= productTabLayout.newTab();
        foods.setText("FOODS");
        productTabLayout.addTab(foods,6);

        TabLayout.Tab others= productTabLayout.newTab();
        others.setText("OTHERS");
        productTabLayout.addTab(others,7);


    }
    private  void getOwnerProfileData() {
        if(GlobalValue.isBusinessOwner()){
            GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentBusinessId())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value!=null){
                                long newOrders = value.get(GlobalValue.TOTAL_NUMBER_OF_NEW_ORDERS) != null ? value.getLong(GlobalValue.TOTAL_NUMBER_OF_NEW_ORDERS) : 0L;
                                if(newOrders >0) {
                                    allProductNewOrderCountTextView.setVisibility(View.VISIBLE);
                                    allProductNewOrderCountTextView.setText("" + newOrders);
                                }else{
                                    allProductNewOrderCountTextView.setVisibility(View.GONE);
                                }

                            }
                        }
                    });
        }
    }

    void manageRefreshLayout(){
        topRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
                topRefreshLayout.setRefreshing(false);
                isFirstLoad = true;
                isLoadingMoreProducts = false;
                getProductData();

            }
        });
    }

    private void getProductData() {

//       Query query =  firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.PRODUCT_CATEGORY,categorySelected).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP,Query.Direction.DESCENDING);
        Query query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, true).whereEqualTo(GlobalValue.IS_PRIVATE, false).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(20);

        if (!isLoadingMoreProducts) {

            if(isFirstLoad) {

                productDataModels.clear();
                adapter.notifyDataSetChanged();
                shimmerLayout.startShimmer();
                shimmerLayout.setVisibility(View.VISIBLE);
            } else {
                progressIndicatorShimmerLayout = GlobalValue.showShimmerLayout(getContext(), containerLinearLayout);
                query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, true).whereEqualTo(GlobalValue.IS_PRIVATE, false).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).startAfter(lastRetrievedProductSnapshot).limit(20);

            }
              if(isAdvertApproval){
                if (isFromSearchContext) {
                    if(isFirstLoad) {
                        query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, true).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereArrayContains(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchKeyword).whereEqualTo(GlobalValue.IS_ADVERT_REQUESTED, true).limit(20);
                    }else{
                        query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, true).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereArrayContains(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchKeyword).whereEqualTo(GlobalValue.IS_ADVERT_REQUESTED, true).startAfter(lastRetrievedProductSnapshot).limit(20);
                    }
                }
                else{
                    if(isFirstLoad) {
                        query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, true).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.IS_ADVERT_REQUESTED, true).limit(20);
                    }else {
                        query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, true).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.IS_ADVERT_REQUESTED, true).limit(20).startAfter(lastRetrievedProductSnapshot);
                    }

                }

            }

              else if (isFromSearchContext) {
                if(isFirstLoad){
                    query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, true).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereArrayContains(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchKeyword).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(20);
                }else {
                    query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, true).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereArrayContains(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchKeyword).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).startAfter(lastRetrievedProductSnapshot).limit(20);
                }
            }
            else {
                if (!categorySelected.equalsIgnoreCase("ALL")) {
                    if(isFirstLoad){
                        query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, true).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.PRODUCT_CATEGORY, categorySelected).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(20);
                    }else {
                        query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, true).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.PRODUCT_CATEGORY, categorySelected).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).startAfter(lastRetrievedProductSnapshot).limit(20);
                    }
                }
                if (categorySelected.equalsIgnoreCase("UNAPPROVED")) {
//                query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, true).whereEqualTo(GlobalValue.PRODUCT_CATEGORY,categorySelected).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING);
                    if(isFirstLoad){
                        query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, false).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(20);
                    }else {
                        query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, false).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).startAfter(lastRetrievedProductSnapshot).limit(20);
                    }
                }
                if (isFromSingleOwner) {

                    if (productOwnerUserId.equals(GlobalValue.getCurrentUserId())) {
//                    query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING);
                        if(isFirstLoad){
                            query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.PRODUCT_OWNER_USER_ID, GlobalValue.getCurrentUserId()).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(20);
                        }else {
                            query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.PRODUCT_OWNER_USER_ID, GlobalValue.getCurrentUserId()).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).startAfter(lastRetrievedProductSnapshot).limit(20);
                        }
//                    Toast.makeText(getContext(), "single...me", Toast.LENGTH_SHORT).show();
                    } else {
//                    query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING);
                        if(isFirstLoad){
                            query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, true).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.PRODUCT_OWNER_USER_ID, productOwnerUserId).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(20);
                            Toast.makeText(getContext(), "approved data.", Toast.LENGTH_SHORT).show();

                        }else {
                            query = firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_APPROVED, true).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.PRODUCT_OWNER_USER_ID, productOwnerUserId).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).startAfter(lastRetrievedProductSnapshot).limit(20);
                            Toast.makeText(getContext(), "disaaproved data.", Toast.LENGTH_SHORT).show();

                        }
//                    Toast.makeText(getContext(), "single...him", Toast.LENGTH_SHORT).show();

                    }

                }
            }

            isLoadingMoreProducts = true;

            query.get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    productFetchListener.onFailed(e.getMessage());
                    isLoadingMoreProducts = false;

                }
            }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

//                    productDataModels.clear();
//                    adapter.notifyDataSetChanged();

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        String productId = documentSnapshot.getId();
                        String productOwnerId = "" + documentSnapshot.get(GlobalValue.PRODUCT_OWNER_USER_ID);
                        String productTitle = "" + documentSnapshot.get(GlobalValue.PRODUCT_TITLE);
                        String productPrice = "" + documentSnapshot.get(GlobalValue.PRODUCT_PRICE);
                        String productDescription = "" + documentSnapshot.get(GlobalValue.PRODUCT_DESCRIPTION);
                        String location = documentSnapshot.get(GlobalValue.LOCATION) + "";
                        String phone = documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_PHONE_NUMBER) + "";
                        String email = documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_EMAIL) + "";
                        String residentialAddress = documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_ADDRESS) + "";
                        String datePosted = documentSnapshot.get(GlobalValue.DATE_POSTED_TIME_STAMP) != null ? documentSnapshot.getTimestamp(GlobalValue.DATE_POSTED_TIME_STAMP).toDate() + "" : "Undefined";
                        if (datePosted.length() > 10) {
                            datePosted = datePosted.substring(0, 10);
                        }
                        long productOrderCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_ORDERS) != null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_ORDERS) : 0L;
                        long productNewOrderCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_NEW_ORDERS) != null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_NEW_ORDERS) : 0L;
                        long productViewCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_VIEWS) != null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_VIEWS) : 0L;
                        ArrayList<String> imageUrlList = documentSnapshot.get(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL_ARRAY_LIST) != null ? (ArrayList<String>) documentSnapshot.get(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL_ARRAY_LIST) : new ArrayList<>();
                        boolean isPrivate = documentSnapshot.get(GlobalValue.IS_PRIVATE) != null ? documentSnapshot.getBoolean(GlobalValue.IS_PRIVATE) : false;
                        boolean isFromSubmission = documentSnapshot.get(GlobalValue.IS_FROM_SUBMISSION) != null ? documentSnapshot.getBoolean(GlobalValue.IS_FROM_SUBMISSION) : false;
                        boolean isApproved = documentSnapshot.get(GlobalValue.IS_APPROVED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_APPROVED) : false;
                        boolean isSold = documentSnapshot.get(GlobalValue.IS_SOLD) != null ? documentSnapshot.getBoolean(GlobalValue.IS_SOLD) : false;
                        boolean isAdvertRequested =  documentSnapshot.get(GlobalValue.IS_ADVERT_REQUESTED)!=null? documentSnapshot.getBoolean(GlobalValue.IS_ADVERT_REQUESTED): false;

                        productFetchListener.onSuccess(new ProductDataModel(productId, productOwnerId, productTitle, productPrice, productDescription, location, phone, email, residentialAddress, isSold, datePosted, productViewCount, productOrderCount, productNewOrderCount, imageUrlList, isPrivate, isFromSubmission, isApproved,isAdvertRequested));
                    }
                    GlobalValue.removeShimmerLayout(containerLinearLayout,progressIndicatorShimmerLayout);
                    if (queryDocumentSnapshots.size() == 0) {
                        if(isFirstLoad) {

                        }
                    }else{
                        lastRetrievedProductSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                    }
                    isFirstLoad = false;
                    isLoadingMoreProducts = false;

                }
            });

        }
    }
    public void displayProductView(Context context,
                                   ViewGroup viewGroup,
                                   String firstPostProfileImageDownloadUrl,
                                   String secondPostProfileImageDownloadUrl,
                                   String thirdPostProfileImageDownloadUrl,
                                   String postProfileVideoDownloadUrl,
                                   String postSubjectName,
                                   String postId,
                                   String datePosted,
                                   String postDetails,
                                   String totalNumberOfViews,
                                   String totalNumberOfComments,
                                   String totalNumberOfLikes,
                                   String totalNumberOfShares
    ) {

        if (context != null) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.all_posts_layout_holder_view, viewGroup, false);


            ImageView firstPostImageView = itemView.findViewById(R.id.firstProductImageViewId);

            ImageView secondPostImageView = itemView.findViewById(R.id.secondProductImageViewId);

//            ImageView thirdPostImageView = itemView.findViewById(R.id.thirdPostImageViewId);

            StyledPlayerView styledPlayerView = itemView.findViewById(R.id.styledPlayerViewId);


//            ImageView totalNumberOfViewsImageView = itemView.findViewById(R.id.totalNumberOfViewsImageViewId);
//
//            ImageView totalNumberOfCommentsImageView = itemView.findViewById(R.id.totalNumberOfCommentsImageViewId);
//
//            ImageView totalNumberOfLikesImageView = itemView.findViewById(R.id.totalNumberOfLikesImageViewId);
//
//            ImageView totalNumberOfSharesImageView = itemView.findViewById(R.id.totalNumberOfSharesImageViewId);

            TextView postTitleTextView = itemView.findViewById(R.id.productDisplayNameTextViewId);

            TextView datePostedTextView = itemView.findViewById(R.id.datePostedTextViewId);

            TextView postDetailsTextView = itemView.findViewById(R.id.productDescriptionTextViewId);
//
//            TextView totalNumberOfViewsTextView = itemView.findViewById(R.id.totalNumberOfViewsTextViewId);
//            TextView totalNumberOfCommentsTextView = itemView.findViewById(R.id.totalNumberOfCommentsTextViewId);
//            TextView totalNumberOfLikesTextView = itemView.findViewById(R.id.totalNumberOfLikesTextViewId);
//            TextView totalNumberOfSharesTextView = itemView.findViewById(R.id.totalNumberOfSharesTextViewId);
//            TextView postIdHolderTextView = itemView.findViewById(R.id.postIdHolderTextViewId);
//
//
//            View addCommentIncludeLayoutView = itemView.findViewById(R.id.addCommentIncludeLayoutViewId);

//
//            EditText addCommentEditText = itemView.findViewById(R.id.addCommentEditTextId);
//            MaterialButton confirmAddCommentActionButton = itemView.findViewById(R.id.confirmAddCommentActionButtonId);
//            confirmAddCommentActionButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    addCommentIncludeLayoutView.setVisibility(View.GONE);
//
////                addCommentToPost(getContext(),postId,addCommentEditText);
//                    GlobalValue.addCommentToPost(context, firebaseFirestore, postId, addCommentEditText.getText().toString(), userId, pageId, userId, true, new GlobalValue.ActionCallback() {
//                        @Override
//                        public void onActionSuccess() {
//
//                        }
//
//                        @Override
//                        public void onActionFailure(@NonNull Exception e) {
//
//                        }
//                    });
//                    addCommentEditText.setText("");
//                    GlobalValue.hideKeyboard(getContext());
//
////                    addCommentToMainUserPost(getContext(),postOwnerUserId,postId);
//
//                }
//            });
//            MaterialButton cancelCommentActionButton = itemView.findViewById(R.id.cancelCommentActionButtonId);
//            cancelCommentActionButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    addCommentIncludeLayoutView.setVisibility(View.GONE);
//                    GlobalValue.hideKeyboard(getContext());
//
//
//                }
//            });
//
//
//            LinearLayout allThreeCommentsLinearLayout = itemView.findViewById(R.id.allThreeCommentsLinearLayoutId);
//

            if (!firstPostProfileImageDownloadUrl.equals("null")) {
                Picasso.get().load(firstPostProfileImageDownloadUrl).error(R.drawable.ic_baseline_image_24).into(firstPostImageView);

            }
            if (firstPostProfileImageDownloadUrl.equals("null")) {
                firstPostImageView.setVisibility(View.GONE);
            }
            if (!secondPostProfileImageDownloadUrl.equals("null")) {
                secondPostImageView.setVisibility(View.VISIBLE);
                Picasso.get().load(secondPostProfileImageDownloadUrl).error(R.drawable.ic_baseline_image_24).into(secondPostImageView);

            }if (secondPostProfileImageDownloadUrl.equals("null")) {
                secondPostImageView.setVisibility(View.GONE);
            }
//            if(postProfileVideoDownloadUrl.equals("null") && !thirdPostProfileImageDownloadUrl.equals("null")) {
//                styledPlayerView.setVisibility(View.GONE);
//                thirdPostImageView.setVisibility(View.VISIBLE);
//                Picasso.get().load(thirdPostProfileImageDownloadUrl).error(R.drawable.ic_baseline_image_24).into(thirdPostImageView);
//
//            }if (!postProfileVideoDownloadUrl.equals("null") || thirdPostProfileImageDownloadUrl.equals("null")) {
//                thirdPostImageView.setVisibility(View.GONE);
//            }

            if(!postProfileVideoDownloadUrl.equals("null")){
                styledPlayerView.setVisibility(View.VISIBLE);
                GlobalValue.displayExoplayerVideo(context,styledPlayerView,activeExoPlayerArrayList, Uri.parse(postProfileVideoDownloadUrl));

            }else{
                styledPlayerView.setVisibility(View.GONE);

            }
//            postTitleTextView.setText(postSubjectName);
            datePostedTextView.setText(GlobalValue.getFormattedDate(datePosted));
//            postDetailsTextView.setText(postDetails);

//            GlobalValue.configureHashTag(getContext(), postDetailsTextView, postDetails,R.color.holo_blue_dark);
//            GlobalValue.configureHashTag(getContext(),postTitleTextView,postSubjectName,R.color.holo_blue_dark);

//            totalNumberOfViewsTextView.setText(totalNumberOfViews);
//            totalNumberOfCommentsTextView.setText(totalNumberOfComments);
//            totalNumberOfLikesTextView.setText(totalNumberOfLikes);
//            totalNumberOfSharesTextView.setText(totalNumberOfShares);



//            GlobalValue.checkIfPostIsLiked(firebaseFirestore, userId, pageId, postId, userId, true,false, new GlobalValue.OnCheckLikeStatusSuccess() {
//                @Override
//                public void onSuccess(boolean isLiked) {
//                    if(isLiked){
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if(getContext() != null) {
//
//                                    totalNumberOfLikesImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_like_thumb_24, getContext().getTheme()));
//                                }
//
//
//                            }
//                        });
//
//
//
//                    }else{
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if(getContext() != null) {
//                                    totalNumberOfLikesImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_twotone_thumb_up_24, getContext().getTheme()));
//                                }
//                            }
//                        });
//
//
//                    }
//                }
//            });

//            firebaseFirestore.collection("ALL_PAGES").document(postOwnerUserId).collection("PAGES").document(pagePosterId).collection("POSTS").document(postId).collection("LIKES").document(userId).get().addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    //userHasLiked=false;
//                }
//            }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    String likerUserId = String.valueOf(documentSnapshot.get("LIKER_USER_ID"));
//                    if (likerUserId!=null) {
//                        if (likerUserId.equalsIgnoreCase(userId)) {
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_like_thumb_24,context.getTheme()));
//                                }
//                            });
//
//
//                        } else {
//                            // userHasLiked = false;
//                            likePagePost(getContext(),postOwnerUserId,pagePosterId, postId, totalNumberOfLikesImageView);
//
//                        }
//                    }else{
////                            userHasLiked = false;
//                        likePagePost(getContext(),postOwnerUserId,pagePosterId, postId, totalNumberOfLikesImageView);
//
//                    }
//                }
//            });


//            totalNumberOfLikesImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    if (checkIfUserHasAlreadyLikedTheMainUserPost(postOwnerUserId, postId)) {
////                        likeMainUserPost(getContext(), postOwnerUserId, postId, totalNumberOfLikesImageView);
////                    } else {
////                        unlikeMainUserPost(getContext(), postOwnerUserId, postId, totalNumberOfLikesImageView);
////                    }
//                    if (likeStatusIsChecked[0] && !likeImageIsClicked[0]) {
//                        if (isAlreadyLiked[0]) {
//                            unlikePost(getContext(), postId, totalNumberOfLikesImageView);
//
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_twotone_thumb_up_24, getContext().getTheme()));
//                                }
//                            });
//
//                        } else {
//                            //user has not like then let him like the post
//                            likePost(getContext(), postId, totalNumberOfLikesImageView);
//
//                        }
//                    } else {
//                        likeDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    DocumentSnapshot documentSnapshot = task.getResult();
//                                    if (documentSnapshot.exists()) {
//                                        unlikePost(getContext(), postId, totalNumberOfLikesImageView);
//
//                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_twotone_thumb_up_24, getContext().getTheme()));
//
//
//                                            }
//                                        });
//
//                                    } else {
//                                        likePost(getContext(), postId, totalNumberOfLikesImageView);
//
//                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_like_thumb_24, getContext().getTheme()));
//                                            }
//                                        });
//
//                                    }
//
//
//                                }
//                            }
//
//                        });
//
//                    }
//                    likeImageIsClicked[0] = true;
//                }
//            });
//            totalNumberOfLikesImageView.setOnClickListener(new View.OnClickListener() {
//                                                               @Override
//                                                               public void onClick(View view) {
//
//                                                                   GlobalValue.checkIfDocumentExists(likeDocumentReference, new GlobalValue.OnDocumentExistStatusCallback() {
//                                                                       @Override
//                                                                       public void onExist() {
//                                                                           GlobalValue.unlikePagePost(getContext(), firebaseFirestore, userId, pageId, postId, userId, new GlobalValue.ActionCallback() {
//                                                                               @Override
//                                                                               public void onActionSuccess() {
//
//                                                                               }
//
//                                                                               @Override
//                                                                               public void onActionFailure(@NonNull Exception e) {
//
//                                                                               }
//                                                                           });
//                                                                           new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                                                               @Override
//                                                                               public void run() {
//                                                                                   totalNumberOfLikesImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_twotone_thumb_up_24, getContext().getTheme()));
//                                                                               }
//                                                                           });
//                                                                       }
//
//                                                                       @Override
//                                                                       public void onNotExist() {
//                                                                           GlobalValue.likePagePost(getContext(), firebaseFirestore, userId, pageId, postId, userId, new GlobalValue.ActionCallback() {
//                                                                               @Override
//                                                                               public void onActionSuccess() {
//
//                                                                               }
//
//                                                                               @Override
//                                                                               public void onActionFailure(@NonNull Exception e) {
//
//                                                                               }
//                                                                           });
//                                                                           new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                                                               @Override
//                                                                               public void run() {
//                                                                                   totalNumberOfLikesImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_like_thumb_24, getContext().getTheme()));
//                                                                               }
//                                                                           });
//                                                                       }
//
//                                                                       @Override
//                                                                       public void onFailed(@NonNull String errorMessage) {
//
//                                                                       }
//                                                                   });
//
//                                                                   //                if (checkIfUserHasAlreadyLikedThePagePost(postOwnerUserId,pagePosterId,postId)) {
////                    likePagePost(getContext(),postOwnerUserId,pagePosterId, postId, totalNumberOfLikesImageView);
////                } else {
////                    unlikePagePost(getContext(),postOwnerUserId,pagePosterId, postId, totalNumberOfLikesImageView);
////                }
//
////                                                                   if (likeStatusIsChecked[0]) {
////                                                                       if (isAlreadyLiked[0]) {
////                                                                           unlikePost(getContext(), postId, totalNumberOfLikesImageView);
////
////                                                                           new Handler(Looper.getMainLooper()).post(new Runnable() {
////                                                                               @Override
////                                                                               public void run() {
////                                                                                   totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_twotone_thumb_up_24, getContext().getTheme()));
////                                                                               }
////                                                                           });
////
////                                                                       } else {
////                                                                           //user has not like then let him like the post
////                                                                           likePost(getContext(), postId, totalNumberOfLikesImageView);
////                                                                           new Handler(Looper.getMainLooper()).post(new Runnable() {
////                                                                               @Override
////                                                                               public void run() {
////                                                                                   totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_like_thumb_24, getContext().getTheme()));
////                                                                               }
////                                                                           });
////                                                                       }
////                                                                   }
////                                                                   else {
//////                    firebaseFirestore.collection("ALL_USERS").document(postOwnerUserId).collection("POSTS").document(postId).collection("LIKES").document(userId).get().addOnFailureListener(new OnFailureListener() {
//////                        @Override
//////                        public void onFailure(@NonNull Exception e) {
//////                            //userHasLiked=false;
//////                        }
//////                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//////                        @Override
//////                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//////                            String likerUserId = String.valueOf(documentSnapshot.get("LIKER_USER_ID"));
//////                            if (likerUserId != null) {
//////                                if (likerUserId.equalsIgnoreCase(userId)) {
////////                                    userHasLiked = true;
//////                                    unlikeMainUserPost(getContext(), postOwnerUserId, postId, totalNumberOfLikesImageView);
//////                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//////                                        @Override
//////                                        public void run() {
//////                                            totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_twotone_thumb_up_24,getContext().getTheme()));
//////                                        }
//////                                    });
//////                                } else {
////////                                    userHasLiked = false;
//////                                    likeMainUserPost(getContext(), postOwnerUserId, postId, totalNumberOfLikesImageView);
//////                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//////                                        @Override
//////                                        public void run() {
//////                                            totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_like_thumb_24,getContext().getTheme()));
//////                                        }
//////                                    });
//////                                }
//////                            } else {
////////                                userHasLiked = false;
//////                                likeMainUserPost(getContext(), postOwnerUserId, postId, totalNumberOfLikesImageView);
//////                                new Handler(Looper.getMainLooper()).post(new Runnable() {
//////                                    @Override
//////                                    public void run() {
//////                                        totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_like_thumb_24,getContext().getTheme()));
//////                                    }
//////                                });
//////                            }
//////                        }
//////                    });
////                                                                       if (likeDocumentReference.get().getResult().exists()) {
////
////                                                                           unlikePost(getContext(), postId, totalNumberOfLikesImageView);
////
////                                                                           new Handler(Looper.getMainLooper()).post(new Runnable() {
////                                                                               @Override
////                                                                               public void run() {
////                                                                                   totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_like_thumb_24, getContext().getTheme()));
////
////
////                                                                               }
////                                                                           });
////
////                                                                       }
////                                                                       else {
////                                                                           likePost(getContext(), postId, totalNumberOfLikesImageView);
////
////
////                                                                           new Handler(Looper.getMainLooper()).post(new Runnable() {
////                                                                               @Override
////                                                                               public void run() {
////                                                                                   totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_twotone_thumb_up_24, getContext().getTheme()));
////                                                                               }
////                                                                           });
////
////                                                                       }
////
////                                                                   }
//                                                               }
//                                                           });

//            totalNumberOfLikesImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    GlobalValue.checkIfPostIsLiked(firebaseFirestore, userId, pageId, postId, userId, true,true, new GlobalValue.OnCheckLikeStatusSuccess() {
//                        @Override
//                        public void onSuccess(boolean isLiked) {
//                            if(isLiked){
//                                GlobalValue.unlikePagePost(getContext(), firebaseFirestore, userId, pageId, postId, userId, new GlobalValue.ActionCallback() {
//                                    @Override
//                                    public void onActionSuccess() {
//
//                                    }
//
//                                    @Override
//                                    public void onActionFailure(@NonNull Exception e) {
//
//                                    }
//                                });
//                                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        totalNumberOfLikesImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_twotone_thumb_up_24, getContext().getTheme()));
//                                    }
//                                });
//
//
//                            }else{
//
//                                GlobalValue.likePagePost(getContext(), firebaseFirestore, userId, pageId, postId, userId, new GlobalValue.ActionCallback() {
//                                    @Override
//                                    public void onActionSuccess() {
//
//                                    }
//
//                                    @Override
//                                    public void onActionFailure(@NonNull Exception e) {
//
//                                    }
//                                });
//                                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        totalNumberOfLikesImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_like_thumb_24, getContext().getTheme()));
//                                    }
//                                });
//
//
//                            }
//                        }
//                    });
//
//
//                }
//            });
//
//            totalNumberOfCommentsImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    addCommentIncludeLayoutView.setVisibility(View.VISIBLE);
//                    addCommentEditText.requestFocus();
//                    GlobalValue.openKeyboard(getContext(),addCommentEditText);
//
////                commentingPostId  = postId;
//                }
//            });
//            totalNumberOfSharesImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
////                    GlobalValue.goToSharePostActivity(getContext(), ShareChatActivity.class, true, userId, pageId, postId, postSubjectName, postDetails, false, true);
//
//                    getContext().startActivity(GlobalValue.goToShareItemActivity( getContext(),  userId, pageId,  userId,  pageId ,  postId ,  true,  false,  false,  false,  true));
//
//                }
//            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), SingleProductActivity.class);
                    intent.putExtra("POST_ID", postId);
                    intent.putExtra("PAGE_POSTER_ID", pageId);
                    intent.putExtra("POST_OWNER_DISPLAY_NAME", pageName);

                    startActivity(intent);
                }
            });
//
//            GlobalValue.getThreePostComment(getContext(), allThreeCommentsLinearLayout, firebaseFirestore, userId, pageId, postId, true,true);
//
//
//            DocumentReference documentEventReference =  firebaseFirestore.collection("ALL_PAGES").document(userId).collection("PAGES").document(pageId).collection("POSTS").document(postId);
////            documentReferenceAttachedListenerArrayList.add(documentEventReference);
//            GlobalValue.detectAndUpdateEventValues(documentEventReference ,documentReferenceAttachedListenerRegistrationArrayList, totalNumberOfLikesTextView, totalNumberOfCommentsTextView, totalNumberOfViewsTextView, new GlobalValue.OnEventListener() {
//                @Override
//                public void onEventHappen(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                }
//            });



            viewGroup.addView(itemView);
        }

    }
/*
    void manageChipSelection(){
        productOptionChip.setCheckable(true);
        serviceOptionChip.setCheckable(true);

        productOptionChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        productOptionChip.setChecked(true);

    }
*/
    interface ProductFetchListener{
        void onFailed(String errorMessage);
        void onSuccess(ProductDataModel productDataModel);
    }
    interface OnNewCategorySelectedListener{
        void onSelected(String categoryName);
    }

}