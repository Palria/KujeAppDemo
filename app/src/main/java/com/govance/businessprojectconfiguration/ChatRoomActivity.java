






package com.govance.businessprojectconfiguration;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    String userId;
    String recipientUserId;
    String recipientDisplayName;
    //String messageDirectoryId;
    boolean isLoadingMessages = false;
    boolean isAllMessagesRetrieved = false;
    boolean isFirstLoad = true;
    boolean isMessageConfigInitialized = false;
    DocumentReference chatDocumentDirectory;
    ListenerRegistration listenerRegistration;
    DocumentSnapshot lastRetrievedDocumentSnapshot;
    DocumentSnapshot documentSnapshotToListenFrom;
    ListenerRegistration chatDocumentDirectoryListenerRegistration;


    ActivityResultLauncher<Intent> openGalleryLauncher;
    ActivityResultLauncher<Intent> openVideoGalleryLauncher;
    ActivityResultLauncher<Intent> openCameraLauncher;


    private final int CAMERA_PERMISSION_REQUEST_CODE = 2002;
    private final int GALLERY_PERMISSION_REQUEST_CODE = 23;
    private final int VIDEO_PERMISSION_REQUEST_CODE = 20;


    ConstraintLayout mainConstraintLayout;
    CardView topCardView;
    static ScrollView topScrollView;
    LinearLayout containerLinearLayout;
    LinearLayout composeMessageLinearLayout;
    SwipeRefreshLayout messageSwipeRefreshLayout;
    Button reloadChatActionButton;
    ImageView recipientProfileImageView;
    TextView recipientDisplayNameTextView;

    EditText chatEditText;
    ImageButton sendChatActionImageButton;
    ImageButton mediaActionImageButton;
    Button loadMoreMessagesActionButton;
    ProgressBar loadMoreMessagesProgressBar;
    private String currentSenderId;

    AlertDialog confirmMessageDeletionDialog;
    String messageIdToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //will modify this to contain page
        currentSenderId = userId;

        recipientDisplayNameTextView = findViewById(R.id.recipientDisplayNameTextViewId);
        recipientProfileImageView = findViewById(R.id.recipientProfileImageViewId);

        manageIntentData();
        confirmMessageDeletionDialog();
        recipientProfileImageView.setDrawingCacheEnabled(true);

        mainConstraintLayout = findViewById(R.id.mainConstraintLayoutId);
        topCardView = findViewById(R.id.topCardViewId);
        topScrollView = findViewById(R.id.topScrollViewId);
        containerLinearLayout = findViewById(R.id.containerLinearLayoutId);
        composeMessageLinearLayout = findViewById(R.id.composeMessageLinearLayoutId);
        messageSwipeRefreshLayout = findViewById(R.id.messageSwipeRefreshLayoutId);
        messageSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                if(!isLoadingMessages) {
//                    getMessages(lastRetrievedDocumentSnapshot);
//                }else{
//                    messageSwipeRefreshLayout.setRefreshing(false);
//                }
                if(isMessageConfigInitialized) {
                    reloadMessages();
                }
            }
        });
        reloadChatActionButton = findViewById(R.id.reloadChatActionButtonId);

        reloadChatActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ChatRoomActivity.super.onCreate(null);

                startActivity(GlobalValue.goToChatRoom(ChatRoomActivity.this,recipientUserId,recipientDisplayName,recipientProfileImageView));
                ChatRoomActivity.this.finish();
            }
        });


        chatEditText = findViewById(R.id.chatEditTextId);
//        chatEditText.requestFocus();
//        chatEditText.setText(GlobalValue.getCurrentUserId());
        mediaActionImageButton = findViewById(R.id.mediaActionImageButtonId);
        sendChatActionImageButton = findViewById(R.id.sendChatActionImageButtonId);
        loadMoreMessagesActionButton = findViewById(R.id.loadMoreMessagesActionButtonId);
        loadMoreMessagesProgressBar = findViewById(R.id.loadMoreMessagesProgressBarId);
        loadMoreMessagesActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMessages(lastRetrievedDocumentSnapshot);
                loadMoreMessagesActionButton.setVisibility(View.GONE);
                loadMoreMessagesProgressBar.setVisibility(View.VISIBLE);

            }
        });
        sendChatActionImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage(true);

            }
        });
        mediaActionImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalValue.createPopUpMenu(ChatRoomActivity.this, R.menu.message_media_menu, mediaActionImageButton, new GlobalValue.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClicked(MenuItem item) {
                        if(item.getItemId() == R.id.openImageGalleryId){

                            openGallery();
                        }
                        if(item.getItemId() == R.id.openImageCameraId){
                            openCamera();

                        }

                        return true;
                    }
                });
            }
        });
        initializeMessageConfig(new GlobalValue.ActionCallback() {
            @Override
            public void onSuccess() {
                isMessageConfigInitialized = true;
                messageSwipeRefreshLayout.setRefreshing(false);
                topCardView.setVisibility(View.VISIBLE);
                reloadChatActionButton.setVisibility(View.GONE);
                composeMessageLinearLayout.setVisibility(View.VISIBLE);
//                recipientProfileImageView.setImageBitmap(GlobalValue.getRecipientImageView().getDrawingCache());

//                getMessages(lastRetrievedDocumentSnapshot);
                checkIfTopIsReached();
                getMessages(lastRetrievedDocumentSnapshot);
//                addOnSnapshotListener();
            }

            @Override
            public void onFailed(String errorMessage) {
                messageSwipeRefreshLayout.setRefreshing(false);
                reloadChatActionButton.setText("Failed, click to reload");
                reloadChatActionButton.setBackgroundColor(Color.RED);
            }
        });
        openGalleryLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null) {
                    Intent data=result.getData();

                    displayAndConfigureSendingImage(String.valueOf(data.getData()), null);


                }


                //addProductCustomRecyclerViewAdapter.notifyDataSetChanged();

            }
        });
        openVideoGalleryLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null) {
                    Intent data=result.getData();


                }
            }
        });
        openCameraLauncher=  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK) {

//                    if(android.os.Build.VERSION.SDK_INT >= 29) {
//                        handleCameraResult();
//                    }else {
                    if (result.getData() != null) {
                        Intent data = result.getData();
                        Bitmap bitmapFromCamera =(Bitmap) data.getExtras().get("data");

//                            uriArrayList.add(uriFromCamera);
//                        if(bitmapFromCamera != null) {
                        displayAndConfigureSendingImage(String.valueOf(data.getData()), bitmapFromCamera);
//                        }

                    }
                    // }
                }else{
                    Toast.makeText(getApplicationContext(), "No image captured!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        recipientProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(GlobalValue.getHostActivityIntent(getApplicationContext(),null,GlobalValue.USER_PROFILE_FRAGMENT_TYPE,recipientUserId));

            }
        });
        recipientDisplayNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(GlobalValue.getHostActivityIntent(getApplicationContext(),null,GlobalValue.USER_PROFILE_FRAGMENT_TYPE,recipientUserId));

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(listenerRegistration != null){
            listenerRegistration.remove();
        }
        if(chatDocumentDirectoryListenerRegistration != null){
            chatDocumentDirectoryListenerRegistration.remove();
        }
