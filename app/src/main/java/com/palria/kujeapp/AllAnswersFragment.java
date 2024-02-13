package com.palria.kujeapp;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.palria.kujeapp.adapters.AnswerRcvAdapter;
import com.palria.kujeapp.models.AnswerDataModel;
import com.palria.kujeapp.widgets.BottomSheetFormBuilderWidget;

import java.util.ArrayList;

public class AllAnswersFragment extends Fragment {

    LinearLayout featuredItemLayoutContainer;
    LinearLayout noDataFound;
    LinearLayout loadingLayout;
    View dummyView;
    public AllAnswersFragment() {
        // Required empty public constructor
    }
    String questionId;
    String answerId;
        boolean isForPreview = false;
        boolean isFromQuestionContext = true;
        boolean isViewAllAnswersForSingleQuestion = false;
        boolean isViewSingleAnswerReplies = false;
        String authorId = "";
    ArrayList<AnswerDataModel> answerDataModels=new ArrayList<>();
    RecyclerView answerRecyclerListView;
    AnswerRcvAdapter adapter;
    Button answerButton;
    AlertDialog alertDialog;
    public static AddAnswerListener addAnswerListener;
    BottomSheetFormBuilderWidget bottomSheetCatalogFormBuilderWidget;
    boolean isPhotoIncluded = false;
    ImageView answerPhoto;
    Uri galleryImageUri;
    /**
     * A  variable for launching the gallery {@link Intent}
     * */
    ActivityResultLauncher<Intent> openGalleryLauncher;

    /**
     * A  variable for launching the camera {@link Intent}
     * */
    ActivityResultLauncher<Intent> openCameraLauncher;
    int CAMERA_PERMISSION_REQUEST_CODE = 2002;
    int GALLERY_PERMISSION_REQUEST_CODE = 23;

    GlobalValue.AnswerCallback answerCallback;
    AnswerDataModel answerDataModelForEdition;
    boolean isEdition=false;
    boolean isAnswer=true;
    String parentId = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
if(getArguments()!= null){
    isForPreview = getArguments().getBoolean(GlobalValue.IS_FOR_PREVIEW,false);
    isFromQuestionContext = getArguments().getBoolean(GlobalValue.IS_FROM_QUESTION_CONTEXT,false);
    isViewAllAnswersForSingleQuestion = getArguments().getBoolean(GlobalValue.IS_VIEW_ALL_ANSWERS_FOR_SINGLE_QUESTION,false);
    isViewSingleAnswerReplies = getArguments().getBoolean(GlobalValue.IS_VIEW_SINGLE_ANSWER_REPLY,false);
    authorId = getArguments().getString(GlobalValue.AUTHOR_ID);
    answerId = getArguments().getString(GlobalValue.ANSWER_ID);
    questionId = getArguments().getString(GlobalValue.QUESTION_ID);
}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View parentView = inflater.inflate(R.layout.fragment_all_answers, container, false);
        initUi(parentView);
        fetchAnswers(new FetchAnswerListener() {
            @Override
            public void onSuccess(AnswerDataModel answerDataModel) {
                answerDataModels.add(answerDataModel);
                adapter.notifyItemChanged(answerDataModels.size());
            }

            @Override
            public void onFailed(String errorMessage) {

            }
        });

        if(isFromQuestionContext || isForPreview){
            noDataFound.setVisibility(View.GONE);
//            answerButton.setVisibility(View.GONE);
            dummyView.setVisibility(View.GONE);
//            Toast.makeText(getContext(), isForPreview+" checking " +parentDiscussionId, Toast.LENGTH_SHORT).show();
        }


        openGalleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null) {
                    Intent data=result.getData();
                    galleryImageUri = data.getData();
//                        Picasso.get().load(galleryImageUri)
//                                .centerCrop()
//                                .into(pickImageActionButton);
                    Glide.with(getContext())
                            .load(galleryImageUri)
                            .centerCrop()
                            .into(answerPhoto);

                    isPhotoIncluded = true;


                }
            }
        });
        openCameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK) {

                    if (result.getData() != null) {
                        Intent data = result.getData();
                        Bitmap bitmapFromCamera =(Bitmap) data.getExtras().get("data");

                        if(bitmapFromCamera != null) {
                            Bitmap   cameraImageBitmap = bitmapFromCamera;
                            //coverPhotoImageView.setImageBitmap(cameraImageBitmap);
                            Glide.with(getContext())
                                    .load(cameraImageBitmap)
                                    .centerCrop()
                                    .into(answerPhoto);
                            isPhotoIncluded = true;
                        }

                    }
                }else{
                    Toast.makeText(getContext(), "No image captured!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(isViewSingleAnswerReplies) {
            openAnswerFragment(answerId);

        }
//        if(isViewAllDiscussionsForSinglePage){
//            openPageFragment();
//
//        }
//            if(isViewSingleAnswerReplies){
        /**listens to when user wants to add an answer*/
        addAnswerListener = new AddAnswerListener() {
            @Override
            public void onAddAnswerTriggered(AnswerDataModel answerDataModel1, String parentId1,boolean isEdition1,boolean isAnswer1) {
                answerDataModelForEdition = answerDataModel1;
                isEdition = isEdition1;
                isAnswer = isAnswer1;
                parentId = parentId1;


//                if(isFromQuestionContext) {
//                if(!isForPreview) {
                showAnswerForm();

//                }
//                }
            }
        };


//        }

        /**listens to  answer start point,media selection,completion,failure, and so on*/
        answerCallback = new GlobalValue.AnswerCallback() {
            @Override
            public void onImageGallerySelected(ImageView imageView) {
                answerPhoto=imageView;
                openGallery();
            }

            @Override
            public void onCameraSelected(ImageView imageView) {
                answerPhoto=imageView;
                openCamera();
            }

            @Override
            public void onVideoGallery(View view) {

            }

            @Override
            public void onStart(String answerId) {
                toggleProgress(true);
            }

            @Override
            public void onSuccess(String answerId) {
                toggleProgress(false);
                answerId = GlobalValue.getRandomString(60);
                bottomSheetCatalogFormBuilderWidget = GlobalValue.getAnswerForm(getContext(), authorId, questionId,parentId, answerId, isEdition,isAnswer, answerDataModelForEdition, answerCallback);
                GlobalValue.createSnackBar(getContext(), featuredItemLayoutContainer, "Thanks for your contribution,your answer was successfully posted, refresh and see your answer added", Snackbar.LENGTH_INDEFINITE);

            }

            @Override
            public void onFailed(String errorMessage) {
                toggleProgress(false);
                GlobalValue.createSnackBar2(getContext(), featuredItemLayoutContainer, "Your answer failed to add, please try again", "Try again", Snackbar.LENGTH_INDEFINITE, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAnswerForm();
                    }
                });
            }
        };
