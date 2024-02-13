package com.palria.kujeapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.palria.kujeapp.GlobalValue;
import com.squareup.picasso.Picasso;

public class AllMessagesFragment extends Fragment {

    LinearLayout containerLinearLayout;
    LinearLayout messageLoadingLinearLayout;
    FirebaseFirestore firebaseFirestore;
    String userId;
    boolean isFirstLoad = false;
    DocumentSnapshot lastMessageSnapshotToListenFrom;
    SwipeRefreshLayout allMessagesRefreshLayout;
    boolean isLoadingMessages = false;

    TextView messageLoadingIndicatorTextView;
    Button messageEmptyIndicatorButton;
    public AllMessagesFragment() {
        // Required empty public constructor
    }
 public AllMessagesFragment(BottomAppBar bar) {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore=FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentView =   inflater.inflate(R.layout.fragment_all_messages, container, false);
        allMessagesRefreshLayout = parentView.findViewById(R.id.allMessagesRefreshLayoutId);
        allMessagesRefreshLayout.setRefreshing(true);

        allMessagesRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //trigger refresh
                GlobalValue.getOnChatsFragmentRefreshTriggeredListener().onRefreshTriggered();
            }
        });
        containerLinearLayout = parentView.findViewById(R.id.containerLinearLayoutId);
        messageLoadingLinearLayout = parentView.findViewById(R.id.messageLoadingLinearLayoutId);
        messageLoadingIndicatorTextView = parentView.findViewById(R.id.messageLoadingIndicatorTextViewId);
        messageEmptyIndicatorButton = parentView.findViewById(R.id.messageEmptyIndicatorButtonId);
        messageEmptyIndicatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                startActivity(GlobalValue.getHostActivityIntent(getContext(),GlobalValue.FRAGMENT_OPENING_PURPOSE_SEND_MESSAGE,GlobalValue.FRAGMENT_OPENING_NAME_USERS,GlobalValue.HEADER_TEXT_CHAT_USERS));
                startActivity( GlobalValue.getHostActivityIntent(getContext(),null,GlobalValue.USERS_FRAGMENT_TYPE,null));

            }
        });
//      getChatsFromMainUsers();
//      getChatsFromPages();

