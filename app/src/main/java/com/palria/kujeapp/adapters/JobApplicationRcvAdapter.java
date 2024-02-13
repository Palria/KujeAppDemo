package com.palria.kujeapp.adapters;

import android.content.Context;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.palria.kujeapp.GlobalValue;
import com.palria.kujeapp.R;
import com.palria.kujeapp.models.JobApplicationDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class JobApplicationRcvAdapter extends RecyclerView.Adapter<JobApplicationRcvAdapter.ViewHolder> {

    ArrayList<JobApplicationDataModel> jobApplicationDataModelArrayList;
    ArrayList<JobApplicationDataModel> jobApplicationDataModelBackupArrayList = new ArrayList<>();
    Context context;
    boolean isSingleJob = true;


    public JobApplicationRcvAdapter(ArrayList<JobApplicationDataModel> jobApplicationDataModelArrayList, Context context, boolean isSingleJob) {
        this.jobApplicationDataModelArrayList = jobApplicationDataModelArrayList;
        this.context = context;
        this.isSingleJob = isSingleJob;
    }

    @NonNull
    @Override
    public JobApplicationRcvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.job_application_item_layout, parent, false);
        JobApplicationRcvAdapter.ViewHolder viewHolder = new JobApplicationRcvAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull JobApplicationRcvAdapter.ViewHolder holder, int position) {
        JobApplicationDataModel jobDataModel = jobApplicationDataModelArrayList.get(position);
//        if(!jobApplicationDataModelBackupArrayList.contains(jobDataModel)) {
        if(GlobalValue.isBusinessOwner()) {
            if (!jobDataModel.isOwnerSeen()) {
                markAsSeen(jobDataModel);
            }
        }else{
            holder.resolveActionButton.setVisibility(View.GONE);

        }
        if(true) {
            holder.jobTitleTextView.setText(jobDataModel.getJobName());
            holder.dateAppliedTextView.setText(jobDataModel.getDateApplied());
            holder.applicationDescriptionTextView.setText(jobDataModel.getApplicationDescription());
            holder.applicantPhoneTextView.setText(jobDataModel.getApplicantContactPhoneNumber());
            holder.applicantEmailTextView.setText(jobDataModel.getApplicantContactEmail());
            holder.applicantAddressTextView.setText(jobDataModel.getApplicantContactAddress());
            if(jobDataModel.getApplicantContactPhoneNumber().isEmpty())holder.applicantPhoneTextView.setText("Not provided");
            if(jobDataModel.getApplicantContactEmail().isEmpty())holder.applicantEmailTextView.setText("Not provided");
            if(jobDataModel.getApplicantContactAddress().isEmpty())holder.applicantAddressTextView.setText("Not provided");
            if(!jobDataModel.getApplicationDescription().isEmpty()){
                holder.descLinearLayout.setVisibility(View.VISIBLE);
            }else{
                holder.descLinearLayout.setVisibility(View.GONE);

            }

            if (!isSingleJob) {
                Picasso.get()
                        .load(jobDataModel.getJobImageDownloadUrl())
                        .error(R.drawable.agg_logo)
                        .into(holder.jobPhotoImageView);
            } else {
                holder.jobLinearLayout.setVisibility(View.GONE);
            }
            if(jobDataModel.isResolved()){
                holder.resolveActionButton.setText("Resolved");
                holder.resolveActionButton.setEnabled(false);
            }
            FirebaseFirestore.getInstance()
                    .collection(GlobalValue.ALL_USERS)
                    .document(jobDataModel.getApplicantId())
                    .get()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String applicantName = "" + documentSnapshot.get(GlobalValue.USER_DISPLAY_NAME);
                    String applicantCoverPhotoDownloadUrl = "" + documentSnapshot.get(GlobalValue.USER_COVER_PHOTO_DOWNLOAD_URL);
                    holder.applicantNameTextView.setText(applicantName);

                    Picasso.get()
                            .load(applicantCoverPhotoDownloadUrl)
                            .error(R.drawable.agg_logo)
                            .into(holder.applicantCoverPhotoImageView);
                }

            });


            holder.resolveActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.resolveActionButton.setText("Resolved");
                    holder.resolveActionButton.setEnabled(false);
                    HashMap<String,Object>applicationDetails = new HashMap<>();
                    applicationDetails.put(GlobalValue.IS_ORDER_RESOLVED, true);
                    FirebaseFirestore.getInstance()
                            .collection(GlobalValue.ALL_ORDERS)
                            .document(jobDataModel.getApplicationId())
                            .update(applicationDetails)
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
            jobApplicationDataModelBackupArrayList.add(jobDataModel);

        }else{
            Toast.makeText(context, "Already bound!", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public int getItemCount() {
        return jobApplicationDataModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView jobTitleTextView;
        public TextView dateAppliedTextView;
        public TextView applicationDescriptionTextView;
        public TextView applicantNameTextView;
        public TextView applicantPhoneTextView;
        public TextView applicantEmailTextView;
        public TextView applicantAddressTextView;
        public ImageView jobPhotoImageView;
        public LinearLayout descLinearLayout;
        public LinearLayout jobLinearLayout;
        public ImageView applicantCoverPhotoImageView;
        public Button resolveActionButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.jobTitleTextView =  itemView.findViewById(R.id.jobDisplayNameTextViewId);
            this.dateAppliedTextView =  itemView.findViewById(R.id.applicationDateTextViewId);
            this.applicationDescriptionTextView =  itemView.findViewById(R.id.applicationDetailsTextViewId);
            this.applicantNameTextView =  itemView.findViewById(R.id.applicantContactNameTextViewId);
            this.applicantPhoneTextView =  itemView.findViewById(R.id.applicantContactPhoneNumberTextViewId);
            this.applicantEmailTextView =  itemView.findViewById(R.id.applicantContactEmailTextViewId);
            this.applicantAddressTextView =  itemView.findViewById(R.id.applicantContactAddressTextViewId);
            this.jobPhotoImageView =  itemView.findViewById(R.id.jobImageViewId);
            this.descLinearLayout =  itemView.findViewById(R.id.descLinearLayoutId);
            this.jobLinearLayout =  itemView.findViewById(R.id.jobLinearLayoutId);
            this.applicantCoverPhotoImageView =  itemView.findViewById(R.id.applicantCoverPhotoImageViewId);
            this.resolveActionButton =  itemView.findViewById(R.id.resolveActionButtonId);

        }
    }

    void markAsSeen(JobApplicationDataModel jobApplicationDataModel ){
        WriteBatch writeBatch = GlobalValue.getFirebaseFirestoreInstance().batch();

        DocumentReference applicationDocumentReference =  GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_APPLICATIONS).document(jobApplicationDataModel.getApplicationId());
        HashMap<String, Object> applicationDetails = new HashMap<>();
        applicationDetails.put(GlobalValue.IS_OWNER_SEEN, true);
        applicationDetails.put(GlobalValue.DATE_SEEN_LAST_TIME_STAMP, FieldValue.serverTimestamp());
        writeBatch.set(applicationDocumentReference,applicationDetails, SetOptions.merge());


        DocumentReference jobDocumentReference =  GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_PRODUCTS).document(jobApplicationDataModel.getJobId());
        HashMap<String, Object> jobApplicationDetails = new HashMap<>();
        jobApplicationDetails.put(GlobalValue.TOTAL_NUMBER_OF_NEW_APPLICANTS, FieldValue.increment(-1L));
        writeBatch.update(jobDocumentReference,jobApplicationDetails);

        DocumentReference applicationOwnerDocumentReference =  GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(jobApplicationDataModel.getJobOwnerId());
        HashMap<String, Object> applicationOwnerApplicationDetails = new HashMap<>();
        applicationOwnerApplicationDetails.put(GlobalValue.TOTAL_NUMBER_OF_NEW_APPLICANTS, FieldValue.increment(-1L));
        writeBatch.update(applicationOwnerDocumentReference,applicationOwnerApplicationDetails);

        writeBatch.commit();
    }
}