//        bottomSheetCatalogFormBuilderWidget = GlobalValue.getAnswerForm(getContext(), authorId, questionId,parentId, newAnswerId, isEdition, isAnswer,answerDataModelForEdition, answerCallback);

        return parentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        /**listens to when user wants to add an answer*/
        addAnswerListener = new AddAnswerListener() {
            @Override
            public void onAddAnswerTriggered(AnswerDataModel answerDataModel1, String parentId1,boolean isEdition1,boolean isAnswer1) {
                answerDataModelForEdition = answerDataModel1;
                isEdition = isEdition1;
                isAnswer = isAnswer1;
                parentId = parentId1;


                showAnswerForm();

            }
        };
    }

    private void toggleProgress(boolean show)
    {
        if(show){
            alertDialog.show();
        }else{
            alertDialog.hide();
        }
    }

    private void toggleContentVisibility(boolean show){
        if(!show){
            loadingLayout.setVisibility(View.VISIBLE);
            answerRecyclerListView.setVisibility(View.GONE);
        }else{
            loadingLayout.setVisibility(View.GONE);
            answerRecyclerListView.setVisibility(View.VISIBLE);
        }
    }

    private void initUi(View parentView) {

        answerRecyclerListView=parentView.findViewById(R.id.answerRecyclerListViewId);

        featuredItemLayoutContainer=parentView.findViewById(R.id.featuredItemLayoutContainerId);
        loadingLayout=parentView.findViewById(R.id.loadingLayout);
        noDataFound=parentView.findViewById(R.id.noDataFound);
        dummyView=parentView.findViewById(R.id.dummyViewId);
        toggleContentVisibility(false);
//
//        pageDataModels.add(new PageDataModel("How to hold","this is content",
//                "",
//                "author",
//                "pageId",
//                "tutorialId",
//                "folderId",
//                "1 hrs ago",true));
//        pageDataModels.add(new PageDataModel("How to hold","this is content",
//                "",
//                "author",
//                "pageId",
//                "tutorialId",
//                "folderId",
//                "1 hrs ago",true));
//        pageDataModels.add(new PageDataModel("How to hold","this is content",
//                "",
//                "author",
//                "pageId",
//                "tutorialId",
//                "folderId",
//                "1 hrs ago",true));
//        pageDataModels.add(new PageDataModel("How to hold","this is content",
//                "",
//                "author",
//                "pageId",
//                "tutorialId",
//                "folderId",
//                "1 hrs ago",true));


        adapter = new AnswerRcvAdapter(answerDataModels, getContext(),authorId);

//        pagesRecyclerListView.setHasFixedSize(true);
        answerRecyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        answerRecyclerListView.setAdapter(adapter);


        alertDialog = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();

    }
    void showAnswerForm(){
        String newAnswerId = GlobalValue.getRandomString(60);
        bottomSheetCatalogFormBuilderWidget = GlobalValue.getAnswerForm(getContext(), authorId, questionId,parentId, newAnswerId, isEdition, isAnswer,answerDataModelForEdition, answerCallback);

        bottomSheetCatalogFormBuilderWidget.show();
    }


    void openAnswerFragment(String answerId){
        AllAnswersFragment allAnswersFragment = new AllAnswersFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(GlobalValue.IS_FOR_PREVIEW,true);
        bundle.putString(GlobalValue.AUTHOR_ID,authorId);
        bundle.putString(GlobalValue.ANSWER_ID,answerId);
        bundle.putString(GlobalValue.QUESTION_ID,questionId);
        allAnswersFragment.setArguments(bundle);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.featuredItemLayoutContainerId, allAnswersFragment)
                .commit();
