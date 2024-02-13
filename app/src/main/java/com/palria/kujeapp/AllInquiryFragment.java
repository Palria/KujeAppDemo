package com.palria.kujeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.palria.kujeapp.adapters.QuestionRcvAdapter;
import com.palria.kujeapp.models.QuestionDataModel;

import java.util.ArrayList;


public class AllInquiryFragment extends Fragment {
QuestionRcvAdapter questionRCVAdapter;
ArrayList<QuestionDataModel> questionDataModels = new ArrayList<>();
RecyclerView recyclerView;
ShimmerFrameLayout questionShimmerLayout;

    boolean isFromSearchContext = false;
    String searchKeyword = "";

    public AllInquiryFragment() {
        // Required empty public constructor
    }
    public AllInquiryFragment(BottomAppBar bottomAppBar) {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            isFromSearchContext = getArguments().getBoolean(GlobalValue.IS_FROM_SEARCH_CONTEXT,false);
            searchKeyword = getArguments().getString(GlobalValue.SEARCH_KEYWORD,"");

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_all_inquiry, container, false);
        initUI(parentView);
        fetchQuestions(new QuestionFetchListener() {
            @Override
            public void onSuccess(QuestionDataModel questionDataModel) {

                questionDataModels.add(questionDataModel);
                questionRCVAdapter.notifyItemChanged(questionDataModels.size());
                questionShimmerLayout.setVisibility(View.GONE);
                questionShimmerLayout.stopShimmer();
            }

            @Override
            public void onFailed(String errorMessage) {

            }
        });

        return parentView;
    }
//
private void initUI(View parentView){
        recyclerView = parentView.findViewById(R.id.questionsRecyclerViewId);
    questionShimmerLayout = parentView.findViewById(R.id.questionShimmerLayoutId);
        questionRCVAdapter = new QuestionRcvAdapter(questionDataModels,getContext());
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
    recyclerView.setAdapter(questionRCVAdapter);
    recyclerView.setLayoutManager(layoutManager);


}
    private void fetchQuestions(QuestionFetchListener questionFetchListener){
//        Query authorQuery = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).whereEqualTo(GlobalValue.IS_USER_AUTHOR_KEY,true).whereArrayContains(GlobalValue.AUTHOR_CATEGORY_TAG_ARRAY_KEY,categoryTag).orderBy(GlobalValue.TOTAL_NUMBER_OF_USER_PROFILE_VISITORS);

        Query authorQuery = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_QUESTIONS);
        if (isFromSearchContext){
                authorQuery = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_QUESTIONS).whereArrayContains(GlobalValue.QUESTION_SEARCH_ANY_MATCH_KEYWORD,searchKeyword);
        }


            authorQuery.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        questionFetchListener.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){


                            String questionId  = documentSnapshot.getId();
                            final String questionBody = ""+ documentSnapshot.get(GlobalValue.QUESTION_BODY);
                            final String photoDownloadUrl = ""+ documentSnapshot.get(GlobalValue.QUESTION_PHOTO_DOWNLOAD_URL);
                            final String authorId = ""+ documentSnapshot.get(GlobalValue.AUTHOR_ID);
                             String dateAsked =  documentSnapshot.get(GlobalValue.DATE_CREATED_TIME_STAMP)!=null ?  documentSnapshot.getTimestamp(GlobalValue.DATE_CREATED_TIME_STAMP).toDate()+""  :"Moments ago";
                            if(dateAsked.length()>10){
                                dateAsked = dateAsked.substring(0,10);
                            }
                            final String finalDateAsked = dateAsked;
                            final boolean isPublic =  documentSnapshot.get(GlobalValue.IS_PUBLIC)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_PUBLIC) :true;
                            final boolean isClosed =  documentSnapshot.get(GlobalValue.IS_CLOSED)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_CLOSED) :false;
                            final boolean isPhotoIncluded =  documentSnapshot.get(GlobalValue.IS_PHOTO_INCLUDED)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_PHOTO_INCLUDED) :false;
                            long numOfAnswers = (documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_ANSWER) != null) ?  documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_ANSWER) : 0L;
                            long numOfViews = (documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_VIEWS) != null) ?  documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_VIEWS) : 0L;


                            questionFetchListener.onSuccess(new QuestionDataModel(
                                    questionId,
                                    questionBody,
                                    photoDownloadUrl,
                                    dateAsked,
                                    authorId,
                                    numOfAnswers,
                                    numOfViews,
                                    isPublic,
                                    isClosed,
                                    isPhotoIncluded


    ));

                        }
                    }
                });
    }

    interface QuestionFetchListener{
        void onSuccess(QuestionDataModel questionDataModel);
        void onFailed(String errorMessage);
    }

}