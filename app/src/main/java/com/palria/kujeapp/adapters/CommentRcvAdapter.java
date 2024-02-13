package com.palria.kujeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.palria.kujeapp.GlobalValue;
import com.palria.kujeapp.R;
import com.palria.kujeapp.models.CommentDataModel;
import com.palria.kujeapp.widgets.BottomSheetFormBuilderWidget;

import java.util.ArrayList;

public class CommentRcvAdapter extends RecyclerView.Adapter<CommentRcvAdapter.ViewHolder> {

    ArrayList<CommentDataModel> commentDataModels;
    Context context;
    String authorId;
    boolean isTutorialUpdate;

    public CommentRcvAdapter(ArrayList<CommentDataModel> commentDataModels, Context context, String authorId, boolean isTutorialUpdate){
        this.commentDataModels = commentDataModels;
        this.context = context;
        this.authorId = authorId;
        this.isTutorialUpdate = isTutorialUpdate;
    }

    @NonNull
    @Override
    public CommentRcvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.update_comment_item_layout, parent, false);
        CommentRcvAdapter.ViewHolder viewHolder = new CommentRcvAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRcvAdapter.ViewHolder holder, int position) {
        CommentDataModel commentDataModel = commentDataModels.get(position);

//        if (commentDataModel.isHiddenByAuthor() || commentDataModel.isHiddenByPoster() || (GlobalValue.getCurrentUserId().equals(commentDataModel.getCommentPosterId()+""))) {
        if (true) {

            holder.dateCreatedTextView.setText(commentDataModel.getDateCreated());

            GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS)
                    .document(commentDataModel.getCommentPosterId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            final String userName = ""+ documentSnapshot.get(GlobalValue.USER_DISPLAY_NAME);
                            final String userProfilePhotoDownloadUrl = ""+ documentSnapshot.get(GlobalValue.USER_PROFILE_PHOTO_DOWNLOAD_URL);

                            holder.posterNameTextView.setText(userName);
                            try {
                                Glide.with(context)
                                        .load(userProfilePhotoDownloadUrl)
                                        .centerCrop()
                                        .placeholder(R.drawable.placeholder)
                                        .into(holder.commentPosterProfilePhoto);
                            } catch (Exception ignored) {
                            }

                            boolean isVerified = documentSnapshot.get(GlobalValue.IS_ACCOUNT_VERIFIED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_ACCOUNT_VERIFIED) : false;
                            if (isVerified) {
                                holder.verificationFlagImageView.setVisibility(View.VISIBLE);
                            } else {
                                holder.verificationFlagImageView.setVisibility(View.INVISIBLE);

                            }
                        }
                    });

            holder.posterNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(GlobalValue.getHostActivityIntent(context,null,GlobalValue.USER_PROFILE_FRAGMENT_TYPE, commentDataModel.getCommentPosterId()));

                }
            });
            holder.commentPosterProfilePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(GlobalValue.getHostActivityIntent(context,null,GlobalValue.USER_PROFILE_FRAGMENT_TYPE,commentDataModel.getCommentPosterId()));

                }
            });


            holder.commentDescriptionTextView.setText(commentDataModel.getDescription());
            holder.commentCountTextView.setText(commentDataModel.getTotalReplies() + "");
            if(commentDataModel.getTotalReplies()==1){
                holder.viewCommentsTextView.setText("See " + commentDataModel.getTotalReplies() + " reply");
            }else {
                holder.viewCommentsTextView.setText("See " + commentDataModel.getTotalReplies() + " replies");
            }
            holder.likeCountTextView.setText(commentDataModel.getTotalLikes() + "");
/*
            DocumentReference likedDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentUserId()).collection(GlobalValue.LIKED_COMMENTS).document(commentDataModel.getCommentId());
            GlobalValue.checkIfDocumentExists(likedDocumentReference, new GlobalValue.OnDocumentExistStatusCallback() {
                @Override
                public void onExist(DocumentSnapshot documentSnapshot) {
                    holder.likeActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_thumb_up_24,context.getTheme()));
                }

                @Override
                public void onNotExist() {
                    holder.likeActionButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                }

                @Override
                public void onFailed(@NonNull String errorMessage) {

                }
            });
            */
//            if(GlobalValue.isCommentLiked(context,commentDataModel.getCommentId())){
            if(commentDataModel.getLikersIdList().contains(GlobalValue.getCurrentUserId())){
                holder.likeActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_thumb_up_24,context.getTheme()));
            }else{
                holder.likeActionButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
            }
