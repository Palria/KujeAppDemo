package com.palria.kujeapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class UserProfileFragment extends Fragment {

    AlertDialog alertDialog;
    AlertDialog createLibraryDialog;
    LinearLayout containerLinearLayout;

    //views
    ImageView startChatActionButton;
    ImageView editProfileButton;
    RoundedImageView profileImageView;
    TextView currentDisplayNameView;
    TextView currentEmailView;
    TextView currentCountryOfResidence;
    TextView joined_dateTextView;

    TextView descriptionTextView;
    TextView birthdateTextView;
    TextView currentPhoneNumberView;
    TextView currentWebsiteLinkView;

    MaterialButton productsActionButton;

    LinearLayout numOfLibraryTutorialRatingsLinearLayout;

    //parent swiper layout
    SwipeRefreshLayout swipeRefreshLayout;
    NestedScrollView parentScrollView;
    BottomAppBar bottomAppBar;

    TextView   failureIndicatorTextView;
    ImageButton profileMoreIconButton;
    Button statsButton;
    String userId = "";
    TextView logButton;
    ImageView bookmarkedIcon, ratedIcon;
    //learn era bottom sheet dialog
    TabLayout tabLayout;
    boolean isFirstLoad = true;
    boolean isMyOrdersFragmentOpened = false;
    boolean isMyJobsFragmentOpened = false;
    boolean isJobsAppliedFragmentOpened = false;
    boolean isMyRequestsFragmentOpened = false;
    boolean isMyProductsFragmentOpened = false;
    FrameLayout myOrdersFrameLayout;
    FrameLayout myJobsFrameLayout;
    FrameLayout jobsAppliedFrameLayout;
//    FrameLayout myRequestsFrameLayout;
    FrameLayout myProductsFrameLayout;
    //shimmer layout loading preloading effect container.
    ShimmerFrameLayout shimmerLayout;

    public UserProfileFragment() {
        // Required empty public constructor
    }
    public UserProfileFragment(BottomAppBar b) {
        bottomAppBar = b;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            userId = "" + getArguments().getString(GlobalValue.USER_ID);
        }

//        getProfile(new OnUserProfileFetchListener() {
//            @Override
//            public void onSuccess(String userDisplayName, String userCountryOfResidence, String contactEmail, String contactPhoneNumber, String genderType, String userProfilePhotoDownloadUrl, boolean isUserBlocked, boolean isUserProfilePhotoIncluded, boolean isUserAnAuthor) {
//
//            }
//
//            @Override
//            public void onFailed(String errorMessage) {
//
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_user_profile, container, false);
            initUI(parentView);
            loadCurrentUserProfile();


            editProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(getContext(), EditUserProfileActivity.class);
                    startActivity(i);

                }
            });
        productsActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(GlobalValue.getHostActivityIntent(getContext(),null,GlobalValue.USER_PRODUCT_FRAGMENT_TYPE,userId));

                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadCurrentUserProfile();
                    swipeRefreshLayout.setRefreshing(true);
                }
            });

            startChatActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(GlobalValue.goToChatRoom(getContext(), userId, currentDisplayNameView.getText()+"",profileImageView));

            }
        });

            parentScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                float y = 0;

                @Override
                public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (bottomAppBar != null) {
                        if (oldScrollY > scrollY) {
                            bottomAppBar.performShow();

                        } else {
                            bottomAppBar.performHide();

                        }
                    }

                    //
//             if(scrollY > 30){
//                 if(bottomAppBar!=null) {
//                     bottomAppBar.performHide();
//                 }
//             }else{
//                 if(bottomAppBar!=null) {
//                     bottomAppBar.performShow();
//                 }
//             }

                }
            });
            initTabLayout();
//            profileMoreIconButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//
//                    leBottomSheetDialog.show();
//
//                }
//            });

