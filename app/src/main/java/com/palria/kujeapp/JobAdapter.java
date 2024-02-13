package com.palria.kujeapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

    ArrayList<JobDataModel> jobDataModels;
    Context context;
    ArrayList<JobDataModel> jobDataModelBackupArrayList = new ArrayList<>();

    public JobAdapter(ArrayList<JobDataModel> jobDataModels, Context context) {
        this.jobDataModels = jobDataModels;
        this.context = context;
    }

    @NonNull
    @Override
    public JobAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.job_item_layout, parent, false);
        JobAdapter.ViewHolder viewHolder = new JobAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull JobAdapter.ViewHolder holder, int position) {
        JobDataModel jobDataModel = jobDataModels.get(position);


        if (!jobDataModelBackupArrayList.contains(jobDataModel)) {


            holder.title.setText(jobDataModel.getJobTitle());
//        holder.datePosted.setText(jobDataModel.getDatePosted());
//        holder.description.setText(jobDataModel.getJobDescription());
            holder.price.setText("Salary: "+jobDataModel.getJobSalary());
            holder.jobApplicantCountTextView.setText(" "+ jobDataModel.getJobApplicantCount());
            holder.jobViewCountTextView.setText(" "+jobDataModel.getJobViewCount());
            holder.jobNewApplicantCountTextView.setText(""+ jobDataModel.getJobNewApplicantCount());
            holder.phoneNumberTextView.setText("Phone No: "+ jobDataModel.getPhone());
            holder.emailAddressTextView.setText("Email: "+ jobDataModel.getEmail());
          //  holder.residentialAddressTextView.setText("Residence: "+ jobDataModel.getResidentialAddress());

            if(jobDataModel.getJobNewApplicantCount() <=0){
                holder.jobNewApplicantCountTextView.setVisibility(View.GONE);
            }
            holder.locationTextView.setText("Location: "+jobDataModel.getLocation());
            holder.datePosted.setText("Date: "+jobDataModel.getDatePosted());
            if(jobDataModel.isClosed()){
                holder.closedIndicator.setVisibility(View.VISIBLE);
                holder.closedIndicator.setText("Closed");
                holder.closedIndicator.setBackground(new ColorDrawable(context.getResources().getColor(R.color.red_dark,context.getTheme())));
                holder.closedIndicatorButton.setText("Mark as Closed");

            }else{
                holder.closedIndicatorButton.setText("Mark as Closed");
                holder.closedIndicator.setVisibility(View.INVISIBLE);


            }
//            if(jobDataModel.isApproved()){
//                holder.approvalIndicatorTextView.setText("Approved");
//                holder.approvalIndicatorTextView.setBackground(new ColorDrawable(context.getResources().getColor(R.color.success_green,context.getTheme())));
//
//            }else{
//                holder.approvalIndicatorTextView.setText("Unapproved");
//                holder.approvalIndicatorTextView.setBackground(new ColorDrawable(context.getResources().getColor(R.color.red_dark,context.getTheme())));
//            }
            if(jobDataModel.getJobOwnerId().equals(GlobalValue.getCurrentUserId()) || GlobalValue.isBusinessOwner()){
//                holder.approvalIndicatorTextView.setVisibility(View.VISIBLE);
                holder.closedIndicatorButton.setVisibility(View.VISIBLE);
                holder.phoneNumberTextView.setVisibility(View.VISIBLE);
                holder.locationTextView.setVisibility(View.VISIBLE);
                holder.emailAddressTextView.setVisibility(View.VISIBLE);
            }
            Picasso.get()
                    .load(jobDataModel.getImageUrlList().get(0))
                    .placeholder(R.drawable.agg_logo)
                    .into(holder.icon);
            holder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalValue.viewImagePreview(context,holder.icon, jobDataModel.getImageUrlList().get(0));
                }
            });

            holder.closedIndicatorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   if(jobDataModel.isClosed()){
                       markAsUnClosed(jobDataModel);
                       holder.closedIndicator.setVisibility(View.INVISIBLE);
                       holder.closedIndicatorButton.setText("Mark as closed");
                   }else{
                       markAsClosed(jobDataModel);
                       holder.closedIndicator.setVisibility(View.VISIBLE);
                       holder.closedIndicator.setText("Closed");
                       holder.closedIndicatorButton.setText("Mark as unclosed");

                   }

                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SingleJobActivity.class);
                    intent.putExtra(GlobalValue.JOB_ID, jobDataModel.getJobId());
                    intent.putExtra(GlobalValue.JOB_DATA_MODEL, jobDataModel);
                    intent.putExtra(GlobalValue.IS_EDITION, false);
                    context.startActivity(intent);

                }
            });
            holder.applyActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ApplyJobActivity.class);
