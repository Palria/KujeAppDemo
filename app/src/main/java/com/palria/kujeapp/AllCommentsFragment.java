package com.palria.kujeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.palria.kujeapp.adapters.CommentRcvAdapter;
import com.palria.kujeapp.models.CommentDataModel;
import com.palria.kujeapp.widgets.BottomSheetFormBuilderWidget;

import java.util.ArrayList;

public class AllCommentsFragment extends Fragment {

    LinearLayout featuredItemLayoutContainer;
    LinearLayout noDataFound;
    LinearLayout loadingLayout;
    View dummyView;
    public AllCommentsFragment() {
        // Required empty public constructor
    }

        boolean isForPreview = false;
        boolean isFromPageContext = true;
        boolean isViewAllCommentsForSinglePage = false;
        boolean isViewSingleCommentReplies = false;
        boolean isTutorialPage = true;
        String authorId = "";
        String tutorialId = "";
        String folderId = "";
        String updateId = "";
        String parentCommentId = "";
//        String commentId = "";
ArrayList<CommentDataModel> updateCommentDataModels=new ArrayList<>();
RecyclerView commentsRecyclerListView;
    CommentRcvAdapter adapter;
    Button commentButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
if(getArguments()!= null){
    isForPreview = getArguments().getBoolean(GlobalValue.IS_FOR_PREVIEW,false);
    isFromPageContext = getArguments().getBoolean(GlobalValue.IS_FROM_UPDATE_CONTEXT,false);
    isViewAllCommentsForSinglePage = getArguments().getBoolean(GlobalValue.IS_VIEW_ALL_COMMENTS_FOR_SINGLE_UPDATE,false);
    isViewSingleCommentReplies = getArguments().getBoolean(GlobalValue.IS_VIEW_SINGLE_COMMENT_REPLY,false);
    authorId = getArguments().getString(GlobalValue.AUTHOR_ID);
    updateId = getArguments().getString(GlobalValue.UPDATE_ID);
    parentCommentId = getArguments().getString(GlobalValue.PARENT_COMMENT_ID);
//    commentId = getArguments().getString(GlobalValue.COMMENT_ID);
}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View parentView = inflater.inflate(R.layout.fragment_all_comments, container, false);
        initUi(parentView);
        fetchComments(new FetchCommentListener() {
            @Override
            public void onSuccess(CommentDataModel updateCommentDataModel) {
                updateCommentDataModels.add(updateCommentDataModel);
                adapter.notifyItemChanged(updateCommentDataModels.size());
            }

            @Override
            public void onFailed(String errorMessage) {

            }
        });

