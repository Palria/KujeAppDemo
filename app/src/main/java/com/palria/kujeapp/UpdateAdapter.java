package com.palria.kujeapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.palria.kujeapp.ProductDataModel;
//import com.makeramen.roundedimageview.RoundedImageView;
//import com.palria.learnera.GlobalValue;
//import com.palria.learnera.LibraryActivity;
//import com.palria.learnera.PageActivity;
//import com.palria.learnera.R;
//import com.palria.learnera.TutorialActivity;
//import com.palria.learnera.TutorialFolderActivity;
//import com.palria.learnera.models.BookmarkDataModel;
//import com.palria.learnera.models.FolderDataModel;
//import com.palria.learnera.models.LibraryDataModel;
//import com.palria.learnera.models.TutorialDataModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.makeramen.roundedimageview.RoundedImageView;
import com.palria.kujeapp.UpdateDataModel;
import com.palria.kujeapp.models.CommentDataModel;
import com.palria.kujeapp.models.NotesDataModel;
import com.palria.kujeapp.models.ProductOrderDataModel;
import com.palria.kujeapp.widgets.BottomSheetFormBuilderWidget;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.ViewHolder> {

    ArrayList<UpdateDataModel> updateDataModels;
    Context context;
    ArrayList<UpdateDataModel> updateDataModelBackupArrayList = new ArrayList<>();

    public UpdateAdapter(ArrayList<UpdateDataModel> updateDataModels, Context context) {
        this.updateDataModels = updateDataModels;
        this.context = context;
    }

    @NonNull
    @Override
    public UpdateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.update_item_layout, parent, false);
        UpdateAdapter.ViewHolder viewHolder = new UpdateAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateAdapter.ViewHolder holder, int position) {
        UpdateDataModel updateDataModel = updateDataModels.get(position);


//        if (!productDataModelBackupArrayList.contains(productDataModel)) {


            holder.title.setText(updateDataModel.getTitle());
//        holder.datePosted.setText(productDataModel.getDatePosted());
//        holder.description.setText(productDataModel.getProductDescription());
            holder.description.setText(updateDataModel.getDescription());
            holder.viewCountTextView.setText(""+updateDataModel.getNumOfViews());
            holder.datePosted.setText(""+ updateDataModel.getDatePosted());


        GlobalValue.getFirebaseFirestoreInstance()
                .collection(GlobalValue.ALL_USERS)
                .document(updateDataModel.getAuthorId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String userProfilePhotoDownloadUrl = "" + documentSnapshot.get(GlobalValue.USER_PROFILE_PHOTO_DOWNLOAD_URL);
                        try {
                            Glide.with(context)
                                    .load(userProfilePhotoDownloadUrl)
                                    .centerCrop()
                                    .placeholder(R.drawable.default_profile)
                                    .into(holder.posterProfilePhoto);
                        } catch (Exception ignored) {
                        }

                        String userDisplayName = "" + documentSnapshot.get(GlobalValue.USER_DISPLAY_NAME);
                        holder.posterTextView.setText(userDisplayName);


                        boolean isVerified = documentSnapshot.get(GlobalValue.IS_ACCOUNT_VERIFIED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_ACCOUNT_VERIFIED) : false;
                        if (isVerified) {
                            holder.verificationFlagImageView.setVisibility(View.VISIBLE);
                        } else {
                            holder.verificationFlagImageView.setVisibility(View.INVISIBLE);

                        }
                    }
                });

        holder.posterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(GlobalValue.getHostActivityIntent(context,null,GlobalValue.USER_PROFILE_FRAGMENT_TYPE, updateDataModel.getAuthorId()));

            }
        });
        holder.posterProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(GlobalValue.getHostActivityIntent(context,null,GlobalValue.USER_PROFILE_FRAGMENT_TYPE,updateDataModel.getAuthorId()));

            }
        });

            if(updateDataModel.getImageUrlList().size() == 0){
                holder.icon.setVisibility(View.GONE);
            }else {
                Picasso.get()
                        .load(updateDataModel.getImageUrlList().get(0))
                        .placeholder(R.drawable.agg_logo)
                        .into(holder.icon);
            }
        holder.likeCountTextView.setText(updateDataModel.getTotalLikes()+"");

