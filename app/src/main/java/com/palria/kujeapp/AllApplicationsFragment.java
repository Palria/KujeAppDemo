
package com.palria.kujeapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.palria.kujeapp.adapters.JobApplicationRcvAdapter;
import com.palria.kujeapp.models.JobApplicationDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AllApplicationsFragment extends Fragment {



    public AllApplicationsFragment() {
        // Required empty public constructor
    }
    ApplicationCallback applicationCallback;
    JobApplicationRcvAdapter applicationRcvAdapter;
    ArrayList<JobApplicationDataModel> applicationDataModelArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    ShimmerFrameLayout shimmerLayout, progressIndicatorShimmerLayout;
    FirebaseFirestore firebaseFirestore;
    String userId;
    LinearLayout containerLinearLayout;
    boolean isLoadingMoreApplications = false;
    boolean isFirstLoad = true;
    boolean isSingleJob = false;
    boolean isSingleCustomer = false;
    String jobId = "";
    String applicantId = "";
    DocumentSnapshot lastRetrievedApplicationsSnapshot = null;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
firebaseFirestore = FirebaseFirestore.getInstance();
userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
if(getArguments() != null){
    jobId = getArguments().getString(GlobalValue.JOB_ID,"");
    applicantId = getArguments().getString(GlobalValue.APPLICANT_ID,"");
    isSingleJob = getArguments().getBoolean(GlobalValue.IS_SINGLE_JOB,false);
    isSingleCustomer = getArguments().getBoolean(GlobalValue.IS_SINGLE_APPLICANT,false);
}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_all_applications, container, false);
        initUI(parentView);
        initRecyclerView();

        applicationCallback = new ApplicationCallback() {
            @Override
            public void onSuccess(JobApplicationDataModel applicationDataModel) {
            applicationDataModelArrayList.add(applicationDataModel);
            applicationRcvAdapter.notifyItemChanged(applicationDataModelArrayList.size());
            }

            @Override
            public void onFailed(String errorMessage) {

            }
        };