//            holder.disLikeAccountActionButton.setText(commentDataModel.getTotalDisLikes() + "");
            if(commentDataModel.getTotalReplies() <=0){
                holder.viewCommentsTextView.setVisibility(View.GONE);
            }
            holder.moreActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalValue.createPopUpMenu(context,R.menu.update_comment_action_menu , holder.moreActionButton, new GlobalValue.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClicked(MenuItem item) {

                            if(item.getItemId() == R.id.deleteId){

                                GlobalValue.deleteComment(commentDataModel.getCommentId(), commentDataModel.getParentCommentId(), commentDataModel.getUpdateId(), commentDataModel.getAuthorId(), commentDataModel.hasParentComment(), new GlobalValue.ActionCallback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onFailed(String errorMessage) {

                                            }
                                        });
                                int positionDeleted = commentDataModels.indexOf(commentDataModel);
                                commentDataModels.remove(commentDataModel);
                                notifyItemRemoved(positionDeleted);
                            }

                            return true;
                        }
                    });
                }
            });
            holder.viewCommentsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    intent.putExtra(GlobalValue.IS_FROM_UPDATE_CONTEXT,false);
                    intent.putExtra(GlobalValue.IS_VIEW_ALL_COMMENTS_FOR_SINGLE_UPDATE,false);
                    intent.putExtra(GlobalValue.IS_VIEW_SINGLE_COMMENT_REPLY,true);
                    intent.putExtra(GlobalValue.AUTHOR_ID,commentDataModel.getAuthorId());
                    intent.putExtra(GlobalValue.PARENT_COMMENT_ID,commentDataModel.getCommentId());
                    intent.putExtra(GlobalValue.UPDATE_ID,commentDataModel.getUpdateId());
                    context.startActivity(GlobalValue.getHostActivityIntent(context,intent,GlobalValue.COMMENT_FRAGMENT_TYPE,""));


                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    intent.putExtra(GlobalValue.IS_FROM_UPDATE_CONTEXT,false);
                    intent.putExtra(GlobalValue.IS_VIEW_ALL_COMMENTS_FOR_SINGLE_UPDATE,false);
                    intent.putExtra(GlobalValue.IS_VIEW_SINGLE_COMMENT_REPLY,true);
                    intent.putExtra(GlobalValue.AUTHOR_ID,commentDataModel.getAuthorId());
                    intent.putExtra(GlobalValue.PARENT_COMMENT_ID,commentDataModel.getCommentId());
                    intent.putExtra(GlobalValue.UPDATE_ID,commentDataModel.getUpdateId());
                    context.startActivity(GlobalValue.getHostActivityIntent(context,intent,GlobalValue.COMMENT_FRAGMENT_TYPE,""));


                }
            });

            holder.likeActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int currentLikesCount = Integer.parseInt((holder.likeCountTextView.getText()+""));

                    holder.likeActionButton.setEnabled(false);
                    if(commentDataModel.getLikersIdList().contains(GlobalValue.getCurrentUserId())){
                        if(!(holder.likeCountTextView.getText()+"").equals("0")) {
                            holder.likeCountTextView.setText((currentLikesCount - 1) + "");
                        }


                        holder.likeActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_outline_thumb_up_24,context.getTheme()));
                        GlobalValue.likeComment(context,commentDataModel,commentDataModel.getCommentId(), commentDataModel.getUpdateId(), commentDataModel.getAuthorId(), false, new GlobalValue.ActionCallback() {
                            @Override
                            public void onSuccess() {
                                holder.likeActionButton.setEnabled(true);

                            }

                            @Override
                            public void onFailed(String errorMessage) {
                                holder.likeActionButton.setEnabled(true);

                            }
                        });

                    }else{
                        holder.likeCountTextView.setText((currentLikesCount+1)+"");
                        holder.likeActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_thumb_up_24,context.getTheme()));
                        GlobalValue.likeComment(context,commentDataModel,commentDataModel.getCommentId(),commentDataModel.getUpdateId(), commentDataModel.getAuthorId(), true, new GlobalValue.ActionCallback() {
                            @Override
                            public void onSuccess() {
                                holder.likeActionButton.setEnabled(true);

                            }

                            @Override
                            public void onFailed(String errorMessage) {
                                holder.likeActionButton.setEnabled(true);

                            }
                        });

                    }
