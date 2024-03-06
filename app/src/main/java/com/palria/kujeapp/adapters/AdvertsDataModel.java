package com.palria.kujeapp.adapters;

import java.io.Serializable;
import java.util.ArrayList;

public class AdvertsDataModel implements Serializable {
    String advertId;
    String ownerId;
    String title;
    String description;
    String datePosted;
    ArrayList<String> imageUrlList;
//    ArrayList<String> customersViewedList;
    int numOfViews;
    int viewLimit;
    boolean isViewLimitExceeded;
    boolean isPrivate;
    boolean isApproved;

    public AdvertsDataModel(
            String advertId,
            String ownerId,
            String title,
            String description,
            String datePosted,
            ArrayList<String> imageUrlList,
//            ArrayList<String> customersViewedList,
            int numOfViews,
            int viewLimit,
            boolean isViewLimitExceeded,
            boolean isPrivate,
            boolean isApproved
    ){
        this.advertId=advertId;
        this.ownerId=ownerId;
        this.title=title;
        this.description=description;
        this.datePosted=datePosted;
        this.imageUrlList=imageUrlList;
//        this.customersViewedList=customersViewedList;
        this.numOfViews=numOfViews;
        this.viewLimit=viewLimit;
        this.isViewLimitExceeded=isViewLimitExceeded;
        this.isPrivate=isPrivate;
        this.isApproved=isApproved;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public ArrayList<String> getImageUrlList() {
        return imageUrlList;
    }

//    public ArrayList<String> getCustomersViewedList() {
//        return customersViewedList;
//    }

    public int getNumOfViews() {
        return numOfViews;
    }
    public int getViewLimit() {
        return viewLimit;
    }

    public boolean isViewLimitExceeded() {
        return isViewLimitExceeded;
    }
    public boolean isPrivate() {
        return isPrivate;
    }

    public String getAdvertId() {
        return advertId;
    }
    public String getOwnerId() {
        return ownerId;
    }

    public boolean isApproved() {
        return isApproved;
    }
}
