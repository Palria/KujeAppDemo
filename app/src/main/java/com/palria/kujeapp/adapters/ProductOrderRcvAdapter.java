package com.palria.kujeapp.adapters;

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
//import com.palria.kujeapp.ProductDataModel;
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
import com.palria.kujeapp.GlobalValue;
import com.palria.kujeapp.ProductDataModel;
import com.palria.kujeapp.R;
import com.palria.kujeapp.models.ProductOrderDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class ProductOrderRcvAdapter extends RecyclerView.Adapter<ProductOrderRcvAdapter.ViewHolder> {

    ArrayList<ProductOrderDataModel> productOrderDataModelArrayList;
    ArrayList<ProductOrderDataModel> productOrderDataModelBackupArrayList = new ArrayList<>();
    Context context;
    boolean isSingleProduct = true;


    public ProductOrderRcvAdapter(ArrayList<ProductOrderDataModel> productOrderDataModelArrayList, Context context,boolean isSingleProduct) {
        this.productOrderDataModelArrayList = productOrderDataModelArrayList;
        this.context = context;
        this.isSingleProduct = isSingleProduct;
    }

    @NonNull
    @Override
    public ProductOrderRcvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.product_order_item_layout, parent, false);
        ProductOrderRcvAdapter.ViewHolder viewHolder = new ProductOrderRcvAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductOrderRcvAdapter.ViewHolder holder, int position) {
        ProductOrderDataModel productDataModel = productOrderDataModelArrayList.get(position);
//        if(!productOrderDataModelBackupArrayList.contains(productDataModel)) {
        if(GlobalValue.isBusinessOwner()) {
            if (!productDataModel.isOwnerSeen()) {
                markAsSeen(productDataModel);
            }
        }else{
            holder.resolveActionButton.setVisibility(View.GONE);

        }
        if(true) {
            holder.productTitleTextView.setText(productDataModel.getProductName());
            holder.dateOrderedTextView.setText(productDataModel.getDateOrdered());
            holder.orderDescriptionTextView.setText(productDataModel.getOrderDescription());
            holder.customerPhoneTextView.setText(productDataModel.getCustomerContactPhoneNumber());
            holder.customerEmailTextView.setText(productDataModel.getCustomerContactEmail());
            holder.customerAddressTextView.setText(productDataModel.getCustomerContactAddress());
            if(productDataModel.getCustomerContactPhoneNumber().isEmpty())holder.customerPhoneTextView.setText("Not provided");
            if(productDataModel.getCustomerContactEmail().isEmpty())holder.customerEmailTextView.setText("Not provided");
            if(productDataModel.getCustomerContactAddress().isEmpty())holder.customerAddressTextView.setText("Not provided");
            if(!productDataModel.getOrderDescription().isEmpty()){
                holder.descLinearLayout.setVisibility(View.VISIBLE);
            }else{
                holder.descLinearLayout.setVisibility(View.GONE);

            }

            if (!isSingleProduct) {
                Picasso.get()
                        .load(productDataModel.getProductImageDownloadUrl())
                        .error(R.drawable.agg_logo)
                        .into(holder.productPhotoImageView);
            } else {
                holder.productLinearLayout.setVisibility(View.GONE);
            }
            if(productDataModel.isResolved()){
                holder.resolveActionButton.setText("Resolved");
                holder.resolveActionButton.setEnabled(false);
            }
            FirebaseFirestore.getInstance()
                    .collection(GlobalValue.ALL_USERS)
                    .document(productDataModel.getCustomerId())
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
                    HashMap<String,Object>orderDetails = new HashMap<>();
                    orderDetails.put(GlobalValue.IS_ORDER_RESOLVED, true);
                    FirebaseFirestore.getInstance()
                            .collection(GlobalValue.ALL_ORDERS)
                            .document(productDataModel.getOrderId())
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
            });
            productOrderDataModelBackupArrayList.add(productDataModel);

        }else{
            Toast.makeText(context, "Already bound!", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public int getItemCount() {
        return productOrderDataModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView productTitleTextView;
        public TextView dateOrderedTextView;
        public TextView orderDescriptionTextView;
        public TextView customerNameTextView;
        public TextView customerPhoneTextView;
        public TextView customerEmailTextView;
        public TextView customerAddressTextView;
        public ImageView productPhotoImageView;
        public LinearLayout descLinearLayout;
        public LinearLayout productLinearLayout;
        public ImageView customerCoverPhotoImageView;
        public Button resolveActionButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.productTitleTextView =  itemView.findViewById(R.id.productDisplayNameTextViewId);
            this.dateOrderedTextView =  itemView.findViewById(R.id.orderDateTextViewId);
            this.orderDescriptionTextView =  itemView.findViewById(R.id.orderDetailsTextViewId);
            this.customerNameTextView =  itemView.findViewById(R.id.customerContactNameTextViewId);
            this.customerPhoneTextView =  itemView.findViewById(R.id.customerContactPhoneNumberTextViewId);
            this.customerEmailTextView =  itemView.findViewById(R.id.customerContactEmailTextViewId);
            this.customerAddressTextView =  itemView.findViewById(R.id.customerContactAddressTextViewId);
            this.productPhotoImageView =  itemView.findViewById(R.id.productImageViewId);
            this.descLinearLayout =  itemView.findViewById(R.id.descLinearLayoutId);
            this.productLinearLayout =  itemView.findViewById(R.id.productLinearLayoutId);
            this.customerCoverPhotoImageView =  itemView.findViewById(R.id.customerCoverPhotoImageViewId);
            this.resolveActionButton =  itemView.findViewById(R.id.resolveActionButtonId);

        }
    }

    void markAsSeen(ProductOrderDataModel productOrderDataModel ){
        WriteBatch writeBatch = GlobalValue.getFirebaseFirestoreInstance().batch();

        DocumentReference orderDocumentReference =  GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_ORDERS).document(productOrderDataModel.getOrderId());
        HashMap<String, Object> orderDetails = new HashMap<>();
        orderDetails.put(GlobalValue.IS_OWNER_SEEN, true);
        orderDetails.put(GlobalValue.DATE_SEEN_LAST_TIME_STAMP, FieldValue.serverTimestamp());
        writeBatch.set(orderDocumentReference,orderDetails, SetOptions.merge());


        DocumentReference productDocumentReference =  GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_PRODUCTS).document(productOrderDataModel.getProductId());
        HashMap<String, Object> productOrderDetails = new HashMap<>();
        productOrderDetails.put(GlobalValue.TOTAL_NUMBER_OF_NEW_ORDERS, FieldValue.increment(-1L));
        writeBatch.update(productDocumentReference,productOrderDetails);

        DocumentReference orderOwnerDocumentReference =  GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(productOrderDataModel.getProductOwnerId());
        HashMap<String, Object> orderOwnerOrderDetails = new HashMap<>();
        orderOwnerOrderDetails.put(GlobalValue.TOTAL_NUMBER_OF_NEW_ORDERS, FieldValue.increment(-1L));
        writeBatch.update(orderOwnerDocumentReference,orderOwnerOrderDetails);

        writeBatch.commit();
    }
}