//        getMessages();
        addMessageSnapshotListener();
        return parentView;

    }


    public void getChatsFromPages(){
        firebaseFirestore.collection("ALL_USERS").document(userId).collection("ALL_CHATS").document("BRIDGE").collection("ALL_PAGES_CHATTED").get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    String pageChattedId = documentSnapshot.getId();
                    String pageOwnerUserId = String.valueOf(documentSnapshot.get("PAGE_OWNER_USER_ID"));

                    firebaseFirestore.collection("ALL_PAGES").document(pageOwnerUserId).collection("PAGES").document(pageChattedId).collection("ALL_MESSAGE_SENDER_USER_ID").document(userId).get().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            String dateSent = String.valueOf(documentSnapshot.get("DATE_SENT"));
                            String latestMessage = String.valueOf(documentSnapshot.get("LATEST_MESSAGE"));
                            String noOfUnreadChats = String.valueOf(documentSnapshot.get("TOTAL_NUMBER_OF_UNREAD_CHATS"));

                            String lastMessageSenderId = String.valueOf(documentSnapshot.get("LAST_MESSAGE_SENDER_ID"));
                            boolean pageIsTheSender = true;

                            firebaseFirestore.collection("ALL_PAGES").document(pageOwnerUserId).get().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadAndInitializeEachChatView(getContext(),
                                            containerLinearLayout,
                                            "FAILED",
                                            "FAILED",
                                            dateSent,
                                            latestMessage,
                                            noOfUnreadChats,
                                            pageOwnerUserId,
                                            pageChattedId,
                                            lastMessageSenderId,
                                            null,
                                            pageIsTheSender
                                    );
                                }
                            }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String senderProfileImageDownloadUrl = String.valueOf(documentSnapshot.get("PROFILE_IMAGE_DOWNLOAD_URL"));
                                    String senderUserName = String.valueOf(documentSnapshot.get("PAGE_DISPLAY_NAME"));
                                    loadAndInitializeEachChatView(getContext(),
                                            containerLinearLayout,
                                            senderProfileImageDownloadUrl,
                                            senderUserName,
                                            dateSent,
                                            latestMessage,
                                            noOfUnreadChats,
                                            pageOwnerUserId,
                                            pageChattedId,
                                            lastMessageSenderId,
                                            null,
                                            pageIsTheSender
                                    );
                                }
                            });
                        }
                    });
                }

            }
        });
    }
    public void getChatsFromMainUsers(){

        firebaseFirestore.collection("ALL_USERS").document(userId).collection("ALL_CHATS").document("BRIDGE").collection("ALL_CHAT_DIRECTORY_IDS").get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    String chatDirectoryId = documentSnapshot.getId();
                    firebaseFirestore.collection("ALL_CHATS").document("BRIDGE").collection("ALL_MAIN_USER_CHATS").document(chatDirectoryId).get().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            String sender_1_UserId = String.valueOf(documentSnapshot.get("SENDER_1ID"));
                            String sender_2_UserId = String.valueOf(documentSnapshot.get("SENDER_2ID"));

                            String dateSent = String.valueOf(documentSnapshot.get("DATE_SENT"));
                            String latestMessage = String.valueOf(documentSnapshot.get("LATEST_MESSAGE"));
                            String lastMessageSenderId = String.valueOf(documentSnapshot.get("LATEST_MESSAGE_SENDER_ID"));
                            String noOfUnreadChats =String.valueOf(documentSnapshot.get("TOTAL_NUMBER_OF_UNREAD_CHATS"+userId));
                            final String[] senderUserId=null;
                            boolean pageIsTheSender = false;

                            if(sender_1_UserId.equalsIgnoreCase(userId)){
                                senderUserId[0] = sender_1_UserId;
                            }else{
                                senderUserId[0] = sender_2_UserId;
                            }

                            firebaseFirestore.collection("ALL_USERS").document(senderUserId[0]).get().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    loadAndInitializeEachChatView(getContext(),
                                            containerLinearLayout,
                                            "FAILED",
                                            "FAILED",
                                            dateSent,
                                            latestMessage,
                                            noOfUnreadChats,
                                            senderUserId[0],
                                            null,
                                            lastMessageSenderId,
                                            chatDirectoryId,
                                            pageIsTheSender
                                    );
                                }
                            }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String senderProfileImageDownloadUrl = String.valueOf(documentSnapshot.get("PROFILE_IMAGE_DOWNLOAD_URL"));
                                    String senderUserName = String.valueOf(documentSnapshot.get("USER_NAME"));

                                    loadAndInitializeEachChatView(getContext(),
                                            containerLinearLayout,
                                            senderProfileImageDownloadUrl,
                                            senderUserName,
                                            dateSent,
                                            latestMessage,
                                            noOfUnreadChats,
                                            senderUserId[0],
                                            null,
                                            lastMessageSenderId,
                                            chatDirectoryId,
                                            pageIsTheSender
                                    );
                                }
                            });

                        }
                    });

                }
            }
        });

    }

    private void getMessages(){

        Query messageDirectoryQuery = null;

        CollectionReference messageSenderDocumentDetailsReference = firebaseFirestore.collection("ALL_USERS").document(userId).collection("ALL_MESSAGES_DIRECTORY_ID");
        messageDirectoryQuery = messageSenderDocumentDetailsReference.orderBy("DATE_CREATED_TIME_STAMP");
        messageDirectoryQuery.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    String messageDirectoryOrder = ""+documentSnapshot.get("MESSAGE_DOCUMENT_DIRECTORY_ORDER_ID");
                    String creatorId = ""+documentSnapshot.get("CREATOR_ID");
                    String creatorIdSecond = ""+documentSnapshot.get("PARTNER_ID");

                    String[] partnerId = new String[1];
                    if(creatorId.equals(userId)){
                        partnerId[0] = creatorIdSecond ;
                    }else{
                        partnerId[0] = creatorId ;
                    }


                    firebaseFirestore.collection("ALL_MESSAGES").document(creatorId).collection("PARTNER_ID").document(creatorIdSecond).get().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            DocumentReference messageDocumentDirectory = documentSnapshot.getReference();

                            String totalNumberOfUnseenMessages = ""+ documentSnapshot.get("TOTAL_NUMBER_OF_UNSEEN_MESSAGES_"+userId);
                            String lastMessageSenderId = ""+ documentSnapshot.get("LAST_MESSAGE_SENDER_ID");

                            String dateSent = ""+ documentSnapshot.get("LAST_SENT");
                            String message  = ""+ documentSnapshot.get("LAST_MESSAGE");



                            firebaseFirestore.collection("ALL_USERS").document(partnerId[0]).collection("USER_PROFILE").document(partnerId[0]).get().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    String  partnerDisplayName = ""+ documentSnapshot.get("USER_NAME");
                                    String partnerProfileImageDownloadUrl = ""+ documentSnapshot.get("PROFILE_IMAGE_DOWNLOAD_URL");

                                    loadAndInitializeEachMessageView(getContext(),containerLinearLayout,messageDirectoryOrder,messageDocumentDirectory,partnerProfileImageDownloadUrl,partnerDisplayName, dateSent,message , totalNumberOfUnseenMessages,creatorId ,creatorIdSecond,partnerId[0],lastMessageSenderId,isFirstLoad);
                                }
                            });

