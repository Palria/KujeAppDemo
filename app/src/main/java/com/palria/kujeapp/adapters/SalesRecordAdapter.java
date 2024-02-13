package com.palria.kujeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.palria.kujeapp.AllSalesRecordActivity;
import com.palria.kujeapp.GlobalValue;
import com.palria.kujeapp.ProductDataModel;
import com.palria.kujeapp.R;
import com.palria.kujeapp.SingleSalesRecordActivity;
import com.palria.kujeapp.models.NotesDataModel;
import com.palria.kujeapp.models.NotificationDataModel;
import com.palria.kujeapp.models.ProductOrderDataModel;
import com.palria.kujeapp.models.SalesRecordDataModel;
import com.palria.kujeapp.widgets.BottomSheetFormBuilderWidget;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class SalesRecordAdapter extends RecyclerView.Adapter<SalesRecordAdapter.ViewHolder> {

    ArrayList<SalesRecordDataModel> salesRecordDataModelArrayList;
    Context context;
View dummyView;

    public SalesRecordAdapter( Context context, ArrayList<SalesRecordDataModel> salesRecordDataModelArrayList,View dummyView) {
        this.salesRecordDataModelArrayList = salesRecordDataModelArrayList;
        this.context = context;
        this.dummyView = dummyView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.all_record_item_view_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SalesRecordAdapter.ViewHolder holder, int position) {
        SalesRecordDataModel recordDataModel = salesRecordDataModelArrayList.get(position);

        holder.recordTitleTextView.setText(recordDataModel.getTitle());
        holder.dateAddedTextView.setText(recordDataModel.getDateAdded());
        holder.moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalValue.createPopUpMenu(context, R.menu.action_menu, holder.itemView, new GlobalValue.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClicked(MenuItem item) {
                        if(item.getItemId() == R.id.deleteId){
                            GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_SALES_RECORD).document(recordDataModel.getRecordId()).delete();
                            int positionDeleted = salesRecordDataModelArrayList.indexOf(recordDataModel);
                            salesRecordDataModelArrayList.remove(recordDataModel);
                            notifyItemChanged(positionDeleted);

                        }
                        else if(item.getItemId() == R.id.editId){

                            BottomSheetFormBuilderWidget bottomSheetFormBuilderWidget =  new BottomSheetFormBuilderWidget(context);
                            bottomSheetFormBuilderWidget.setTitle("Organize your records in different file records")
                                    .setPositiveTitle("Edit");
                            BottomSheetFormBuilderWidget.EditTextInput titleEditTextInput = new BottomSheetFormBuilderWidget.EditTextInput(context);
                            titleEditTextInput.setHint("Enter title")
                                    .autoFocus()
                                    .setText(recordDataModel.getTitle());
                            bottomSheetFormBuilderWidget.addInputField(titleEditTextInput);

                            BottomSheetFormBuilderWidget.EditTextInput captionEditTextInput = new BottomSheetFormBuilderWidget.EditTextInput(context);
                            captionEditTextInput.setHint("Enter caption");
                            captionEditTextInput.setText(recordDataModel.getCaption());
                            bottomSheetFormBuilderWidget.addInputField(captionEditTextInput);


                            bottomSheetFormBuilderWidget.setOnSubmit(new BottomSheetFormBuilderWidget.OnSubmitHandler() {
                                        @Override
                                        public void onSubmit(String title, String body) {
                                            super.onSubmit(title, body);
                                            if (!(title == null || title.isEmpty())) {
                                                Toast.makeText(context, title + body, Toast.LENGTH_SHORT).show();
                                                //values will be returned as array of strings as per input list position
                                                //eg first added input has first value


                                                GlobalValue.createSalesRecord(recordDataModel.getRecordId(),title, body,false, new GlobalValue.ActionCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        GlobalValue.createSnackBar(context, dummyView, "Record Created successfully", Snackbar.LENGTH_SHORT);

                                                    }

                                                    @Override
                                                    public void onFailed(String errorMessage) {
                                                        GlobalValue.createSnackBar(context, dummyView, "Record failed to create with error: " + errorMessage, Snackbar.LENGTH_INDEFINITE);
                                                    }
                                                });
                                                int position = salesRecordDataModelArrayList.indexOf(recordDataModel);

                                                salesRecordDataModelArrayList.remove(recordDataModel);
                                                notifyItemChanged(position);

                                                salesRecordDataModelArrayList.add(position,new SalesRecordDataModel(recordDataModel.getRecordId(), title,body, recordDataModel.getDateAdded()));
                                                notifyItemChanged(position);

                                                bottomSheetFormBuilderWidget.hide();

//                                           leBottomSheetDialog.setTitle("Creating "+values[0]+" folder in progress...");
//                                           leBottomSheetDialog.hide();

                                                //create folder process here
                                            }else{
                                                GlobalValue.createSnackBar(context,dummyView,"Record title is needed", Snackbar.LENGTH_SHORT);

                                            }
                                        }
                                    })
                                    .render("Save changes")
                                    .show();
                        }
                        return true;
                    }
                });
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleSalesRecordActivity.class);
                intent.putExtra(GlobalValue.RECORD_ID,recordDataModel.getRecordId());
                intent.putExtra(GlobalValue.RECORD_TITLE,recordDataModel.getTitle());
                context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return salesRecordDataModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView recordTitleTextView;
        public TextView dateAddedTextView;
        public TextView captionTextView;
        public ImageButton moreView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.recordTitleTextView =  itemView.findViewById(R.id.recordTitleTextViewId);
            this.dateAddedTextView =  itemView.findViewById(R.id.dateAddedTextViewId);
            this.captionTextView =  itemView.findViewById(R.id.captionTextViewId);
            this.moreView =  itemView.findViewById(R.id.moreViewId);

        }
    }

}