        if(isFromPageContext || isForPreview){
            noDataFound.setVisibility(View.GONE);
            commentButton.setVisibility(View.GONE);
            dummyView.setVisibility(View.GONE);
//            Toast.makeText(getContext(), isForPreview+" checking " +parentCommentId, Toast.LENGTH_SHORT).show();
        }


        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentForm();
            }
        });
        if(isViewAllCommentsForSinglePage){
            openPageFragment();

        }else if(isViewSingleCommentReplies){
            openCommentFragment();
        }
         return parentView;
    }

    private void toggleContentVisibility(boolean show){
        if(!show){
            loadingLayout.setVisibility(View.VISIBLE);
            commentsRecyclerListView.setVisibility(View.GONE);
        }else{
            loadingLayout.setVisibility(View.GONE);
            commentsRecyclerListView.setVisibility(View.VISIBLE);
        }
    }

    private void initUi(View parentView) {

        commentsRecyclerListView=parentView.findViewById(R.id.commentsRecyclerListViewId);
        commentButton=parentView.findViewById(R.id.commentButtonId);

        featuredItemLayoutContainer=parentView.findViewById(R.id.featuredItemLayoutContainerId);
        loadingLayout=parentView.findViewById(R.id.loadingLayout);
        noDataFound=parentView.findViewById(R.id.noDataFound);
        dummyView=parentView.findViewById(R.id.dummyViewId);
        toggleContentVisibility(false);
//
//        updateDataModels.add(new PageDataModel("How to hold","this is content",
//                "",
//                "author",
//                "updateId",
//                "tutorialId",
//                "folderId",
//                "1 hrs ago",true));
//        updateDataModels.add(new PageDataModel("How to hold","this is content",
//                "",
//                "author",
//                "updateId",
//                "tutorialId",
//                "folderId",
//                "1 hrs ago",true));
//        updateDataModels.add(new PageDataModel("How to hold","this is content",
//                "",
//                "author",
//                "updateId",
//                "tutorialId",
//                "folderId",
//                "1 hrs ago",true));
//        updateDataModels.add(new PageDataModel("How to hold","this is content",
//                "",
//                "author",
//                "updateId",
//                "tutorialId",
//                "folderId",
//                "1 hrs ago",true));


        adapter = new CommentRcvAdapter(updateCommentDataModels, getContext(),authorId,isTutorialPage);

//        updatesRecyclerListView.setHasFixedSize(true);
        commentsRecyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        commentsRecyclerListView.setAdapter(adapter);

    }
    void showCommentForm(){
        BottomSheetFormBuilderWidget bottomSheetFormBuilderWidget = new BottomSheetFormBuilderWidget(getContext())
                .setTitle("Comment your idea")
                .setPositiveTitle("Comment");
        bottomSheetFormBuilderWidget.addInputField(new BottomSheetFormBuilderWidget.EditTextInput(getContext())
                        .setHint("Enter your idea")
                        .autoFocus());
        bottomSheetFormBuilderWidget.setOnSubmit(new BottomSheetFormBuilderWidget.OnSubmitHandler() {
                    @Override
                    public void onSubmit(String title, String body) {
                        super.onSubmit(title,body);

//                         Toast.makeText(PageActivity.this, values[0], Toast.LENGTH_SHORT).show();
                        //values will be returned as array of strings as per input list position
                        //eg first added input has first value
//                        String body = values[0];
                        if (body.trim().equals("")) {
                            //     leBottomSheetDialog.setTitle("Folder needs name, must enter name for the folder");

                            Toast.makeText(getContext(), "Please enter your idea",Toast.LENGTH_SHORT).show();

                        } else {

                            String commentId = GlobalValue.getRandomString(80);
                            GlobalValue.createSnackBar(getContext(),commentButton, "Posting comment: "+body, Snackbar.LENGTH_INDEFINITE);
				CommentDataModel pd = new CommentDataModel(commentId,GlobalValue.getCurrentUserId(),body,"",updateId,authorId,"",false,false,false,false,"",0L,0L,new ArrayList(),new ArrayList());
                            GlobalValue.comment(pd,new GlobalValue.ActionCallback(){
                                @Override
                                public void onFailed(String errorMessage){
                                    Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();

                                }
                                @Override
                                public void onSuccess(){
//                                     Toast.makeText(PageActivity.this, body, Toast.LENGTH_SHORT).show();
                                    GlobalValue.createSnackBar(getContext(),commentButton, "Thanks comment added: "+body,Snackbar.LENGTH_SHORT);
//                                    int currentCommentCount = Integer.parseInt((commentCountTextView.getText()+""));
//                                    commentCountTextView.setText((currentCommentCount+1)+"");
					updateCommentDataModels.add(pd);
					adapter.notifyDataSetChanged();

                                }
                            });
//                             createNewFolder(values[0],isPublic[0]);
//                                           leBottomSheetDialog.setTitle("Creating "+values[0]+" folder in progress...");
//                                           leBottomSheetDialog.hide();
                        }
                        //create folder process here
                    }
                })
                .render("Comment")
                .show();
    }

    void openPageFragment(){
        AllUpdatesFragment allUpdatesFragment = new AllUpdatesFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(GlobalValue.IS_FOR_PREVIEW,true);
        bundle.putString(GlobalValue.AUTHOR_ID,authorId);
        bundle.putString(GlobalValue.UPDATE_ID,updateId);
        allUpdatesFragment.setArguments(bundle);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.featuredItemLayoutContainerId, allUpdatesFragment)
                .commit();

    }

    void openCommentFragment(){
        AllCommentsFragment allCommentsFragment = new AllCommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(GlobalValue.IS_FOR_PREVIEW,true);
        bundle.putString(GlobalValue.AUTHOR_ID,authorId);
        bundle.putString(GlobalValue.UPDATE_ID,updateId);
        bundle.putString(GlobalValue.PARENT_COMMENT_ID,parentCommentId);
        allCommentsFragment.setArguments(bundle);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.featuredItemLayoutContainerId, allCommentsFragment)
                .commit();