//        topScrollView = null;

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
                fireCameraIntent();
            }
            if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
                fireGalleryIntent();
            }
            if (requestCode == VIDEO_PERMISSION_REQUEST_CODE) {
                fireVideoIntent();
            }
        }
    }


    private void manageIntentData(){
        Intent intent =  getIntent();
        recipientUserId = intent.getStringExtra("RECIPIENT_USER_ID");
        recipientDisplayName = intent.getStringExtra("RECIPIENT_DISPLAY_NAME");
//        recipientDisplayNameTextView.setText(recipientDisplayName);
    }

    private void getRecipientDetails(){
        firebaseFirestore.collection(GlobalValue.ALL_USERS).document(recipientUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userName  = ""+documentSnapshot.get(GlobalValue.USER_DISPLAY_NAME);
                String userProfileImageDownloadUrl  = ""+documentSnapshot.get(GlobalValue.USER_COVER_PHOTO_DOWNLOAD_URL);

                recipientDisplayNameTextView.setText(userName);
                if(userProfileImageDownloadUrl != null) {
                    Picasso.get().load(userProfileImageDownloadUrl).error(R.drawable.ic_baseline_person_24).into(recipientProfileImageView);
                }
            }
        });
    }
    private void initializeMessageConfig(GlobalValue.ActionCallback actionCallback){
        if(recipientUserId == null){
            //No need to continue chatting if the recipient is null
            ChatRoomActivity.super.onBackPressed();
        }

        getRecipientDetails();
        messageSwipeRefreshLayout.setRefreshing(true);
        DocumentReference guessDocumentReference_1 = firebaseFirestore.collection("ALL_MESSAGES").document(userId).collection("PARTNER_ID").document(recipientUserId);
        DocumentReference guessDocumentReference_2 = firebaseFirestore.collection("ALL_MESSAGES").document(recipientUserId).collection("PARTNER_ID").document(userId);

        //     DocumentReference guessDocumentReference_1 = firebaseFirestore.collection("ALL_MESSAGES").document(userId + "_" + recipientUserId);
//    DocumentReference guessDocumentReference_2 = firebaseFirestore.collection("ALL_MESSAGES").document(recipientUserId+"_"+userId);

        GlobalValue.checkIfDocumentExists(guessDocumentReference_1, new GlobalValue.OnDocumentExistStatusCallback() {
            @Override
            public void onExist() {
//                messageDirectoryId = guessDocumentReference_1.getId();
                chatDocumentDirectory = guessDocumentReference_1;
                actionCallback.onSuccess();

            }

            @Override
            public void onNotExist() {
                GlobalValue.checkIfDocumentExists(guessDocumentReference_2, new GlobalValue.OnDocumentExistStatusCallback() {
                    @Override
                    public void onExist() {
//                        messageDirectoryId = guessDocumentReference_2.getId();
                        chatDocumentDirectory = guessDocumentReference_2;

                        actionCallback.onSuccess();


                    }

                    @Override
                    public void onNotExist() {
                        GlobalValue.generateMessageDocumentDirectory(firebaseFirestore, userId, recipientUserId, new GlobalValue.OnGenerateMessageDocumentDirectoryReferenceListener() {
                            @Override
                            public void onGenerateSuccess(DocumentReference messageDocumentDirectoryReference) {
                                ChatRoomActivity.this.chatDocumentDirectory = messageDocumentDirectoryReference;
                                actionCallback.onSuccess();
                            }

                            @Override
                            public void onGenerateFailed(String errorMessage) {

                                actionCallback.onFailed("error");

                            }
                        });

                    }

                    @Override
                    public void onFailed(@NonNull String errorMessage) {
                        actionCallback.onFailed("error");

                    }
                });
            }

            @Override
            public void onFailed(@NonNull String errorMessage) {
                actionCallback.onFailed("error");

            }
        });


    }

    private void sendMessage(boolean isPlainText){
        if(isPlainText ) {
            if(!chatEditText.getText().toString().equals("")) {
                GlobalValue.openKeyboard(getApplicationContext(), chatEditText);

                int position = containerLinearLayout.getChildCount();
                String messageBody =  chatEditText.getText().toString();
                chatEditText.setText("");
                String messageId = GlobalValue.getRandomString(60);
                GlobalValue.sendPlainMessage(ChatRoomActivity.this, firebaseFirestore, chatDocumentDirectory, userId, recipientUserId,messageId, messageBody, null, null,null,null, true, false, false, new GlobalValue.ActionCallback() {
                    @Override
                    public void onSuccess() {

                        if(containerLinearLayout.getChildCount() != 0) {

//                        for (int i = 0; i < containerLinearLayout.getChildCount(); i++) {
                            View messageView = containerLinearLayout.getChildAt(position);
                            if(Integer.parseInt(String.valueOf(messageView.getTag())) == position){
                                ImageView messageStateIndicatorImageView = messageView.findViewById(R.id.messageStateIndicatorImageViewId);
                                messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_sent_12);
                            }

//                        }
                        }

                        Map<String,Object> configureSentMessageDetails = new HashMap<>();
                        configureSentMessageDetails.put("LAST_MESSAGE",messageBody);
                        configureSentMessageDetails.put("LAST_MESSAGE_ID",messageId);
                        configureSentMessageDetails.put("LAST_MESSAGE_SENDER_ID",userId);
                        configureSentMessageDetails.put("IS_DELIVERED_"+currentSenderId,true);
                        configureSentMessageDetails.put("IS_SEEN_"+currentSenderId,true);
                        configureSentMessageDetails.put("IS_DELIVERED_"+recipientUserId,false);
                        configureSentMessageDetails.put("IS_SEEN_"+recipientUserId,false);
                        configureSentMessageDetails.put("TOTAL_NUMBER_OF_MESSAGES",FieldValue.increment(1L));
//                    configureSentMessageDetails.put("TOTAL_NUMBER_OF_UNSEEN_MESSAGES_"+currentSenderId,0L);
                        configureSentMessageDetails.put("TOTAL_NUMBER_OF_UNSEEN_MESSAGES_"+recipientUserId, FieldValue.increment(1L));
                        configureSentMessageDetails.put("IS_PLAIN_TEXT_INCLUDED",true);
                        configureSentMessageDetails.put("IS_IMAGE_INCLUDED",false);
                        configureSentMessageDetails.put("IS_VIDEO_INCLUDED",false);
//                        configureSentMessageDetails.put("LAST_SENT",new TimeStamp().getDate());
                        configureSentMessageDetails.put("LAST_SENT_TIME_STAMP",FieldValue.serverTimestamp());

                        chatDocumentDirectory.set(configureSentMessageDetails, SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
                    }

                    @Override
                    public void onFailed(String errorMessage) {

                        if(containerLinearLayout.getChildCount() != 0) {

//                        for (int i = 0; i < containerLinearLayout.getChildCount(); i++) {
                            View messageView = containerLinearLayout.getChildAt(position);
                            if(Integer.parseInt(String.valueOf(messageView.getTag())) == position){
                                ImageView messageStateIndicatorImageView = messageView.findViewById(R.id.messageStateIndicatorImageViewId);
                                messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_error_12);
                            }

//                        }
                        }

                    }
                });

            }else{
                chatEditText.setHint("Enter a text");
                chatEditText.setHintTextColor(Color.RED);
//            ArrayList<String> s = GlobalValue.generateSearchKeyWords("Henry",65);
//            for(int i=0;i<s.size();i++){
//
//                chatEditText.append(" "+s.get(i));
//            }

                GlobalValue.openKeyboard(ChatRoomActivity.this,chatEditText);
            }
        }
    }



    private void getMessages(DocumentSnapshot latestDocumentSnapshot) {
        Query messageQuery = null;
        if (!isLoadingMessages) {
            if (!isAllMessagesRetrieved) {
                if (lastRetrievedDocumentSnapshot == null) {
                    messageQuery = chatDocumentDirectory.collection("MESSAGES").limit(20L).orderBy("DATE_SENT_TIME_STAMP", Query.Direction.DESCENDING);
                }
                if (lastRetrievedDocumentSnapshot != null) {

                    messageQuery = chatDocumentDirectory.collection("MESSAGES").limit(20L).orderBy("DATE_SENT_TIME_STAMP", Query.Direction.DESCENDING).startAfter(lastRetrievedDocumentSnapshot);

                }

                if (messageQuery != null) {

                    isLoadingMessages = true;
                    loadMoreMessagesActionButton.setVisibility(View.GONE);
                    loadMoreMessagesProgressBar.setVisibility(View.VISIBLE);
//                messageSwipeRefreshLayout.setRefreshing(true);

                    messageQuery.get().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isLoadingMessages = false;
                            loadMoreMessagesActionButton.setVisibility(View.VISIBLE);
                            loadMoreMessagesProgressBar.setVisibility(View.GONE);

                            messageSwipeRefreshLayout.setRefreshing(false);


                        }
                    }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                String messageId = documentSnapshot.getId();
                                String textMessage = "" + documentSnapshot.get("MESSAGE_BODY");
                                String imageDownloadUrl = "" + documentSnapshot.get("IMAGE_DOWNLOAD_URL");
                                String videoDownloadUrl = "" + documentSnapshot.get("VIDEO_DOWNLOAD_URL");
                                String imageStorageReference = "" + documentSnapshot.get("IMAGE_STORAGE_REFERENCE");
                                String videoStorageReference = "" + documentSnapshot.get("VIDEO_STORAGE_REFERENCE");
                                String senderUserId = "" + documentSnapshot.get("SENDER_USER_ID");
                                String recipientUserId = "" + documentSnapshot.get("RECIPIENT_USER_ID");
                                String[] dateSent = new String[1];
                                dateSent[0] =  documentSnapshot.get(GlobalValue.DATE_SENT_TIME_STAMP)!=null ?  documentSnapshot.getTimestamp(GlobalValue.DATE_SENT_TIME_STAMP).toDate()+""  :"Moments ago";
                                if(dateSent[0].length()>10){
                                    dateSent[0] = dateSent[0].substring(0,10);
                                }
                                boolean[] isCurrentUserTheSender = new boolean[1];
                                boolean[] isPlainTextIncluded = new boolean[1];
                                boolean[] isImageIncluded = new boolean[1];
                                boolean[] isVideoIncluded = new boolean[1];

                                boolean[] isDeliveredToRecipient = new boolean[1];
                                boolean[] isSeenByRecipient = new boolean[1];

                                boolean[] isDeliveredToCurrentUser = new boolean[1];
                                boolean[] isSeenByCurrentUser = new boolean[1];

                                isCurrentUserTheSender[0] = false;
                                isPlainTextIncluded[0] = false;
                                isImageIncluded[0] = false;
                                isVideoIncluded[0] = false;

                                isDeliveredToRecipient[0] = false;
                                isSeenByRecipient[0] = false;
                                isDeliveredToCurrentUser[0] = false;
                                isSeenByCurrentUser[0] = false;

                                String[] senderDisplayName = new String[1];
                                senderDisplayName[0] = "";
                                if (senderUserId.equals(userId)) {
                                    isCurrentUserTheSender[0] = true;
                                    senderDisplayName[0] = "me";
                                } else {
                                    senderDisplayName[0] = recipientDisplayNameTextView.getText().toString();
                                }

                                if (documentSnapshot.get("IS_PLAIN_TEXT_INCLUDED") != null) {
                                    isPlainTextIncluded[0] = documentSnapshot.getBoolean("IS_PLAIN_TEXT_INCLUDED");
                                }

                                if (documentSnapshot.get("IS_IMAGE_INCLUDED") != null) {
                                    isImageIncluded[0] = documentSnapshot.getBoolean("IS_IMAGE_INCLUDED");
                                }

                                if (documentSnapshot.get("IS_VIDEO_INCLUDED") != null) {
                                    isVideoIncluded[0] = documentSnapshot.getBoolean("IS_VIDEO_INCLUDED");
                                }

                                if (documentSnapshot.get("IS_DELIVERED_"+ChatRoomActivity.this.recipientUserId) != null) {
                                    isDeliveredToRecipient[0] = documentSnapshot.getBoolean("IS_DELIVERED_"+ChatRoomActivity.this.recipientUserId);
                                }
                                if (documentSnapshot.get("IS_SEEN_"+ChatRoomActivity.this.recipientUserId) != null) {
                                    isSeenByRecipient[0] = documentSnapshot.getBoolean("IS_SEEN_"+ChatRoomActivity.this.recipientUserId);
                                }

                                if (documentSnapshot.get("IS_DELIVERED_"+ChatRoomActivity.this.userId) != null) {
                                    isDeliveredToCurrentUser[0] = documentSnapshot.getBoolean("IS_DELIVERED_"+ChatRoomActivity.this.userId);
                                }
                                if (documentSnapshot.get("IS_SEEN_"+ChatRoomActivity.this.userId) != null) {
                                    isSeenByCurrentUser[0] = documentSnapshot.getBoolean("IS_SEEN_"+ChatRoomActivity.this.userId);
                                }
                                boolean isLoadFromCache = documentSnapshot.getMetadata().isFromCache();
                                boolean hasPendingWrite = documentSnapshot.getMetadata().hasPendingWrites();
//                            documentSnapshot.getTimestamp("");
//                            documentSnapshot.getMetadata().

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        GlobalValue.displayPlainMessage(ChatRoomActivity.this, containerLinearLayout,chatDocumentDirectory, documentSnapshot.getReference(), messageId, textMessage, imageDownloadUrl, videoDownloadUrl, ChatRoomActivity.this.currentSenderId, senderDisplayName[0], senderUserId, recipientUserId, dateSent[0], isPlainTextIncluded[0], isImageIncluded[0], false,null, isVideoIncluded[0], isCurrentUserTheSender[0], false, isLoadFromCache, hasPendingWrite, isDeliveredToRecipient[0], isSeenByRecipient[0], isDeliveredToCurrentUser[0], isSeenByCurrentUser[0], new GlobalValue.OnMessageImageUploadListener() {
                                            @Override
                                            public void onUploadSuccess(String messageId, String messageImageDownloadUrl, String messageImageStorageReference) {

                                            }

                                            @Override
                                            public void onUploadFailed(String errorMessage) {

                                            }
                                        });
                                        isLoadingMessages = false;
                                        loadMoreMessagesProgressBar.setVisibility(View.GONE);
                                        messageSwipeRefreshLayout.setRefreshing(false);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                topScrollView.fullScroll(View.FOCUS_DOWN);
                                            }
                                        });
                                    }
                                });

