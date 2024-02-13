package com.palria.kujeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.palria.kujeapp.adapters.PersonalizedNotificationAdapter;
import com.palria.kujeapp.models.PersonalizedNotificationDataModel;

import java.util.ArrayList;


public class AllPersonalizedNotificationsFragment extends Fragment {

//    LinearLayout loadingLayout;
//    LinearLayout noDataFoundLayout;
RecyclerView notificationRecyclerView;
    PersonalizedNotificationAdapter notificationAdapter;
    ArrayList<PersonalizedNotificationDataModel> notificationDataModelArrayList = new ArrayList<>();

    public AllPersonalizedNotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){

             }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_all_personalized_notifications, container, false);


        initUi(view);


        initRecyclerView();
        getNotifications();

        return view;

    }

//    private void toggleContentVisibility(boolean show){
//        if(!show){
//            loadingLayout.setVisibility(View.VISIBLE);
//            foldersRcv.setVisibility(View.GONE);
//        }else{
//            loadingLayout.setVisibility(View.GONE);
//            foldersRcv.setVisibility(View.VISIBLE);
//        }
//    }

    private void initUi(View view) {
//        noDataFoundLayout=view.findViewById(R.id.noDataFound);
//        loadingLayout=view.findViewById(R.id.loadingLayout);
//        toggleContentVisibility(false);

        notificationRecyclerView = view.findViewById(R.id.notificationRecyclerViewId);


    }





    void initRecyclerView(){
        notificationAdapter = new PersonalizedNotificationAdapter(getContext(),notificationDataModelArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        notificationRecyclerView.setLayoutManager(linearLayoutManager);
        notificationRecyclerView.setAdapter(notificationAdapter);
    }

    void getNotifications(){
        GlobalValue.getFirebaseFirestoreInstance()
                .collection(GlobalValue.ALL_USERS)
                .document(GlobalValue.getCurrentUserId())
                .collection(GlobalValue.PERSONALIZED_NOTIFICATIONS)
                .get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String notificationId = documentSnapshot.getId();
                    String type = ""+ documentSnapshot.get(GlobalValue.NOTIFICATION_TYPE);
                    String senderId = ""+ documentSnapshot.get(GlobalValue.NOTIFICATION_SENDER_ID);
                    String title = ""+ documentSnapshot.get(GlobalValue.NOTIFICATION_TITLE);
                    String message = ""+ documentSnapshot.get(GlobalValue.NOTIFICATION_MESSAGE);
                    ArrayList<String> notification_model_info_list =  documentSnapshot.get(GlobalValue.NOTIFICATION_MODEL_INFO_LIST)!=null? (ArrayList<String>) documentSnapshot.get(GlobalValue.NOTIFICATION_MODEL_INFO_LIST): new ArrayList<>();
                    boolean isSeen =  documentSnapshot.get(GlobalValue.IS_SEEN)!=null? documentSnapshot.getBoolean(GlobalValue.IS_SEEN): false;
                    String dateNotified =  documentSnapshot.get(GlobalValue.DATE_NOTIFIED_TIME_STAMP)!=null? documentSnapshot.getTimestamp(GlobalValue.DATE_NOTIFIED_TIME_STAMP).toDate()+"": "Undefined";
                    if(dateNotified.length()>10){
                        dateNotified = dateNotified.substring(0,10);
                    }
                    notificationDataModelArrayList.add(new PersonalizedNotificationDataModel( notificationId,type,senderId, title, message, dateNotified,notification_model_info_list,isSeen));
                    notificationAdapter.notifyItemChanged(notificationDataModelArrayList.size());

                }
            }
        });
    }


}