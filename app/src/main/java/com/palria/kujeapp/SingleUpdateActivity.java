package com.palria.kujeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.palria.kujeapp.UpdateDataModel;
import com.palria.kujeapp.models.CommentDataModel;
import com.palria.kujeapp.widgets.BottomSheetFormBuilderWidget;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SingleUpdateActivity extends AppCompatActivity {
    UpdateFetchListener updateFetchListener;
    FirebaseFirestore firebaseFirestore;
    String updateId;
    String authorId;
    UpdateDataModel updateDataModel;
    AlertDialog alertDialog;
    TextView updateDisplayNameTextView;
    TextView updateDescriptionTextView;
    TextView viewCountTextView;
    TextView dateAddedTextView;
    ImageButton backButton;
    LinearLayout descLinearLayout;
    LinearLayout mediaLinearLayout;
    String updateImageDownloadUrl;
    TextView dummyTextView;
    ShimmerFrameLayout shimmerLayout;

    ImageView likeActionButton;
    TextView likeCountTextView;
    ImageView commentActionButton;
    TextView commentCountTextView;

    FrameLayout commentsFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_update);
        firebaseFirestore = FirebaseFirestore.getInstance();
        initUI();
        fetchIntentData();

        getWindow().setNavigationBarColor(getColor(R.color.secondary_app_color));
        getWindow().setStatusBarColor(getColor(R.color.secondary_app_color));

        alertDialog = new AlertDialog.Builder(SingleUpdateActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();
        updateFetchListener = new UpdateFetchListener() {
            @Override
            public void onFailed(String errorMessage) {
                toggleProgress(false);

            }

            @Override
            public void onSuccess(UpdateDataModel updateDataModel) {
                GlobalValue.incrementUpdateViews(updateId);
                toggleProgress(false);
                updateDisplayNameTextView.setText(updateDataModel.getTitle());
                updateDescriptionTextView.setText(updateDataModel.getDescription());
                if(!updateDataModel.getDescription().isEmpty()){
                    descLinearLayout.setVisibility(View.VISIBLE);
                }else{
                    descLinearLayout.setVisibility(View.GONE);

                }
                viewCountTextView.setText(updateDataModel.getNumOfViews()+ " Views");
                dateAddedTextView.setText(updateDataModel.getDatePosted());
                updateImageDownloadUrl = updateDataModel.getImageUrlList().size()>0 ?updateDataModel.getImageUrlList().get(0):"NULL";

                for(int i=0; i<updateDataModel.getImageUrlList().size(); i++){
                    int finalI = i;
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    ImageView imageView = new ImageView(SingleUpdateActivity.this);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    imageView.setBackgroundResource(R.drawable.agg_logo);
                    imageView.setLayoutParams(layoutParams);
                    try{
                        Picasso.get()
                                .load(updateDataModel.getImageUrlList().get(i))
                                .error(R.drawable.agg_logo)
                                .into(imageView);
                    }catch (Exception e){}
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GlobalValue.viewImagePreview(SingleUpdateActivity.this,imageView, updateDataModel.getImageUrlList().get(finalI));
                        }
                    });
                    View dummyView = new View(getApplicationContext());
                    dummyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,5));
                    mediaLinearLayout.addView(imageView);
                    mediaLinearLayout.addView(dummyView);
                }
                stopShimmer();
            }
        };