//
//            statsButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Intent intent = new Intent(getContext(), UserStatsActivity.class);
//                    intent.putExtra(GlobalConfig.USER_ID_KEY, authorId);
//                    startActivity(intent);
//
//                }
//            });
//
//            logButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Intent intent = new Intent(getContext(), UserActivityLogActivity.class);
//                    startActivity(intent);
//
//                }
//            });


        return parentView;
    }


    private void loadCurrentUserProfile(){
        getProfile(new OnUserProfileFetchListener() {
            @Override
            public void onSuccess(String userDisplayName, String userCountryOfResidence, String contactEmail, String contactPhoneNumber, String genderType, String userProfilePhotoDownloadUrl, String joined_date, boolean isUserBlocked, boolean isUserProfilePhotoIncluded,String description,String birthdate,String webLink) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    Glide.with(getContext())
                            .load(userProfilePhotoDownloadUrl)
                            .centerCrop()
                            .into(profileImageView);
                    profileImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GlobalValue.viewImagePreview(getContext(),profileImageView, userProfilePhotoDownloadUrl);
                        }
                    });
                }catch(Exception e){}

                currentEmailView.setText(Html.fromHtml("Contact Email <b>"+contactEmail+"</b> "));
                currentDisplayNameView.setText(userDisplayName);
                currentCountryOfResidence.setText(Html.fromHtml("From <b>"+userCountryOfResidence+"</b> "));
                joined_dateTextView.setText(Html.fromHtml("Joined <b>"+joined_date+"</b> "));


                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);

                parentScrollView.setVisibility(View.VISIBLE);
//                Toast.makeText(getContext(), "Libraries loaded.", Toast.LENGTH_SHORT).show();