//                    intent.putExtra(GlobalValue.JOB_ID, jobDataModel.getJobId());
//                    intent.putExtra(GlobalValue.JOB_DATA_MODEL, jobDataModel);
//                    intent.putExtra(GlobalValue.JOB_TITLE, jobDataModel.getJobTitle());
//                    intent.putExtra(GlobalValue.JOB_OWNER_USER_ID, jobDataModel.getJobOwnerId());
//                    intent.putExtra(GlobalValue.JOB_IMAGE_DOWNLOAD_URL, jobDataModel.getImageUrlList().get(0));
                    context.startActivity(intent);
                }
            });
            jobDataModelBackupArrayList.add(jobDataModel);
                PopupMenu popupMenu = new PopupMenu(context,holder.itemView);
                Menu actionMenu = GlobalValue.createPopUpMenu(context, R.menu.job_action_menu, holder.itemView,popupMenu,false, new GlobalValue.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClicked(MenuItem item) {
                        if(item.getItemId() == R.id.deleteId){
//                            GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_JOBS).document(jobDataModel.getJobId()).delete();
                            int positionDeleted = jobDataModels.indexOf(jobDataModel);
                            jobDataModels.remove(jobDataModel);
                            notifyItemChanged(positionDeleted);

                            for(int i=0; i<jobDataModel.getImageUrlList().size(); i++){
                                FirebaseStorage.getInstance().getReferenceFromUrl(jobDataModel.getImageUrlList().get(i)).delete();
                            }

                        }
                        else  if(item.getItemId() == R.id.editId){


                            Intent intent = new Intent(context, PostNewJobActivity.class);
//                            intent.putExtra(GlobalValue.JOB_ID, jobDataModel.getJobId());
//                            intent.putExtra(GlobalValue.JOB_DATA_MODEL, jobDataModel);
//                            if(!jobDataModel.isApproved()){
////                                intent.putExtra(GlobalValue.IS_JOB_APPROVAL, true);
//                            }else {
                                intent.putExtra(GlobalValue.IS_EDITION, true);
//                            }
                            context.startActivity(intent);

                        }
//                        else  if(item.getItemId() == R.id.approveId) {
//                            approveJob(jobDataModel);
//                            jobDataModel.setIsApproved(true);
//                        }
                        else  if(item.getItemId() == R.id.applyId) {
                            Intent intent = new Intent(context, ApplyJobActivity.class);
//                            intent.putExtra(GlobalValue.JOB_ID, jobDataModel.getJobId());
//                            intent.putExtra(GlobalValue.JOB_DATA_MODEL, jobDataModel);
//                            intent.putExtra(GlobalValue.JOB_TITLE, jobDataModel.getJobTitle());
//                            intent.putExtra(GlobalValue.JOB_OWNER_USER_ID, jobDataModel.getJobOwnerId());
//                            intent.putExtra(GlobalValue.JOB_IMAGE_DOWNLOAD_URL, jobDataModel.getImageUrlList().get(0));
                            context.startActivity(intent);
                        }



                        return true;
                    }
                });

                holder.icon.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

//
//                        if(jobDataModel.isApproved()){
//                            popupMenu.getMenu().removeItem(R.id.approveId);
//                            popupMenu.show();
//                        }else{
                            popupMenu.show();

//                        }
//                        if(!GlobalValue.isBusinessOwner()){
//                            popupMenu.getMenu().removeItem(R.id.approveId);
//
//                        }
                        if(!(GlobalValue.isBusinessOwner() || jobDataModel.getJobOwnerId().equals(GlobalValue.getCurrentUserId()))){
                            popupMenu.getMenu().removeItem(R.id.deleteId);
                            popupMenu.getMenu().removeItem(R.id.editId);

                        }

                            return false;
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


//                        if(jobDataModel.isApproved()){
//                            popupMenu.getMenu().removeItem(R.id.approveId);
//                            popupMenu.show();
//                        }else{
                            popupMenu.show();

