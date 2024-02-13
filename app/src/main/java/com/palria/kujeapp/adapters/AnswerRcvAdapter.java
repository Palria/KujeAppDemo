package com.palria.kujeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.palria.kujeapp.AllAnswersFragment;
import com.palria.kujeapp.GlobalValue;
import com.palria.kujeapp.HostActivity;
import com.palria.kujeapp.R;
import com.palria.kujeapp.models.AnswerDataModel;

import java.util.ArrayList;

public class AnswerRcvAdapter extends RecyclerView.Adapter<AnswerRcvAdapter.ViewHolder> {

    ArrayList<AnswerDataModel> answerDataModels;
    Context context;
    String authorId;

    public AnswerRcvAdapter(ArrayList<AnswerDataModel> answerDataModels, Context context, String authorId){
        this.answerDataModels = answerDataModels;
        this.context = context;
        this.authorId = authorId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.answer_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnswerDataModel answerDataModel = answerDataModels.get(position);

//        if (pageDiscussionDataModel.isHiddenByAuthor() || pageDiscussionDataModel.isHiddenByPoster() || (GlobalValue.getCurrentUserId().equals(pageDiscussionDataModel.getDiscussionPosterId()+""))) {

            holder.dateCreatedTextView.setText(answerDataModel.getDateCreated());

            GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS)
                    .document(answerDataModel.getContributorId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            final String userName = ""+ documentSnapshot.get(GlobalValue.USER_DISPLAY_NAME);
                            final String userProfilePhotoDownloadUrl = ""+ documentSnapshot.get(GlobalValue.USER_PROFILE_PHOTO_DOWNLOAD_URL);

                            holder.contributorNameTextView.setText(userName);
                            try {
                                Glide.with(context)
                                        .load(userProfilePhotoDownloadUrl)
                                        .centerCrop()
                                        .placeholder(R.drawable.placeholder)
                                        .into(holder.contributorProfilePhoto);
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

        holder.contributorNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(GlobalValue.getHostActivityIntent(context,null,GlobalValue.USER_PROFILE_FRAGMENT_TYPE,answerDataModel.getContributorId()));

            }
        });
        holder.contributorProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(GlobalValue.getHostActivityIntent(context,null,GlobalValue.USER_PROFILE_FRAGMENT_TYPE, answerDataModel.getContributorId()));

            }
        });


        holder.answerTextView.setText(answerDataModel.getAnswer());
            if(answerDataModel.isPhotoIncluded()){

                Glide.with(context)
                        .load(answerDataModel.getAnswerPhotoDownloadUrl())
                        .centerCrop()
                        .into(holder.answerPhoto);
            }
            holder.replyCountTextView.setText(answerDataModel.getTotalReplies() + "");
            if(answerDataModel.getTotalReplies()==1){
                holder.viewReplyTextView.setText("See " + answerDataModel.getTotalReplies() + " reply");
            }else {
                holder.viewReplyTextView.setText("See " + answerDataModel.getTotalReplies() + " replies");
            }

            holder.upVoteCountTextView.setText(answerDataModel.getTotalUpVotes() + "");
            holder.downVoteCountTextView.setText(answerDataModel.getTotalDownVotes() + "");
