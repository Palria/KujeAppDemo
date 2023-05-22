package com.govance.businessprojectconfiguration.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.govance.businessprojectconfiguration.CatalogDataModel;
import com.govance.businessprojectconfiguration.GlobalValue;
import com.govance.businessprojectconfiguration.ProductDataModel;
import com.govance.businessprojectconfiguration.R;
import com.govance.businessprojectconfiguration.models.ProductOrderDataModel;
import com.govance.businessprojectconfiguration.models.RequestDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {

    ArrayList<CatalogDataModel> catalogDataModelArrayList;
    Context context;
    View dummyView;

    public CatalogAdapter(ArrayList<CatalogDataModel> catalogDataModelArrayList, Context context, View dummyView) {
        this.catalogDataModelArrayList = catalogDataModelArrayList;
        this.context = context;
        this.dummyView = dummyView;
    }

    @NonNull
    @Override
    public CatalogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.catalog_item_layout, parent, false);
        CatalogAdapter.ViewHolder viewHolder = new CatalogAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogAdapter.ViewHolder holder, int position) {
        CatalogDataModel catalogDataModel = catalogDataModelArrayList.get(position);

        holder.catalogTitleTextView.setText(catalogDataModel.getCatalogTitle());
        holder.dateAddedTextView.setText(catalogDataModel.getDateAdded());
        holder.catalogDescriptionTextView.setText(catalogDataModel.getCatalogDescription());
        Picasso.get()
                .load(catalogDataModel.getCatalogCoverPhotoDownloadUrl())
                .error(R.drawable.agg_logo)
                .into(holder.catalogCoverPhotoImageView);
        holder.catalogCoverPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalValue.viewImagePreview(context,holder.catalogCoverPhotoImageView, catalogDataModel.getCatalogCoverPhotoDownloadUrl());
            }
        });
        if(!GlobalValue.isBusinessOwner()) {
            holder.removeCatalogCoverPhotoImageButton.setVisibility(View.GONE);
        }
        holder.removeCatalogCoverPhotoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference documentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_SERVICES).document(catalogDataModel.getServiceId()).collection(GlobalValue.SERVICE_CATALOG).document(catalogDataModel.getCatalogId());
                documentReference.delete();
                FirebaseStorage.getInstance().getReferenceFromUrl(catalogDataModel.getCatalogCoverPhotoDownloadUrl()).delete();

                int positionDeleted = catalogDataModelArrayList.indexOf(catalogDataModel);
                catalogDataModelArrayList.remove(catalogDataModel);
                notifyItemChanged(positionDeleted);
                GlobalValue.createSnackBar(context,dummyView,"Deleting item from catalog", Snackbar.LENGTH_SHORT);
            }
        });



    }


    @Override
    public int getItemCount() {
        return catalogDataModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView catalogTitleTextView;
        public TextView dateAddedTextView;
        public TextView catalogDescriptionTextView;
        public ImageView catalogCoverPhotoImageView;
        public ImageButton removeCatalogCoverPhotoImageButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.catalogTitleTextView =  itemView.findViewById(R.id.catalogTitleTextViewId);
            this.dateAddedTextView =  itemView.findViewById(R.id.dateAddedTextViewId);
            this.catalogDescriptionTextView =  itemView.findViewById(R.id.catalogDescriptionTextViewId);
            this.catalogCoverPhotoImageView =  itemView.findViewById(R.id.catalogCoverPhotoImageViewId);
            this.removeCatalogCoverPhotoImageButton =  itemView.findViewById(R.id.removeCatalogCoverPhotoImageButtonId);

        }
    }

}

