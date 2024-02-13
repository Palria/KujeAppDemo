package com.palria.kujeapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AllJobsFragment extends Fragment {

    FirebaseFirestore firebaseFirestore;
    String userId;
    String pageId;
    String jobOwnerUserId;
    boolean isFromSingleOwner;
    //
//    EditText addCommentEditText;
//
//    View addCommentIncludeLayoutView;
//    MaterialButton confirmAddCommentActionButton;
//    MaterialButton cancelCommentActionButton;
//
//    String commentingPostId;
    LinearLayout containerLinearLayout;
//    LinearLayout orderSymbolLinearLayout;
    JobFetchListener jobFetchListener;
    RecyclerView jobRecyclerView;
    SwipeRefreshLayout topRefreshLayout;
    boolean isFromSearchContext = false;
    String searchKeyword = "";
    boolean isFirstLoad = true;
    boolean isLoadingMoreJobs = false;
    DocumentSnapshot lastRetrievedJobSnapshot = null;

    FrameLayout topFrameLayout;
    JobAdapter adapter;
    ArrayList<JobDataModel> jobDataModels = new ArrayList<>();
//    TabLayout tabLayout;
//    TextView allJobNewApplicantsCountTextView;
    String pageName;
    String categorySelected = "ALL";
    ArrayList<ListenerRegistration> documentReferenceAttachedListenerRegistrationArrayList = new ArrayList<>();
    ArrayList<ExoPlayer> activeExoPlayerArrayList = new ArrayList<>();
    ShimmerFrameLayout shimmerLayout,progressIndicatorShimmerLayout;

    public AllJobsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            pageId = getArguments().getString("PAGE_ID");
            pageName = getArguments().getString("PAGE_DISPLAY_NAME");

            isFromSearchContext = getArguments().getBoolean(GlobalValue.IS_FROM_SEARCH_CONTEXT,false);
            searchKeyword = getArguments().getString(GlobalValue.SEARCH_KEYWORD,"");
            jobOwnerUserId = getArguments().getString(GlobalValue.JOB_OWNER_USER_ID,"0");
            isFromSingleOwner = getArguments().getBoolean(GlobalValue.IS_FROM_SINGLE_OWNER,false);

        }


        firebaseFirestore = FirebaseFirestore.getInstance();
        userId =FirebaseAuth.getInstance().getCurrentUser().getUid();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_all_jobs, container, false);
//        addCommentEditText = parentView.findViewById(R.id.addCommentEditTextId);
        topRefreshLayout = parentView.findViewById(R.id.topRefreshLayoutId);
        containerLinearLayout = parentView.findViewById(R.id.containerLinearLayoutId);
        topFrameLayout = parentView.findViewById(R.id.topFrameLayoutId);
//        tabLayout = parentView.findViewById(R.id.categoryTabLayoutId);
//        orderSymbolLinearLayout = parentView.findViewById(R.id.orderSymbolLinearLayoutId);
        shimmerLayout = parentView.findViewById(R.id.shimmerLayout);


//        confirmAddCommentActionButton = parentView.findViewById(R.id.confirmAddCommentActionButtonId);
//        confirmAddCommentActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addCommentIncludeLayoutView.setVisibility(View.GONE);
//                addCommentToPost(getContext(),commentingPostId);
//            }
//        });
//        cancelCommentActionButton = parentView.findViewById(R.id.cancelCommentActionButtonId);
//        cancelCommentActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                cancelComment();
//            }
//        });

//        containerLinearLayout = parentView.findViewById(R.id.containerLinearLayoutId);
//        allJobNewApplicantsCountTextView = parentView.findViewById(R.id.allJobNewApplicantsCountTextViewId);
        jobRecyclerView = parentView.findViewById(R.id.jobRecyclerViewId);
        adapter = new JobAdapter(jobDataModels,getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2, GridLayoutManager.VERTICAL,false);
        jobRecyclerView.setLayoutManager(gridLayoutManager);
        jobRecyclerView.setAdapter(adapter);
        if(isFromSearchContext || isFromSingleOwner){
//            topFrameLayout.setVisibility(View.GONE);
//            tabLayout.setVisibility(View.GONE);
        }

        manageRefreshLayout();
        jobFetchListener = new JobFetchListener() {
            @Override
            public void onFailed(String errorMessage) {

            }

            @Override
            public void onSuccess(JobDataModel jobDataModel) {

                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);

                jobDataModels.add(jobDataModel);
//                jobDataModels.add(jobDataModel);
//                jobDataModels.add(jobDataModel);
//                jobDataModels.add(jobDataModel);
//                jobDataModels.add(jobDataModel);

                adapter.notifyItemChanged(jobDataModels.size());
//                progressIndicatorShimmerLayout = GlobalValue.showShimmerLayout(getContext(), containerLinearLayout);

            }
        };
        ArrayList urlList =new ArrayList<>();
        urlList.add("url");