//        Toast.makeText(getContext(), isForPreview+" now opening fragment "+parentCommentId, Toast.LENGTH_SHORT).show();

    }


    private void fetchComments(FetchCommentListener fetchCommentListener){
        Query updateQuery = null;
        if(isFromPageContext){
            updateQuery =   GlobalValue.getFirebaseFirestoreInstance()
                    .collection(GlobalValue.ALL_USERS)
                    .document(authorId)
                    .collection(GlobalValue.MY_UPDATES_COMMENT)
                    .whereEqualTo(GlobalValue.UPDATE_ID,updateId)
                    .whereNotEqualTo(GlobalValue.HAS_PARENT_COMMENT,true)
                    .orderBy(GlobalValue.HAS_PARENT_COMMENT,Query.Direction.DESCENDING)
                    .orderBy(GlobalValue.DATE_CREATED_TIME_STAMP,Query.Direction.DESCENDING)
                    .limit(3L);
//            if(!GlobalValue.getCurrentUserId().equals(authorId+"")){
//                updateQuery.whereEqualTo(GlobalValue.IS_PUBLIC,true);
//
//            }
//            .orderBy(GlobalValue.UPDATE_NUMBER, Query.Direction.DESCENDING)
        }
       else if(isViewAllCommentsForSinglePage){
            updateQuery =   GlobalValue.getFirebaseFirestoreInstance()
                    .collection(GlobalValue.ALL_USERS)
                    .document(authorId)
                    .collection(GlobalValue.MY_UPDATES_COMMENT)
                    .whereEqualTo(GlobalValue.UPDATE_ID,updateId)
                    .whereNotEqualTo(GlobalValue.HAS_PARENT_COMMENT,true)
                    .orderBy(GlobalValue.HAS_PARENT_COMMENT,Query.Direction.DESCENDING)
                    .orderBy(GlobalValue.DATE_CREATED_TIME_STAMP,Query.Direction.DESCENDING);
        }

        else if(isViewSingleCommentReplies){
            updateQuery =   GlobalValue.getFirebaseFirestoreInstance()
                    .collection(GlobalValue.ALL_USERS)
                    .document(authorId)
                    .collection(GlobalValue.MY_UPDATES_COMMENT)
                    .whereEqualTo(GlobalValue.UPDATE_ID,updateId)
                    .whereEqualTo(GlobalValue.PARENT_COMMENT_ID,parentCommentId)
                    .orderBy(GlobalValue.PARENT_COMMENT_ID,Query.Direction.DESCENDING)
                    .orderBy(GlobalValue.DATE_CREATED_TIME_STAMP,Query.Direction.DESCENDING);
        }
        else if(isForPreview){
            updateQuery =   GlobalValue.getFirebaseFirestoreInstance()
                    .collection(GlobalValue.ALL_USERS)
                    .document(authorId)
                    .collection(GlobalValue.MY_UPDATES_COMMENT)
                    .whereEqualTo(GlobalValue.UPDATE_ID,updateId)
                    .whereEqualTo(GlobalValue.COMMENT_ID,parentCommentId)
                    .limit(1L);
//            Toast.makeText(getContext(), isForPreview+" trying to query "+parentCommentId, Toast.LENGTH_SHORT).show();

        }

        else{

            updateQuery =   GlobalValue.getFirebaseFirestoreInstance()
                    .collection(GlobalValue.ALL_USERS)
                    .document(authorId)
                    .collection(GlobalValue.MY_UPDATES_COMMENT)
                    .whereNotEqualTo(GlobalValue.UPDATE_ID,updateId)
                    .orderBy(GlobalValue.UPDATE_ID,Query.Direction.ASCENDING)
                    .orderBy(GlobalValue.DATE_CREATED_TIME_STAMP,Query.Direction.DESCENDING)
                    .limit(100L);
if(!GlobalValue.getCurrentUserId().equals(authorId+"")){
    updateQuery.whereEqualTo(GlobalValue.IS_HIDDEN_BY_POSTER,false);
    updateQuery.whereEqualTo(GlobalValue.IS_HIDDEN_BY_AUTHOR,false);

}

//            .orderBy(GlobalValue.UPDATE_NUMBER, Query.Direction.DESCENDING)

        }


                updateQuery.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        fetchCommentListener.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.size()==0){
                                noDataFound.setVisibility(View.VISIBLE);
                                TextView title = noDataFound.findViewById(R.id.title);
                                TextView body = noDataFound.findViewById(R.id.body);
                                title.setText("No Comments ");
                                body.setText("There is no comment yet, click the add button to comment.");

//                                Toast.makeText(getContext(), isForPreview+"data not found "+parentCommentId, Toast.LENGTH_LONG).show();

                                if(isFromPageContext || isForPreview){
                                    noDataFound.setVisibility(View.GONE);
                                    commentButton.setVisibility(View.GONE);
                                }
                            }

                            toggleContentVisibility(true);
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            String commentId = documentSnapshot.getId();
                            String commentPosterId  = ""+ documentSnapshot.get(GlobalValue.COMMENT_POSTER_ID);
                            String description  = ""+ documentSnapshot.get(GlobalValue.COMMENT_DESCRIPTION);
                            String coverPhotoDownloadUrl  = ""+ documentSnapshot.get(GlobalValue.COMMENT_COVER_PHOTO_DOWNLOAD_URL);
                            String updateId  = ""+ documentSnapshot.get(GlobalValue.UPDATE_ID);
                            String parentCommentId  = ""+ documentSnapshot.get(GlobalValue.PARENT_COMMENT_ID);
                            boolean hasParentComment  =  documentSnapshot.get(GlobalValue.HAS_PARENT_COMMENT)!=null ? documentSnapshot.getBoolean(GlobalValue.HAS_PARENT_COMMENT): true;
                            boolean hasReplies  =  documentSnapshot.get(GlobalValue.HAS_REPLIES)!=null ? documentSnapshot.getBoolean(GlobalValue.HAS_REPLIES): true;
                            boolean isHiddenByAuthor  =  documentSnapshot.get(GlobalValue.IS_HIDDEN_BY_AUTHOR)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_HIDDEN_BY_AUTHOR): false;
                            boolean isHiddenByPoster  =  documentSnapshot.get(GlobalValue.IS_HIDDEN_BY_POSTER)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_HIDDEN_BY_POSTER): false;
                            long totalReplies  =  documentSnapshot.get(GlobalValue.TOTAL_REPLIES)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_REPLIES): 0L;
                            long totalLikes  =  documentSnapshot.get(GlobalValue.TOTAL_LIKES)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_LIKES): 0L;
