package com.palria.kujeapp.models;

public class JobApplicationDataModel {
    String jobImageDownloadUrl;
    String jobId;
    String jobOwnerId;
    String jobName;
    String applicationId;
    String applicantId;
    String dateApplied;
    String applicationDescription;
    String applicantContactPhoneNumber;
    String applicantContactEmail;
    String applicantContactAddress;
    boolean isResolved;
    boolean isOwnerSeen;

    public JobApplicationDataModel(
            String jobId,
            String jobOwnerId,
            String jobName,
            String jobImageDownloadUrl,
            String applicationId,
            String applicantId,
            String dateApplied,
            String applicationDescription,
            String applicantContactPhoneNumber,
            String applicantContactEmail,
            String applicantContactAddress,
            boolean isResolved,
            boolean isOwnerSeen
    ){
        this.jobImageDownloadUrl = jobImageDownloadUrl;
        this.jobId = jobId;
        this.jobOwnerId = jobOwnerId;
        this.jobName = jobName;
        this.applicationId = applicationId;
        this.applicantId = applicantId;
        this.dateApplied = dateApplied;
        this.applicationDescription = applicationDescription;
        this.applicantContactPhoneNumber = applicantContactPhoneNumber;
        this.applicantContactEmail = applicantContactEmail;
        this.applicantContactAddress = applicantContactAddress;
        this.isResolved = isResolved;
        this.isOwnerSeen = isOwnerSeen;
    }

    public String getJobImageDownloadUrl() {
        return jobImageDownloadUrl;
    }

    public String getJobId() {
        return jobId;
    }
    public String getJobOwnerId() {
        return jobOwnerId;
    }

    public String getJobName() {
        return jobName;
    }

    public String getDateApplied() {
        return dateApplied;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public String getApplicationDescription() {
        return applicationDescription;
    }

    public String getApplicantContactPhoneNumber() {
        return applicantContactPhoneNumber;
    }

    public String getApplicantContactEmail() {
        return applicantContactEmail;
    }

    public String getApplicantContactAddress() {
        return applicantContactAddress;
    }

    public boolean isResolved() {
        return isResolved;
    }
    public boolean isOwnerSeen() {
        return isOwnerSeen;
    }
}