//      jobDataModels.add( new JobDataModel("jobId","Chair", "30","For sitting1","2002",urlList,false));
//      jobDataModels.add( new JobDataModel("jobId","Chair1","50","For sitting2","2003",urlList,false));
//      jobDataModels.add( new JobDataModel("jobId","Chair2","90","For sitting3","2004",urlList,false));
//      jobDataModels.add( new JobDataModel("jobId","Chair3","300","For sitting4","2005",urlList,false));
//      jobDataModels.add( new JobDataModel("jobId","Chair4","80","For sitting5","2006",urlList,false));
//      jobDataModels.add( new JobDataModel("jobId","Chair5","20","For sitting6","2007",urlList,false));
//      jobDataModels.add( new JobDataModel("jobId","Chair6","390","For sitting7","2008",urlList,false));
        if(GlobalValue.isBusinessOwner()) {
//            orderSymbolLinearLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivity(GlobalValue.getHostActivityIntent(getContext(), null, GlobalValue.APPLICANT_FRAGMENT_TYPE, null));
//
//                }
//            });
//            getOwnerProfileData();

        }
        adapter.notifyItemChanged(jobDataModels.size());
//        createTabLayout(new OnNewCategorySelectedListener() {
//            @Override
//            public void onSelected(String categoryName) {
//                categorySelected = categoryName;
//
//                jobDataModels.clear();
//                adapter.notifyDataSetChanged();
//
//                isFirstLoad = true;
//                isLoadingMoreJobs = false;
//                getJobData();
//            }
//        });
getJobData();
listenToScrollChange();

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
            if (allJobNewApplicantsCountTextView != null) {
                if (GlobalValue.ALL_JOB_NEW_APPLICANT_COUNT <= 0) {
                    allJobNewApplicantsCountTextView.setVisibility(View.GONE);
                } else {
                    allJobNewApplicantsCountTextView.setText("" + GlobalValue.ALL_JOB_NEW_APPLICANT_COUNT);
                }

            }
        }
    }
*/
    void listenToScrollChange(){
//        if(true){
//            return;
//        }

        jobRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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
                if(!jobRecyclerView.canScrollVertically(View.LAYOUT_DIRECTION_RTL)){
//                           Toast.makeText(getContext(), "end", Toast.LENGTH_SHORT).show();
                    if(!isLoadingMoreJobs){
                        getJobData();
//                        Toast.makeText(getContext(), "Scrolled", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
//        jobRecyclerView.getViewTreeObserver()
//                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        jobRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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

//    public void createTabLayout(OnNewCategorySelectedListener onNewCategorySelectedListener){
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                //change categories here
////                toggleContentsVisibility(false);
//                onNewCategorySelectedListener.onSelected(tab.getText().toString());
//
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//
//
//
//        TabLayout.Tab allTabItem= tabLayout.newTab();
//        allTabItem.setText("ALL");
//        tabLayout.addTab(allTabItem,0,true);
//
//            TabLayout.Tab webDevTabItem = tabLayout.newTab();
//            webDevTabItem.setText("UNAPPROVED");
//            tabLayout.addTab(webDevTabItem, 1);
//
//
//        TabLayout.Tab graphicDesignTabItem= tabLayout.newTab();
//        graphicDesignTabItem.setText("HOUSES");
//        tabLayout.addTab(graphicDesignTabItem,2);
//
//        TabLayout.Tab lands=tabLayout.newTab();
//        lands.setText("LANDS");
//        tabLayout.addTab(lands,3);
//
//
//        TabLayout.Tab others= tabLayout.newTab();
//        others.setText("OTHERS");
//        tabLayout.addTab(others,4);
////
////        TabLayout.Tab uiDesignTabItem= tabLayout.newTab();
////        uiDesignTabItem.setText("Electrical");
////        tabLayout.addTab(uiDesignTabItem,4);
////
////        TabLayout.Tab ethicalHackingTabItem= tabLayout.newTab();
////        ethicalHackingTabItem.setText("Phones");
////        tabLayout.addTab(ethicalHackingTabItem,5);
////
////
////        TabLayout.Tab gameDevTabItem= tabLayout.newTab();
////        gameDevTabItem.setText("Laptops");
////        tabLayout.addTab(gameDevTabItem,6);
////
////
////        TabLayout.Tab prototypingTabItem= tabLayout.newTab();
////        prototypingTabItem.setText("Vehicles");
////        tabLayout.addTab(prototypingTabItem,7);
////
////        TabLayout.Tab SEOTabItem= tabLayout.newTab();
////        SEOTabItem.setText("Building Tools");
////        tabLayout.addTab(SEOTabItem,8);
////
////        TabLayout.Tab androidDevTabItem=tabLayout.newTab();
////        androidDevTabItem.setText("Kitchen Utensils");
////        tabLayout.addTab(androidDevTabItem,9);
////
////        TabLayout.Tab javaTabItem=tabLayout.newTab();
////        javaTabItem.setText("Children Kits");
////        tabLayout.addTab(javaTabItem,10);
////
////        TabLayout.Tab pythonTabItem=tabLayout.newTab();
////        pythonTabItem.setText("Computers");
////        tabLayout.addTab(pythonTabItem,11);
////
////        TabLayout.Tab dataLearningTabItem =tabLayout.newTab();
////        dataLearningTabItem.setText("Motorcycle Parts");
////        tabLayout.addTab(dataLearningTabItem,12);
////
////
////        TabLayout.Tab OOPsConceptTabItem =tabLayout.newTab();
////        OOPsConceptTabItem.setText("Drinks");
////        tabLayout.addTab(OOPsConceptTabItem,13);
////
////
////        TabLayout.Tab artificialIntelligenceTabItem =tabLayout.newTab();
////        artificialIntelligenceTabItem.setText("Home Gadgets");
////        tabLayout.addTab(artificialIntelligenceTabItem,14);
//
//        if(!GlobalValue.isBusinessOwner()) {
//            tabLayout.removeTab(webDevTabItem);
//
//        }
//
//
//
//    }
//    private  void getOwnerProfileData() {
//       if(GlobalValue.isBusinessOwner()){
//        GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentBusinessId())
//                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                        if(value!=null){
//                            long newApplicantss = value.get(GlobalValue.TOTAL_NUMBER_OF_NEW_APPLICANTS) != null ? value.getLong(GlobalValue.TOTAL_NUMBER_OF_NEW_APPLICANTS) : 0L;
//                            if(newApplicantss >0) {
//                                allJobNewApplicantsCountTextView.setVisibility(View.VISIBLE);
//                                allJobNewApplicantsCountTextView.setText("" + newApplicantss);
//                            }else{
//                                allJobNewApplicantsCountTextView.setVisibility(View.GONE);
//                            }
//
//                        }
//                    }
//                });
//    }
//    }

    void manageRefreshLayout(){
        topRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
                topRefreshLayout.setRefreshing(false);
                isFirstLoad = true;
                isLoadingMoreJobs = false;
                getJobData();

            }
        });
    }

    private void getJobData() {

        Query query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).limit(20);
