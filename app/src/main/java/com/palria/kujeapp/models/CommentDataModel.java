package com.palria.kujeapp.models;

import java.io.Serializable;
import java.util.ArrayList;

public class CommentDataModel implements Serializable {

    private String commentId;
    private String commentPosterId;
    private String description;
    private String coverDownloadUrl;
    private String updateId;
    private String authorId;
    private String parentCommentId;
    private boolean  hasParentComment;
    private boolean  hasReplies;
    private boolean  isHiddenByAuthor;
    private boolean  isHiddenByPoster;
    private String dateCreated;
    private long totalReplies;
    private long totalLikes;
//    private long totalDisLikes;
    private ArrayList repliersIdList;
    private ArrayList likersIdList;
//    private ArrayList disLikersIdList;

    public CommentDataModel(
             String commentId,
             String commentPosterId,
             String description,
             String coverDownloadUrl,
             String updateId,
             String authorId,
             String parentCommentId,
             boolean  hasParentComment,
             boolean  hasReplies,
             boolean  isHiddenByAuthor,
             boolean  isHiddenByPoster,
             String dateCreated,
             long totalReplies,
             long totalLikes,
//             long totalDisLikes,
             ArrayList repliersIdList,
             ArrayList likersIdList
//             ArrayList disLikersIdList
    ){
        this.commentId = commentId;
        this.commentPosterId = commentPosterId;
        this.description = description;
        this.coverDownloadUrl = coverDownloadUrl;
        this.updateId = updateId;
        this.authorId = authorId;
        this.parentCommentId = parentCommentId;
        this.hasParentComment = hasParentComment;
        this.hasReplies = hasReplies;
        this.isHiddenByAuthor = isHiddenByAuthor;
        this.isHiddenByPoster = isHiddenByPoster;
        this.totalReplies = totalReplies;
        this.totalLikes = totalLikes;
//        this.totalDisLikes = totalDisLikes;
        this.repliersIdList = repliersIdList;
        this.likersIdList = likersIdList;
//        this.disLikersIdList = disLikersIdList;
        this.dateCreated = dateCreated;

    }

    public String getCommentId() {
        return commentId;
    }
    public String getCommentPosterId() {
        return commentPosterId;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverDownloadUrl() {
        return coverDownloadUrl;
    }

    public String getUpdateId() {
        return updateId;
    }
    public String getAuthorId() {
        return authorId;
    }
    public String getParentCommentId() {
        return parentCommentId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public boolean hasParentComment() {
        return hasParentComment;
    }
    public boolean hasReplies() {
        return hasReplies;
    }
    public boolean isHiddenByAuthor() {
        return isHiddenByAuthor;
    }

    public boolean isHiddenByPoster() {
        return isHiddenByPoster ;
    }

    public long getTotalReplies() {
        return totalReplies;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

//    public long getTotalDisLikes() {
//        return totalDisLikes;
//    }

    public ArrayList getRepliersIdList() {
        return repliersIdList;
    }

    public ArrayList getLikersIdList() {
        return likersIdList;
    }

//    public ArrayList getDisLikersIdList() {
//        return disLikersIdList;
//    }
}