//        Toast.makeText(getContext(), isForPreview+" now opening fragment "+parentDiscussionId, Toast.LENGTH_SHORT).show();

    }


    private void fetchAnswers(FetchAnswerListener answerListener){
//        if(true)return ;

        Query pageQuery = GlobalValue.getFirebaseFirestoreInstance()
                        .collection(GlobalValue.ALL_QUESTIONS)
                        .document(questionId)
                        .collection(GlobalValue.ALL_ANSWERS)
                        .orderBy(GlobalValue.DATE_CREATED_TIME_STAMP,Query.Direction.DESCENDING);
        if(isFromQuestionContext){
            pageQuery =   GlobalValue.getFirebaseFirestoreInstance()
                    .collection(GlobalValue.ALL_QUESTIONS)
                    .document(questionId)
                    .collection(GlobalValue.ALL_ANSWERS)
                    .whereEqualTo(GlobalValue.IS_ANSWER,true);
//                    .orderBy(GlobalValue.IS_ANSWER,Query.Direction.DESCENDING)
//                    .orderBy(GlobalValue.DATE_CREATED_TIME_STAMP,Query.Direction.DESCENDING);
//            if(!GlobalValue.getCurrentUserId().equals(authorId+"")){
//                pageQuery.whereEqualTo(GlobalValue.IS_PUBLIC,true);
//
//            }
//            .orderBy(GlobalValue.PAGE_NUMBER, Query.Direction.DESCENDING)
        }
//       else if(isViewAllAnswersForSingleQuestion){
//            pageQuery =   GlobalValue.getFirebaseFirestoreInstance()
//                    .collection(GlobalValue.ALL_USERS)
//                    .document(authorId)
//                    .collection(GlobalValue.MY_PAGES_DISCUSSION)
//                    .whereEqualTo(GlobalValue.PAGE_ID,pageId)
//                    .whereNotEqualTo(GlobalValue.HAS_PARENT_DISCUSSION,true)
//                    .orderBy(GlobalValue.HAS_PARENT_DISCUSSION,Query.Direction.DESCENDING)
//                    .orderBy(GlobalValue.DATE_CREATED_TIME_STAMP,Query.Direction.DESCENDING);
//        }

        else if(isViewSingleAnswerReplies){
            pageQuery =   GlobalValue.getFirebaseFirestoreInstance()
                    .collection(GlobalValue.ALL_QUESTIONS)
                    .document(questionId)
                    .collection(GlobalValue.ALL_ANSWERS)
                    .whereEqualTo(GlobalValue.IS_ANSWER,false)
                    .whereEqualTo(GlobalValue.PARENT_ID,answerId);
//                    .orderBy(GlobalValue.IS_ANSWER,Query.Direction.DESCENDING)
//                    .orderBy(GlobalValue.PARENT_ID,Query.Direction.DESCENDING)
//                    .orderBy(GlobalValue.DATE_CREATED_TIME_STAMP,Query.Direction.DESCENDING);
        }
        else if(isForPreview){
            pageQuery =   GlobalValue.getFirebaseFirestoreInstance()
                .collection(GlobalValue.ALL_QUESTIONS)
                .document(questionId)
                .collection(GlobalValue.ALL_ANSWERS)
                    .whereEqualTo(GlobalValue.ANSWER_ID,answerId)
                    .limit(1L);
//            Toast.makeText(getContext(), isForPreview+" trying to query "+parentDiscussionId, Toast.LENGTH_SHORT).show();

        }

        else{
                        Toast.makeText(getContext(), "do sth", Toast.LENGTH_SHORT).show();

        }


                pageQuery.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        answerListener.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.size()==0){
                                noDataFound.setVisibility(View.VISIBLE);
                                TextView title = noDataFound.findViewById(R.id.title);
                                TextView body = noDataFound.findViewById(R.id.body);
                                title.setText("No Replies ");
                                body.setText("There is no reply yet, be the first to reply this answer.");

//                                Toast.makeText(getContext(), isForPreview+"data not found "+parentDiscussionId, Toast.LENGTH_LONG).show();

                                if(isFromQuestionContext || isForPreview){
                                    noDataFound.setVisibility(View.GONE);
                                    //answerButton.setVisibility(View.GONE);
                                }
                            }

                            toggleContentVisibility(true);
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            String answerId = documentSnapshot.getId();
                            String contributorId  = ""+ documentSnapshot.get(GlobalValue.CONTRIBUTOR_ID);
                            String parentId  = ""+ documentSnapshot.get(GlobalValue.PARENT_ID);
                            String answer  = ""+ documentSnapshot.get(GlobalValue.ANSWER_BODY);
                            String answerPhotoDownloadUrl  = ""+ documentSnapshot.get(GlobalValue.ANSWER_PHOTO_DOWNLOAD_URL);

                            boolean hasParent  =  documentSnapshot.get(GlobalValue.HAS_PARENT)!=null ? documentSnapshot.getBoolean(GlobalValue.HAS_PARENT): true;
                            boolean isAnswer  =  documentSnapshot.get(GlobalValue.IS_ANSWER)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_ANSWER): true;
                            boolean hasReplies  =  documentSnapshot.get(GlobalValue.HAS_REPLIES)!=null ? documentSnapshot.getBoolean(GlobalValue.HAS_REPLIES): true;
                            boolean isPhotoIncluded  =  documentSnapshot.get(GlobalValue.IS_PHOTO_INCLUDED)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_PHOTO_INCLUDED): false;
                            boolean isHiddenByAuthor  =  documentSnapshot.get(GlobalValue.IS_HIDDEN_BY_AUTHOR)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_HIDDEN_BY_AUTHOR): false;
                            boolean isHiddenByContributor  =  documentSnapshot.get(GlobalValue.IS_HIDDEN_BY_CONTRIBUTOR)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_HIDDEN_BY_CONTRIBUTOR): false;
                            long  totalReplies  =  documentSnapshot.get(GlobalValue.TOTAL_REPLIES)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_REPLIES): 0L;
                            long  totalUpVotes =  documentSnapshot.get(GlobalValue.TOTAL_UP_VOTES)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_UP_VOTES): 0L;
                            long totalDownVotes  =  documentSnapshot.get(GlobalValue.TOTAL_DOWN_VOTES)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_DOWN_VOTES): 0L;
                            ArrayList repliersIdList  =  documentSnapshot.get(GlobalValue.REPLIERS_ID_LIST)!=null ? (ArrayList)documentSnapshot.get(GlobalValue.REPLIERS_ID_LIST): new ArrayList();
                            ArrayList upVotersIdList  =  documentSnapshot.get(GlobalValue.UP_VOTERS_ID_LIST)!=null ? (ArrayList)documentSnapshot.get(GlobalValue.UP_VOTERS_ID_LIST): new ArrayList();
                            ArrayList downVotersIdList  =  documentSnapshot.get(GlobalValue.DOWN_VOTERS_ID_LIST)!=null ? (ArrayList)documentSnapshot.get(GlobalValue.DOWN_VOTERS_ID_LIST): new ArrayList();
                            String dateCreated  =  documentSnapshot.get(GlobalValue.DATE_CREATED_TIME_STAMP)!=null ? documentSnapshot.getTimestamp(GlobalValue.DATE_CREATED_TIME_STAMP).toDate()+"" : "Moment ago";
                            if(dateCreated.length()>10){
                                dateCreated = dateCreated.substring(0,10);
//                                Toast.makeText(getContext(), isForPreview+" well data found well"+parentDiscussionId, Toast.LENGTH_SHORT).show();

                            }
                            answerListener.onSuccess(new AnswerDataModel(
                                     answerId,
                                     questionId,
                                     contributorId,
                                     answer,
                                     answerPhotoDownloadUrl,
                                     authorId,
                                      parentId,
                                     hasParent,
                                     isAnswer,
                                      hasReplies,
                                      isPhotoIncluded,
                                      isHiddenByAuthor,
                                      isHiddenByContributor,
                                     dateCreated,
                                     totalReplies,
                                     totalUpVotes,
                                     totalDownVotes,
                                     repliersIdList,
                                     upVotersIdList,
                                     downVotersIdList
    ));


                        }

                    }
                });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            if (requestCode == CAMERA_PERMISSION_REQUEST_CODE){
                fireCameraIntent();
            }

            if (requestCode == GALLERY_PERMISSION_REQUEST_CODE){
                fireGalleryIntent();
            }

        }
    }

    public void openGallery(){
        requestForPermissionAndPickImage(GALLERY_PERMISSION_REQUEST_CODE);

    }
    public void openCamera(){
        requestForPermissionAndPickImage(CAMERA_PERMISSION_REQUEST_CODE);
    }

    public void fireGalleryIntent(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        openGalleryLauncher.launch(galleryIntent);
    }
    public void fireCameraIntent(){
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraLauncher.launch(cameraIntent);
    }
    public void requestForPermissionAndPickImage(int requestCode){
        if(getContext().checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED || getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},requestCode);
        }else{
            if(requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
                fireCameraIntent();
            }
            if(requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
                fireGalleryIntent();
            }
        }



    }

    interface FetchAnswerListener{
//        void onSuccess(String pageId , String pageName, String dateCreated);
        void onSuccess(AnswerDataModel answerDataModel);
        void onFailed(String errorMessage);
    }
    public interface AddAnswerListener{
         void onAddAnswerTriggered(@NonNull AnswerDataModel answerDataModel, String parentId, boolean isEdition, boolean isAnswer);
    }
}

