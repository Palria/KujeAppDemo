package com.palria.kujeapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SingleSalesRecordActivity extends AppCompatActivity {
    ImageButton backButton;
    ImageButton editButton;
    TextView headerTextView;
LinearLayout containerLinearLayout;
ArrayList<Integer>changedRows = new ArrayList<>();
 long numberOfContents;
String recordTitle = "";
String recordId = "";
boolean isEditing = false;
LinearLayout actionLinearLayout;
Button cancelButton;
Button saveButton;
ImageButton addRowImageButton;
ImageButton removeRowImageButton;
    AlertDialog confirmSaveDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_sales_record);

        initUI();
        fetchIntentData();
        headerTextView.setText(recordTitle);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingleSalesRecordActivity.super.onBackPressed();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEditing){
                    setNotEditing();
                }else{
                    setIsEditing();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingleSalesRecordActivity.super.onBackPressed();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                recordChanges();
                confirmSaveDialog.show();
            }
        });
        addRowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRow(false,(containerLinearLayout.getChildCount()-1),"","","","","","");
            }
        });
        removeRowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(containerLinearLayout.getChildCount()-1);
            }
        });
        addRow(false,0,"","","","","","");
            getRecords();
            initConfirmOrderDialog(false,"");

            setNotEditing();
    }

    void initUI(){
        backButton = findViewById(R.id.backButton);
        editButton = findViewById(R.id.editButton);
        headerTextView = findViewById(R.id.textHeaderTextViewId);
        containerLinearLayout = findViewById(R.id.containerLinearLayoutId);
        actionLinearLayout = findViewById(R.id.actionLinearLayoutId);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);
        addRowImageButton = findViewById(R.id.addRowImageButtonId);
        removeRowImageButton = findViewById(R.id.removeRowImageButtonId);

    }
void fetchIntentData(){
        Intent intent = getIntent();
        recordTitle = intent.getStringExtra(GlobalValue.RECORD_TITLE);
        recordId = intent.getStringExtra(GlobalValue.RECORD_ID);


}
    public void initConfirmOrderDialog(boolean isRetry, String errorMessage){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        String button = "Confirm";
        String message = "Confirm to save your record";
        if(isRetry){
            button = "Retry";
            message = "Your Record Failed,"+errorMessage+" Please try again";
        }

        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
//                        toggleProgress(true);
//                        uploadInBackground();
//                        postActionButton.setEnabled(false);

                        recordChanges();
                    }
                });
            }
        }).setNeutralButton("Not yet",null);
        confirmSaveDialog =builder.create();
        if(isRetry){
            confirmSaveDialog.show();
        }


    }


private void getRecords() {
    GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_SALES_RECORD)
            .document(recordId)
            .get()
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    initConfirmOrderDialog(true,e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            long totalNumberOfContents = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_RECORD_CONTENTS) != null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_RECORD_CONTENTS) : 0L;
           int i=0;
            while (i < totalNumberOfContents) {
                ArrayList<String> cellContents = documentSnapshot.get(GlobalValue.CELL_RECORD_CONTENT_LIST_ + i) != null ? (ArrayList<String>) documentSnapshot.get(GlobalValue.CELL_RECORD_CONTENT_LIST_ + i) : new ArrayList<>();
                String cell1 = "";
                String cell2 = "";
                String cell3 = "";
                String cell4 = "";
                String cell5 = "";
                String cell6 = "";
                if (cellContents.size() >= 6) {
                    cell1 = cellContents.get(0);
                    cell2 = cellContents.get(1);
                    cell3 = cellContents.get(2);
                    cell4 = cellContents.get(3);
                    cell5 = cellContents.get(4);
                    cell6 = cellContents.get(5);
                    addRow(true, containerLinearLayout.getChildCount(), cell1, cell2, cell3, cell4, cell5, cell6);

                }

                i++;
            }

            if(i>0) {
                setNotEditing();
            }else{
                setIsEditing();

            }

        }
    });

}