getAllApplications();

        if(getContext().getClass().equals(SingleJobActivity.class)){
            recyclerView.setPadding(2,2,2,200);
        }
        return parentView;
    }
  private  void initUI(View parentView){
        recyclerView = parentView.findViewById(R.id.jobApplicationRecyclerViewId);
      containerLinearLayout = parentView.findViewById(R.id.containerLinearLayoutId);
      shimmerLayout = parentView.findViewById(R.id.applicationsShimmerLayoutId);

  }
  private void initRecyclerView(){
      applicationRcvAdapter = new JobApplicationRcvAdapter(applicationDataModelArrayList,getContext(),isSingleJob);
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
      recyclerView.setLayoutManager(linearLayoutManager);
      recyclerView.setAdapter(applicationRcvAdapter);

     /* applicationDataModelArrayList.add(new JobApplicationDataModel(
              jobId,
              "jobName",
              "jobImageDownloadUrl",
              "applicationId",
              "applicantId",
              "dateApplicationed2",
              "applicationDescription",
              "applicantContactPhoneNumber",
              "applicantContactEmail",
              "applicantContactAddress",
              "applicantContactLocation",
              false
      ));
      */

      applicationRcvAdapter.notifyItemChanged(applicationDataModelArrayList.size());
  }
    private void getAllApplications() {
        Query applicationQuery = firebaseFirestore.collection(GlobalValue.ALL_APPLICATIONS).limit(20L);

        if (!isLoadingMoreApplications) {

            if (isFirstLoad) {

                applicationDataModelArrayList.clear();
                applicationRcvAdapter.notifyDataSetChanged();
                shimmerLayout.startShimmer();
                shimmerLayout.setVisibility(View.VISIBLE);
            } else {
                applicationQuery = firebaseFirestore.collection(GlobalValue.ALL_APPLICATIONS).limit(20L).startAfter(lastRetrievedApplicationsSnapshot);
                progressIndicatorShimmerLayout = GlobalValue.showShimmerLayout(getContext(), containerLinearLayout);

            }
            if (isSingleJob && isSingleCustomer) {
                if (isFirstLoad) {
                    applicationQuery = firebaseFirestore.collection(GlobalValue.ALL_APPLICATIONS).whereEqualTo(GlobalValue.JOB_ID, jobId).whereEqualTo(GlobalValue.APPLICANT_ID, applicantId).limit(20);
                }else {
                        applicationQuery = firebaseFirestore.collection(GlobalValue.ALL_APPLICATIONS).whereEqualTo(GlobalValue.JOB_ID, jobId).whereEqualTo(GlobalValue.APPLICANT_ID, applicantId).startAfter(lastRetrievedApplicationsSnapshot).limit(20);
                }
            } else if (isSingleJob) {
                if (isFirstLoad) {
                    applicationQuery = firebaseFirestore.collection(GlobalValue.ALL_APPLICATIONS).whereEqualTo(GlobalValue.JOB_ID, jobId).limit(20);
                }else {
                    applicationQuery = firebaseFirestore.collection(GlobalValue.ALL_APPLICATIONS).whereEqualTo(GlobalValue.JOB_ID, jobId).startAfter(lastRetrievedApplicationsSnapshot).limit(20);
                }
            } else if (isSingleCustomer) {
                if (isFirstLoad) {
                    applicationQuery = firebaseFirestore.collection(GlobalValue.ALL_APPLICATIONS).whereEqualTo(GlobalValue.APPLICANT_ID, applicantId).limit(20);
                }else {
                    applicationQuery = firebaseFirestore.collection(GlobalValue.ALL_APPLICATIONS).whereEqualTo(GlobalValue.APPLICANT_ID, applicantId).startAfter(lastRetrievedApplicationsSnapshot).limit(20);
                }
            }

            isLoadingMoreApplications = true;

            applicationQuery.get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    isLoadingMoreApplications = false;

                }
            }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String applicationId = documentSnapshot.getId();

                        String dateApplicationed = documentSnapshot.get(GlobalValue.DATE_APPLIED_TIME_STAMP) != null ? documentSnapshot.getTimestamp(GlobalValue.DATE_APPLIED_TIME_STAMP).toDate() + "" : "Undefined";
                        if (dateApplicationed.length() > 10) {
                            dateApplicationed = dateApplicationed.substring(0, 10);
                        }
                        final String dateApplicationed2 = dateApplicationed;
                        String jobId = "" + documentSnapshot.get(GlobalValue.JOB_ID);
                        String jobOwnerId = "" + documentSnapshot.get(GlobalValue.JOB_OWNER_USER_ID);
                        String jobName = "" + documentSnapshot.get(GlobalValue.JOB_TITLE);
                        String jobImageDownloadUrl = "" + documentSnapshot.get(GlobalValue.JOB_IMAGE_DOWNLOAD_URL);

                        String applicantId = "" + documentSnapshot.get(GlobalValue.APPLICANT_ID);
                        String applicantContactPhoneNumber = "" + documentSnapshot.get(GlobalValue.APPLICANT_CONTACT_PHONE_NUMBER);
                        String applicantContactEmail = "" + documentSnapshot.get(GlobalValue.APPLICANT_CONTACT_EMAIL);
                        String applicantContactAddress = "" + documentSnapshot.get(GlobalValue.APPLICANT_CONTACT_ADDRESS);
                        String applicationDescription = "" + documentSnapshot.get(GlobalValue.APPLICATION_DESCRIPTION);
                        boolean isResolved = documentSnapshot.get(GlobalValue.IS_RESOLVED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_RESOLVED) : false;
                        boolean isOwnerSeen = documentSnapshot.get(GlobalValue.IS_OWNER_SEEN) != null ? documentSnapshot.getBoolean(GlobalValue.IS_OWNER_SEEN) : false;

                        applicationCallback.onSuccess(new JobApplicationDataModel(
                                jobId,
                                jobOwnerId,
                                jobName,
                                jobImageDownloadUrl,
                                applicationId,
                                applicantId,
                                dateApplicationed2,
                                applicationDescription,
                                applicantContactPhoneNumber,
                                applicantContactEmail,
                                applicantContactAddress,
                                isResolved,
                                isOwnerSeen
                        ));


//                    firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(GlobalValue.getBusinessAdminId()).collection(GlobalValue.PAGES).document(GlobalValue.getCurrentBusinessId()).collection(GlobalValue.JOBS).document(jobId).collection(GlobalValue.JOB_PROFILE).document(jobId).get().addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                            String jobImageImageDownloadUrl = ""+documentSnapshot.get(GlobalValue.IMAGE_DOWNLOAD_URL);
//                            String jobName  = ""+documentSnapshot.get(GlobalValue.JOB_TITLE);
//                            String jobDescription = ""+documentSnapshot.get(GlobalValue.JOB_DESCRIPTION);
//
////                            displayApplicationView(getContext(),
////                                    containerLinearLayout,
////                                     jobImageImageDownloadUrl,
////                                     jobName,
////                                     jobId,
////                                     dateApplicationed2,
////                                     jobDescription,
////                                     applicantName,
////                                     applicantContactPhoneNumber,
////                                     applicantContactEmail,
////                                     applicantContactAddress,
////                                     applicantContactLocation,
////                                     applicationDescription
////                            );
//                        }
//                    });
                    }

                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.INVISIBLE);
                    GlobalValue.removeShimmerLayout(containerLinearLayout, progressIndicatorShimmerLayout);
                    if (queryDocumentSnapshots.size() == 0) {
                        if (isFirstLoad) {

                        }
                    } else {
                        lastRetrievedApplicationsSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    }
                    isFirstLoad = false;
                    isLoadingMoreApplications = false;

                }
            });
        }
    }


    interface ApplicationCallback{
        void onSuccess(JobApplicationDataModel applicationDataModel);
        void onFailed(String errorMessage);
    }

}