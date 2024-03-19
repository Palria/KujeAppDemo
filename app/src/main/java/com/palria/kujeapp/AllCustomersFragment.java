package com.palria.kujeapp;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.palria.kujeapp.GlobalValue;
import com.palria.kujeapp.adapters.UsersRCVAdapter;
import com.palria.kujeapp.models.UsersDataModel;

import java.util.ArrayList;

public class AllCustomersFragment extends Fragment {
    UsersRCVAdapter usersRCVAdapter;
    ArrayList<UsersDataModel> usersDataModelArrayList = new ArrayList<>();
    RecyclerView recyclerView;


    boolean isFromSearchContext = false;
    String searchKeyword = "";
boolean isAdvertApproval =false;
    public AllCustomersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
//            isAuthorOpenType = getArguments().getBoolean(GlobalConfig.IS_AUTHOR_OPEN_TYPE_KEY,false);
            isAdvertApproval = getArguments().getBoolean(GlobalValue.IS_FOR_APPROVAL,false);
            isFromSearchContext = getArguments().getBoolean(GlobalValue.IS_FROM_SEARCH_CONTEXT,false);
            searchKeyword = getArguments().getString(GlobalValue.SEARCH_KEYWORD,"");

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_all_customers, container, false);
        initUI(parentView);
        fetchUsers(new UserFetchListener() {
            @Override
            public void onSuccess(UsersDataModel usersDataModel) {

                usersDataModelArrayList.add(usersDataModel);
                usersRCVAdapter.notifyItemChanged(usersDataModelArrayList.size());

            }

            @Override
            public void onFailed(String errorMessage) {

            }
        });

        return parentView;
    }

    private void initUI(View parentView){
        recyclerView = parentView.findViewById(R.id.usersRecyclerListViewId);
        usersRCVAdapter = new UsersRCVAdapter(usersDataModelArrayList,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setAdapter(usersRCVAdapter);
        recyclerView.setLayoutManager(layoutManager);


    }
    private void fetchUsers(UserFetchListener userFetchListener){
//        Query authorQuery = GlobalConfig.getFirebaseFirestoreInstance().collection(GlobalConfig.ALL_USERS_KEY).whereEqualTo(GlobalConfig.IS_USER_AUTHOR_KEY,true).whereArrayContains(GlobalConfig.AUTHOR_CATEGORY_TAG_ARRAY_KEY,categoryTag).orderBy(GlobalConfig.TOTAL_NUMBER_OF_USER_PROFILE_VISITORS_KEY);

        Query authorQuery = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS);

//            authorQuery = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS_KEY);
//                 if(isAdvertApproval){
//            if (isFromSearchContext) {
//                if(isFirstLoad) {
//                    query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PAGES).whereArrayContains(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchKeyword).whereEqualTo(GlobalValue.IS_ADVERT_REQUESTED, true).limit(20);
//                }else{
//                    query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PAGES).whereArrayContains(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchKeyword).whereEqualTo(GlobalValue.IS_ADVERT_REQUESTED, true).startAfter(lastRetrievedServiceSnapshot).limit(20);
//                }
//            }
//            else{
//                if(isFirstLoad) {
//                    query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PAGES).whereEqualTo(GlobalValue.CATEGORY,category).whereEqualTo(GlobalValue.IS_ADVERT_REQUESTED, true).limit(40L);
//                }else {
//                    query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PAGES).whereEqualTo(GlobalValue.CATEGORY,category).whereEqualTo(GlobalValue.IS_ADVERT_REQUESTED, true).limit(40L).startAfter(lastRetrievedServiceSnapshot);
//                }
//
//            }
//
//        }

        if(isFromSearchContext){
                authorQuery = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).whereArrayContains(GlobalValue.USER_SEARCH_ANY_MATCH_KEYWORD,searchKeyword);
        }


        authorQuery.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        userFetchListener.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                            String authorId  = documentSnapshot.getId();
                            final String userName = ""+ documentSnapshot.get(GlobalValue.USER_DISPLAY_NAME);
                            final String gender = ""+ documentSnapshot.get(GlobalValue.USER_GENDER_TYPE);
                            final String userPhoneNumber = ""+ documentSnapshot.get(GlobalValue.USER_CONTACT_PHONE_NUMBER);
                            final String userEmail = ""+ documentSnapshot.get(GlobalValue.USER_EMAIL_ADDRESS);
                            final String userCountryOfOrigin = ""+ documentSnapshot.get(GlobalValue.USER_COUNTRY_OF_RESIDENCE);
                            String dateRegistered =  documentSnapshot.get(GlobalValue.USER_PROFILE_DATE_CREATED_TIME_STAMP)!=null ?  documentSnapshot.getTimestamp(GlobalValue.USER_PROFILE_DATE_CREATED_TIME_STAMP).toDate()+""  :"Undefined";
                            if(dateRegistered.length()>10){
                                dateRegistered = dateRegistered.substring(0,10);
                            }
                            final String userProfilePhotoDownloadUrl = ""+ documentSnapshot.get(GlobalValue.USER_COVER_PHOTO_DOWNLOAD_URL);
//                            long totalNumberOfProfileVisitor = (documentSnapshot.getLong(GlobalConfig.TOTAL_NUMBER_OF_USER_PROFILE_VISITORS_KEY) != null) ?  documentSnapshot.getLong(GlobalConfig.TOTAL_NUMBER_OF_USER_PROFILE_VISITORS_KEY) : 0L;
//                            long totalNumberOfProfileReach = (documentSnapshot.getLong(GlobalConfig.TOTAL_NUMBER_OF_USER_PROFILE_REACH_KEY) != null) ?  documentSnapshot.getLong(GlobalConfig.TOTAL_NUMBER_OF_USER_PROFILE_REACH_KEY) : 0L;


                            userFetchListener.onSuccess(new UsersDataModel(
                                    userName,
                                    authorId,
                                    userProfilePhotoDownloadUrl,
                                    gender,
                                    dateRegistered,
                                    userPhoneNumber,
                                    userEmail,
                                    userCountryOfOrigin
                            ));

                        }
                    }
                });
    }

    interface UserFetchListener{
        void onSuccess(UsersDataModel usersDataModel);
        void onFailed(String errorMessage);
    }

}