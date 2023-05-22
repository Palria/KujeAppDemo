package com.govance.businessprojectconfiguration.adapters;

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
//import com.govance.businessprojectconfiguration.ProductDataModel;
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
import com.govance.businessprojectconfiguration.GlobalValue;
import com.govance.businessprojectconfiguration.ProductDataModel;
import com.govance.businessprojectconfiguration.R;
import com.govance.businessprojectconfiguration.models.NotesDataModel;
import com.govance.businessprojectconfiguration.models.NotificationDataModel;
import com.govance.businessprojectconfiguration.models.ProductOrderDataModel;
import com.govance.businessprojectconfiguration.models.SalesRecordDataModel;
import com.govance.businessprojectconfiguration.widgets.BottomSheetFormBuilderWidget;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    ArrayList<NotesDataModel> notesDataModelArrayList;
    Context context;
    View dummyView;

    public NotesAdapter( Context context, ArrayList<NotesDataModel> notesDataModelArrayList,View dummyView) {
        this.notesDataModelArrayList = notesDataModelArrayList;
        this.context = context;
        this.dummyView = dummyView;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.note_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        NotesDataModel notesDataModel = notesDataModelArrayList.get(position);

        holder.noteTitleTextView.setText(notesDataModel.getTitle());
        holder.dateAddedTextView.setText(notesDataModel.getDateAdded());
        holder.noteBodyTextView.setText(notesDataModel.getBody());

        holder.moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalValue.createPopUpMenu(context, R.menu.action_menu, holder.itemView, new GlobalValue.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClicked(MenuItem item) {
                        if(item.getItemId() == R.id.deleteId){
                            GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_NOTES).document(notesDataModel.getNoteId()).delete();
                            int positionDeleted = notesDataModelArrayList.indexOf(notesDataModel);
                            notesDataModelArrayList.remove(notesDataModel);
                            notifyItemChanged(positionDeleted);

                        }
                        else if(item.getItemId() == R.id.editId){

                            BottomSheetFormBuilderWidget bottomSheetFormBuilderWidget =  new BottomSheetFormBuilderWidget(context);
                            bottomSheetFormBuilderWidget.setTitle("Edit note")
                                    .setPositiveTitle("Edit");
                            BottomSheetFormBuilderWidget.EditTextInput titleEditTextInput = new BottomSheetFormBuilderWidget.EditTextInput(context);
                            titleEditTextInput.setHint("Enter title")
                                    .autoFocus()
                                    .setText(notesDataModel.getTitle());
                            bottomSheetFormBuilderWidget.addInputField(titleEditTextInput);

                            BottomSheetFormBuilderWidget.EditTextInput captionEditTextInput = new BottomSheetFormBuilderWidget.EditTextInput(context);
                            captionEditTextInput.setHint("Enter body");
                            captionEditTextInput.setText(notesDataModel.getBody());
                            bottomSheetFormBuilderWidget.addInputField(captionEditTextInput);


                            bottomSheetFormBuilderWidget.setOnSubmit(new BottomSheetFormBuilderWidget.OnSubmitHandler() {
                                @Override
                                public void onSubmit(String title, String body) {
                                    super.onSubmit(title, body);
                                    if (!(title == null || title.isEmpty())) {
//                                        Toast.makeText(context, title + body, Toast.LENGTH_SHORT).show();
                                        //values will be returned as array of strings as per input list position
                                        //eg first added input has first value


                                        GlobalValue.createNote(notesDataModel.getNoteId(),title, body,false, new GlobalValue.ActionCallback() {
                                            @Override
                                            public void onSuccess() {
                                                GlobalValue.createSnackBar(context, dummyView, "Record Created successfully", Snackbar.LENGTH_SHORT);

                                            }

                                            @Override
                                            public void onFailed(String errorMessage) {
                                                GlobalValue.createSnackBar(context, dummyView, "Record failed to create with error: " + errorMessage, Snackbar.LENGTH_INDEFINITE);
                                            }
                                        });
                                        int position = notesDataModelArrayList.indexOf(notesDataModel);

                                        notesDataModelArrayList.remove(notesDataModel);
                                        notifyItemChanged(position);

                                        notesDataModelArrayList.add(position,new NotesDataModel(notesDataModel.getNoteId(), title,body, notesDataModel.getDateAdded()));
                                        notifyItemChanged(position);

                                        bottomSheetFormBuilderWidget.hide();

//                                           leBottomSheetDialog.setTitle("Creating "+values[0]+" folder in progress...");
//                                           leBottomSheetDialog.hide();

                                        //create folder process here
                                    }else{
                                        GlobalValue.createSnackBar(context,dummyView,"Note title is needed", Snackbar.LENGTH_SHORT);

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

    }


    @Override
    public int getItemCount() {
        return notesDataModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView noteTitleTextView;
        public TextView dateAddedTextView;
        public TextView noteBodyTextView;
        public ImageButton moreView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.noteTitleTextView =  itemView.findViewById(R.id.noteTitleTextViewId);
            this.dateAddedTextView =  itemView.findViewById(R.id.dateAddedTextViewId);
            this.noteBodyTextView =  itemView.findViewById(R.id.noteBodyTextViewId);
            this.moreView =  itemView.findViewById(R.id.moreViewId);

        }
    }

}