//                        }
//                        if(!GlobalValue.isBusinessOwner()){
//                            popupMenu.getMenu().removeItem(R.id.approveId);
//
//                        }
                        if(!(GlobalValue.isBusinessOwner() || jobDataModel.getJobOwnerId().equals(GlobalValue.getCurrentUserId()))){
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
        return jobDataModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public TextView datePosted;
        public TextView locationTextView;
        public TextView description;
        public TextView price;
        public TextView jobViewCountTextView;
        public TextView jobApplicantCountTextView;
        public TextView jobNewApplicantCountTextView;
//        public TextView approvalIndicatorTextView;
        public TextView closedIndicator;
        public TextView phoneNumberTextView;
        public TextView emailAddressTextView;
//        public TextView residentialAddressTextView;
        public ImageView icon;
        public Button applyActionButton;
        public Button closedIndicatorButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title =  itemView.findViewById(R.id.jobTitleTextViewId);
            this.price =  itemView.findViewById(R.id.jobSalaryTextViewId);
            this.jobApplicantCountTextView =  itemView.findViewById(R.id.jobApplicantCountTextViewId);
            this.jobViewCountTextView =  itemView.findViewById(R.id.jobViewCountTextViewId);
            this.jobNewApplicantCountTextView =  itemView.findViewById(R.id.jobNewApplicantCountTextViewId);
//            this.approvalIndicatorTextView =  itemView.findViewById(R.id.approvalIndicatorTextViewId);
            this.locationTextView =  itemView.findViewById(R.id.locationTextViewId);
            this.datePosted =  itemView.findViewById(R.id.datePostedTextViewId);
            this.closedIndicator =  itemView.findViewById(R.id.closedIndicatorTextViewId);
            this.closedIndicatorButton =  itemView.findViewById(R.id.closedIndicatorButtonId);
            this.phoneNumberTextView =  itemView.findViewById(R.id.phoneNumberTextViewId);
            this.emailAddressTextView =  itemView.findViewById(R.id.emailAddressTextViewId);
//            this.residentialAddressTextView =  itemView.findViewById(R.id.residentialAddressTextViewId);
//            this.datePosted = (TextView) itemView.findViewById(R.id.datePosted);
//            description=itemView.findViewById(R.id.description);
            icon = itemView.findViewById(R.id.jobImageViewId);
            applyActionButton = itemView.findViewById(R.id.applyJobActionButtonId);

        }
    }

//    private void approveJob(JobDataModel jobDataModel) {
//        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
//        DocumentReference jobDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_JOBS).document(jobDataModel.getJobId());
//        HashMap<String, Object> postDetails = new HashMap<>();
//        postDetails.put(GlobalValue.IS_APPROVED, true);
//        postDetails.put(GlobalValue.IS_NEW, false);
//        postDetails.put(GlobalValue.DATE_TIME_STAMP_APPROVED, FieldValue.serverTimestamp());
//        writeBatch.set(jobDocumentReference,postDetails, SetOptions.merge());
//        writeBatch.commit();
//        Toast.makeText(context, "Approving "+jobDataModel.jobTitle, Toast.LENGTH_SHORT).show();
//
//
//    }
    private void markAsClosed(JobDataModel jobDataModel) {
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        DocumentReference jobDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_JOBS).document(jobDataModel.getJobId());
        HashMap<String, Object> postDetails = new HashMap<>();
        postDetails.put(GlobalValue.IS_CLOSED, true);
        postDetails.put(GlobalValue.IS_NEW, false);
        postDetails.put(GlobalValue.DATE_TIME_STAMP_CLOSED, FieldValue.serverTimestamp());
        writeBatch.set(jobDocumentReference,postDetails, SetOptions.merge());
        writeBatch.commit();
        jobDataModel.setIsClosed(true);

//        Toast.makeText(context, "Approving "+jobDataModel.jobTitle, Toast.LENGTH_SHORT).show();


    }
    private void markAsUnClosed(JobDataModel jobDataModel) {
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        DocumentReference jobDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_JOBS).document(jobDataModel.getJobId());
        HashMap<String, Object> postDetails = new HashMap<>();
        postDetails.put(GlobalValue.IS_CLOSED, false);
        postDetails.put(GlobalValue.IS_NEW, false);
//        postDetails.put(GlobalValue.DATE_TIME_STAMP_CLOSED, FieldValue.serverTimestamp());
        writeBatch.set(jobDocumentReference,postDetails, SetOptions.merge());
        writeBatch.commit();
        jobDataModel.setIsClosed(false);

//        Toast.makeText(context, "Approving "+jobDataModel.jobTitle, Toast.LENGTH_SHORT).show();


    }

}