//                            long totalDisLikes  =  documentSnapshot.get(GlobalValue.TOTAL_DISLIKES)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_DISLIKES): 0L;
                            ArrayList repliersIdList  =  documentSnapshot.get(GlobalValue.REPLIERS_ID_LIST)!=null ? (ArrayList)documentSnapshot.get(GlobalValue.REPLIERS_ID_LIST): new ArrayList();
                            ArrayList likersIdList  =  documentSnapshot.get(GlobalValue.LIKERS_ID_LIST)!=null ? (ArrayList)documentSnapshot.get(GlobalValue.LIKERS_ID_LIST): new ArrayList();
//                            ArrayList disLikersIdList  =  documentSnapshot.get(GlobalValue.DISLIKERS_ID_LIST)!=null ? (ArrayList)documentSnapshot.get(GlobalValue.DISLIKERS_ID_LIST): new ArrayList();
                            String dateCreated  =  documentSnapshot.get(GlobalValue.DATE_CREATED_TIME_STAMP)!=null ? documentSnapshot.getTimestamp(GlobalValue.DATE_CREATED_TIME_STAMP).toDate()+"" : "Moment ago";
                            if(dateCreated.length()>10){
                                dateCreated = dateCreated.substring(0,10);
//                                Toast.makeText(getContext(), isForPreview+" well data found well"+parentCommentId, Toast.LENGTH_SHORT).show();

                            }
                            fetchCommentListener.onSuccess(new CommentDataModel(
                                     commentId,
                                     commentPosterId,
                                     description,
                                    coverPhotoDownloadUrl,
                                     updateId,
                                     authorId,
                                     parentCommentId,
                                      hasParentComment,
                                      hasReplies,
                                      isHiddenByAuthor,
                                      isHiddenByPoster,
                                     dateCreated,
                                     totalReplies,
                                     totalLikes,
//                                     totalDisLikes,
                                     repliersIdList,
                                     likersIdList
//                                     disLikersIdList
    ));


                        }

                    }
                });
    }

    interface FetchCommentListener{
//        void onSuccess(String updateId , String updateName, String dateCreated);
        void onSuccess(CommentDataModel updateCommentDataModel);
        void onFailed(String errorMessage);
    }
}