//        productFetchListener.onSuccess(productDataModel);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingleUpdateActivity.this.onBackPressed();

            }
        });

        toggleProgress(true);
        getUpdateData();
        likeActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentLikesCount = Integer.parseInt((likeCountTextView.getText()+""));

                likeActionButton.setEnabled(false);
                DocumentReference likedDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentUserId()).collection(GlobalValue.LIKED_UPDATES).document(updateId);
                GlobalValue.checkIfDocumentExists(likedDocumentReference, new GlobalValue.OnDocumentExistStatusCallback() {
                    @Override
                    public void onExist() {
                        likeCountTextView.setText((currentLikesCount-1)+"");
                        likeActionButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                        GlobalValue.likeUpdate(SingleUpdateActivity.this,updateDataModel, false, new GlobalValue.ActionCallback() {
                            @Override
                            public void onSuccess() {
                                likeActionButton.setEnabled(true);

                            }

                            @Override
                            public void onFailed(String errorMessage) {
                                likeActionButton.setEnabled(true);

                            }
                        });

                    }

                    @Override
                    public void onNotExist() {
                        likeCountTextView.setText((currentLikesCount+1)+"");
                        likeActionButton.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                        GlobalValue.likeUpdate(SingleUpdateActivity.this,updateDataModel, true, new GlobalValue.ActionCallback() {
                            @Override
                            public void onSuccess() {
                                likeActionButton.setEnabled(true);

                            }

                            @Override
                            public void onFailed(String errorMessage) {
                                likeActionButton.setEnabled(true);

                            }
                        });

                    }

                    @Override
                    public void onFailed(@NonNull String errorMessage) {
                        likeActionButton.setEnabled(true);

                    }
                });
            }
        });
        commentActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentForm();
            }
        });

        if(GlobalValue.isUpdateLiked(this,updateId) ){
            likeActionButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_thumb_up_24,getTheme()));
        }else{
            likeActionButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
        }

    }

    private void initUI(){
        updateDisplayNameTextView = findViewById(R.id.updateDisplayNameTextViewId);
        updateDescriptionTextView = findViewById(R.id.updateDescriptionTextViewId);
        viewCountTextView = findViewById(R.id.numberOfViewsTextViewId);
        dateAddedTextView = findViewById(R.id.datePostedTextViewId);
        backButton = findViewById(R.id.backButton);
        descLinearLayout = findViewById(R.id.descLinearLayoutId);
        mediaLinearLayout = findViewById(R.id.mediaLinearLayoutId);
        dummyTextView = findViewById(R.id.dummyTextViewId);
        shimmerLayout = findViewById(R.id.shimmerLayout);

        likeActionButton=findViewById(R.id.likeActionButtonId);
        likeCountTextView=findViewById(R.id.likeCountTextViewId);

        commentActionButton=findViewById(R.id.commentActionButtonId);
        commentCountTextView=findViewById(R.id.commentCountTextViewId);

        commentsFrameLayout=findViewById(R.id.commentsFrameLayoutId);


    }
    void fetchIntentData(){
        Intent intent = getIntent();
        updateId = intent.getStringExtra(GlobalValue.UPDATE_ID);
        authorId = intent.getStringExtra(GlobalValue.AUTHOR_ID);
        updateDataModel = (UpdateDataModel) intent.getSerializableExtra(GlobalValue.UPDATE_DATA_MODEL);
    }

    private void toggleProgress(boolean show) {
        if (true) {
            return;
        }
        if(show){
            alertDialog.show();
        }else{
            alertDialog.cancel();
        }
    }
    void showCommentForm(){
        BottomSheetFormBuilderWidget bottomSheetFormBuilderWidget = new BottomSheetFormBuilderWidget(SingleUpdateActivity.this)
                .setTitle("Comment your opinion")
                .setPositiveTitle("Comment").setOnSubmit(new BottomSheetFormBuilderWidget.OnSubmitHandler() {
            @Override
            public void onSubmit(String title, String body) {
                super.onSubmit(title,body);

//                         Toast.makeText(PageActivity.this, values[0], Toast.LENGTH_SHORT).show();
                //values will be returned as array of strings as per input list position
                //eg first added input has first value
//                        String body = values[0];
                if (title.trim().equals("")) {
                    //     leBottomSheetDialog.setTitle("Folder needs name, must enter name for the folder");

                    Toast.makeText(SingleUpdateActivity.this, "Please enter your idea",Toast.LENGTH_SHORT).show();

                }
                else {

                    String commentId = GlobalValue.getRandomString(80);
                    GlobalValue.createSnackBar(SingleUpdateActivity.this,commentActionButton, "Posting comment: "+body, Snackbar.LENGTH_INDEFINITE);

                    GlobalValue.comment(new CommentDataModel(commentId,GlobalValue.getCurrentUserId(),body,"",updateId,authorId,"",false,false,false,false,"",0L,0L,new ArrayList(),new ArrayList()),new GlobalValue.ActionCallback(){
                        @Override
                        public void onFailed(String errorMessage){
                            Toast.makeText(SingleUpdateActivity.this, "failed", Toast.LENGTH_SHORT).show();

                        }
                        @Override
                        public void onSuccess(){
//                                     Toast.makeText(PageActivity.this, body, Toast.LENGTH_SHORT).show();
                            GlobalValue.createSnackBar(SingleUpdateActivity.this,commentActionButton, "Thanks comment posted: "+body,Snackbar.LENGTH_SHORT);
                            int currentDiscussionCount = Integer.parseInt((commentCountTextView.getText()+""));
                            commentCountTextView.setText((currentDiscussionCount+1)+"");

                        }
                    });
//                             createNewFolder(values[0],isPublic[0]);
//                                           leBottomSheetDialog.setTitle("Creating "+values[0]+" folder in progress...");
//                                           leBottomSheetDialog.hide();
                }
                //create folder process here
            }
        });
        bottomSheetFormBuilderWidget.addInputField(new BottomSheetFormBuilderWidget.EditTextInput(SingleUpdateActivity.this)
                        .setHint("Enter your idea")
                        .autoFocus());
        bottomSheetFormBuilderWidget.render("Comment")
                .show();
    }


    private void getUpdateData(){
        toggleProgress(true);
        firebaseFirestore.collection(GlobalValue.PLATFORM_UPDATES).document(updateId).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateFetchListener.onFailed(e.getMessage());
            }
        })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        shimmerLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);

                        String updateId = documentSnapshot.getId();
                        String updateTitle =""+ documentSnapshot.get(GlobalValue.UPDATE_TITLE);
                        String authorId =""+ documentSnapshot.get(GlobalValue.AUTHOR_ID);
                        String updateDescription =""+ documentSnapshot.get(GlobalValue.UPDATE_DESCRIPTION);
                        String datePosted = documentSnapshot.get(GlobalValue.DATE_POSTED_TIME_STAMP)!=null ? documentSnapshot.getTimestamp(GlobalValue.DATE_POSTED_TIME_STAMP).toDate()+""  : "Moment ago";
                        if(datePosted.length()>10){
                            datePosted = datePosted.substring(0,10);
                        }
                        long updateViewCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_VIEWS)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_VIEWS) : 0L;
                        ArrayList<String> imageUrlList = documentSnapshot.get(GlobalValue.UPDATE_IMAGE_DOWNLOAD_URL_ARRAY_LIST)!=null ? (ArrayList<String>) documentSnapshot.get(GlobalValue.UPDATE_IMAGE_DOWNLOAD_URL_ARRAY_LIST) : new ArrayList<>();
                        boolean isPrivate = documentSnapshot.get(GlobalValue.IS_PRIVATE)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_PRIVATE) : false;

                        long totalLikes = documentSnapshot.get(GlobalValue.TOTAL_LIKES)!=null? documentSnapshot.getLong(GlobalValue.TOTAL_LIKES) :0L;
                        long totalComments = documentSnapshot.get(GlobalValue.TOTAL_COMMENTS)!=null? documentSnapshot.getLong(GlobalValue.TOTAL_COMMENTS) :0L;
                        likeCountTextView.setText(totalLikes+"");
                        commentCountTextView.setText(totalComments+"");

                        ArrayList repliersIdList  =  documentSnapshot.get(GlobalValue.REPLIERS_ID_LIST)!=null ? (ArrayList)documentSnapshot.get(GlobalValue.REPLIERS_ID_LIST): new ArrayList();
                        ArrayList likersIdList  =  documentSnapshot.get(GlobalValue.LIKERS_ID_LIST)!=null ? (ArrayList)documentSnapshot.get(GlobalValue.LIKERS_ID_LIST): new ArrayList();



                        updateFetchListener.onSuccess(new UpdateDataModel(updateId,authorId,updateTitle,updateDescription,datePosted,imageUrlList, (int) updateViewCount,isPrivate,totalComments,totalLikes,repliersIdList,likersIdList));

                    }
                });
    }
    void stopShimmer(){
        dummyTextView.setVisibility(View.GONE);
    }
    interface UpdateFetchListener{
        void onFailed(String errorMessage);
        void onSuccess(UpdateDataModel updateDataModel);
    }

}