//                            messageSwipeRefreshLayout.setRefreshing(false);
                            }


                            if (queryDocumentSnapshots.size() != 0) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        lastRetrievedDocumentSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                        loadMoreMessagesActionButton.setVisibility(View.VISIBLE);

                                    }
                                });

                                if (queryDocumentSnapshots.size() < 20) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            isAllMessagesRetrieved = true;
                                            loadMoreMessagesActionButton.setVisibility(View.GONE);

                                        }
                                    });
                                }
                                if(isFirstLoad) {

//                                    topScrollView.scrollTo(0,containerLinearLayout.getBottom());

                                    for(int i = 0;  i <= queryDocumentSnapshots.size()-1;  i++){

                                        //checks all the documents returned and starts form the one retrieved from server
                                        if(!queryDocumentSnapshots.getDocuments().get(i).getMetadata().hasPendingWrites()){
                                            documentSnapshotToListenFrom = queryDocumentSnapshots.getDocuments().get(i);

                                            break;
                                        }

                                    }

                                    //start listening from the zero index document because the order is descending
//                                    addSnapshotListenerOnChatDocumentDirectory();

                                    //  Toast.makeText(getApplicationContext(), "ABOUT TO", Toast.LENGTH_SHORT).show();

                                    addOnSnapshotListener();
                                    topScrollView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            topScrollView.fullScroll(View.FOCUS_DOWN);
                                        }
                                    });

                                }
                            } else{
                                // querysnapshot has no item
                                if(isFirstLoad){
                                    loadMoreMessagesProgressBar.setVisibility(View.GONE);
//                                addSnapshotListenerOnChatDocumentDirectory();
                                    addOnSnapshotListener();

                                }
                            }

                            isFirstLoad = false;

