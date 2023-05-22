package com.govance.businessprojectconfiguration;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.govance.businessprojectconfiguration.adapters.NotesAdapter;
import com.govance.businessprojectconfiguration.adapters.NotificationAdapter;
import com.govance.businessprojectconfiguration.models.NotesDataModel;
import com.govance.businessprojectconfiguration.models.NotificationDataModel;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {
    RecyclerView notesRecyclerView;
    NotesAdapter notesAdapter;
    ArrayList<NotesDataModel> notesDataModelArrayList = new ArrayList<>();
    ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        initUI();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotesActivity.super.onBackPressed();
            }
        });

        initRecyclerView();
        getNotes();
    }

    void initUI(){
        notesRecyclerView = findViewById(R.id.notesRecyclerViewId);
        backButton = findViewById(R.id.backButton);

    }

    void initRecyclerView(){
        notesAdapter = new NotesAdapter(this,notesDataModelArrayList,backButton);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        notesRecyclerView.setLayoutManager(linearLayoutManager);
        notesRecyclerView.setAdapter(notesAdapter);
    }

    void getNotes(){
        GlobalValue.getFirebaseFirestoreInstance()
                .collection(GlobalValue.PLATFORM_NOTES)
                .get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String noteId = documentSnapshot.getId();
                    String title = ""+ documentSnapshot.get(GlobalValue.NOTE_TITLE);
                    String body = ""+ documentSnapshot.get(GlobalValue.NOTE_BODY);
                    String dateAdded =  documentSnapshot.get(GlobalValue.DATE_ADDED_TIME_STAMP)!=null? documentSnapshot.getTimestamp(GlobalValue.DATE_ADDED_TIME_STAMP).toDate()+"": "Undefined";
                    if(dateAdded.length()>10){
                        dateAdded = dateAdded.substring(0,10);
                    }
                    notesDataModelArrayList.add(new NotesDataModel( noteId, title, body, dateAdded));
                    notesAdapter.notifyItemChanged(notesDataModelArrayList.size());

                }
            }
        });
    }
}