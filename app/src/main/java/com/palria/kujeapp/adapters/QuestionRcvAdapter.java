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
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.palria.kujeapp.GlobalValue;
import com.palria.kujeapp.R;
import com.palria.kujeapp.SingleQuestionActivity;
import com.palria.kujeapp.models.QuestionDataModel;

import java.util.ArrayList;


public class QuestionRcvAdapter extends RecyclerView.Adapter<QuestionRcvAdapter.ViewHolder> {

    ArrayList<QuestionDataModel> questionDataModels;
    Context context;

    public QuestionRcvAdapter(ArrayList<QuestionDataModel> questionDataModels, Context context){
        this.questionDataModels = questionDataModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.question_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionDataModel questionDataModel = questionDataModels.get(position);

        if (questionDataModel.isPublic() || (GlobalValue.getCurrentUserId().equals(questionDataModel.getAuthorId()+""))) {

            holder.dateAskedTextView.setText(questionDataModel.getDateAsked()+"");
/*
            //avoid fetching the name of the user who asked the question to reduce cost of query
            GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS_KEY)
                    .document(questionDataModel.getAuthorId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            final String userName = ""+ documentSnapshot.get(GlobalValue.USER_DISPLAY_NAME_KEY);
                            final String userProfilePhotoDownloadUrl = ""+ documentSnapshot.get(GlobalValue.USER_PROFILE_PHOTO_DOWNLOAD_URL_KEY);

                            holder.posterNameTextView.setText(userName);
                            try {
                                Glide.with(context)
                                        .load(userProfilePhotoDownloadUrl)
                                        .centerCrop()
                                        .placeholder(R.drawable.default_profile)
                                        .into(holder.askerProfilePhoto);
                            } catch (Exception ignored) {
                            }

                                boolean isVerified = documentSnapshot.get(GlobalValue.IS_ACCOUNT_VERIFIED_KEY) != null ? documentSnapshot.getBoolean(GlobalValue.IS_ACCOUNT_VERIFIED_KEY) : false;
                                if (isVerified) {
                                    holder.verificationFlagImageView.setVisibility(View.VISIBLE);
                                } else {
                                    holder.verificationFlagImageView.setVisibility(View.INVISIBLE);

                                }
                        }
                    });
*/

            holder.questionBodyTextView.setText(questionDataModel.getQuestionBody());
            holder.ansCountTextView.setText(questionDataModel.getNumOfAnswers() + " Ans");

            holder.moreActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalValue.createPopUpMenu(context,R.menu.question_action_menu , holder.moreActionButton, new GlobalValue.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClicked(MenuItem item) {

                            if(item.getItemId() == R.id.deleteId){

                                int positionDeleted =questionDataModels.indexOf(questionDataModel);
                                questionDataModels.remove(questionDataModel);
                                notifyItemRemoved(positionDeleted);
                            }

                            return true;
                        }
                    });
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, SingleQuestionActivity.class);
                    intent.putExtra(GlobalValue.QUESTION_ID,questionDataModel.getQuestionId());
                    intent.putExtra(GlobalValue.QUESTION_DATA_MODEL,questionDataModel);
                    context.startActivity(intent);
                }
            });


        }
        else{
            holder.questionBodyTextView.setText("question is hidden");
            holder.itemView.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return questionDataModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public RoundedImageView askerProfilePhoto;
        public TextView posterNameTextView;
        public ImageView verificationFlagImageView;
        public TextView  questionBodyTextView;
        public TextView dateAskedTextView;
        public ImageButton moreActionButton;
        public TextView ansCountTextView;
        public TextView viewCountTextView;



        public ViewHolder(View itemView) {
            super(itemView);
            this.askerProfilePhoto = itemView.findViewById(R.id.askerProfilePhotoId);
            this.posterNameTextView =  itemView.findViewById(R.id.posterNameTextViewId);
            this.verificationFlagImageView = itemView.findViewById(R.id.verificationFlagImageViewId);
            questionBodyTextView=itemView.findViewById(R.id.questionBodyTextViewId);
            moreActionButton=itemView.findViewById(R.id.moreActionButtonId);
            dateAskedTextView=itemView.findViewById(R.id.dateAskedTextViewId);
            ansCountTextView=itemView.findViewById(R.id.ansCountTextViewId);
            viewCountTextView=itemView.findViewById(R.id.viewCountTextViewId);

        }
    }

}