/*
            DocumentReference likedDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentUserId()).collection(GlobalValue.LIKED_DISCUSSIONS).document(pageDiscussionDataModel.getDiscussionId());
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
//            if(GlobalValue.isAnswerLiked(context,answerDataModel.getAnswerId())){
//                holder.likeActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_thumb_up_24,context.getTheme()));
//            }else{
//                holder.likeActionButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
//            }
//            holder.disLikeAccountActionButton.setText(pageDiscussionDataModel.getTotalDisLikes() + "");
            if(answerDataModel.getTotalReplies() <=0){
                holder.viewReplyTextView.setVisibility(View.GONE);
            }
            holder.moreActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalValue.createPopUpMenu(context,R.menu.answer_action_menu , holder.moreActionButton, new GlobalValue.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClicked(MenuItem item) {

                            if(item.getItemId() == R.id.deleteId){

                                GlobalValue.deleteAnswer(answerDataModel.getQuestionId(), answerDataModel.getParentId(), answerDataModel.getAnswerId(), answerDataModel.getAuthorId(), answerDataModel.isAnswer(), new GlobalValue.ActionCallback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onFailed(String errorMessage) {

                                            }
                                        });
                                int positionDeleted = answerDataModels.indexOf(answerDataModel);
                                answerDataModels.remove(answerDataModel);
                                notifyItemRemoved(positionDeleted);
                            }
                            else if(item.getItemId() == R.id.editId){

                                if(AllAnswersFragment.addAnswerListener!=null) {
                                    AllAnswersFragment.addAnswerListener.onAddAnswerTriggered(answerDataModel, answerDataModel.getParentId(),true,answerDataModel.isAnswer());
                                }
                            }

                            return true;
                        }
                    });
                }
            });
            holder.viewReplyTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context,HostActivity.class);
                    intent.putExtra(GlobalValue.IS_FROM_QUESTION_CONTEXT,false);
                    intent.putExtra(GlobalValue.IS_VIEW_ALL_ANSWERS_FOR_SINGLE_QUESTION,false);
                    intent.putExtra(GlobalValue.IS_VIEW_SINGLE_ANSWER_REPLY,true);
                    intent.putExtra(GlobalValue.AUTHOR_ID,answerDataModel.getAuthorId());
                    intent.putExtra(GlobalValue.ANSWER_ID,answerDataModel.getAnswerId());
                    intent.putExtra(GlobalValue.QUESTION_ID,answerDataModel.getQuestionId());
                    context.startActivity(GlobalValue.getHostActivityIntent(context,intent,GlobalValue.ANSWER_FRAGMENT_TYPE,""));


                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context,HostActivity.class);
                    intent.putExtra(GlobalValue.IS_FROM_QUESTION_CONTEXT,false);
                    intent.putExtra(GlobalValue.IS_VIEW_ALL_ANSWERS_FOR_SINGLE_QUESTION,false);
                    intent.putExtra(GlobalValue.IS_VIEW_SINGLE_ANSWER_REPLY,true);
                    intent.putExtra(GlobalValue.AUTHOR_ID,answerDataModel.getAuthorId());
                    intent.putExtra(GlobalValue.ANSWER_ID,answerDataModel.getAnswerId());
                    intent.putExtra(GlobalValue.QUESTION_ID,answerDataModel.getQuestionId());
                    context.startActivity(GlobalValue.getHostActivityIntent(context,intent,GlobalValue.ANSWER_FRAGMENT_TYPE,""));


                }
            });

            if(answerDataModel.getUpVotersIdList().contains(GlobalValue.getCurrentUserId())){
                holder.upVoteActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_arrow_drop_up_24_1,context.getTheme()));
                holder.downVoteActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_arrow_drop_down_24,context.getTheme()));

            }
            if(answerDataModel.getDownVotersIdList().contains(GlobalValue.getCurrentUserId())){
                holder.upVoteActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_arrow_drop_up_24,context.getTheme()));
                holder.downVoteActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_arrow_drop_down_24_1,context.getTheme()));

            }

            holder.upVoteActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.upVoteActionButton.setEnabled(false);
                    holder.downVoteActionButton.setEnabled(false);
                    GlobalValue.voteAnswer(answerDataModel, true, new GlobalValue.ActionCallback() {
                        @Override
                        public void onSuccess() {
                            holder.upVoteCountTextView.setText((answerDataModel.getTotalUpVotes()) + "");
                            holder.downVoteCountTextView.setText((answerDataModel.getTotalDownVotes()) + "");
                            holder.upVoteActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_arrow_drop_up_24_1,context.getTheme()));
                            holder.downVoteActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_arrow_drop_down_24,context.getTheme()));
                            holder.upVoteActionButton.setEnabled(true);
                            holder.downVoteActionButton.setEnabled(true);
                        }

                        @Override
                        public void onFailed(String errorMessage) {

                        }
                    });

                }
            });
            holder.downVoteActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.upVoteActionButton.setEnabled(false);
                    holder.downVoteActionButton.setEnabled(false);
                    GlobalValue.voteAnswer(answerDataModel, false, new GlobalValue.ActionCallback() {
                        @Override
                        public void onSuccess() {
                            holder.upVoteCountTextView.setText((answerDataModel.getTotalUpVotes()) + "");
                            holder.downVoteCountTextView.setText((answerDataModel.getTotalDownVotes()) + "");
                            holder.upVoteActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_arrow_drop_up_24,context.getTheme()));
                            holder.downVoteActionButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_arrow_drop_down_24_1,context.getTheme()));
                            holder.upVoteActionButton.setEnabled(true);
                            holder.downVoteActionButton.setEnabled(true);
                        }

                        @Override
                        public void onFailed(String errorMessage) {

                        }
                    });

                }
            });
            holder.replyActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showReplyForm(holder,answerDataModel);
                }
            });



//        }
//        else{
//            holder.discussionDescriptionTextView.setText("Discussion is hidden");
//            holder.itemView.setEnabled(false);
//        }
    }

    @Override
    public int getItemCount() {
        return answerDataModels.size();
    }
    void showReplyForm(ViewHolder holder, AnswerDataModel answerDataModel){

        if(AllAnswersFragment.addAnswerListener!=null) {
            AllAnswersFragment.addAnswerListener.onAddAnswerTriggered(answerDataModel, answerDataModel.getAnswerId(),false,false);
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public RoundedImageView contributorProfilePhoto;
        public TextView contributorNameTextView;
        public ImageView verificationFlagImageView;
        public TextView answerTextView;
        public TextView dateCreatedTextView;
        public ImageButton moreActionButton;
        public ImageButton upVoteActionButton;
        public ImageButton downVoteActionButton;
        public ImageView replyActionButton;
        public ImageView answerPhoto;
        public TextView upVoteCountTextView;
        public TextView downVoteCountTextView;
        public TextView replyCountTextView;
        public TextView viewReplyTextView;



        public ViewHolder(View itemView) {
            super(itemView);
            this.contributorProfilePhoto = itemView.findViewById(R.id.contributorProfilePhotoId);
            this.contributorNameTextView =  itemView.findViewById(R.id.contributorNameTextViewId);
            this.verificationFlagImageView = itemView.findViewById(R.id.verificationFlagImageViewId);
            answerTextView=itemView.findViewById(R.id.answerTextViewId);
            answerPhoto=itemView.findViewById(R.id.answerPhotoId);
            moreActionButton=itemView.findViewById(R.id.moreActionButtonId);
            dateCreatedTextView=itemView.findViewById(R.id.dateCreatedTextViewId);
            upVoteActionButton=itemView.findViewById(R.id.upVoteActionButtonId);
            downVoteActionButton=itemView.findViewById(R.id.downVoteActionButtonId);
            upVoteCountTextView=itemView.findViewById(R.id.upVoteCountTextViewId);
            downVoteCountTextView=itemView.findViewById(R.id.downVoteCountTextViewId);

            replyActionButton=itemView.findViewById(R.id.replyActionButtonId);
            replyCountTextView=itemView.findViewById(R.id.replyCountTextViewId);
            viewReplyTextView=itemView.findViewById(R.id.viewReplyTextViewId);



        }
    }

}
