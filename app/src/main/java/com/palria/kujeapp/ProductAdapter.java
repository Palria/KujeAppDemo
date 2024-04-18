package com.palria.kujeapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.palria.kujeapp.ProductDataModel;
import com.palria.kujeapp.models.NotesDataModel;
import com.palria.kujeapp.models.ProductOrderDataModel;
import com.palria.kujeapp.widgets.BottomSheetFormBuilderWidget;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    ArrayList<ProductDataModel> productDataModels;
    Context context;
    ArrayList<ProductDataModel> productDataModelBackupArrayList = new ArrayList<>();

    public ProductAdapter(ArrayList<ProductDataModel> productDataModels, Context context) {
        this.productDataModels = productDataModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.product_item_layout, parent, false);
        ProductAdapter.ViewHolder viewHolder = new ProductAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        ProductDataModel productDataModel = productDataModels.get(position);


        if (!productDataModelBackupArrayList.contains(productDataModel)) {


            holder.title.setText(productDataModel.getProductTitle());
//        holder.datePosted.setText(productDataModel.getDatePosted());
//        holder.description.setText(productDataModel.getProductDescription());
            holder.price.setText(Html.fromHtml("Price: <span style='font-size:40px; color:"+ context.getResources().getColor(R.color.teal_700)+";'>"+productDataModel.getProductPrice() +"</span>"));
            holder.productOrderCountTextView.setText(" "+ productDataModel.getProductOrderCount());
            holder.productViewCountTextView.setText(" "+productDataModel.getProductViewCount());
            holder.productNewOrderCountTextView.setText(""+ productDataModel.getProductNewOrderCount());
            holder.phoneNumberTextView.setText("Phone No: "+ productDataModel.getPhone());
            holder.emailAddressTextView.setText("Email: "+ productDataModel.getEmail());
            holder.residentialAddressTextView.setText("Residence: "+ productDataModel.getResidentialAddress());

            if(productDataModel.getProductNewOrderCount() <=0){
                holder.productNewOrderCountTextView.setVisibility(View.GONE);
            }
            String loc= TextUtils.isEmpty(productDataModel.getLocation()) ?"Not Available" : productDataModel.getLocation();
            holder.locationTextView.setText(loc);
            holder.datePosted.setText(productDataModel.getDatePosted());
            if(productDataModel.isSold()){
               // holder.soldIndicator.setVisibility(View.VISIBLE);
               // holder.soldIndicator.setText("Sold");
               // holder.soldIndicator.setBackground(new ColorDrawable(context.getResources().getColor(R.color.red_dark,context.getTheme())));
                //holder.soldIndicatorButton.setText("Mark as unsold");

            }else{
               // holder.soldIndicatorButton.setText("Mark as sold");
               // holder.soldIndicator.setVisibility(View.INVISIBLE);
            }
//            if(productDataModel.isApproved()){
//                holder.approvalIndicatorTextView.setText("Approved");
//                holder.approvalIndicatorTextView.setBackground(new ColorDrawable(context.getResources().getColor(R.color.success_green,context.getTheme())));
//
//            }else{
//                holder.approvalIndicatorTextView.setText("Unapproved");
//                holder.approvalIndicatorTextView.setBackground(new ColorDrawable(context.getResources().getColor(R.color.red_dark,context.getTheme())));
//            }
            if(productDataModel.getProductOwnerId().equals(GlobalValue.getCurrentUserId()) || GlobalValue.isBusinessOwner()){
//                holder.approvalIndicatorTextView.setVisibility(View.VISIBLE);
                //holder.soldIndicatorButton.setVisibility(View.VISIBLE);
                //holder.phoneNumberTextView.setVisibility(View.VISIBLE);
               // holder.residentialAddressTextView.setVisibility(View.VISIBLE);
                //holder.emailAddressTextView.setVisibility(View.VISIBLE);
            }
            Picasso.get()
                    .load(productDataModel.getImageUrlList().get(0))
                    .placeholder(R.drawable.agg_logo)
                    .into(holder.icon);
            holder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalValue.viewImagePreview(context,holder.icon, productDataModel.getImageUrlList().get(0));
                }
            });

            holder.soldIndicatorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   if(productDataModel.isSold()){
                       markAsUnSold(productDataModel);
                       holder.soldIndicator.setVisibility(View.INVISIBLE);
                       holder.soldIndicatorButton.setText("Mark as sold");
                   }else{
                       markAsSold(productDataModel);
                       holder.soldIndicator.setVisibility(View.VISIBLE);
                       holder.soldIndicator.setText("Sold");
                       holder.soldIndicatorButton.setText("Mark as unsold");

                   }

                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SingleProductActivity.class);
                    intent.putExtra(GlobalValue.PRODUCT_ID, productDataModel.getProductId());
                    intent.putExtra(GlobalValue.PRODUCT_DATA_MODEL, productDataModel);
                    intent.putExtra(GlobalValue.IS_EDITION, false);
                    context.startActivity(intent);

                }
            });
            holder.orderActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MakeNewProductOrderActivity.class);
                    intent.putExtra(GlobalValue.PRODUCT_ID, productDataModel.getProductId());
                    intent.putExtra(GlobalValue.PRODUCT_DATA_MODEL, productDataModel);
                    intent.putExtra(GlobalValue.PRODUCT_TITLE, productDataModel.getProductTitle());
                    intent.putExtra(GlobalValue.PRODUCT_OWNER_USER_ID, productDataModel.getProductOwnerId());
                    intent.putExtra(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL, productDataModel.getImageUrlList().get(0));
                    context.startActivity(intent);
                }
            });
            productDataModelBackupArrayList.add(productDataModel);
                PopupMenu popupMenu = new PopupMenu(context,holder.itemView);
                Menu actionMenu = GlobalValue.createPopUpMenu(context, R.menu.action_menu, holder.itemView,popupMenu,false, new GlobalValue.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClicked(MenuItem item) {
                        if(item.getItemId() == R.id.deleteId){
                            GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_PRODUCTS).document(productDataModel.getProductId()).delete();
                            int positionDeleted = productDataModels.indexOf(productDataModel);
                            productDataModels.remove(productDataModel);
                            notifyItemChanged(positionDeleted);

                            for(int i=0; i<productDataModel.getImageUrlList().size(); i++){
                                FirebaseStorage.getInstance().getReferenceFromUrl(productDataModel.getImageUrlList().get(i)).delete();
                            }

                        }
                        else  if(item.getItemId() == R.id.editId){


                            Intent intent = new Intent(context, PostNewProductActivity.class);
                            intent.putExtra(GlobalValue.PRODUCT_ID, productDataModel.getProductId());
                            intent.putExtra(GlobalValue.PRODUCT_DATA_MODEL, productDataModel);
                            if(!productDataModel.isApproved()){
                                intent.putExtra(GlobalValue.IS_PRODUCT_APPROVAL, true);
                            }else {
                                intent.putExtra(GlobalValue.IS_EDITION, true);
                            }
                            context.startActivity(intent);

                        }
                        else  if(item.getItemId() == R.id.approveId) {
                            approveProduct(productDataModel);
                            productDataModel.setIsApproved(true);
                        }
                        else  if(item.getItemId() == R.id.orderId) {
                            Intent intent = new Intent(context, MakeNewProductOrderActivity.class);
                            intent.putExtra(GlobalValue.PRODUCT_ID, productDataModel.getProductId());
                            intent.putExtra(GlobalValue.PRODUCT_DATA_MODEL, productDataModel);
                            intent.putExtra(GlobalValue.PRODUCT_TITLE, productDataModel.getProductTitle());
                            intent.putExtra(GlobalValue.PRODUCT_OWNER_USER_ID, productDataModel.getProductOwnerId());
                            intent.putExtra(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL, productDataModel.getImageUrlList().get(0));
                            context.startActivity(intent);
                        }



                        return true;
                    }
                });

                holder.icon.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


                        if(productDataModel.isApproved()){
                            popupMenu.getMenu().removeItem(R.id.approveId);
                            popupMenu.show();
                        }else{
                            popupMenu.show();

                        }
                        if(!GlobalValue.isBusinessOwner()){
                            popupMenu.getMenu().removeItem(R.id.approveId);

                        }
                        if(!(GlobalValue.isBusinessOwner() || productDataModel.getProductOwnerId().equals(GlobalValue.getCurrentUserId()))){
                            popupMenu.getMenu().removeItem(R.id.deleteId);
                            popupMenu.getMenu().removeItem(R.id.editId);

                        }

                            return false;
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


                        if(productDataModel.isApproved()){
                            popupMenu.getMenu().removeItem(R.id.approveId);
                            popupMenu.show();
                        }else{
                            popupMenu.show();

                        }
                        if(!GlobalValue.isBusinessOwner()){
                            popupMenu.getMenu().removeItem(R.id.approveId);

                        }
                        if(!(GlobalValue.isBusinessOwner() || productDataModel.getProductOwnerId().equals(GlobalValue.getCurrentUserId()))){
                            popupMenu.getMenu().removeItem(R.id.deleteId);
                            popupMenu.getMenu().removeItem(R.id.editId);

                        }

                            return false;
                    }
                });



        } else {
            Toast.makeText(context, "Already bound!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return productDataModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public TextView datePosted;
        public TextView locationTextView;
        public TextView description;
        public TextView price;
        public TextView productViewCountTextView;
        public TextView productOrderCountTextView;
        public TextView productNewOrderCountTextView;
//        public TextView approvalIndicatorTextView;
        public TextView soldIndicator;
        public TextView phoneNumberTextView;
        public TextView emailAddressTextView;
        public TextView residentialAddressTextView;
        public ImageView icon;
        public Button orderActionButton;
        public Button soldIndicatorButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title =  itemView.findViewById(R.id.productTitleTextViewId);
            this.price =  itemView.findViewById(R.id.productPriceTextViewId);
            this.productOrderCountTextView =  itemView.findViewById(R.id.productOrderCountTextViewId);
            this.productViewCountTextView =  itemView.findViewById(R.id.productViewCountTextViewId);
            this.productNewOrderCountTextView =  itemView.findViewById(R.id.productNewOrderCountTextViewId);
//            this.approvalIndicatorTextView =  itemView.findViewById(R.id.approvalIndicatorTextViewId);
            this.locationTextView =  itemView.findViewById(R.id.locationTextViewId);
            this.datePosted =  itemView.findViewById(R.id.datePostedTextViewId);
            this.soldIndicator =  itemView.findViewById(R.id.soldIndicatorTextViewId);
            this.soldIndicatorButton =  itemView.findViewById(R.id.soldIndicatorButtonId);
            this.phoneNumberTextView =  itemView.findViewById(R.id.phoneNumberTextViewId);
            this.emailAddressTextView =  itemView.findViewById(R.id.emailAddressTextViewId);
            this.residentialAddressTextView =  itemView.findViewById(R.id.residentialAddressTextViewId);
//            this.datePosted = (TextView) itemView.findViewById(R.id.datePosted);
//            description=itemView.findViewById(R.id.description);
            icon = itemView.findViewById(R.id.productImageViewId);
            orderActionButton = itemView.findViewById(R.id.orderProductActionButtonId);

        }
    }

    private void approveProduct(ProductDataModel productDataModel) {
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        DocumentReference productDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_PRODUCTS).document(productDataModel.getProductId());
        HashMap<String, Object> postDetails = new HashMap<>();
        postDetails.put(GlobalValue.IS_APPROVED, true);
        postDetails.put(GlobalValue.IS_NEW, false);
        postDetails.put(GlobalValue.DATE_TIME_STAMP_APPROVED, FieldValue.serverTimestamp());
        writeBatch.set(productDocumentReference,postDetails, SetOptions.merge());
        writeBatch.commit();
        Toast.makeText(context, "Approving "+productDataModel.productTitle, Toast.LENGTH_SHORT).show();


    }
    private void markAsSold(ProductDataModel productDataModel) {
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        DocumentReference productDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_PRODUCTS).document(productDataModel.getProductId());
        HashMap<String, Object> postDetails = new HashMap<>();
        postDetails.put(GlobalValue.IS_SOLD, true);
        postDetails.put(GlobalValue.IS_NEW, false);
        postDetails.put(GlobalValue.DATE_TIME_STAMP_SOLD, FieldValue.serverTimestamp());
        writeBatch.set(productDocumentReference,postDetails, SetOptions.merge());
        writeBatch.commit();
        productDataModel.setIsSold(true);

//        Toast.makeText(context, "Approving "+productDataModel.productTitle, Toast.LENGTH_SHORT).show();


    }
    private void markAsUnSold(ProductDataModel productDataModel) {
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        DocumentReference productDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_PRODUCTS).document(productDataModel.getProductId());
        HashMap<String, Object> postDetails = new HashMap<>();
        postDetails.put(GlobalValue.IS_SOLD, false);
        postDetails.put(GlobalValue.IS_NEW, false);
//        postDetails.put(GlobalValue.DATE_TIME_STAMP_SOLD, FieldValue.serverTimestamp());
        writeBatch.set(productDocumentReference,postDetails, SetOptions.merge());
        writeBatch.commit();
        productDataModel.setIsSold(false);

//        Toast.makeText(context, "Approving "+productDataModel.productTitle, Toast.LENGTH_SHORT).show();


    }

}

