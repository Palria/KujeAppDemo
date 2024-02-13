package com.palria.kujeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.palria.kujeapp.GlobalValue;
import com.palria.kujeapp.PostNewAdvertActivity;
import com.palria.kujeapp.PostNewAdvertActivity;
import com.palria.kujeapp.R;
//import com.palria.kujeapp.SingleAdvertsActivity;
//import com.palria.kujeapp.AdvertsDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class AdvertsAdapter extends RecyclerView.Adapter<AdvertsAdapter.ViewHolder> {

    ArrayList<AdvertsDataModel> advertDataModels;
    Context context;
    ArrayList<AdvertsDataModel> advertDataModelBackupArrayList = new ArrayList<>();

    public AdvertsAdapter(ArrayList<AdvertsDataModel> advertDataModels, Context context) {
        this.advertDataModels = advertDataModels;
        this.context = context;
    }

    @NonNull
    @Override
    public AdvertsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.adverts_item_layout, parent, false);
        AdvertsAdapter.ViewHolder viewHolder = new AdvertsAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdvertsAdapter.ViewHolder holder, int position) {
        AdvertsDataModel advertDataModel = advertDataModels.get(position);


//        if (!productDataModelBackupArrayList.contains(productDataModel)) {


            holder.title.setText(advertDataModel.getTitle());
//        holder.datePosted.setText(productDataModel.getDatePosted());
//        holder.description.setText(productDataModel.getProductDescription());
            holder.description.setText(advertDataModel.getDescription());
            holder.viewCountTextView.setText(""+advertDataModel.getNumOfViews());
            holder.datePosted.setText(""+ advertDataModel.getDatePosted());

            if(advertDataModel.getImageUrlList().size() == 0){
                holder.icon.setVisibility(View.GONE);
            }else {
                Picasso.get()
                        .load(advertDataModel.getImageUrlList().get(0))
                        .placeholder(R.drawable.agg_logo)
                        .into(holder.icon);
            }
         incrementViewCount(advertDataModel);
            if(!advertDataModel.isViewLimitExceeded() && advertDataModel.getViewLimit()>= advertDataModel.getNumOfViews()){
                markViewExceeded(advertDataModel);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(context, SingleAdvertsActivity.class);
//                    intent.putExtra(GlobalValue.ADVERT_ID, advertDataModel.getAdvertsId());
//                    intent.putExtra(GlobalValue.ADVERT_DATA_MODEL, advertDataModel);
//                    intent.putExtra(GlobalValue.IS_EDITION, false);
//                    context.startActivity(intent);

                }
            });

            if(GlobalValue.isBusinessOwner()){
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


                        GlobalValue.createPopUpMenu(context, R.menu.advert_menu, holder.itemView, new GlobalValue.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClicked(MenuItem item) {
                                if(item.getItemId() == R.id.deleteId){
                                    GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_ADVERTS).document(advertDataModel.getAdvertId()).delete();
                                    int positionDeleted = advertDataModels.indexOf(advertDataModel);
                                    advertDataModels.remove(advertDataModel);
                                    notifyItemChanged(positionDeleted);

                                    for(int i=0; i<advertDataModel.getImageUrlList().size(); i++){
                                        FirebaseStorage.getInstance().getReferenceFromUrl(advertDataModel.getImageUrlList().get(i)).delete();
                                    }

                                }
                                else  if(item.getItemId() == R.id.editId){
//not yet implemented for edition
                                    Intent intent = new Intent(context, PostNewAdvertActivity.class);
                                    intent.putExtra(GlobalValue.ADVERT_ID, advertDataModel.getAdvertId());
                                    intent.putExtra(GlobalValue.ADVERT_DATA_MODEL, advertDataModel);
                                    intent.putExtra(GlobalValue.IS_EDITION, true);
                                    context.startActivity(intent);
                                }


                                return true;
                            }
                        });

                        return false;
                    }
                });

            }



//        else {
//            Toast.makeText(context, "Already bound!", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public int getItemCount() {
        return advertDataModels.size();
    }


    void markViewExceeded(AdvertsDataModel advertsDataModel){

        WriteBatch writeBatch = GlobalValue.getFirebaseFirestoreInstance().batch();
        DocumentReference walletReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_ADVERTS).document(advertsDataModel.getAdvertId());
        HashMap<String, Object> walletDetails = new HashMap<>();
        walletDetails.put(GlobalValue.IS_VIEW_EXCEEDED,true);
        walletDetails.put(GlobalValue.DATE_VIEW_LIMIT_EXCEEDED_TIME_STAMP, FieldValue.serverTimestamp());

        writeBatch.set(walletReference, walletDetails, SetOptions.merge());

        writeBatch.commit();

    }

    void incrementViewCount(AdvertsDataModel advertsDataModel){

        WriteBatch writeBatch = GlobalValue.getFirebaseFirestoreInstance().batch();
        DocumentReference walletReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_ADVERTS).document(advertsDataModel.getAdvertId());
        HashMap<String, Object> walletDetails = new HashMap<>();
        walletDetails.put(GlobalValue.TOTAL_NUMBER_OF_VIEWS,FieldValue.increment(1L));

        writeBatch.set(walletReference, walletDetails, SetOptions.merge());

        writeBatch.commit();

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public TextView datePosted;
        public TextView description;
        public TextView viewCountTextView;
        public ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title =  itemView.findViewById(R.id.advertTitleTextViewId);
            this.description =  itemView.findViewById(R.id.descriptionTextViewId);
            this.viewCountTextView =  itemView.findViewById(R.id.viewCountTextViewId);
            this.datePosted =  itemView.findViewById(R.id.datePostedTextViewId);
//            this.datePosted = (TextView) itemView.findViewById(R.id.datePosted);
//            description=itemView.findViewById(R.id.description);
            icon = itemView.findViewById(R.id.advertImageViewId);

        }
    }

}

