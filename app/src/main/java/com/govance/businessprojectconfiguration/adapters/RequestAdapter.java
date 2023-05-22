package com.govance.businessprojectconfiguration.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.govance.businessprojectconfiguration.GlobalValue;
import com.govance.businessprojectconfiguration.ProductDataModel;
import com.govance.businessprojectconfiguration.R;
import com.govance.businessprojectconfiguration.models.ProductOrderDataModel;
import com.govance.businessprojectconfiguration.models.RequestDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    ArrayList<RequestDataModel> requestDataModelArrayList;
    Context context;
    boolean isSingleService = true;


    public RequestAdapter(ArrayList<RequestDataModel> requestDataModelArrayList, Context context,boolean isSingleService) {
        this.requestDataModelArrayList = requestDataModelArrayList;
        this.context = context;
        this.isSingleService = isSingleService;
    }

    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.request_item_layout, parent, false);
        RequestAdapter.ViewHolder viewHolder = new RequestAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {
        RequestDataModel requestDataModel = requestDataModelArrayList.get(position);
//        if(!productOrderDataModelBackupArrayList.contains(productDataModel)) {
        if(GlobalValue.isBusinessOwner()) {
            markAsSeen(requestDataModel);
        }else{
            holder.resolveActionButton.setVisibility(View.GONE);
            holder.resolveActionButton.setEnabled(false);
        }
            holder.serviceTitleTextView.setText(requestDataModel.getServiceTitle());
            holder.dateRequestedTextView.setText(requestDataModel.getDateRequested());
            holder.requestDescriptionTextView.setText(requestDataModel.getRequestDescription());
            holder.customerPhoneTextView.setText(requestDataModel.getCustomerContactPhoneNumber());
            holder.customerEmailTextView.setText(requestDataModel.getCustomerContactEmail());
            holder.customerAddressTextView.setText(requestDataModel.getCustomerContactAddress());

        if(requestDataModel.getCustomerContactPhoneNumber().isEmpty())holder.customerPhoneTextView.setText("Not provided");
        if(requestDataModel.getCustomerContactEmail().isEmpty())holder.customerEmailTextView.setText("Not provided");
        if(requestDataModel.getCustomerContactAddress().isEmpty())holder.customerAddressTextView.setText("Not provided");
        if(!requestDataModel.getRequestDescription().isEmpty()){
            holder.descLinearLayout.setVisibility(View.VISIBLE);
        }else{
            holder.descLinearLayout.setVisibility(View.GONE);

        }


        if (!isSingleService) {

            } else {
                holder.serviceLinearLayout.setVisibility(View.GONE);
            }
            if(requestDataModel.isResolved()){
                holder.resolveActionButton.setText("Resolved");
                holder.resolveActionButton.setEnabled(false);
            }
            FirebaseFirestore.getInstance()
                    .collection(GlobalValue.ALL_USERS)
                    .document(requestDataModel.getCustomerId())
                    .get()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String customerName = "" + documentSnapshot.get(GlobalValue.USER_DISPLAY_NAME);
                    String customerCoverPhotoDownloadUrl = "" + documentSnapshot.get(GlobalValue.USER_COVER_PHOTO_DOWNLOAD_URL);
                    holder.customerNameTextView.setText(customerName);

                    Picasso.get()
                            .load(customerCoverPhotoDownloadUrl)
                            .error(R.drawable.agg_logo)
                            .into(holder.customerCoverPhotoImageView);
                }

            });


            holder.resolveActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.resolveActionButton.setText("Resolved");
                    holder.resolveActionButton.setEnabled(false);
                   markAsResolved(requestDataModel);

                }
            });


    }


    @Override
    public int getItemCount() {
        return requestDataModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView serviceTitleTextView;
        public TextView dateRequestedTextView;
        public TextView requestDescriptionTextView;
        public TextView customerNameTextView;
        public TextView customerPhoneTextView;
        public TextView customerEmailTextView;
        public TextView customerAddressTextView;
        public ImageView productPhotoImageView;
        public LinearLayout descLinearLayout;
        public LinearLayout serviceLinearLayout;
        public ImageView customerCoverPhotoImageView;
        public Button resolveActionButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.serviceTitleTextView =  itemView.findViewById(R.id.serviceTitleTextViewId);
            this.dateRequestedTextView =  itemView.findViewById(R.id.dateRequestedTextViewId);
            this.requestDescriptionTextView =  itemView.findViewById(R.id.requestDescriptionTextViewId);
            this.customerNameTextView =  itemView.findViewById(R.id.customerContactNameTextViewId);
            this.customerPhoneTextView =  itemView.findViewById(R.id.customerContactPhoneNumberTextViewId);
            this.customerEmailTextView =  itemView.findViewById(R.id.customerContactEmailTextViewId);
            this.customerAddressTextView =  itemView.findViewById(R.id.customerContactAddressTextViewId);
            this.serviceLinearLayout =  itemView.findViewById(R.id.serviceLinearLayoutId);
            this.descLinearLayout =  itemView.findViewById(R.id.descLinearLayoutId);
            this.customerCoverPhotoImageView =  itemView.findViewById(R.id.customerCoverPhotoImageViewId);
            this.resolveActionButton =  itemView.findViewById(R.id.resolveActionButtonId);

        }
    }

    void markAsSeen(RequestDataModel requestDataModel ){
        WriteBatch writeBatch = GlobalValue.getFirebaseFirestoreInstance().batch();

        DocumentReference orderDocumentReference =  GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_REQUESTS).document(requestDataModel.getRequestId());
        HashMap<String, Object> orderDetails = new HashMap<>();
        orderDetails.put(GlobalValue.IS_OWNER_SEEN, true);
        orderDetails.put(GlobalValue.DATE_SEEN_LAST_TIME_STAMP, FieldValue.serverTimestamp());
        writeBatch.set(orderDocumentReference,orderDetails, SetOptions.merge());
        writeBatch.commit();
    }
    void markAsResolved(RequestDataModel requestDataModel){
        HashMap<String,Object>orderDetails = new HashMap<>();
        orderDetails.put(GlobalValue.IS_SERVICE_REQUEST_RESOLVED, true);
        FirebaseFirestore.getInstance()
                .collection(GlobalValue.ALL_REQUESTS)
                .document(requestDataModel.getRequestId())
                .update(orderDetails)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });

    }
}