//                            firebaseFirestore.collection("ALL_MESSAGES").document(creatorId).collection("PARTNER_ID").document(recipientId).collection("MESSAGES").orderBy("DATE_SENT_TIME_STAMP").limit(1).get().addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                }
//                            }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                @Override
//                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
//
//
//
//
//                                    }
//
//                                }
//                            });
                        }
                    });

                }

            }
        });
    }

    private void addMessageSnapshotListener(){

        Query messageDirectoryQuery = null;

        CollectionReference messageSenderDocumentDetailsReference = firebaseFirestore.collection("ALL_USERS").document(userId).collection("ALL_MESSAGES_DIRECTORY_ID");
        messageDirectoryQuery = messageSenderDocumentDetailsReference.orderBy("DATE_CREATED_TIME_STAMP",Query.Direction.DESCENDING).limit(100);

        if(!isLoadingMessages) {
            isLoadingMessages = true;
            messageDirectoryQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null){
                        isLoadingMessages = false;
                        allMessagesRefreshLayout.setRefreshing(false);

                        messageLoadingIndicatorTextView.setTextColor(Color.RED);
                        messageLoadingIndicatorTextView.setText("Error occurred: "+error.getMessage()+"swipe down to refresh");
                    }
                    if (value != null) {
                        if(value.size() == 0){

                            isLoadingMessages = false;
                            allMessagesRefreshLayout.setRefreshing(false);
                            messageLoadingIndicatorTextView.setTextColor(Color.GRAY);
                            messageLoadingIndicatorTextView.setText("No messages yet");
                            messageEmptyIndicatorButton.setVisibility(View.VISIBLE);
                        }else {
                            for (DocumentChange documentChange : value.getDocumentChanges()) {

                                DocumentSnapshot documentSnapshot = documentChange.getDocument();


                                String[] messageDirectoryOrder = new String[1];
                                messageDirectoryOrder[0] = "" + documentSnapshot.get("MESSAGE_DOCUMENT_DIRECTORY_ORDER_ID");

                                String orderIndex_1 = messageDirectoryOrder[0].split("_")[0];
                                String orderIndex_2 = messageDirectoryOrder[0].split("_")[1];

                                String creatorId = "" + documentSnapshot.get("CREATOR_ID");
                                String creatorIdSecond = "" + documentSnapshot.get("PARTNER_ID");

                                String[] partnerId = new String[1];
                                if (creatorId.equals(userId)) {
                                    partnerId[0] = creatorIdSecond;
                                } else {
                                    partnerId[0] = creatorId;
                                }
                                boolean isAlreadyLoaded = false;

                                if (containerLinearLayout.getChildCount() != 0) {
                                    for (int i = 0; i < containerLinearLayout.getChildCount(); i++) {
                                        TextView messageDirectoryOrderHolderTextView = containerLinearLayout.findViewById(R.id.messageDirectoryOrderHolderTextViewId);
                                        if (messageDirectoryOrder[0].equals(messageDirectoryOrderHolderTextView.getText().toString())) {
                                            isAlreadyLoaded = true;
                                        }
                                    }
                                }
                                if (!isAlreadyLoaded) {
                                    firebaseFirestore.collection("ALL_MESSAGES").document(orderIndex_1).collection("PARTNER_ID").document(orderIndex_2).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//here gets the chat details

                                            DocumentReference messageDocumentDirectory = documentSnapshot.getReference();
                                            String totalNumberOfUnseenMessages = "" + documentSnapshot.get("TOTAL_NUMBER_OF_UNSEEN_MESSAGES_" + userId);
                                            String lastMessageSenderId = "" + documentSnapshot.get("LAST_MESSAGE_SENDER_ID");

                                            String[] dateSent = new String[1];
                                            String[] message = new String[1];

                                            dateSent[0] =  documentSnapshot.get(GlobalValue.LAST_SENT_TIME_STAMP)!=null ?  documentSnapshot.getTimestamp(GlobalValue.LAST_SENT_TIME_STAMP).toDate()+""  :"Moment ago";
                                            if(dateSent[0].length()>10){
                                                dateSent[0] = dateSent[0].substring(0,10);
                                            }
                                            message[0] = "" + documentSnapshot.get("LAST_MESSAGE");

                                            if (dateSent[0].equals("null")) {
                                                dateSent[0] = "-";
                                            }

                                            if (message[0].equals("null")) {
                                                message[0] = "-";
                                            }

                                            firebaseFirestore.collection(GlobalValue.ALL_USERS).document(partnerId[0]).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    String userName  = ""+documentSnapshot.get(GlobalValue.USER_DISPLAY_NAME);
                                                    String userProfileImageDownloadUrl  = ""+documentSnapshot.get(GlobalValue.USER_COVER_PHOTO_DOWNLOAD_URL);

                                                    loadAndInitializeEachMessageView(getContext(), containerLinearLayout, messageDirectoryOrder[0], messageDocumentDirectory, userProfileImageDownloadUrl, userName, dateSent[0], message[0], totalNumberOfUnseenMessages, creatorId, creatorIdSecond, partnerId[0], lastMessageSenderId, isFirstLoad);

                                                    isLoadingMessages = false;
                                                    allMessagesRefreshLayout.setRefreshing(false);

                                                }
                                            });

                                            //                            firebaseFirestore.collection("ALL_MESSAGES").document(creatorId).collection("PARTNER_ID").document(recipientId).collection("MESSAGES").orderBy("DATE_SENT_TIME_STAMP").limit(1).get().addOnFailureListener(new OnFailureListener() {
                                            //                                @Override
                                            //                                public void onFailure(@NonNull Exception e) {
                                            //
                                            //                                }
                                            //                            }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            //                                @Override
                                            //                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            //
                                            //                                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                            //
                                            //
                                            //
                                            //
                                            //                                    }
                                            //
                                            //                                }
                                            //                            });

                                        }
                                    });
                                }else{

                                    isLoadingMessages = false;
                                    allMessagesRefreshLayout.setRefreshing(false);
                                    messageLoadingIndicatorTextView.setVisibility(View.GONE);
                                }


                            }
                        }

                        isFirstLoad = false;
                    }
                    else {
                        isLoadingMessages = false;
                        allMessagesRefreshLayout.setRefreshing(false);

                        messageLoadingIndicatorTextView.setTextColor(Color.RED);
                        messageLoadingIndicatorTextView.setText("Error occurred OR no messages yet: Swipe down to refresh");

                    }
                }
            });

        }
    }

    public void loadAndInitializeEachChatView(Context context,
                                              ViewGroup viewGroup,
                                              String senderProfileImageDownloadUrl,
                                              String senderUserName,
                                              String dateSent,
                                              String message,
                                              String noOfUnreadChats,
                                              String senderUserId,
                                              String pageSenderId,
                                              String lastMessageSenderId,
                                              String chatDirectoryId,
                                              boolean pageIsTheSender
    ) {
        if (context != null) {
            View holder = LayoutInflater.from(context).inflate(R.layout.all_messages_holder_layout, viewGroup, false);
            ImageView senderProfileImageView = holder.findViewById(R.id.senderProfileImageViewId);
            TextView senderUserNameTextView = holder.findViewById(R.id.senderUserNameTextViewId);
            TextView dateSentTextView = holder.findViewById(R.id.dateSentTextViewId);
            TextView messageTextView = holder.findViewById(R.id.messageBodyTextViewId);
            TextView noOfUnreadChatsTextView = holder.findViewById(R.id.noOfUnreadChatsTextViewId);
            TextView senderUserIdHolderTextView = holder.findViewById(R.id.senderUserIdHolderTextViewId);

            if (senderProfileImageDownloadUrl != null) {
                Picasso.get().load(senderProfileImageDownloadUrl).error(R.drawable.ic_baseline_person_24).into(senderProfileImageView);
            }
            senderUserNameTextView.setText(senderUserName);
            dateSentTextView.setText(dateSent);
            messageTextView.setText(message);
            noOfUnreadChatsTextView.setText(noOfUnreadChats);

            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
//                        if(pageIsTheSender){
//
//                        }else{
                    intent = new Intent(getContext(), ChatRoomActivity.class);
//                        intent.putExtra("CHAT_DIRECTORY_ID",chatDirectoryId);
//                        intent.putExtra("RECIPIENT_USER_ID",partnerId);
//                        intent.putExtra("RECIPIENT_DISPLAY_NAME",partnerDisplayName);
////                        }

                    startActivity(intent);

                }
            });

        }

    }





    public void loadAndInitializeEachMessageView(Context context,
                                                 ViewGroup viewGroup,
                                                 String messageDirectoryOrder,
                                                 DocumentReference messageDocumentDirectory,
                                                 String partnerProfileImageDownloadUrl,
                                                 String partnerDisplayName,
                                                 String dateSent,
                                                 String message,
                                                 String totalNumberOfUnseenMessages,
                                                 String creatorId,
                                                 String creatorIdSecond,
                                                 String partnerUserId,
                                                 String lastMessageSenderId,
                                                 boolean isFirstLoad
    ) {
        if (context != null) {
            View holder = LayoutInflater.from(context).inflate(R.layout.all_messages_holder_layout, viewGroup, false);
            ImageView senderProfileImageView = holder.findViewById(R.id.senderProfileImageViewId);

            ImageView messageStateIndicatorImageView = holder.findViewById(R.id.messageStateIndicatorImageViewId);
            ImageView messageImageVideoIndicatorImageView = holder.findViewById(R.id.messageImageVideoIndicatorImageViewId);
            TextView messageSenderIndicatorTextView = holder.findViewById(R.id.messageSenderIndicatorTextViewId);

            TextView partnerDisplayNameTextView = holder.findViewById(R.id.senderUserNameTextViewId);
            TextView dateSentTextView = holder.findViewById(R.id.dateSentTextViewId);
            TextView messageTextView = holder.findViewById(R.id.messageBodyTextViewId);
            TextView totalNumberOfUnseenMessagesTextView = holder.findViewById(R.id.noOfUnreadChatsTextViewId);
            TextView messageDirectoryOrderHolderTextView = holder.findViewById(R.id.messageDirectoryOrderHolderTextViewId);
            TextView senderUserIdHolderTextView = holder.findViewById(R.id.senderUserIdHolderTextViewId);

            isLoadingMessages = false;
            allMessagesRefreshLayout.setRefreshing(false);
            messageLoadingIndicatorTextView.setVisibility(View.GONE);

            messageStateIndicatorImageView.setVisibility(View.VISIBLE);
            boolean isAlreadyLoaded = false;
            if (containerLinearLayout.getChildCount() != 0) {
                for (int i = 0; i < containerLinearLayout.getChildCount(); i++) {
                    TextView messageDirectoryOrderHolderTextView_1 = containerLinearLayout.findViewById(R.id.messageDirectoryOrderHolderTextViewId);
                    if (messageDirectoryOrder.equals(messageDirectoryOrderHolderTextView_1.getText().toString())) {
                        isAlreadyLoaded = true;
                    }
                }
            }

            String orderIndex_1 = messageDirectoryOrder.split("_")[0];
            String orderIndex_2 = messageDirectoryOrder.split("_")[1];

            if (!isAlreadyLoaded) {
                messageDirectoryOrderHolderTextView.setText(messageDirectoryOrder);

                if (partnerProfileImageDownloadUrl != null) {
                    try {
                        Picasso.get().load(partnerProfileImageDownloadUrl).error(R.drawable.ic_baseline_person_24).into(senderProfileImageView);
                    } catch (Exception e) {
                    }
                }
                partnerDisplayNameTextView.setText(partnerDisplayName);

                dateSentTextView.setText(GlobalValue.getFormattedDate(dateSent));
                messageTextView.setText(message);
                if (lastMessageSenderId.equals(userId)) {
//                    messageTextView.setText("You : " + message);

                    //Is current user's message
                    messageSenderIndicatorTextView.setVisibility(View.VISIBLE);
                }else{
                    messageStateIndicatorImageView.setVisibility(View.GONE);

                }

                if (totalNumberOfUnseenMessages.equals("null")) {
                    totalNumberOfUnseenMessages = "0";
                }
                totalNumberOfUnseenMessagesTextView.setText(totalNumberOfUnseenMessages);

                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent intent = null;
//                        if(pageIsTheSender){
//
//                        }else{
//                            intent = new Intent(getContext(),ChatRoomActivity.class);
////                        intent.putExtra("CHAT_DIRECTORY_ID",chatDirectoryId);
//                            intent.putExtra("RECIPIENT_USER_ID",partnerUserId);
//                            intent.putExtra("RECIPIENT_DISPLAY_NAME",partnerDisplayName);
//                        }
//                        GlobalValue.setRecipientImageView(senderProfileImageView);

                      startActivity(GlobalValue.goToChatRoom(getContext(), partnerUserId, partnerDisplayName, senderProfileImageView));

//                        startActivity(intent);

                    }
                });


                firebaseFirestore.collection("ALL_MESSAGES").document(orderIndex_1).collection("PARTNER_ID").document(orderIndex_2).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {

                            String totalNumberOfUnseenMessages = "" + value.get("TOTAL_NUMBER_OF_UNSEEN_MESSAGES_" + userId);
                            if (totalNumberOfUnseenMessages.equals("null")) {
                                totalNumberOfUnseenMessages = "0";
                                totalNumberOfUnseenMessagesTextView.setVisibility(View.INVISIBLE);
                            }
                            totalNumberOfUnseenMessagesTextView.setText(totalNumberOfUnseenMessages);

//notify new message
                        }
                    }
                });

                messageDocumentDirectory.collection("MESSAGES").orderBy("DATE_SENT_TIME_STAMP", Query.Direction.DESCENDING).limit(1L).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {

                            for (DocumentChange documentChange : value.getDocumentChanges()) {

                                DocumentSnapshot documentSnapshot = documentChange.getDocument();


                                String lastMessageSenderId = "" + documentSnapshot.get("SENDER_USER_ID");


                                String dateSent =  documentSnapshot.get(GlobalValue.DATE_SENT_TIME_STAMP)!=null ?  documentSnapshot.getTimestamp(GlobalValue.DATE_SENT_TIME_STAMP).toDate()+""  :"Moments ago";
                                if(dateSent.length()>10){
                                    dateSent = dateSent.substring(0,10);
                                }
                                String message = "" + documentSnapshot.get("MESSAGE_BODY");

                                boolean[] isImageIncluded = new boolean[1];
                                isImageIncluded[0] = false;

                                if (documentSnapshot.get("IS_IMAGE_INCLUDED") != null) {
                                    isImageIncluded[0] = documentSnapshot.getBoolean("IS_IMAGE_INCLUDED");
                                }

                                boolean[] isDeliveredToCurrentUser = new boolean[1];
                                boolean[] isSeenByCurrentUser = new boolean[1];

                                isDeliveredToCurrentUser[0] = false;
                                isSeenByCurrentUser[0] = false;

                                if (documentSnapshot.get("IS_DELIVERED_"+userId) != null) {
                                    isDeliveredToCurrentUser[0] = documentSnapshot.getBoolean("IS_DELIVERED_"+userId);
                                }
                                if (documentSnapshot.get("IS_SEEN_"+userId) != null) {
                                    isSeenByCurrentUser[0] = documentSnapshot.getBoolean("IS_SEEN_"+userId);
                                }

                                boolean[] isDeliveredToRecipient = new boolean[1];
                                boolean[] isSeenByRecipient = new boolean[1];

                                isDeliveredToRecipient[0] = false;
                                isSeenByRecipient[0] = false;

                                if (documentSnapshot.get("IS_DELIVERED_"+userId) != null) {
                                    isDeliveredToRecipient[0] = documentSnapshot.getBoolean("IS_DELIVERED_"+partnerUserId);
                                }
                                if (documentSnapshot.get("IS_SEEN_"+userId) != null) {
                                    isSeenByRecipient[0] = documentSnapshot.getBoolean("IS_SEEN_"+partnerUserId);
                                }

                                messageImageVideoIndicatorImageView.setVisibility(View.INVISIBLE);
                                messageSenderIndicatorTextView.setVisibility(View.INVISIBLE);
                                messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_sending_12);
                                if(isImageIncluded[0]){
                                    messageImageVideoIndicatorImageView.setVisibility(View.VISIBLE);

                                }
                                if(!documentSnapshot.getMetadata().hasPendingWrites()){
                                    messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_sent_12);

                                }
                                if(isDeliveredToRecipient[0]){
                                    messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_delivered_12);;
                                }

                                if(isSeenByRecipient[0]){
                                    messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_seen_blue_eye_12);;
                                }

                                if(!isDeliveredToCurrentUser[0]){
                                    GlobalValue.setIsAlreadyDeliveredFlag(context,documentSnapshot.getReference(),userId);
                                }

