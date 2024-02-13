package com.palria.kujeapp.models;


public class SalesRecordDataModel {
    String recordId;
    String title;
    String caption;
    String dateAdded;

    public SalesRecordDataModel(final String recordId,String title,String caption,String dateAdded){
        this.recordId = recordId;
        this.title = title;
        this.caption = caption;
        this.dateAdded = dateAdded;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getTitle() {
        return title;
    }

    public String getCaption() {
        return caption;
    }

    public String getDateAdded() {
        return dateAdded;
    }

}