//        Query query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(20);

        if (!isLoadingMoreJobs) {

            if(isFirstLoad) {

                jobDataModels.clear();
                adapter.notifyDataSetChanged();
                shimmerLayout.startShimmer();
                shimmerLayout.setVisibility(View.VISIBLE);
            } else {
                progressIndicatorShimmerLayout = GlobalValue.showShimmerLayout(getContext(), containerLinearLayout);
                query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).startAfter(lastRetrievedJobSnapshot).limit(20);
//                query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).startAfter(lastRetrievedJobSnapshot).limit(20);

            }
            if (isFromSearchContext) {
                if(isFirstLoad){
                    query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereArrayContains(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchKeyword).limit(20);
//                    query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereArrayContains(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchKeyword).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(20);
                }else {
                    query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereArrayContains(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchKeyword).startAfter(lastRetrievedJobSnapshot).limit(20);
//                    query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereArrayContains(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchKeyword).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).startAfter(lastRetrievedJobSnapshot).limit(20);
                }
            } else {
                if (!categorySelected.equalsIgnoreCase("ALL")) {
                    if(isFirstLoad){
                        query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.JOB_CATEGORY, categorySelected).limit(20);
//                        query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.JOB_CATEGORY, categorySelected).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(20);
                    }else {
                        query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.JOB_CATEGORY, categorySelected).startAfter(lastRetrievedJobSnapshot).limit(20);
//                        query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.JOB_CATEGORY, categorySelected).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).startAfter(lastRetrievedJobSnapshot).limit(20);
                    }
                }
//                if (categorySelected.equalsIgnoreCase("UNAPPROVED")) {
////                query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_APPROVED, true).whereEqualTo(GlobalValue.JOB_CATEGORY,categorySelected).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING);
//                    if(isFirstLoad){
//                        query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_APPROVED, false).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(20);
//                    }else {
//                        query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_APPROVED, false).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).startAfter(lastRetrievedJobSnapshot).limit(20);
//                    }
//                }
                if (isFromSingleOwner) {

                    if (jobOwnerUserId.equals(GlobalValue.getCurrentUserId())) {
                    query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING);
//                        if(isFirstLoad){
//                            query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.JOB_OWNER_USER_ID, GlobalValue.getCurrentUserId()).limit(20);
////                            query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.JOB_OWNER_USER_ID, GlobalValue.getCurrentUserId()).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(20);
//                        }else {
//                            query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.JOB_OWNER_USER_ID, GlobalValue.getCurrentUserId()).startAfter(lastRetrievedJobSnapshot).limit(20);
////                            query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.JOB_OWNER_USER_ID, GlobalValue.getCurrentUserId()).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).startAfter(lastRetrievedJobSnapshot).limit(20);
//                        }
//                    Toast.makeText(getContext(), "single...me", Toast.LENGTH_SHORT).show();
                    } else {
                    query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING);