//                     addOnSnapshotListener();


                        }
                    });
                }
            }
        }
    }

    private void addOnSnapshotListener() {
        Query messageQuery = null;
        if (documentSnapshotToListenFrom == null) {
            messageQuery = chatDocumentDirectory.collection("MESSAGES").orderBy("DATE_SENT_TIME_STAMP");
//         }
//  if(!isFirstLoad){
//      messageQuery = chatDocumentDirectory.collection("MESSAGES").orderBy("DATE_SENT_TIME_STAMP");
//  }
        }
        if (documentSnapshotToListenFrom != null) {
            messageQuery = chatDocumentDirectory.collection("MESSAGES").orderBy("DATE_SENT_TIME_STAMP").startAfter(documentSnapshotToListenFrom);

        }
        if(isFirstLoad){
            {

                listenerRegistration = messageQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                         Toast.makeText(getApplicationContext(), "Change detected true", Toast.LENGTH_SHORT).show();
//
//                         if(value == null){
//                             Toast.makeText(getApplicationContext(), "value is null", Toast.LENGTH_SHORT).show();
//
//                         }
//                         if(value != null){
//                             Toast.makeText(getApplicationContext(), "value is not null", Toast.LENGTH_SHORT).show();
//                             for(int i =0; i<value.size(); i++){
//                                 Toast.makeText(getApplicationContext(), ""+value.getDocuments().get(i).getString("MESSAGE_BODY"), Toast.LENGTH_SHORT).show();
//                             }
//                         }
//                         if(value != null && !value.getDocumentChanges().isEmpty()){
//                             Toast.makeText(getApplicationContext(), "value is not null and changes not found", Toast.LENGTH_SHORT).show();
//
//                         }
//                         if (error != null) {
//                             Toast.makeText(getApplicationContext(), "error occurred", Toast.LENGTH_SHORT).show();

//                    if (isFirstLoad) {
//                        loadMoreMessagesActionButton.setVisibility(View.VISIBLE);
//                    }
//                         }




//                 if (!isFirstLoad) {
                        if (value != null && !value.getDocumentChanges().isEmpty() && value.size() != 0) {
//                             Toast.makeText(getApplicationContext(), "value is null,empty,0", Toast.LENGTH_SHORT).show();

                            for (DocumentChange documentChange : value.getDocumentChanges()) {
//                                 Toast.makeText(getApplicationContext(), "value.getDocumentChanges()", Toast.LENGTH_SHORT).show();


//                                     Toast.makeText(getApplicationContext(), "value is added", Toast.LENGTH_SHORT).show();

                                DocumentSnapshot documentSnapshot = documentChange.getDocument();

                                String messageId = documentSnapshot.getId();
                                String textMessage = "" + documentSnapshot.get("MESSAGE_BODY");
                                String imageDownloadUrl = "" + documentSnapshot.get("IMAGE_DOWNLOAD_URL");
                                String videoDownloadUrl = "" + documentSnapshot.get("VIDEO_DOWNLOAD_URL");
                                String imageStorageReference = "" + documentSnapshot.get("IMAGE_STORAGE_REFERENCE");
                                String videoStorageReference = "" + documentSnapshot.get("VIDEO_STORAGE_REFERENCE");
                                String senderUserId = "" + documentSnapshot.get("SENDER_USER_ID");
                                String recipientUserId = "" + documentSnapshot.get("RECIPIENT_USER_ID");
                                String dateSent = "" + documentSnapshot.get("DATE_SENT");

                                boolean[] isCurrentUserTheSender = new boolean[1];
                                boolean[] isPlainTextIncluded = new boolean[1];
                                boolean[] isImageIncluded = new boolean[1];
                                boolean[] isVideoIncluded = new boolean[1];
                                boolean[] isDeliveredToRecipient = new boolean[1];
                                boolean[] isSeenByRecipient = new boolean[1];

                                boolean[] isDeliveredToCurrentUser = new boolean[1];
                                boolean[] isSeenByCurrentUser = new boolean[1];

                                isCurrentUserTheSender[0] = false;
                                isPlainTextIncluded[0] = false;
                                isImageIncluded[0] = false;
                                isVideoIncluded[0] = false;

                                isDeliveredToRecipient[0] = false;
                                isSeenByRecipient[0] = false;
                                isDeliveredToCurrentUser[0] = false;
                                isSeenByCurrentUser[0] = false;

                                String[] senderDisplayName = new String[1];
                                senderDisplayName[0] = "";
                                if (senderUserId.equals(userId)) {
                                    isCurrentUserTheSender[0] = true;
                                    senderDisplayName[0] = "me";
                                } else {
                                    senderDisplayName[0] = recipientDisplayNameTextView.getText().toString();
                                }

                                if (documentSnapshot.get("IS_PLAIN_TEXT_INCLUDED") != null) {
                                    isPlainTextIncluded[0] = documentSnapshot.getBoolean("IS_PLAIN_TEXT_INCLUDED");
                                }

                                if (documentSnapshot.get("IS_IMAGE_INCLUDED") != null) {
                                    isImageIncluded[0] = documentSnapshot.getBoolean("IS_IMAGE_INCLUDED");
                                }

                                if (documentSnapshot.get("IS_VIDEO_INCLUDED") != null) {
                                    isVideoIncluded[0] = documentSnapshot.getBoolean("IS_VIDEO_INCLUDED");
                                }

                                if (documentSnapshot.get("IS_DELIVERED_"+ChatRoomActivity.this.recipientUserId) != null) {
                                    isDeliveredToRecipient[0] = documentSnapshot.getBoolean("IS_DELIVERED_"+ChatRoomActivity.this.recipientUserId);
                                }
                                if (documentSnapshot.get("IS_SEEN_"+ChatRoomActivity.this.recipientUserId) != null) {
                                    isSeenByRecipient[0] = documentSnapshot.getBoolean("IS_SEEN_"+ChatRoomActivity.this.recipientUserId);
                                }

                                if (documentSnapshot.get("IS_DELIVERED_"+ChatRoomActivity.this.userId) != null) {
                                    isDeliveredToCurrentUser[0] = documentSnapshot.getBoolean("IS_DELIVERED_"+ChatRoomActivity.this.userId);
                                }
                                if (documentSnapshot.get("IS_SEEN_"+ChatRoomActivity.this.userId) != null) {
                                    isSeenByCurrentUser[0] = documentSnapshot.getBoolean("IS_SEEN_"+ChatRoomActivity.this.userId);
                                }
                                boolean isLoadFromCache = value.getMetadata().isFromCache();
                                boolean hasPendingWrite = value.getMetadata().hasPendingWrites();

//messageId

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        boolean isAdded = false;
                                        if (containerLinearLayout.getChildCount() != 0) {

                                            for (int i = 0; i < containerLinearLayout.getChildCount(); i++) {
                                                View messageView = containerLinearLayout.getChildAt(i);
                                                TextView messageIdHolderDummyTextView = messageView.findViewById(R.id.messageIdHolderDummyTextViewId);
                                                TextView textMessageTextView = messageView.findViewById(R.id.textMessageTextViewId);
                                                ImageView messageStateIndicatorImageView = messageView.findViewById(R.id.messageStateIndicatorImageViewId);

                                                if (messageIdHolderDummyTextView.getText().toString().equals(messageId)) {
//                                                         Toast.makeText(getApplicationContext(), "value is already added", Toast.LENGTH_SHORT).show();
                                                    if(documentSnapshot.exists()) {
//                                                             Toast.makeText(getApplicationContext(), "value exists" + messageId, Toast.LENGTH_SHORT).show();

                                                        if (!documentSnapshot.getMetadata().hasPendingWrites()) {
                                                            messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_sent_12);
//                                                                 Toast.makeText(getApplicationContext(), "value is  sent to recipient now" + messageId, Toast.LENGTH_SHORT).show();

                                                            if (isDeliveredToRecipient[0]) {
                                                                messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_delivered_12);
                                                                //Toast.makeText(getApplicationContext(), "value is delivered to recipient now" + messageId, Toast.LENGTH_SHORT).show();//

                                                            }
                                                            if (isSeenByRecipient[0]) {
                                                                messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_seen_blue_eye_12);
//                                                                     Toast.makeText(getApplicationContext(), "value is seen by recipient now" + messageId, Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    }
                                                    else{

                                                        messageStateIndicatorImageView.setVisibility(View.VISIBLE);
                                                        messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_error_12);
                                                        textMessageTextView.setText("DELETED");
                                                        Toast.makeText(getApplicationContext(), "value is deleted" + messageId, Toast.LENGTH_SHORT).show();

                                                    }




                                                    isAdded = true;
                                                }


                                            }
                                        }

                                        if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                            if (!isAdded) {
//                                                 Toast.makeText(getApplicationContext(), "value is later added", Toast.LENGTH_SHORT).show();

                                                GlobalValue.displayPlainMessage(ChatRoomActivity.this, containerLinearLayout,chatDocumentDirectory, documentSnapshot.getReference(), messageId, textMessage, imageDownloadUrl, videoDownloadUrl, ChatRoomActivity.this.currentSenderId, senderDisplayName[0], senderUserId, recipientUserId, dateSent, isPlainTextIncluded[0], isImageIncluded[0], false,null, isVideoIncluded[0], isCurrentUserTheSender[0], true, hasPendingWrite, isLoadFromCache, isDeliveredToRecipient[0], isSeenByRecipient[0], isDeliveredToCurrentUser[0], isSeenByCurrentUser[0], new GlobalValue.OnMessageImageUploadListener() {
                                                    @Override
                                                    public void onUploadSuccess(String messageId, String messageImageDownloadUrl, String messageImageStorageReference) {

                                                    }

                                                    @Override
                                                    public void onUploadFailed(String errorMessage) {

                                                    }
                                                });

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        topScrollView.fullScroll(View.FOCUS_DOWN);
                                                    }
                                                });

////                                                     chatEditText.requestFocus();
//                                                     if(value.getMetadata().hasPendingWrites()){
//                                                         GlobalValue.openKeyboard(getApplicationContext(), chatEditText);
//                                                     }

                                            }
                                        }