private void recordChanges(){
        if(containerLinearLayout.getChildCount()>0) {
        HashMap<String, Object> contents = new HashMap<>();
        for (int i = 0; i < containerLinearLayout.getChildCount(); i++) {
            LinearLayout changedView = (LinearLayout) containerLinearLayout.getChildAt(i);
            EditText cell1 = changedView.findViewById(R.id.cellEditText_1Id);
            EditText cell2 = changedView.findViewById(R.id.cellEditText_2Id);
            EditText cell3 = changedView.findViewById(R.id.cellEditText_3Id);
            EditText cell4 = changedView.findViewById(R.id.cellEditText_4Id);
            EditText cell5 = changedView.findViewById(R.id.cellEditText_5Id);
            EditText cell6 = changedView.findViewById(R.id.cellEditText_6Id);
            ArrayList<String> cellContents = new ArrayList<>();
            cellContents.add(0, cell1.getText().toString());
            cellContents.add(1, cell2.getText().toString());
            cellContents.add(2, cell3.getText().toString());
            cellContents.add(3, cell4.getText().toString());
            cellContents.add(4, cell5.getText().toString());
            cellContents.add(5, cell6.getText().toString());
            contents.put(GlobalValue.CELL_RECORD_CONTENT_LIST_ + i, cellContents);
        }
        contents.put(GlobalValue.TOTAL_NUMBER_OF_RECORD_CONTENTS, numberOfContents);

        GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_SALES_RECORD)
                .document(recordId)
                .update(contents)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        GlobalValue.createSnackBar(SingleSalesRecordActivity.this, backButton, "Recorded " + numberOfContents + " ROWS", Snackbar.LENGTH_INDEFINITE);
                    }
                });
    }
        else{
            GlobalValue.createSnackBar(SingleSalesRecordActivity.this, backButton, "Record is 0, please record at least 1 item", Snackbar.LENGTH_SHORT);

        }}

@SuppressLint("SetTextI18n")
void addRow(boolean isCopy, int position, String cell1, String cell2, String cell3, String cell4, String cell5, String cell6) {

    LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.record_row_layout, containerLinearLayout, false);

    if(position>=0){
        containerLinearLayout.addView(linearLayout, position);
}else{
        containerLinearLayout.addView(linearLayout);

    }

//        changedRows.add(containerLinearLayout.getChildCount());

   ImageButton addRowImageButton = linearLayout.findViewById(R.id.addRowImageButtonId);
    ImageButton removeRowImageButton = linearLayout.findViewById(R.id.removeRowImageButtonId);

    TextView sNumber = linearLayout.findViewById(R.id.sNumberId);
    EditText cell_1_EditText = linearLayout.findViewById(R.id.cellEditText_1Id);
    EditText cell_2_EditText  = linearLayout.findViewById(R.id.cellEditText_2Id);
    EditText cell_3_EditText  = linearLayout.findViewById(R.id.cellEditText_3Id);
    EditText cell_4_EditText  = linearLayout.findViewById(R.id.cellEditText_4Id);
    EditText cell_5_EditText  = linearLayout.findViewById(R.id.cellEditText_5Id);
    EditText cell_6_EditText  = linearLayout.findViewById(R.id.cellEditText_6Id);
    cell_6_EditText.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(SingleSalesRecordActivity.this,new DatePickerDialog.OnDateSetListener(){
                    @Override
                            public void onDateSet(DatePicker view, int year, int month,int dayOfMonth){
                        month++;
                        String date = dayOfMonth+"/"+month+"/"+year;
                        cell_6_EditText.setText(date);
            }
            },year,month,day);
            datePickerDialog.setCancelable(true);
            datePickerDialog.show();
        }
    });
    addRowImageButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            addRow(true,position+1,cell_1_EditText.getText().toString(),cell_2_EditText.getText().toString(),cell_3_EditText.getText().toString(),cell_4_EditText.getText().toString(),cell_5_EditText.getText().toString(),cell_6_EditText.getText().toString());
        }
    });
    removeRowImageButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            removeItem(containerLinearLayout.indexOfChild(linearLayout));
        }
    });

    sNumber.setText((containerLinearLayout.indexOfChild(linearLayout)+1)+"");
    if(isCopy){
        cell_1_EditText.setText(cell1);
        cell_2_EditText.setText(cell2);
        cell_3_EditText.setText(cell3);
        cell_4_EditText.setText(cell4);
        cell_5_EditText.setText(cell5);
        cell_6_EditText.setText(cell6);

    }else{
        cell_6_EditText.setText(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"/"+(Calendar.getInstance().get(Calendar.MONTH)+1)+"/"+Calendar.getInstance().get(Calendar.YEAR));
    }

    for(int i=0; i<containerLinearLayout.getChildCount(); i++){
        LinearLayout linearLayout1 = (LinearLayout) containerLinearLayout.getChildAt(i);

        TextView sNumber1 = linearLayout1.findViewById(R.id.sNumberId);
        sNumber1.setText((containerLinearLayout.indexOfChild(linearLayout1)+1)+"");

    }
        numberOfContents++;
}

