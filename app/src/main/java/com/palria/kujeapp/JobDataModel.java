package com.palria.kujeapp;

import java.io.Serializable;
import java.util.ArrayList;

public class JobDataModel implements Serializable {

    String jobId ;
    String jobOwnerId ;
    String jobTitle;
    String jobSalary;
    String jobDescription ;
    String location;
    String phone;
    String email;
    String residentialAddress;
    boolean isClosed;
    String datePosted;
    long jobViewCount;
    long jobApplicantCount;
    long jobNewApplicantCount;
    ArrayList<String> imageUrlList ;
    boolean isPrivate;
                public JobDataModel(String jobId, String jobOwnerId, String jobTitle, String jobSalary, String jobDescription, String location, String phone, String email, boolean isClosed, String datePosted, long jobViewCount, long jobApplicantCount, long jobNewApplicantCount, ArrayList<String> imageUrlList, boolean isPrivate){
                    this.jobId = jobId;
                    this.jobOwnerId = jobOwnerId;
                    this.jobTitle=jobTitle;
                    this.jobSalary=jobSalary;
                    this.jobDescription= jobDescription;
                    this.location=location;
                    this.phone=phone;
                    this.email=email;
                    this.residentialAddress=residentialAddress;
                    this.isClosed=isClosed;
                    this.datePosted=datePosted;
                    this.jobViewCount=jobViewCount;
                    this.jobApplicantCount=jobApplicantCount;
                    this.jobNewApplicantCount=jobNewApplicantCount;
                    this.imageUrlList= imageUrlList;
                    this.isPrivate=isPrivate;
                }
    public  String getJobId(){
                    return jobId;
                }

    public  String getJobOwnerId() {
        return jobOwnerId;
    }
    public  String getJobTitle() {
        return jobTitle;
    }

    public String getJobSalary() {
        return jobSalary;
    }

    public  String getJobDescription() {
        return jobDescription;
    }

    public  String getLocation() {
        return location;
    }
    public  String getPhone() {
        return phone;
    }
    public  String getEmail() {
        return email;
    }
    public  String getResidentialAddress() {
        return residentialAddress;
    }
    public  boolean isClosed() {
        return isClosed;
    }
    public  boolean setIsClosed(boolean isClosed) {
        return this.isClosed = isClosed;
    }
    public  String getDatePosted() {
        return datePosted;
    }
    public  long getJobViewCount() {
        return jobViewCount;
    }
    public  long getJobApplicantCount() {
        return jobApplicantCount;
    }
    public  long getJobNewApplicantCount() {
        return jobNewApplicantCount;
    }

    public  ArrayList<String> getImageUrlList() {
        return imageUrlList;
    }

    public boolean isPrivate() {
        return isPrivate;
    }
}