//
//                                    if (isFirstLoad) {
//                                        loadMoreMessagesActionButton.setVisibility(View.VISIBLE);
//                                    }if(if(



                                        isFirstLoad = false;
                                    }
                                });


//                            if(!value.getMetadata().hasPendingWrites()){
//                                GlobalValue.setIsAlreadyDeliveredAndReadFlag(chatDocumentDirectory,userId);
//                            }

                                if(value.getMetadata().hasPendingWrites()){

                                }
                            }
//                    if (value.size() != 0) {
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                lastRetrievedDocumentSnapshot = value.getDocuments().get(value.size() - 1);
//                                loadMoreMessagesActionButton.setVisibility(View.VISIBLE);
//
//                            }
//                        });
//
//                        if (value.size() < 20) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    isAllMessagesRetrieved = true;
//                                    loadMoreMessagesActionButton.setVisibility(View.GONE);
//
//                                }
//                            });
//                        }
//
//                    }


                        }
//                 }

                        isFirstLoad = false;


                    }
                });

            }
        }

    }

    //not currently useful
    private void addSnapshotListenerOnChatDocumentDirectory(){
        if(chatDocumentDirectory != null){
            chatDocumentDirectoryListenerRegistration = chatDocumentDirectory.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                    if (value != null && !value.getMetadata().hasPendingWrites()) {
                        boolean isDelivered = false;
                        boolean isSeen = false;
                        Toast.makeText(getApplicationContext(), "General directory detected a change", Toast.LENGTH_SHORT).show();
                        if (value.get("IS_DELIVERED_" + recipientUserId) != null) {
                            isDelivered = value.getBoolean("IS_DELIVERED_" + recipientUserId);
                        }

                        if (value.get("IS_SEEN_" + recipientUserId) != null) {
                            isSeen = value.getBoolean("IS_SEEN_" + recipientUserId);
                        }

                        configureDeliveredAndReadMessages(isDelivered, isSeen);

                    }
                }

            });
        }
    }

    private void checkIfTopIsReached(){

        topScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                topScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                        if(topScrollView.getChildAt(0).getTop() ==(topScrollView.getHeight() + topScrollView.getScrollY()) ){
                            if(!isLoadingMessages) {
//                             getMessages(lastRetrievedDocumentSnapshot);
//                             Toast.makeText(getApplicationContext(), "Top Reached : "+ topScrollView.getChildAt(0).getTop()+"  height : "+ topScrollView.getHeight() +" : scroll-y : "+topScrollView.getScrollY(), Toast.LENGTH_SHORT).show();
//                         messageSwipeRefreshLayout.setRefreshing(true);
                            }

                        }
                    }
                });
            }
        });

    }

    private void reloadMessages(){
        lastRetrievedDocumentSnapshot = null;
        containerLinearLayout.removeAllViews();
        if(listenerRegistration != null){
            listenerRegistration.remove();
        }
        isFirstLoad = true;
        isLoadingMessages = false;
        isAllMessagesRetrieved = false;
        getMessages(lastRetrievedDocumentSnapshot);
        messageSwipeRefreshLayout.setRefreshing(true);
        reloadChatActionButton.setVisibility(View.GONE);

    }




    //not useful
    @Deprecated
    private void configureDeliveredAndReadMessages(boolean isDelivered , boolean isSeen){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(containerLinearLayout.getChildCount() != 0){

                    for(int i=0; i<containerLinearLayout.getChildCount(); i++){
                        View messageView = containerLinearLayout.getChildAt(i);
                        TextView recipientIdHolderDummyTextView = messageView.findViewById(R.id.recipientIdHolderDummyTextViewId);
                        TextView senderIdHolderDummyTextView = messageView.findViewById(R.id.senderIdHolderDummyTextViewId);


                        TextView errorStatusTextView = messageView.findViewById(R.id.errorStatusTextViewId);
                        TextView sentStatusTextView = messageView.findViewById(R.id.sentStatusTextViewId);
                        TextView deliveredStatusTextView = messageView.findViewById(R.id.deliveredStatusTextViewId);
                        TextView seenStatusTextView = messageView.findViewById(R.id.seenStatusTextViewId);


                        //checks if the current user is the sender of the message
                        if(senderIdHolderDummyTextView.getText().toString().equals(userId)){

                            if(isDelivered){

                            }

                            if(isSeen){
                                errorStatusTextView.setVisibility(View.GONE);
                                sentStatusTextView.setVisibility(View.GONE);
                                deliveredStatusTextView.setVisibility(View.GONE);
                                seenStatusTextView.setVisibility(View.VISIBLE);
                            }

                        }


                        if(recipientIdHolderDummyTextView.getText().toString().equals(userId)){

                        }
                    }

                }

            }
        });

    }

    void doThis(){
        MaterialTextView materialTextView = new MaterialTextView(this);
        String string = "kj";
    }

    private void displayAndConfigureSendingImage(String imageDownloadUrl, Bitmap bitmap){
        Dialog messageImageViewDialog = new Dialog(this);
        messageImageViewDialog.setContentView(R.layout.message_sending_image_dialog_layout);
//        messageImageViewDialog.requestWindowFeature();

        ImageView messageImagePreview = messageImageViewDialog.findViewById(R.id.messageImagePreviewId);
        EditText imageCaptionMessageEditText = messageImageViewDialog.findViewById(R.id.imageCaptionMessageEditTextId);
        Button cancelSendImageActionButton = messageImageViewDialog.findViewById(R.id.cancelSendImageActionButtonId);
        Button sendImageActionButton = messageImageViewDialog.findViewById(R.id.sendImageActionButton);
        if(bitmap == null) {
            messageImagePreview.setImageURI(Uri.parse(imageDownloadUrl));
        }else{

            messageImagePreview.setImageBitmap(bitmap);
        }


        cancelSendImageActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageImageViewDialog.cancel();
            }
        });

        sendImageActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView messageImagePreviewClone = messageImagePreview;
                String textMessage = imageCaptionMessageEditText.getText().toString();
                boolean isTextIncluded = textMessage.isEmpty();

                messageImageViewDialog.cancel();
                GlobalValue.displayPlainMessage(ChatRoomActivity.this, containerLinearLayout, chatDocumentDirectory,null, null, textMessage, imageDownloadUrl, null, currentSenderId, "me", currentSenderId, recipientUserId, "Now", isTextIncluded, true, true,messageImagePreviewClone, false, true, false, false, true, false, false, true, true, new GlobalValue.OnMessageImageUploadListener() {
                    @Override
                    public void onUploadSuccess(String messageId, String messageImageDownloadUrl, String messageImageStorageReference) {
                        GlobalValue.sendPlainMessage(ChatRoomActivity.this, firebaseFirestore, chatDocumentDirectory, currentSenderId, recipientUserId, messageId, textMessage, messageImageDownloadUrl, null, messageImageStorageReference, null, isTextIncluded, true, false, new GlobalValue.ActionCallback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailed(String errorMessage) {

                            }
                        });
                    }

                    @Override
                    public void onUploadFailed(String errorMessage) {

                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        topScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });

        messageImageViewDialog.create();
        messageImageViewDialog.show();
    }



    public void openGallery(){
//        findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.GONE);
//        isVisible = false;
        requestForPermission(GALLERY_PERMISSION_REQUEST_CODE);

    }
    public void openVideoGallery(){
//        findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.GONE);
//        isVisible = false;
        requestForPermission(VIDEO_PERMISSION_REQUEST_CODE);

    }
    public void openCamera(){
//        findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.GONE);
//        isVisible = false;
        requestForPermission(CAMERA_PERMISSION_REQUEST_CODE);
    }

    public void fireGalleryIntent(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_PICK);