//                    DocumentReference likedDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentUserId()).collection(GlobalValue.LIKED_COMMENTS).document(commentDataModel.getCommentId());
//                    GlobalValue.checkIfDocumentExists(likedDocumentReference, new GlobalValue.OnDocumentExistStatusCallback() {
//                        @Override
//                        public void onExist(DocumentSnapshot documentSnapshot) {
//
//
//                        }
//
//                        @Override
//                        public void onNotExist() {
//                              }
//
//                        @Override
//                        public void onFailed(@NonNull String errorMessage) {
//                            holder.likeActionButton.setEnabled(true);
//
//                        }
//                    });



                }
            });

            holder.discussActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCommentForm(holder,commentDataModel);
                }
            });



        }
        else{
            holder.commentDescriptionTextView.setText("Comment is hidden");
            holder.itemView.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return commentDataModels.size();
    }
    void showCommentForm(ViewHolder holder, CommentDataModel commentDataModel){
        BottomSheetFormBuilderWidget bottomSheetFormBuilderWidget = new BottomSheetFormBuilderWidget(context)
                .setTitle("Comment your opinion")
                .setPositiveTitle("Comment")
                .setOnSubmit(new BottomSheetFormBuilderWidget.OnSubmitHandler() {
                    @Override
                    public void onSubmit(String title, String body) {
                        super.onSubmit( title,  body);

//                         Toast.makeText(UpdateActivity.this, values[0], Toast.LENGTH_SHORT).show();
                        //values will be returned as array of strings as per input list position
                        //eg first added input has first value
//                        String body = values[0];
                        if (body.trim().equals("")) {
                            //     leBottomSheetDialog.setTitle("Folder needs name, must enter name for the folder");

                            Toast.makeText(context, "Please enter some texts",Toast.LENGTH_SHORT).show();

                        } else {

                            String commentId = GlobalValue.getRandomString(80);
                            GlobalValue.createSnackBar(context,holder.discussActionButton, "Adding comment: "+body,Snackbar.LENGTH_INDEFINITE);

                            GlobalValue.comment(new CommentDataModel(commentId,GlobalValue.getCurrentUserId(),body,"",commentDataModel.getUpdateId(),commentDataModel.getAuthorId(),commentDataModel.getCommentId(),true,false,false,false,"",0L,0L,new ArrayList(),new ArrayList()),new GlobalValue.ActionCallback(){
                                @Override
                                public void onFailed(String errorMessage){
                                    Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();

                                }
                                @Override
                                public void onSuccess(){
//                                     Toast.makeText(UpdateActivity.this, body, Toast.LENGTH_SHORT).show();
                                    GlobalValue.createSnackBar(context,holder.discussActionButton, "Thanks comment added: "+body,Snackbar.LENGTH_SHORT);
                                    int currentCommentCount = Integer.parseInt((holder.commentCountTextView.getText()+""));
                                    holder.commentCountTextView.setText((currentCommentCount+1)+"");

                                }
                            });
//                             createNewFolder(values[0],isPublic[0]);
//                                           leBottomSheetDialog.setTitle("Creating "+values[0]+" folder in progress...");
//                                           leBottomSheetDialog.hide();
                        }
                        //create folder process here
                    }
                });
        bottomSheetFormBuilderWidget.addInputField(new BottomSheetFormBuilderWidget.EditTextInput(context)
                        .setHint("Enter your idea")
                        .autoFocus());
        bottomSheetFormBuilderWidget.render("Comment")
                .show();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public RoundedImageView commentPosterProfilePhoto;
        public TextView posterNameTextView;
        public ImageView verificationFlagImageView;
        public TextView commentDescriptionTextView;
        public TextView dateCreatedTextView;
        public ImageButton moreActionButton;
        public Button likeAccountActionButton;
        public Button disLikeAccountActionButton;
        public Button replyAccountActionButton;

        public ImageView likeActionButton;
        public ImageView discussActionButton;
        public TextView likeCountTextView;
        public TextView commentCountTextView;
        public TextView viewCommentsTextView;



        public ViewHolder(View itemView) {
            super(itemView);
            this.commentPosterProfilePhoto = itemView.findViewById(R.id.commentPosterProfilePhotoId);
            this.posterNameTextView =  itemView.findViewById(R.id.posterNameTextViewId);
            this.verificationFlagImageView = itemView.findViewById(R.id.verificationFlagImageViewId);
            commentDescriptionTextView=itemView.findViewById(R.id.commentDescriptionTextViewId);
            moreActionButton=itemView.findViewById(R.id.moreActionButtonId);
            dateCreatedTextView=itemView.findViewById(R.id.dateCreatedTextViewId);
            likeAccountActionButton=itemView.findViewById(R.id.likeAccountActionButtonId);
            disLikeAccountActionButton=itemView.findViewById(R.id.disLikeAccountActionButtonId);
            replyAccountActionButton=itemView.findViewById(R.id.replyAccountActionButtonId);

            likeActionButton=itemView.findViewById(R.id.likeActionButtonId);
            likeCountTextView=itemView.findViewById(R.id.likeCountTextViewId);

            discussActionButton=itemView.findViewById(R.id.commentActionButtonId);
            commentCountTextView=itemView.findViewById(R.id.commentCountTextViewId);
            viewCommentsTextView=itemView.findViewById(R.id.viewCommentsTextViewId);



        }
    }

}
