package com.govance.businessprojectconfiguration;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.govance.businessprojectconfiguration.adapters.NotesAdapter;
import com.govance.businessprojectconfiguration.adapters.NotificationAdapter;
import com.govance.businessprojectconfiguration.adapters.SalesRecordAdapter;
import com.govance.businessprojectconfiguration.models.NotesDataModel;
import com.govance.businessprojectconfiguration.models.NotificationDataModel;
import com.govance.businessprojectconfiguration.models.SalesRecordDataModel;
import com.govance.businessprojectconfiguration.widgets.BottomSheetFormBuilderWidget;

import java.util.ArrayList;

public class AllSalesRecordActivity extends AppCompatActivity {
    RecyclerView recordRecyclerView;
    SalesRecordAdapter recordAdapter;
    ArrayList<SalesRecordDataModel> recordDataModelArrayList = new ArrayList<>();
    ImageButton backButton;
    ImageButton addNewRecordButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sales_record);
        initUI();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllSalesRecordActivity.super.onBackPressed();
            }
        });
        addNewRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] recordId = {""};

                BottomSheetFormBuilderWidget bottomSheetFormBuilderWidget =  new BottomSheetFormBuilderWidget(AllSalesRecordActivity.this);
                bottomSheetFormBuilderWidget.setTitle("Organize your records in different file records")
                        .setPositiveTitle("Create");
                BottomSheetFormBuilderWidget.EditTextInput titleEditTextInput = new BottomSheetFormBuilderWidget.EditTextInput(AllSalesRecordActivity.this);
                titleEditTextInput.setHint("Enter title")
                        .autoFocus();
                bottomSheetFormBuilderWidget.addInputField(titleEditTextInput);

                BottomSheetFormBuilderWidget.EditTextInput captionEditTextInput = new BottomSheetFormBuilderWidget.EditTextInput(AllSalesRecordActivity.this);
                captionEditTextInput.setHint("Enter caption");
                bottomSheetFormBuilderWidget.addInputField(captionEditTextInput);

                bottomSheetFormBuilderWidget.setOnSubmit(new BottomSheetFormBuilderWidget.OnSubmitHandler() {
                            @Override
                            public void onSubmit(String title, String body) {
                                super.onSubmit(title, body);
                                if (!(title == null || title.isEmpty())) {
                                    Toast.makeText(AllSalesRecordActivity.this, title + body, Toast.LENGTH_SHORT).show();
                                    //values will be returned as array of strings as per input list position
                                    //eg first added input has first value


                                    recordId[0] = GlobalValue.createSalesRecord(null,title, body,true, new GlobalValue.ActionCallback() {
                                        @Override
                                        public void onSuccess() {
                                            GlobalValue.createSnackBar(AllSalesRecordActivity.this, addNewRecordButton, "Record Created successfully", Snackbar.LENGTH_SHORT);

                                            recordDataModelArrayList.add(new SalesRecordDataModel(recordId[0], title, body, "Now"));
                                            recordAdapter.notifyItemChanged(recordDataModelArrayList.size());

                                        }

                                        @Override
                                        public void onFailed(String errorMessage) {
                                            GlobalValue.createSnackBar(AllSalesRecordActivity.this, addNewRecordButton, "Record failed to create with error: " + errorMessage, Snackbar.LENGTH_INDEFINITE);
                                        }
                                    });
                                    bottomSheetFormBuilderWidget.hide();

//                                           leBottomSheetDialog.setTitle("Creating "+values[0]+" folder in progress...");
//                                           leBottomSheetDialog.hide();

                                    //create folder process here
                                }else{
                                    GlobalValue.createSnackBar(AllSalesRecordActivity.this,addNewRecordButton,"Record title is needed", Snackbar.LENGTH_SHORT);

                                }
                            }
                        })
                        .render("Save")
                        .show();


            }
        });

        initRecyclerView();
        getRecords();
    }

    void initUI(){
        recordRecyclerView = findViewById(R.id.recordRecyclerViewId);
        backButton = findViewById(R.id.backButton);
        addNewRecordButton = findViewById(R.id.addNewRecordButton);

    }

    void initRecyclerView(){
        recordAdapter = new SalesRecordAdapter(this,recordDataModelArrayList,backButton);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recordRecyclerView.setLayoutManager(linearLayoutManager);
        recordRecyclerView.setAdapter(recordAdapter);
    }

    void getRecords(){
        GlobalValue.getFirebaseFirestoreInstance()
                .collection(GlobalValue.PLATFORM_SALES_RECORD)
                .get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String recordId = documentSnapshot.getId();
                    String title = ""+ documentSnapshot.get(GlobalValue.RECORD_TITLE);
                    String caption = ""+ documentSnapshot.get(GlobalValue.RECORD_CAPTION);
                    String dateAdded =  documentSnapshot.get(GlobalValue.DATE_ADDED_TIME_STAMP)!=null? documentSnapshot.getTimestamp(GlobalValue.DATE_ADDED_TIME_STAMP).toDate()+"": "Undefined";
                    if(dateAdded.length()>10){
                        dateAdded = dateAdded.substring(0,10);
                    }
                    recordDataModelArrayList.add(new SalesRecordDataModel( recordId, title, caption, dateAdded));
                    recordAdapter.notifyItemChanged(recordDataModelArrayList.size());

                }
            }
        });
    }
}