//                    query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING);
//                        if(isFirstLoad){
//                            query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.JOB_OWNER_USER_ID, jobOwnerUserId).limit(20);
////                            query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.JOB_OWNER_USER_ID, jobOwnerUserId).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(20);
//                        }else {
//                            query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.JOB_OWNER_USER_ID, jobOwnerUserId).startAfter(lastRetrievedJobSnapshot).limit(20);
////                            query = firebaseFirestore.collection(GlobalValue.ALL_JOBS).whereEqualTo(GlobalValue.IS_CLOSED, false).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.JOB_OWNER_USER_ID, jobOwnerUserId).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).startAfter(lastRetrievedJobSnapshot).limit(20);
//                        }
//                    Toast.makeText(getContext(), "single...him", Toast.LENGTH_SHORT).show();

                    }

                }
            }

            isLoadingMoreJobs = true;

            query.get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    jobFetchListener.onFailed(e.getMessage());
                    isLoadingMoreJobs = false;

                }
            }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

//                    jobDataModels.clear();
//                    adapter.notifyDataSetChanged();

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        String jobId = documentSnapshot.getId();
                        String jobOwnerId = "" + documentSnapshot.get(GlobalValue.JOB_OWNER_USER_ID);
                        String jobTitle = "" + documentSnapshot.get(GlobalValue.JOB_TITLE);
                        String jobSalary = "" + documentSnapshot.get(GlobalValue.JOB_SALARY);
                        String jobDescription = "" + documentSnapshot.get(GlobalValue.JOB_DESCRIPTION);
                        String location = documentSnapshot.get(GlobalValue.LOCATION) + "";
                        String phone = documentSnapshot.get(GlobalValue.CONTACT_PHONE_NUMBER) + "";
                        String email = documentSnapshot.get(GlobalValue.CONTACT_EMAIL) + "";
//                        String residentialAddress = documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_ADDRESS) + "";
                        String datePosted = documentSnapshot.get(GlobalValue.DATE_POSTED_TIME_STAMP) != null ? documentSnapshot.getTimestamp(GlobalValue.DATE_POSTED_TIME_STAMP).toDate() + "" : "Undefined";
                        if (datePosted.length() > 10) {
                            datePosted = datePosted.substring(0, 10);
                        }
                        long jobApplicantsCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_APPLICANTS) != null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_APPLICANTS) : 0L;
                        long jobNewApplicantsCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_NEW_APPLICANTS) != null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_NEW_APPLICANTS) : 0L;
                        long jobViewCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_VIEWS) != null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_VIEWS) : 0L;
                        ArrayList<String> imageUrlList = documentSnapshot.get(GlobalValue.JOB_IMAGE_DOWNLOAD_URL_ARRAY_LIST) != null ? (ArrayList<String>) documentSnapshot.get(GlobalValue.JOB_IMAGE_DOWNLOAD_URL_ARRAY_LIST) : new ArrayList<>();
                        boolean isPrivate = documentSnapshot.get(GlobalValue.IS_PRIVATE) != null ? documentSnapshot.getBoolean(GlobalValue.IS_PRIVATE) : false;
//                        boolean isFromSubmission = documentSnapshot.get(GlobalValue.IS_FROM_SUBMISSION) != null ? documentSnapshot.getBoolean(GlobalValue.IS_FROM_SUBMISSION) : false;
//                        boolean isApproved = documentSnapshot.get(GlobalValue.IS_APPROVED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_APPROVED) : false;
                        boolean isClosed = documentSnapshot.get(GlobalValue.IS_CLOSED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_CLOSED) : false;

                        jobFetchListener.onSuccess(new JobDataModel(jobId, jobOwnerId, jobTitle, jobSalary, jobDescription, location, phone, email, isClosed, datePosted, jobViewCount, jobApplicantsCount, jobNewApplicantsCount, imageUrlList, isPrivate));
                    }
                    GlobalValue.removeShimmerLayout(containerLinearLayout,progressIndicatorShimmerLayout);
                    if (queryDocumentSnapshots.size() == 0) {
                        if(isFirstLoad) {

                        }
                    }else{
                        lastRetrievedJobSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                    }
                    isFirstLoad = false;
                    isLoadingMoreJobs = false;

                }
            });

        }
    }

    //    public void displayJobView(Context context,
