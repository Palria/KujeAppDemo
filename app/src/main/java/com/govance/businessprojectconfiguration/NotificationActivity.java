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
import com.govance.businessprojectconfiguration.adapters.NotificationAdapter;
import com.govance.businessprojectconfiguration.models.NotificationDataModel;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {
RecyclerView notificationRecyclerView;
NotificationAdapter notificationAdapter;
ArrayList<NotificationDataModel> notificationDataModelArrayList = new ArrayList<>();
ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initUI();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationActivity.super.onBackPressed();
            }
        });

        initRecyclerView();
        getNotifications();
    }

void initUI(){
    notificationRecyclerView = findViewById(R.id.notificationRecyclerViewId);
    backButton = findViewById(R.id.backButton);

}

void initRecyclerView(){
    notificationAdapter = new NotificationAdapter(this,notificationDataModelArrayList);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
    notificationRecyclerView.setLayoutManager(linearLayoutManager);
    notificationRecyclerView.setAdapter(notificationAdapter);
}

    void getNotifications(){
        GlobalValue.getFirebaseFirestoreInstance()
                .collection(GlobalValue.PLATFORM_NOTIFICATIONS)
                .get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String notificationId = documentSnapshot.getId();
                    String title = ""+ documentSnapshot.get(GlobalValue.NOTIFICATION_TITLE);
                    String message = ""+ documentSnapshot.get(GlobalValue.NOTIFICATION_MESSAGE);
                    ArrayList<String> notificationViewers =  documentSnapshot.get(GlobalValue.NOTIFICATION_VIEWERS_LIST)!=null? (ArrayList<String>) documentSnapshot.get(GlobalValue.NOTIFICATION_VIEWERS_LIST): new ArrayList<>();
                    String dateNotified =  documentSnapshot.get(GlobalValue.DATE_NOTIFIED_TIME_STAMP)!=null? documentSnapshot.getTimestamp(GlobalValue.DATE_NOTIFIED_TIME_STAMP).toDate()+"": "Undefined";
                    if(dateNotified.length()>10){
                        dateNotified = dateNotified.substring(0,10);
                    }
                    notificationDataModelArrayList.add(new NotificationDataModel( notificationId, title, message, dateNotified,notificationViewers));
                    notificationAdapter.notifyItemChanged(notificationDataModelArrayList.size());

                }
            }
        });
    }
}