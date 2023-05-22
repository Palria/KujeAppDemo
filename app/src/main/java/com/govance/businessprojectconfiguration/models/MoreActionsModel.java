package com.govance.businessprojectconfiguration.models;

public class MoreActionsModel{
    String title;
    int resInt;
    public MoreActionsModel(String title, int resInt){
        this.title = title.toUpperCase();
        this.resInt= resInt;
    }

    public String getTitle() {
        return title;
    }

    public int getResInt() {
        return resInt;
    }
}
