package com.govance.businessprojectconfiguration;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.govance.businessprojectconfiguration.models.NotificationDataModel;

import java.util.ArrayList;
import java.util.Random;

public class NotificationHandlerService extends Service {

    int oldOrders = 0;
    public NotificationHandlerService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "created", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        listenToProductsListing();
        listenToMessages();
        listenToNotifications();
        listenToCustomersRegistration();
        listenToNewOrders();
        Toast.makeText(this, "started", Toast.LENGTH_SHORT).show();
        postNotification("BOOT_COMPLETED","You just booted your system device", R.drawable.ic_baseline_notifications_24);
        return Service.START_STICKY;
    }

    void listenToProductsListing(){
        Query query = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.IS_NEW, true).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).limit(1L);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_PRODUCTS).whereEqualTo(GlobalValue.IS_PRIVATE, false).whereEqualTo(GlobalValue.IS_NEW, true).orderBy(GlobalValue.DATE_POSTED_TIME_STAMP, Query.Direction.DESCENDING).startAfter(documentSnapshot).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                            final String CHANNEL_ID = getString(R.string.app_name)+"_channel";
//                            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,getString(R.string.app_name)+" Notification", NotificationManager.IMPORTANCE_HIGH);
//                            getSystemService(NotificationManager.class).createNotificationChannel(channel);
//                            Notification.Builder notification = new Notification.Builder(NotificationHandlerService.this,CHANNEL_ID)
//                                    .setContentTitle("New Product Added")
//                                    .setContentText("A new product is awaiting your order")
//                                    .setSmallIcon(R.drawable.ic_baseline_local_grocery_store_24)
////                                    .setContentIntent(pendingIntent)
//                                    .setAutoCancel(true);
//                            NotificationManagerCompat.from(NotificationHandlerService.this).notify(new Random().nextInt(10),notification.build());
                            if (error!=null){
                                listenToProductsListing();
                            }else {
                                postNotification("New Product Added ", "A new product is awaiting your order", R.drawable.ic_baseline_store_mall_directory_24);
                            }

                        }
                    });
                }
            }
        });
    }

    void listenToMessages(){

            Query messageDirectoryQuery = GlobalValue.getFirebaseFirestoreInstance().collection("ALL_USERS").document(GlobalValue.getCurrentUserId()).collection("ALL_MESSAGES_DIRECTORY_ID").orderBy("DATE_CREATED_TIME_STAMP",Query.Direction.DESCENDING).limit(1);
        messageDirectoryQuery.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listenToMessages();
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    Query messageDirectoryQuery2 = GlobalValue.getFirebaseFirestoreInstance().collection("ALL_USERS").document(GlobalValue.getCurrentUserId()).collection("ALL_MESSAGES_DIRECTORY_ID").orderBy("DATE_CREATED_TIME_STAMP",Query.Direction.DESCENDING).startAfter(documentSnapshot);
                    messageDirectoryQuery2.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null) {
                                for (DocumentChange documentChange : value.getDocumentChanges()) {
                                    DocumentSnapshot documentSnapshot = documentChange.getDocument();

                                    String title = "New Message ";
//                                String title = documentSnapshot.get(GlobalValue.NOTIFICATION_TITLE) != null ? documentSnapshot.getTimestamp(GlobalValue.NOTIFICATION_TITLE).toDate() + "" : "New Message ";
//                                if (title.length() > 20) {
//                                    title = title.substring(0, 20);
//                                }
                                    String message = "A new message is awaiting your reply";
//                                String message = documentSnapshot.get(GlobalValue.NOTIFICATION_MESSAGE) != null ? documentSnapshot.getTimestamp(GlobalValue.NOTIFICATION_MESSAGE).toDate() + "" : "A new message is awaiting your reply";
//                                if (message.length() > 100) {
//                                    message = message.substring(0, 100);
//                                }

//                                    final String CHANNEL_ID = getString(R.string.app_name) + "_channel";
//                                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name) + " Notification", NotificationManager.IMPORTANCE_HIGH);
//                                    getSystemService(NotificationManager.class).createNotificationChannel(channel);
//                                    Notification.Builder notification = new Notification.Builder(NotificationHandlerService.this, CHANNEL_ID)
//                                            .setContentTitle(title)
//                                            .setContentText(message)
//                                            .setSmallIcon(R.drawable.ic_baseline_email_24)
////                                    .setContentIntent(pendingIntent)
//                                            .setAutoCancel(true);
//                                    NotificationManagerCompat.from(NotificationHandlerService.this).notify(new Random().nextInt(10), notification.build());

                                    postNotification(title,message,R.drawable.ic_baseline_email_24);


                                }


                            }
                            if (error!=null){
                                listenToMessages();
                            }

                        }
                    });

                }
            }
        });

        }

    void listenToNotifications(){
        GlobalValue.getFirebaseFirestoreInstance()
                .collection(GlobalValue.PLATFORM_NOTIFICATIONS).orderBy(GlobalValue.DATE_NOTIFIED_TIME_STAMP).limit(1L)
                .get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    listenToNotifications();
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                          @Override
                                                          public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                              for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                                                  GlobalValue.getFirebaseFirestoreInstance()
                                                                          .collection(GlobalValue.PLATFORM_NOTIFICATIONS).orderBy(GlobalValue.DATE_NOTIFIED_TIME_STAMP).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                                      @Override
                                                                      public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                                                          if (value != null) {
                                                                              for (DocumentChange documentChange : value.getDocumentChanges()) {
                                                                                  DocumentSnapshot documentSnapshot = documentChange.getDocument();
//                                    String notificationId = documentSnapshot.getId();

                                                                                  String title = documentSnapshot.get(GlobalValue.NOTIFICATION_TITLE) != null ? documentSnapshot.getString(GlobalValue.NOTIFICATION_TITLE) + "" : "Notification";
//                                                                                  if (title.length() > 20) {
//                                                                                      title = title.substring(0, 20);
//                                                                                  }
                                                                                  String message = documentSnapshot.get(GlobalValue.NOTIFICATION_MESSAGE) != null ? documentSnapshot.getString(GlobalValue.NOTIFICATION_MESSAGE) + "" : "Notification";
//                                                                                  if (message.length() > 100) {
//                                                                                      message = message.substring(0, 100);
//                                                                                  }
//                                                                                  final String CHANNEL_ID = getString(R.string.app_name) + "_channel";
//                                                                                  NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name) + " Notification", NotificationManager.IMPORTANCE_HIGH);
//                                                                                  getSystemService(NotificationManager.class).createNotificationChannel(channel);
//                                                                                  Notification.Builder notification = new Notification.Builder(NotificationHandlerService.this, CHANNEL_ID)
//                                                                                          .setContentTitle(title)
//                                                                                          .setContentText(message)
//                                                                                          .setSmallIcon(R.drawable.ic_baseline_notifications_24)
////                                    .setContentIntent(pendingIntent)
//                                                                                          .setAutoCancel(true);
//                                                                                  NotificationManagerCompat.from(NotificationHandlerService.this).notify(new Random().nextInt(10), notification.build());

                                                                                  postNotification(title,message,R.drawable.ic_baseline_notifications_24);


                                                                              }
                                                                          }
                                                                          if (error!=null){
                                                                              listenToNotifications();
                                                                          }

                                                                      }
                                                                  });

                                                              }
                                                          }
                                                      }

    );
    }

    void listenToCustomersRegistration(){
        GlobalValue.getFirebaseFirestoreInstance()
                .collection(GlobalValue.ALL_USERS).orderBy(GlobalValue.USER_PROFILE_DATE_CREATED_TIME_STAMP).limit(1L)
                .get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listenToCustomersRegistration();
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                          @Override
                                                          public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                              for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                                                  GlobalValue.getFirebaseFirestoreInstance()
                                                                          .collection(GlobalValue.ALL_USERS).orderBy(GlobalValue.USER_PROFILE_DATE_CREATED_TIME_STAMP).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                                      @Override
                                                                      public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                                                          if (value != null) {
                                                                              for (DocumentChange documentChange : value.getDocumentChanges()) {
                                                                                  DocumentSnapshot documentSnapshot = documentChange.getDocument();
//                                    String notificationId = documentSnapshot.getId();

                                                                                  String title = documentSnapshot.get(GlobalValue.USER_DISPLAY_NAME) != null ? documentSnapshot.getString(GlobalValue.USER_DISPLAY_NAME)+ "" : "A Customer has registered";
//                                                                                  if (title.length() > 20) {
//                                                                                      title = title.substring(0, 20);
//                                                                                  }
//                                                                                  title=title+" registered.";
                                                                                  String body =  "A new Customer registered in "+getString(R.string.app_name);

//                                                                                  final String CHANNEL_ID = getString(R.string.app_name) + "_channel";
//                                                                                  NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name) + " Notification", NotificationManager.IMPORTANCE_HIGH);
//                                                                                  getSystemService(NotificationManager.class).createNotificationChannel(channel);
//                                                                                  Notification.Builder notification = new Notification.Builder(NotificationHandlerService.this, CHANNEL_ID)
//                                                                                          .setContentTitle(title)
//                                                                                          .setContentText(body)
//                                                                                          .setSmallIcon(R.drawable.ic_baseline_person_24)
////                                    .setContentIntent(pendingIntent)
//                                                                                          .setAutoCancel(true);
//                                                                                  NotificationManagerCompat.from(NotificationHandlerService.this).notify(new Random().nextInt(10), notification.build());
                                                                                  postNotification(title,body,R.drawable.ic_baseline_person_24);


                                                                              }
                                                                          }
                                                                          if (error!=null){
                                                                              listenToCustomersRegistration();
                                                                          }

                                                                      }
                                                                  });

                                                              }
                                                          }
                                                      }

    );
    }

    private  void listenToNewOrders() {
        if(GlobalValue.isBusinessOwner()){
            GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentBusinessId())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value!=null){
                                long newOrders = value.get(GlobalValue.TOTAL_NUMBER_OF_NEW_ORDERS) != null ? value.getLong(GlobalValue.TOTAL_NUMBER_OF_NEW_ORDERS) : 0L;
                                if(newOrders >0 && oldOrders<newOrders) {
                                    oldOrders = (int) newOrders;
                                   postNotification("New order","A customer ordered for a product",R.drawable.ic_baseline_local_grocery_store_24);

                                }

                            }
                            if (error!=null){
                                listenToNewOrders();
                            }

                        }
                    });
        }
    }

    private void postNotification(String title,String body,int icon){

        if (title.length() > 70) {
            title = title.substring(0, 70);
        }
        if (body.length() > 100) {
            body = body.substring(0, 100);
        }
        Intent intent = new Intent(NotificationHandlerService.this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationHandlerService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final String CHANNEL_ID = getString(R.string.app_name) + "_channel";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name) + " Notification", NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(NotificationHandlerService.this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat.from(NotificationHandlerService.this).notify(new Random().nextInt(10), notification.build());

    }

}