//        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        galleryIntent.setType("image/*");
        openGalleryLauncher.launch(galleryIntent);
    }
    public void fireVideoIntent(){
        Intent videoGalleryIntent = new Intent();
//        videoGalleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        videoGalleryIntent.setAction(Intent.ACTION_PICK);
        videoGalleryIntent.setType("video/*");
        openVideoGalleryLauncher.launch(videoGalleryIntent);
    }
    public void fireCameraIntent(){
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraLauncher.launch(cameraIntent);
    }
    public void handleCameraResult(){
        ContentResolver contentResolver=getApplicationContext().getContentResolver();
        String[] path =new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
        };
        Cursor cursor=contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,path,null,null,path[1]+" DESC");
        if(cursor.moveToFirst()) {
            String uriFromCamera = "content://media/external/images/media/" + cursor.getInt(0);
            displayAndConfigureSendingImage(uriFromCamera,null);
        }
        cursor.close();
    }
    public void requestForPermission(int requestCode){
        if(getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED || getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},requestCode);
        }else{
            if(requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
                fireCameraIntent();
            }
            if(requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
                fireGalleryIntent();
            } if(requestCode == VIDEO_PERMISSION_REQUEST_CODE) {
                fireVideoIntent();
            }
        }



    }

    private void confirmMessageDeletionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirm message deletion")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                deleteMessage();
                    }
                });

        confirmMessageDeletionDialog = builder.create();
    }


}