//        if(updateDataModel.getTotalComments()>0) {
//            if (updateDataModel.getTotalComments() == 1) {
//                holder.viewCommentsTextView.setText("See " + updateDataModel.getTotalComments() + " Comments");
//            } else {
//                holder.viewCommentsTextView.setText("See " + updateDataModel.getTotalComments() + " Comments");
//            }
//        }
        holder.commentCountTextView.setText(updateDataModel.getTotalComments()+"");
/*
            DocumentReference likedDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentUserId()).collection(GlobalValue.LIKED_PAGES).document(updateDataModel.getPageId());
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

        if(GlobalValue.isUpdateLiked(context,updateDataModel.getUpdateId())){
            holder.likeActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_thumb_up_24,context.getTheme()));
        }
        else{
            holder.likeActionButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
        }

        holder.likeActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currentLikesCount = Integer.parseInt((holder.likeCountTextView.getText()+""));

                holder.likeActionButton.setEnabled(false);
                DocumentReference likedDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentUserId()).collection(GlobalValue.LIKED_UPDATES).document(updateDataModel.getUpdateId());
                GlobalValue.checkIfDocumentExists(likedDocumentReference, new GlobalValue.OnDocumentExistStatusCallback() {
                    @Override
                    public void onExist() {
                        if(!(holder.likeCountTextView.getText()+"").equals("0")) {
                            holder.likeCountTextView.setText((currentLikesCount - 1) + "");
                        }
                        holder.likeActionButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                        GlobalValue.likeUpdate(context,updateDataModel, false, new GlobalValue.ActionCallback() {
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

                    @Override
                    public void onNotExist() {
                        holder.likeCountTextView.setText((currentLikesCount+1)+"");
                        holder.likeActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_thumb_up_24,context.getTheme()));
                        GlobalValue.likeUpdate(context,updateDataModel, true, new GlobalValue.ActionCallback() {
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

                    @Override
                    public void onFailed(@NonNull String errorMessage) {
                        holder.likeActionButton.setEnabled(true);

                    }
                });



            }
        });

        holder.commentActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentForm(holder,updateDataModel);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SingleUpdateActivity.class);
                    intent.putExtra(GlobalValue.UPDATE_ID, updateDataModel.getUpdateId());
                    intent.putExtra(GlobalValue.AUTHOR_ID, updateDataModel.getAuthorId());
                    intent.putExtra(GlobalValue.UPDATE_DATA_MODEL, updateDataModel);
                    intent.putExtra(GlobalValue.IS_EDITION, false);
                    context.startActivity(intent);

                }
            });

            if(GlobalValue.isBusinessOwner()){
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


                        GlobalValue.createPopUpMenu(context, R.menu.update_menu, holder.itemView, new GlobalValue.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClicked(MenuItem item) {
                                if(item.getItemId() == R.id.deleteId){
                                    GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_UPDATES).document(updateDataModel.getUpdateId()).delete();
                                    int positionDeleted = updateDataModels.indexOf(updateDataModel);
                                    updateDataModels.remove(updateDataModel);
                                    notifyItemChanged(positionDeleted);

                                    for(int i=0; i<updateDataModel.getImageUrlList().size(); i++){
                                        FirebaseStorage.getInstance().getReferenceFromUrl(updateDataModel.getImageUrlList().get(i)).delete();
                                    }

                                }
                                else  if(item.getItemId() == R.id.editId){
//not yet implemented for edition
                                    Intent intent = new Intent(context, PostNewUpdateActivity.class);
                                    intent.putExtra(GlobalValue.UPDATE_ID, updateDataModel.getUpdateId());
                                    intent.putExtra(GlobalValue.UPDATE_DATA_MODEL, updateDataModel);
                                    intent.putExtra(GlobalValue.IS_EDITION, true);
                                    context.startActivity(intent);
                                }


                                return true;
                            }
                        });

                        return false;
                    }
                });

            }



//        else {
//            Toast.makeText(context, "Already bound!", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public int getItemCount() {
        return updateDataModels.size();
    }

    void showCommentForm(ViewHolder holder,UpdateDataModel updateDataModel){
        BottomSheetFormBuilderWidget bottomSheetFormBuilderWidget =new BottomSheetFormBuilderWidget(context)
                .setTitle("Join in comment of this lesson by contributing your idea")
                .setPositiveTitle("Discuss");
        bottomSheetFormBuilderWidget.addInputField(new BottomSheetFormBuilderWidget.EditTextInput(context)
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
                        if (title.trim().equals("")) {
                            //     leBottomSheetDialog.setTitle("Folder needs name, must enter name for the folder");

                            Toast.makeText(context, "Please enter your idea",Toast.LENGTH_SHORT).show();

                        } else {

                            String commentId = GlobalValue.getRandomString(80);
                            GlobalValue.createSnackBar(context,holder.commentActionButton, "Posting comment: "+body,Snackbar.LENGTH_INDEFINITE);
                            GlobalValue.comment(new CommentDataModel(commentId,GlobalValue.getCurrentUserId(),body,"",updateDataModel.getUpdateId(),updateDataModel.getAuthorId(),"",false,false,false,false,"",0L,0L,new ArrayList(),new ArrayList()),new GlobalValue.ActionCallback(){
                                @Override
                                public void onFailed(String errorMessage){
                                    Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();

                                }
                                @Override
                                public void onSuccess(){
//                                     Toast.makeText(PageActivity.this, body, Toast.LENGTH_SHORT).show();
                                    GlobalValue.createSnackBar(context,holder.commentActionButton, "Thanks comment posted: "+body,Snackbar.LENGTH_SHORT);
                                    int currentDiscussionCount = Integer.parseInt((holder.commentCountTextView.getText()+""));
                                    holder.commentCountTextView.setText((currentDiscussionCount+1)+"");

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

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public TextView datePosted;
        public TextView description;
        public TextView viewCountTextView;
        public ImageView icon;

        public ImageView likeActionButton;
        public ImageView commentActionButton;
        public TextView likeCountTextView;
        public TextView commentCountTextView;
        public RoundedImageView posterProfilePhoto;
        public TextView posterTextView;
        public ImageView verificationFlagImageView;


        public ViewHolder(View itemView) {
            super(itemView);
            this.title =  itemView.findViewById(R.id.updateTitleTextViewId);
            this.description =  itemView.findViewById(R.id.descriptionTextViewId);
            this.viewCountTextView =  itemView.findViewById(R.id.viewCountTextViewId);
            this.datePosted =  itemView.findViewById(R.id.datePostedTextViewId);
//            this.datePosted = (TextView) itemView.findViewById(R.id.datePosted);
//            description=itemView.findViewById(R.id.description);
            icon = itemView.findViewById(R.id.updateImageViewId);

            likeActionButton=itemView.findViewById(R.id.likeActionButtonId);
            likeCountTextView=itemView.findViewById(R.id.likeCountTextViewId);

            commentActionButton=itemView.findViewById(R.id.commentActionButtonId);
            commentCountTextView=itemView.findViewById(R.id.commentCountTextViewId);

            posterProfilePhoto = itemView.findViewById(R.id.posterProfilePhotoId);
            posterTextView = itemView.findViewById(R.id.posterTextViewId);
            this.verificationFlagImageView =  itemView.findViewById(R.id.verificationFlagImageViewId);

        }
    }

}