//                if(!isUserAuthor && authorId.equals(GlobalConfig.getCurrentUserId()))

                if(userId.equals(GlobalValue.getCurrentUserId())){
                    //he is the owner of this profile

                }else{
                    //he is not the owner of this profile
                    editProfileButton.setVisibility(View.GONE);
//                    logButton.setVisibility(View.INVISIBLE);
//                    GlobalConfig.incrementNumberOfVisitors(authorId,null,null,null,null,true,false,false,false,false,false);
                }
            }

            @Override
            public void onFailed(String errorMessage) {
//                toggleProgress(false);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Failed to retrieve profile data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI(View parentView){
        //use the parentView to find the by Id as in : parentView.findViewById(...);
        parentScrollView = parentView.findViewById(R.id.scrollView);
        failureIndicatorTextView = parentView.findViewById(R.id.failureIndicatorTextViewId);
        //init views
        startChatActionButton = parentView.findViewById(R.id.startChatActionButtonId);
        editProfileButton = parentView.findViewById(R.id.editProfileIcon);
        profileImageView = parentView.findViewById(R.id.imageView1);
        currentDisplayNameView = parentView.findViewById(R.id.current_name);
        currentEmailView = parentView.findViewById(R.id.current_email);
        currentCountryOfResidence = parentView.findViewById(R.id.current_country);
        joined_dateTextView = parentView.findViewById(R.id.joined_date);
        descriptionTextView = parentView.findViewById(R.id.descriptionTextViewId);
        birthdateTextView = parentView.findViewById(R.id.birthdateTextViewId);
        currentWebsiteLinkView = parentView.findViewById(R.id.current_website_link);
        currentPhoneNumberView = parentView.findViewById(R.id.currentPhoneNumberViewId);


        tabLayout = parentView.findViewById(R.id.tabLayoutId);
        myOrdersFrameLayout = parentView.findViewById(R.id.myOrdersFrameLayoutId);
        myJobsFrameLayout = parentView.findViewById(R.id.myJobsFrameLayoutId);
        jobsAppliedFrameLayout = parentView.findViewById(R.id.jobsAppliedFrameLayoutId);
//        myRequestsFrameLayout = parentView.findViewById(R.id.myRequestsFrameLayoutId);
        myProductsFrameLayout = parentView.findViewById(R.id.myProFrameLayoutId);
        productsActionButton = parentView.findViewById(R.id.productsActionButtonId);


//        numOfLibraryTutorialRatingsLinearLayout = parentView.findViewById(R.id.numOfLibraryTutorialRatingsLinearLayoutId);

//
//        numOfLibraryTextView = parentView.findViewById(R.id.numOfLibraryCreatedTextView);
//        numOfTutorialsTextView = parentView.findViewById(R.id.numOfTutorialCreatedTextView);
//        numOfRatingsTextView = parentView.findViewById(R.id.numOfRatingsCreatedTextView);

        swipeRefreshLayout = parentView.findViewById(R.id.swiperRefreshLayout);
//        profileMoreIconButton = parentView.findViewById(R.id.profileMoreIcon);
        shimmerLayout = parentView.findViewById(R.id.shimmerLayout);


        alertDialog = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout, null))
                .create();



    }


    private  void getProfile(OnUserProfileFetchListener onUserProfileFetchListener){
        GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(userId)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onUserProfileFetchListener.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {


                        String userDisplayName =""+ documentSnapshot.get(GlobalValue.USER_DISPLAY_NAME);
                        String description =""+ documentSnapshot.get(GlobalValue.USER_DESCRIPTION);
                        String birthdate =""+ documentSnapshot.get(GlobalValue.USER_BIRTH_DATE);
                        String userCountryOfResidence =""+ documentSnapshot.get(GlobalValue.USER_COUNTRY_OF_RESIDENCE);
                        String contactEmail =""+ documentSnapshot.get(GlobalValue.USER_CONTACT_EMAIL_ADDRESS);
                        String webLink = documentSnapshot.get(GlobalValue.USER_PERSONAL_WEBSITE_LINK)!=null? ""+documentSnapshot.get(GlobalValue.USER_PERSONAL_WEBSITE_LINK):"No link";
                        String contactPhoneNumber =""+ documentSnapshot.get(GlobalValue.USER_CONTACT_PHONE_NUMBER);
                        String genderType =""+ documentSnapshot.get(GlobalValue.USER_GENDER_TYPE);
                        String userProfilePhotoDownloadUrl =""+ documentSnapshot.get(GlobalValue.USER_COVER_PHOTO_DOWNLOAD_URL);
                        String joined_date = documentSnapshot.get(GlobalValue.USER_PROFILE_DATE_CREATED_TIME_STAMP)!=null ? documentSnapshot.getTimestamp(GlobalValue.USER_PROFILE_DATE_CREATED_TIME_STAMP).toDate()+"" :"Undefined";
                        if(joined_date.length()>10){
                            joined_date = joined_date.substring(0,10);
                        }

                        boolean isUserBlocked = false;
                        boolean isUserProfilePhotoIncluded = false;
                        if(documentSnapshot.get(GlobalValue.IS_USER_BLOCKED) != null){
                            isUserBlocked =documentSnapshot.getBoolean(GlobalValue.IS_USER_BLOCKED);

                        }
                        if(documentSnapshot.get(GlobalValue.IS_USER_PROFILE_PHOTO_INCLUDED) != null){
                            isUserProfilePhotoIncluded = documentSnapshot.getBoolean(GlobalValue.IS_USER_PROFILE_PHOTO_INCLUDED);

                        }

                        onUserProfileFetchListener.onSuccess( userDisplayName, userCountryOfResidence, contactEmail, contactPhoneNumber, genderType, userProfilePhotoDownloadUrl,joined_date, isUserBlocked, isUserProfilePhotoIncluded,description,birthdate,webLink);
                    }
                });
    }

    private void toggleProgress(boolean show)
    {
        if(show){
            alertDialog.show();
        }else{
            alertDialog.cancel();
        }
    }

    private void  initTabLayout(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(false){
                    if(isMyProductsFragmentOpened){
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(myProductsFrameLayout);
//                        Toast.makeText(getContext(), "already open", Toast.LENGTH_SHORT).show();

                    }else {
                        isMyProductsFragmentOpened =true;
                        setFrameLayoutVisibility(myProductsFrameLayout);
                        MarketFragment allProductsFragment = new MarketFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString(GlobalValue.USER_ID,userId);
                        bundle.putString(GlobalValue.PRODUCT_OWNER_USER_ID,userId);
                        bundle.putBoolean(GlobalValue.IS_FROM_SINGLE_OWNER,true);
                        bundle.putBoolean(GlobalValue.IS_FROM_SEARCH_CONTEXT,false);
                        bundle.putString(GlobalValue.CUSTOMER_ID,userId);
                        allProductsFragment.setArguments(bundle);

                        initFragment(allProductsFragment, myProductsFrameLayout);
//                        Toast.makeText(getContext(), "final...all", Toast.LENGTH_SHORT).show();

                    }

                }

               else if(tab.getPosition()==0){
                    if(isMyOrdersFragmentOpened){
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(myOrdersFrameLayout);
                    }
                    else {
                        isMyOrdersFragmentOpened =true;
                        setFrameLayoutVisibility(myOrdersFrameLayout);
                        AllOrdersFragment allOrdersFragment = new AllOrdersFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString(GlobalValue.USER_ID,userId);
                        bundle.putBoolean(GlobalValue.IS_SINGLE_PRODUCT,false);
                        bundle.putBoolean(GlobalValue.IS_SINGLE_CUSTOMER,true);
                        bundle.putString(GlobalValue.PRODUCT_ID,"");
                        bundle.putString(GlobalValue.CUSTOMER_ID,userId);
                        allOrdersFragment.setArguments(bundle);

                        initFragment(allOrdersFragment, myOrdersFrameLayout);
                    }

                }
               else if(tab.getPosition()==1){
                    if(isJobsAppliedFragmentOpened){
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(jobsAppliedFrameLayout);
                    }
                    else {
                        isJobsAppliedFragmentOpened =true;
                        setFrameLayoutVisibility(jobsAppliedFrameLayout);
                        AllJobsFragment allJobsFragment = new AllJobsFragment();

                        Bundle bundle = new Bundle();
                        bundle.putBoolean(GlobalValue.IS_FROM_SINGLE_OWNER,true);
                        bundle.putString(GlobalValue.JOB_OWNER_USER_ID,userId);
                        allJobsFragment.setArguments(bundle);

                        initFragment(allJobsFragment, myJobsFrameLayout);
                    }

                }
               else if(tab.getPosition()==1){
                    if(isMyJobsFragmentOpened){
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(myJobsFrameLayout);
                    }
                    else {
                        isMyJobsFragmentOpened =true;
                        setFrameLayoutVisibility(myJobsFrameLayout);
                        AllJobsFragment allJobsFragment = new AllJobsFragment();

                        Bundle bundle = new Bundle();
                        bundle.putBoolean(GlobalValue.IS_FROM_SINGLE_OWNER,true);
                        bundle.putString(GlobalValue.JOB_OWNER_USER_ID,userId);
                        allJobsFragment.setArguments(bundle);

                        initFragment(allJobsFragment, myJobsFrameLayout);
                    }

                }
             /*   else if(tab.getPosition()==2){
                    if(isMyRequestsFragmentOpened){
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(myRequestsFrameLayout);
                    }else {
                        isMyRequestsFragmentOpened =true;
                        setFrameLayoutVisibility(myRequestsFrameLayout);
                        ServiceRequestsFragment serviceRequestsFragment = new ServiceRequestsFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString(GlobalValue.USER_ID,userId);
                        bundle.putBoolean(GlobalValue.IS_SINGLE_SERVICE,false);
                        bundle.putBoolean(GlobalValue.IS_SINGLE_CUSTOMER,true);
                        bundle.putString(GlobalValue.SERVICE_ID,"");
                        bundle.putString(GlobalValue.CUSTOMER_ID,userId);
                        serviceRequestsFragment.setArguments(bundle);

                        initFragment(serviceRequestsFragment, myRequestsFrameLayout);
                    }

                }*/


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

//        TabLayout.Tab productsItem = tabLayout.newTab();
//        productsItem.setText("My Products");
//        tabLayout.addTab(productsItem,0,true);

        TabLayout.Tab ordersItem = tabLayout.newTab();
        ordersItem.setText("My Orders");
        tabLayout.addTab(ordersItem,0,true);

        TabLayout.Tab jobsItem = tabLayout.newTab();
        jobsItem.setText("Jobs Applied");
        tabLayout.addTab(jobsItem,1);

        TabLayout.Tab myJobsItem = tabLayout.newTab();
        myJobsItem.setText("My Jobs");
        tabLayout.addTab(myJobsItem,2);
//
//        TabLayout.Tab messagesItem = tabLayout.newTab();
//        ordersItem.setText("Messages");
//        tabLayout.addTab(ordersItem,1);

    }



    private void initFragment(Fragment fragment, ViewGroup frameLayout){
        getChildFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(), fragment)
                .commit();


    }

    private void setFrameLayoutVisibility(ViewGroup frameLayoutToSetVisible){
        myOrdersFrameLayout.setVisibility(View.GONE);
//        myRequestsFrameLayout.setVisibility(View.GONE);
        myProductsFrameLayout.setVisibility(View.GONE);
        frameLayoutToSetVisible.setVisibility(View.VISIBLE);
    }

    interface OnUserProfileFetchListener{
        void onSuccess(String userDisplayName,String userCountryOfResidence,String contactEmail,String contactPhoneNumber,String genderType,String userProfilePhotoDownloadUrl,String joined_date,boolean isUserBlocked,boolean isUserProfilePhotoIncluded,String description,String birthdate,String webLink);
        void onFailed(String errorMessage);
    }
}