void removeItem(int position){
if(!(position>=0 && position<containerLinearLayout.getChildCount()))return;
    containerLinearLayout.removeViewAt(position);
    numberOfContents--;


    for(int i=0; i<containerLinearLayout.getChildCount(); i++){
        LinearLayout linearLayout1 = (LinearLayout) containerLinearLayout.getChildAt(i);

        TextView sNumber1 = linearLayout1.findViewById(R.id.sNumberId);
        sNumber1.setText((containerLinearLayout.indexOfChild(linearLayout1)+1)+"");

    }
}

void setIsEditing(){
    actionLinearLayout.setVisibility(View.VISIBLE);

    for(int i=0; i<containerLinearLayout.getChildCount(); i++) {
        LinearLayout linearLayout = (LinearLayout) containerLinearLayout.getChildAt(i);


        EditText cell_1_EditText = linearLayout.findViewById(R.id.cellEditText_1Id);
        EditText cell_2_EditText = linearLayout.findViewById(R.id.cellEditText_2Id);
        EditText cell_3_EditText = linearLayout.findViewById(R.id.cellEditText_3Id);
        EditText cell_4_EditText = linearLayout.findViewById(R.id.cellEditText_4Id);
        EditText cell_5_EditText = linearLayout.findViewById(R.id.cellEditText_5Id);
        EditText cell_6_EditText = linearLayout.findViewById(R.id.cellEditText_6Id);

        cell_1_EditText.setEnabled(true);;
        cell_2_EditText.setEnabled(true);;
        cell_3_EditText.setEnabled(true);;
        cell_4_EditText.setEnabled(true);;
        cell_5_EditText.setEnabled(true);;
        cell_6_EditText.setEnabled(true);

        LinearLayout actionAddLinearLayout = linearLayout.findViewById(R.id.actionAddLinearLayoutId);
        actionAddLinearLayout.setVisibility(View.VISIBLE);
    }
    containerLinearLayout.setEnabled(false);

//    containerLinearLayout.setFocusable(true);
    editButton.setBackgroundResource(R.drawable.ic_baseline_lock_24);
    isEditing = true;

}

void setNotEditing(){
    editButton.setBackgroundResource(R.drawable.ic_baseline_edit_24);
//    containerLinearLayout.setContextClickable(false);
    actionLinearLayout.setVisibility(View.GONE);
    for(int i=0; i<containerLinearLayout.getChildCount(); i++) {
        LinearLayout linearLayout = (LinearLayout) containerLinearLayout.getChildAt(i);


        EditText cell_1_EditText = linearLayout.findViewById(R.id.cellEditText_1Id);
        EditText cell_2_EditText = linearLayout.findViewById(R.id.cellEditText_2Id);
        EditText cell_3_EditText = linearLayout.findViewById(R.id.cellEditText_3Id);
        EditText cell_4_EditText = linearLayout.findViewById(R.id.cellEditText_4Id);
        EditText cell_5_EditText = linearLayout.findViewById(R.id.cellEditText_5Id);
        EditText cell_6_EditText = linearLayout.findViewById(R.id.cellEditText_6Id);

        cell_1_EditText.setEnabled(false);;
        cell_2_EditText.setEnabled(false);;
        cell_3_EditText.setEnabled(false);;
        cell_4_EditText.setEnabled(false);;
        cell_5_EditText.setEnabled(false);;
        cell_6_EditText.setEnabled(false);

        LinearLayout actionAddLinearLayout = linearLayout.findViewById(R.id.actionAddLinearLayoutId);
        actionAddLinearLayout.setVisibility(View.GONE);

    }

    containerLinearLayout.setEnabled(false);
    isEditing = false;
}
}