//                    if(totalNumberOfUnseenMessages.equals("null")){
//                        totalNumberOfUnseenMessages = "0";
//                    }
//                    totalNumberOfUnseenMessagesTextView.setText(totalNumberOfUnseenMessages);i

                                if(!isSeenByCurrentUser[0]){
                                    messageTextView.setTextColor(Color.BLACK);
                                }else{
                                    messageTextView.setTextColor(Color.GRAY);

                                }
                                messageTextView.setText(message);
                                dateSentTextView.setText(GlobalValue.getFormattedDate(dateSent));
                                if (lastMessageSenderId.equals(userId)) {
//                                    messageTextView.setText("You : " + message);
                                    messageSenderIndicatorTextView.setVisibility(View.VISIBLE);
                                }else{
                                    messageSenderIndicatorTextView.setVisibility(View.INVISIBLE);

                                }

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewGroup.removeView(holder);
                                        viewGroup.addView(holder, 0);
//                                containerLinearLayout.
                                    }
                                });


                            }

                        }
                    }
                });

                if (isFirstLoad) {
                    viewGroup.addView(holder);
                } else {
                    viewGroup.addView(holder, 0);
                }

            }
        }

    }

    private void addSnapshotListener(){

        CollectionReference messageSenderDocumentDetailsReference = firebaseFirestore.collection("ALL_USERS").document(userId).collection("ALL_MESSAGES_DIRECTORY_ID");
        messageSenderDocumentDetailsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

            }
        });

    }

    void doThis(){

    }

}