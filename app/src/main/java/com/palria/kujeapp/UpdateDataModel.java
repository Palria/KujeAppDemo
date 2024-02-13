package com.palria.kujeapp;

import java.io.Serializable;
import java.util.ArrayList;

public class UpdateDataModel implements Serializable {
    String updateId;
    String authorId;
    String title;
    String description;
    String datePosted;
    ArrayList<String> imageUrlList;
//    ArrayList<String> customersViewedList;
    int numOfViews;
    long totalComments;
    long totalLikes;

    private ArrayList<String> commentContributorsIdList;
    private ArrayList<String> likersIdList;

    public UpdateDataModel(
            String updateId,
            String authorId,
            String title,
            String description,
            String datePosted,
            ArrayList<String> imageUrlList,
//            ArrayList<String> customersViewedList,
            int numOfViews,
            boolean isPrivate,
            long totalComments,
            long totalLikes,
             ArrayList<String> commentContributorsIdList,
             ArrayList<String> likersIdList
    ){
        this.updateId=updateId;
        this.authorId=authorId;
        this.title=title;
        this.description=description;
        this.datePosted=datePosted;
        this.imageUrlList=imageUrlList;
//        this.customersViewedList=customersViewedList;
        this.numOfViews=numOfViews;
        this.totalComments=totalComments;
        this.totalLikes=totalLikes;
        this.commentContributorsIdList = commentContributorsIdList;
        this.likersIdList = likersIdList;
    }

    public String getAuthorId() {
        return authorId;
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

    public String getUpdateId() {
        return updateId;
    }

    public long getTotalComments() {
        return totalComments;
    }

    public long getTotalLikes() {
        return totalLikes;
    }


    public ArrayList<String> getLikersIdList() {
        return likersIdList;
    }

    public ArrayList<String> getCommentContributorsIdList() {
        return commentContributorsIdList;
    }
}