//                                                    ViewGroup viewGroup,
//                                                    String firstPostProfileImageDownloadUrl,
//                                                    String secondPostProfileImageDownloadUrl,
//                                                    String thirdPostProfileImageDownloadUrl,
//                                                    String postProfileVideoDownloadUrl,
//                                                    String postSubjectName,
//                                                    String postId,
//                                                    String datePosted,
//                                                    String postDetails,
//                                                    String totalNumberOfViews,
//                                                    String totalNumberOfComments,
//                                                    String totalNumberOfLikes,
//                                                    String totalNumberOfShares
//    ) {
//
//        if (context != null) {
//            View itemView = LayoutInflater.from(context).inflate(R.layout.all_posts_layout_holder_view, viewGroup, false);
//
//
////            ImageView firstPostImageView = itemView.findViewById(R.id.firstJobImageViewId);
//
////            ImageView secondPostImageView = itemView.findViewById(R.id.secondJobImageViewId);
//
////            ImageView thirdPostImageView = itemView.findViewById(R.id.thirdPostImageViewId);
//
//            StyledPlayerView styledPlayerView = itemView.findViewById(R.id.styledPlayerViewId);
//
//
////            ImageView totalNumberOfViewsImageView = itemView.findViewById(R.id.totalNumberOfViewsImageViewId);
////
////            ImageView totalNumberOfCommentsImageView = itemView.findViewById(R.id.totalNumberOfCommentsImageViewId);
////
////            ImageView totalNumberOfLikesImageView = itemView.findViewById(R.id.totalNumberOfLikesImageViewId);
////
////            ImageView totalNumberOfSharesImageView = itemView.findViewById(R.id.totalNumberOfSharesImageViewId);
//
////            TextView postTitleTextView = itemView.findViewById(R.id.jobDisplayNameTextViewId);
//
////            TextView datePostedTextView = itemView.findViewById(R.id.datePostedTextViewId);
//
////            TextView postDetailsTextView = itemView.findViewById(R.id.jobDescriptionTextViewId);
////
////            TextView totalNumberOfViewsTextView = itemView.findViewById(R.id.totalNumberOfViewsTextViewId);
////            TextView totalNumberOfCommentsTextView = itemView.findViewById(R.id.totalNumberOfCommentsTextViewId);
////            TextView totalNumberOfLikesTextView = itemView.findViewById(R.id.totalNumberOfLikesTextViewId);
////            TextView totalNumberOfSharesTextView = itemView.findViewById(R.id.totalNumberOfSharesTextViewId);
////            TextView postIdHolderTextView = itemView.findViewById(R.id.postIdHolderTextViewId);
////
////
////            View addCommentIncludeLayoutView = itemView.findViewById(R.id.addCommentIncludeLayoutViewId);
//
////
////            EditText addCommentEditText = itemView.findViewById(R.id.addCommentEditTextId);
////            MaterialButton confirmAddCommentActionButton = itemView.findViewById(R.id.confirmAddCommentActionButtonId);
////            confirmAddCommentActionButton.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    addCommentIncludeLayoutView.setVisibility(View.GONE);
////
//////                addCommentToPost(getContext(),postId,addCommentEditText);
////                    GlobalValue.addCommentToPost(context, firebaseFirestore, postId, addCommentEditText.getText().toString(), userId, pageId, userId, true, new GlobalValue.ActionCallback() {
////                        @Override
////                        public void onActionSuccess() {
////
////                        }
////
////                        @Override
////                        public void onActionFailure(@NonNull Exception e) {
////
////                        }
////                    });
////                    addCommentEditText.setText("");
////                    GlobalValue.hideKeyboard(getContext());
////
//////                    addCommentToMainUserPost(getContext(),postOwnerUserId,postId);
////
////                }
////            });
////            MaterialButton cancelCommentActionButton = itemView.findViewById(R.id.cancelCommentActionButtonId);
////            cancelCommentActionButton.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////
////                    addCommentIncludeLayoutView.setVisibility(View.GONE);
////                    GlobalValue.hideKeyboard(getContext());
////
////
////                }
////            });
////
////
////            LinearLayout allThreeCommentsLinearLayout = itemView.findViewById(R.id.allThreeCommentsLinearLayoutId);
////
//
//            if (!firstPostProfileImageDownloadUrl.equals("null")) {
//                Picasso.get().load(firstPostProfileImageDownloadUrl).error(R.drawable.ic_baseline_image_24).into(firstPostImageView);
//
//            }
//            if (firstPostProfileImageDownloadUrl.equals("null")) {
//                firstPostImageView.setVisibility(View.GONE);
//            }
//            if (!secondPostProfileImageDownloadUrl.equals("null")) {
//                secondPostImageView.setVisibility(View.VISIBLE);
//                Picasso.get().load(secondPostProfileImageDownloadUrl).error(R.drawable.ic_baseline_image_24).into(secondPostImageView);
//
//            }if (secondPostProfileImageDownloadUrl.equals("null")) {
//                secondPostImageView.setVisibility(View.GONE);
//            }
////            if(postProfileVideoDownloadUrl.equals("null") && !thirdPostProfileImageDownloadUrl.equals("null")) {
////                styledPlayerView.setVisibility(View.GONE);
////                thirdPostImageView.setVisibility(View.VISIBLE);
////                Picasso.get().load(thirdPostProfileImageDownloadUrl).error(R.drawable.ic_baseline_image_24).into(thirdPostImageView);
////
////            }if (!postProfileVideoDownloadUrl.equals("null") || thirdPostProfileImageDownloadUrl.equals("null")) {
////                thirdPostImageView.setVisibility(View.GONE);
////            }
//
//            if(!postProfileVideoDownloadUrl.equals("null")){
//                styledPlayerView.setVisibility(View.VISIBLE);
//                GlobalValue.displayExoplayerVideo(context,styledPlayerView,activeExoPlayerArrayList, Uri.parse(postProfileVideoDownloadUrl));
//
//            }else{
//                styledPlayerView.setVisibility(View.GONE);
//
//            }
////            postTitleTextView.setText(postSubjectName);
//            datePostedTextView.setText(GlobalValue.getFormattedDate(datePosted));
////            postDetailsTextView.setText(postDetails);
//
////            GlobalValue.configureHashTag(getContext(), postDetailsTextView, postDetails,R.color.holo_blue_dark);
////            GlobalValue.configureHashTag(getContext(),postTitleTextView,postSubjectName,R.color.holo_blue_dark);
//
////            totalNumberOfViewsTextView.setText(totalNumberOfViews);
////            totalNumberOfCommentsTextView.setText(totalNumberOfComments);
////            totalNumberOfLikesTextView.setText(totalNumberOfLikes);
////            totalNumberOfSharesTextView.setText(totalNumberOfShares);
//
//
//
////            GlobalValue.checkIfPostIsLiked(firebaseFirestore, userId, pageId, postId, userId, true,false, new GlobalValue.OnCheckLikeStatusSuccess() {
////                @Override
////                public void onSuccess(boolean isLiked) {
////                    if(isLiked){
////                        new Handler(Looper.getMainLooper()).post(new Runnable() {
////                            @Override
////                            public void run() {
////                                if(getContext() != null) {
////
////                                    totalNumberOfLikesImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_like_thumb_24, getContext().getTheme()));
////                                }
////
////
////                            }
////                        });
////
////
////
////                    }else{
////                        new Handler(Looper.getMainLooper()).post(new Runnable() {
////                            @Override
////                            public void run() {
////                                if(getContext() != null) {
////                                    totalNumberOfLikesImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_twotone_thumb_up_24, getContext().getTheme()));
////                                }
////                            }
////                        });
////
////
////                    }
////                }
////            });
//
////            firebaseFirestore.collection("ALL_PAGES").document(postOwnerUserId).collection("PAGES").document(pagePosterId).collection("POSTS").document(postId).collection("LIKES").document(userId).get().addOnFailureListener(new OnFailureListener() {
////                @Override
////                public void onFailure(@NonNull Exception e) {
////                    //userHasLiked=false;
////                }
////            }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
////                @Override
////                public void onSuccess(DocumentSnapshot documentSnapshot) {
////                    String likerUserId = String.valueOf(documentSnapshot.get("LIKER_USER_ID"));
////                    if (likerUserId!=null) {
////                        if (likerUserId.equalsIgnoreCase(userId)) {
////                            new Handler(Looper.getMainLooper()).post(new Runnable() {
////                                @Override
////                                public void run() {
////                                    totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_like_thumb_24,context.getTheme()));
////                                }
////                            });
////
////
////                        } else {
////                            // userHasLiked = false;
////                            likePagePost(getContext(),postOwnerUserId,pagePosterId, postId, totalNumberOfLikesImageView);
////
////                        }
////                    }else{
//////                            userHasLiked = false;
////                        likePagePost(getContext(),postOwnerUserId,pagePosterId, postId, totalNumberOfLikesImageView);
////
////                    }
////                }
////            });
//
//
////            totalNumberOfLikesImageView.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
//////                    if (checkIfUserHasAlreadyLikedTheMainUserPost(postOwnerUserId, postId)) {
//////                        likeMainUserPost(getContext(), postOwnerUserId, postId, totalNumberOfLikesImageView);
//////                    } else {
//////                        unlikeMainUserPost(getContext(), postOwnerUserId, postId, totalNumberOfLikesImageView);
//////                    }
////                    if (likeStatusIsChecked[0] && !likeImageIsClicked[0]) {
////                        if (isAlreadyLiked[0]) {
////                            unlikePost(getContext(), postId, totalNumberOfLikesImageView);
////
////                            new Handler(Looper.getMainLooper()).post(new Runnable() {
////                                @Override
////                                public void run() {
////                                    totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_twotone_thumb_up_24, getContext().getTheme()));
////                                }
////                            });
////
////                        } else {
////                            //user has not like then let him like the post
////                            likePost(getContext(), postId, totalNumberOfLikesImageView);
////
////                        }
////                    } else {
////                        likeDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
////                            @Override
////                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
////                                if (task.isSuccessful()) {
////                                    DocumentSnapshot documentSnapshot = task.getResult();
////                                    if (documentSnapshot.exists()) {
////                                        unlikePost(getContext(), postId, totalNumberOfLikesImageView);
////
////                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
////                                            @Override
////                                            public void run() {
////                                                totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_twotone_thumb_up_24, getContext().getTheme()));
////
////
////                                            }
////                                        });
////
////                                    } else {
////                                        likePost(getContext(), postId, totalNumberOfLikesImageView);
////
////                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
////                                            @Override
////                                            public void run() {
////                                                totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_like_thumb_24, getContext().getTheme()));
////                                            }
////                                        });
////
////                                    }
////
////
////                                }
////                            }
////
////                        });
////
////                    }
////                    likeImageIsClicked[0] = true;
////                }
////            });
////            totalNumberOfLikesImageView.setOnClickListener(new View.OnClickListener() {
////                                                               @Override
////                                                               public void onClick(View view) {
////
////                                                                   GlobalValue.checkIfDocumentExists(likeDocumentReference, new GlobalValue.OnDocumentExistStatusCallback() {
////                                                                       @Override
////                                                                       public void onExist() {
////                                                                           GlobalValue.unlikePagePost(getContext(), firebaseFirestore, userId, pageId, postId, userId, new GlobalValue.ActionCallback() {
////                                                                               @Override
////                                                                               public void onActionSuccess() {
////
////                                                                               }
////
////                                                                               @Override
////                                                                               public void onActionFailure(@NonNull Exception e) {
////
////                                                                               }
////                                                                           });
////                                                                           new Handler(Looper.getMainLooper()).post(new Runnable() {
////                                                                               @Override
////                                                                               public void run() {
////                                                                                   totalNumberOfLikesImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_twotone_thumb_up_24, getContext().getTheme()));
////                                                                               }
////                                                                           });
////                                                                       }
////
////                                                                       @Override
////                                                                       public void onNotExist() {
////                                                                           GlobalValue.likePagePost(getContext(), firebaseFirestore, userId, pageId, postId, userId, new GlobalValue.ActionCallback() {
////                                                                               @Override
////                                                                               public void onActionSuccess() {
////
////                                                                               }
////
////                                                                               @Override
////                                                                               public void onActionFailure(@NonNull Exception e) {
////
////                                                                               }
////                                                                           });
////                                                                           new Handler(Looper.getMainLooper()).post(new Runnable() {
////                                                                               @Override
////                                                                               public void run() {
////                                                                                   totalNumberOfLikesImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_like_thumb_24, getContext().getTheme()));
////                                                                               }
////                                                                           });
////                                                                       }
////
////                                                                       @Override
////                                                                       public void onFailed(@NonNull String errorMessage) {
////
////                                                                       }
////                                                                   });
////
////                                                                   //                if (checkIfUserHasAlreadyLikedThePagePost(postOwnerUserId,pagePosterId,postId)) {
//////                    likePagePost(getContext(),postOwnerUserId,pagePosterId, postId, totalNumberOfLikesImageView);
//////                } else {
//////                    unlikePagePost(getContext(),postOwnerUserId,pagePosterId, postId, totalNumberOfLikesImageView);
//////                }
////
//////                                                                   if (likeStatusIsChecked[0]) {
//////                                                                       if (isAlreadyLiked[0]) {
//////                                                                           unlikePost(getContext(), postId, totalNumberOfLikesImageView);
//////
//////                                                                           new Handler(Looper.getMainLooper()).post(new Runnable() {
//////                                                                               @Override
//////                                                                               public void run() {
//////                                                                                   totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_twotone_thumb_up_24, getContext().getTheme()));
//////                                                                               }
//////                                                                           });
//////
//////                                                                       } else {
//////                                                                           //user has not like then let him like the post
//////                                                                           likePost(getContext(), postId, totalNumberOfLikesImageView);
//////                                                                           new Handler(Looper.getMainLooper()).post(new Runnable() {
//////                                                                               @Override
//////                                                                               public void run() {
//////                                                                                   totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_like_thumb_24, getContext().getTheme()));
//////                                                                               }
//////                                                                           });
//////                                                                       }
//////                                                                   }
//////                                                                   else {
////////                    firebaseFirestore.collection("ALL_USERS").document(postOwnerUserId).collection("POSTS").document(postId).collection("LIKES").document(userId).get().addOnFailureListener(new OnFailureListener() {
////////                        @Override
////////                        public void onFailure(@NonNull Exception e) {
////////                            //userHasLiked=false;
////////                        }
////////                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
////////                        @Override
////////                        public void onSuccess(DocumentSnapshot documentSnapshot) {
////////                            String likerUserId = String.valueOf(documentSnapshot.get("LIKER_USER_ID"));
////////                            if (likerUserId != null) {
////////                                if (likerUserId.equalsIgnoreCase(userId)) {
//////////                                    userHasLiked = true;
////////                                    unlikeMainUserPost(getContext(), postOwnerUserId, postId, totalNumberOfLikesImageView);
////////                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
////////                                        @Override
////////                                        public void run() {
////////                                            totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_twotone_thumb_up_24,getContext().getTheme()));
////////                                        }
////////                                    });
////////                                } else {
//////////                                    userHasLiked = false;
////////                                    likeMainUserPost(getContext(), postOwnerUserId, postId, totalNumberOfLikesImageView);
////////                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
////////                                        @Override
////////                                        public void run() {
////////                                            totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_like_thumb_24,getContext().getTheme()));
////////                                        }
////////                                    });
////////                                }
////////                            } else {
//////////                                userHasLiked = false;
////////                                likeMainUserPost(getContext(), postOwnerUserId, postId, totalNumberOfLikesImageView);
////////                                new Handler(Looper.getMainLooper()).post(new Runnable() {
////////                                    @Override
////////                                    public void run() {
////////                                        totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_like_thumb_24,getContext().getTheme()));
////////                                    }
////////                                });
////////                            }
////////                        }
////////                    });
//////                                                                       if (likeDocumentReference.get().getResult().exists()) {
//////
//////                                                                           unlikePost(getContext(), postId, totalNumberOfLikesImageView);
//////
//////                                                                           new Handler(Looper.getMainLooper()).post(new Runnable() {
//////                                                                               @Override
//////                                                                               public void run() {
//////                                                                                   totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_like_thumb_24, getContext().getTheme()));
//////
//////
//////                                                                               }
//////                                                                           });
//////
//////                                                                       }
//////                                                                       else {
//////                                                                           likePost(getContext(), postId, totalNumberOfLikesImageView);
//////
//////
//////                                                                           new Handler(Looper.getMainLooper()).post(new Runnable() {
//////                                                                               @Override
//////                                                                               public void run() {
//////                                                                                   totalNumberOfLikesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_twotone_thumb_up_24, getContext().getTheme()));
//////                                                                               }
//////                                                                           });
//////
//////                                                                       }
//////
//////                                                                   }
////                                                               }
////                                                           });
//
////            totalNumberOfLikesImageView.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////
////                    GlobalValue.checkIfPostIsLiked(firebaseFirestore, userId, pageId, postId, userId, true,true, new GlobalValue.OnCheckLikeStatusSuccess() {
////                        @Override
////                        public void onSuccess(boolean isLiked) {
////                            if(isLiked){
////                                GlobalValue.unlikePagePost(getContext(), firebaseFirestore, userId, pageId, postId, userId, new GlobalValue.ActionCallback() {
////                                    @Override
////                                    public void onActionSuccess() {
////
////                                    }
////
////                                    @Override
////                                    public void onActionFailure(@NonNull Exception e) {
////
////                                    }
////                                });
////                                new Handler(Looper.getMainLooper()).post(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        totalNumberOfLikesImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_twotone_thumb_up_24, getContext().getTheme()));
////                                    }
////                                });
////
////
////                            }else{
////
////                                GlobalValue.likePagePost(getContext(), firebaseFirestore, userId, pageId, postId, userId, new GlobalValue.ActionCallback() {
////                                    @Override
////                                    public void onActionSuccess() {
////
////                                    }
////
////                                    @Override
////                                    public void onActionFailure(@NonNull Exception e) {
////
////                                    }
////                                });
////                                new Handler(Looper.getMainLooper()).post(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        totalNumberOfLikesImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_like_thumb_24, getContext().getTheme()));
////                                    }
////                                });
////
////
////                            }
////                        }
////                    });
////
////
////                }
////            });
////
////            totalNumberOfCommentsImageView.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    addCommentIncludeLayoutView.setVisibility(View.VISIBLE);
////                    addCommentEditText.requestFocus();
////                    GlobalValue.openKeyboard(getContext(),addCommentEditText);
////
//////                commentingPostId  = postId;
////                }
////            });
////            totalNumberOfSharesImageView.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////
//////                    GlobalValue.goToSharePostActivity(getContext(), ShareChatActivity.class, true, userId, pageId, postId, postSubjectName, postDetails, false, true);
////
////                    getContext().startActivity(GlobalValue.goToShareItemActivity( getContext(),  userId, pageId,  userId,  pageId ,  postId ,  true,  false,  false,  false,  true));
////
////                }
////            });
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(getContext(), SingleJobActivity.class);
//                    intent.putExtra("POST_ID", postId);
//                    intent.putExtra("PAGE_POSTER_ID", pageId);
//                    intent.putExtra("POST_OWNER_DISPLAY_NAME", pageName);
//
//                    startActivity(intent);
//                }
//            });
////
////            GlobalValue.getThreePostComment(getContext(), allThreeCommentsLinearLayout, firebaseFirestore, userId, pageId, postId, true,true);
////
////
////            DocumentReference documentEventReference =  firebaseFirestore.collection("ALL_PAGES").document(userId).collection("PAGES").document(pageId).collection("POSTS").document(postId);
//////            documentReferenceAttachedListenerArrayList.add(documentEventReference);
////            GlobalValue.detectAndUpdateEventValues(documentEventReference ,documentReferenceAttachedListenerRegistrationArrayList, totalNumberOfLikesTextView, totalNumberOfCommentsTextView, totalNumberOfViewsTextView, new GlobalValue.OnEventListener() {
////                @Override
////                public void onEventHappen(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
////
////                }
////            });
//
//
//
//            viewGroup.addView(itemView);
//        }
//
//    }


    interface JobFetchListener{
        void onFailed(String errorMessage);
        void onSuccess(JobDataModel jobDataModel);
    }
    interface OnNewCategorySelectedListener{
        void onSelected(String categoryName);
    }

}