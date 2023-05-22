package com.govance.businessprojectconfiguration.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.govance.businessprojectconfiguration.GlobalValue;
import com.govance.businessprojectconfiguration.MakeNewProductOrderActivity;
import com.govance.businessprojectconfiguration.ProductDataModel;
import com.govance.businessprojectconfiguration.R;
import com.govance.businessprojectconfiguration.RequestServiceActivity;
import com.govance.businessprojectconfiguration.SingleServiceActivity;
import com.govance.businessprojectconfiguration.models.NotificationDataModel;
import com.govance.businessprojectconfiguration.models.ProductOrderDataModel;
import com.govance.businessprojectconfiguration.models.ServiceDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    ArrayList<ServiceDataModel> serviceDataModelArrayList;
    Context context;


    public ServiceAdapter( Context context, ArrayList<ServiceDataModel> serviceDataModelArrayList) {
        this.serviceDataModelArrayList = serviceDataModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.service_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceAdapter.ViewHolder holder, int position) {
        ServiceDataModel serviceDataModel = serviceDataModelArrayList.get(position);

        holder.serviceTitleTextView.setText(serviceDataModel.getTitle());
        holder.dateAddedTextView.setText(serviceDataModel.getDateAdded());
        holder.serviceDescriptionTextView.setText(serviceDataModel.getDescription());
        holder.submitRequestActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, RequestServiceActivity.class);
                intent.putExtra(GlobalValue.SERVICE_ID,serviceDataModel.getServiceId());
                intent.putExtra(GlobalValue.SERVICE_DATA_MODEL,serviceDataModel);
//                intent.putExtra(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL,productImageDownloadUrl);
                intent.putExtra(GlobalValue.SERVICE_OWNER_USER_ID,serviceDataModel.getServiceOwnerId());
                intent.putExtra(GlobalValue.SERVICE_TITLE,serviceDataModel.getTitle());

                context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, SingleServiceActivity.class);
                intent.putExtra(GlobalValue.SERVICE_ID,serviceDataModel.getServiceId());
                intent.putExtra(GlobalValue.SERVICE_DATA_MODEL,serviceDataModel);
//                intent.putExtra(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL,productImageDownloadUrl);
                intent.putExtra(GlobalValue.SERVICE_OWNER_USER_ID,serviceDataModel.getServiceOwnerId());
                intent.putExtra(GlobalValue.SERVICE_TITLE,serviceDataModel.getTitle());

                context.startActivity(intent);
            }
        });

        if(GlobalValue.isBusinessOwner()){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    GlobalValue.createPopUpMenu(context, R.menu.delete_menu, holder.itemView, new GlobalValue.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClicked(MenuItem item) {
                            if(item.getItemId() == R.id.deleteId){
                                GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_SERVICES).document(serviceDataModel.getServiceId()).delete();
                                int positionDeleted = serviceDataModelArrayList.indexOf(serviceDataModel);
                                serviceDataModelArrayList.remove(serviceDataModel);
                                notifyItemChanged(positionDeleted);

                            }


                            return true;
                        }
                    });

                    return false;
                }
            });

            decrementNewRequestCounter(serviceDataModel);
        }

    }


    @Override
    public int getItemCount() {
        return serviceDataModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView serviceTitleTextView;
        public TextView dateAddedTextView;
        public TextView serviceDescriptionTextView;
        public MaterialButton submitRequestActionButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.serviceTitleTextView =  itemView.findViewById(R.id.serviceTitleTextViewId);
            this.dateAddedTextView =  itemView.findViewById(R.id.dateAddedTextViewId);
            this.serviceDescriptionTextView =  itemView.findViewById(R.id.serviceDescriptionTextViewId);
            this.submitRequestActionButton =  itemView.findViewById(R.id.submitRequestActionButtonId);

        }
    }

    void decrementNewRequestCounter(ServiceDataModel serviceDataModel){
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();

        DocumentReference serviceDocumentReference =  GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_SERVICES).document(serviceDataModel.getServiceId());
        HashMap<String, Object> serviceDetails = new HashMap<>();
        serviceDetails.put(GlobalValue.TOTAL_NEW_SERVICE_REQUESTS, 0);
        writeBatch.update(serviceDocumentReference,serviceDetails);

        writeBatch.commit();
    }
}

