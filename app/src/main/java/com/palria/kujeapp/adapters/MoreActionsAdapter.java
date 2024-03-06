package com.palria.kujeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.palria.kujeapp.ProductDataModel;
//import com.makeramen.roundedimageview.RoundedImageView;
//import com.palria.learnera.GlobalConfig;
//import com.palria.learnera.LibraryActivity;
//import com.palria.learnera.PageActivity;
//import com.palria.learnera.R;
//import com.palria.learnera.TutorialActivity;
//import com.palria.learnera.TutorialFolderActivity;
//import com.palria.learnera.models.BookmarkDataModel;
//import com.palria.learnera.models.FolderDataModel;
//import com.palria.learnera.models.LibraryDataModel;
//import com.palria.learnera.models.TutorialDataModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.palria.kujeapp.AddNewServiceActivity;
import com.palria.kujeapp.AllSalesRecordActivity;
import com.palria.kujeapp.CreateNewNotificationActivity;
import com.palria.kujeapp.GlobalValue;
import com.palria.kujeapp.HostActivity;
import com.palria.kujeapp.MainActivity;
import com.palria.kujeapp.MakeNewInquiryActivity;
import com.palria.kujeapp.NotesActivity;
import com.palria.kujeapp.NotificationActivity;
import com.palria.kujeapp.PostNewAdvertActivity;
import com.palria.kujeapp.PostNewJobActivity;
import com.palria.kujeapp.PostNewProductActivity;
import com.palria.kujeapp.PostNewUpdateActivity;
import com.palria.kujeapp.ProductDataModel;
import com.palria.kujeapp.R;
import com.palria.kujeapp.SendFeedbackActivity;
import com.palria.kujeapp.models.MoreActionsModel;
import com.palria.kujeapp.models.NotesDataModel;
import com.palria.kujeapp.models.NotificationDataModel;
import com.palria.kujeapp.models.ProductOrderDataModel;
import com.palria.kujeapp.widgets.BottomSheetFormBuilderWidget;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class MoreActionsAdapter extends RecyclerView.Adapter<MoreActionsAdapter.ViewHolder> {

    ArrayList<MoreActionsModel> moreActionsModelArrayList;
    Context context;
BottomSheetDialog bottomSheetDialog;

    public MoreActionsAdapter( Context context, ArrayList<MoreActionsModel> moreActionsModelArrayList,BottomSheetDialog bottomSheetDialog) {
        this.moreActionsModelArrayList = moreActionsModelArrayList;
        this.context = context;
        this.bottomSheetDialog = bottomSheetDialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.more_actions_item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MoreActionsAdapter.ViewHolder holder, int position) {
        MoreActionsModel moreActionsModel = moreActionsModelArrayList.get(position);

        holder.actionTitleTextView.setText(moreActionsModel.getTitle());
        holder.actionIconView.setImageResource(moreActionsModel.getResInt());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.hide();
                switch(moreActionsModel.getTitle()){

                    case "ADD PRODUCT":
                        context.startActivity(new Intent(context, PostNewProductActivity.class));
                        break;
                    case "ORDERS":
                        context.startActivity( GlobalValue.getHostActivityIntent(context,null,GlobalValue.ORDER_FRAGMENT_TYPE,null));
                        break;
                    case "POST JOB":
                        context.startActivity(new Intent(context, PostNewJobActivity.class));
                        break;
                    case "NEW ADVERT":
                        context.startActivity(new Intent(context, PostNewAdvertActivity.class));
                        break;
                    case "NEW INQUIRY":
                        context.startActivity(new Intent(context, MakeNewInquiryActivity.class));
                        break;
                    case "REQUESTS":
                        context.startActivity( GlobalValue.getHostActivityIntent(context,null,GlobalValue.REQUEST_FRAGMENT_TYPE,null));
                        break;
                    case "PEOPLE":
                        context.startActivity( GlobalValue.getHostActivityIntent(context,null,GlobalValue.USERS_FRAGMENT_TYPE,null));
                        break;
                    case "CREATE NOTE":
                        BottomSheetFormBuilderWidget bottomSheetFormBuilderWidget = new BottomSheetFormBuilderWidget(context)
                                .setTitle("Note helps you remember some events")
                                .setPositiveTitle("Create");

                        BottomSheetFormBuilderWidget.EditTextInput titleEditTextInput = new BottomSheetFormBuilderWidget.EditTextInput(context);
                        titleEditTextInput.setHint("Enter title")
                                .autoFocus();
                        bottomSheetFormBuilderWidget.addInputField(titleEditTextInput);

                        BottomSheetFormBuilderWidget.EditTextInput captionEditTextInput = new BottomSheetFormBuilderWidget.EditTextInput(context);
                        captionEditTextInput.setHint("Enter body");
                        bottomSheetFormBuilderWidget.addInputField(captionEditTextInput);


                        bottomSheetFormBuilderWidget.setOnSubmit(new BottomSheetFormBuilderWidget.OnSubmitHandler() {
                                    @Override
                                    public void onSubmit(String title, String body) {
                                        super.onSubmit(title, body);

                                        Toast.makeText(context, title+body, Toast.LENGTH_SHORT).show();
                                        //values will be returned as array of strings as per input list position
                                        //eg first added input has first value


                                        GlobalValue.createNote(null,title, body,true, new GlobalValue.ActionCallback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onFailed(String errorMessage) {

                                            }
                                        });
                                        bottomSheetFormBuilderWidget.hide();
//                                           leBottomSheetDialog.setTitle("Creating "+values[0]+" folder in progress...");
//                                           leBottomSheetDialog.hide();

                                        //create folder process here
                                    }
                                })
                                .render("Save")
                                .show();

                        break;
                    case "NOTES":
                        Intent intent1 = new Intent(context, NotesActivity.class);
                        context.startActivity(intent1);
                        break;
                    case "RECORDS":
                        context.startActivity(new Intent(context, AllSalesRecordActivity.class));
                        break;
                    case "NOTIFY":
                        context.startActivity(new Intent(context, CreateNewNotificationActivity.class));
                        break;
                    case "NEW SERVICE":
                        context.startActivity(new Intent(context, AddNewServiceActivity.class));
                    case "NEW UPDATE":
                        context.startActivity(new Intent(context, PostNewUpdateActivity.class));
                    case "APPROVE ADVERTS":
                        context.startActivity( GlobalValue.getHostActivityIntent(context,null,GlobalValue.APPROVE_ADVERTS_FRAGMENT_TYPE,null));
                        break;
                }

            }
        });



    }


    @Override
    public int getItemCount() {
        return moreActionsModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView actionIconView;
        public TextView actionTitleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.actionIconView =  itemView.findViewById(R.id.actionIconViewId);
            this.actionTitleTextView =  itemView.findViewById(R.id.actionTitleTextViewId);

        }
    }
}