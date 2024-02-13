package com.palria.kujeapp.models;

import java.util.ArrayList;

public class NotesDataModel {
    String noteId;
    String title;
    String body;
    String dateAdded;

    public NotesDataModel(String noteId,String title,String body,String dateAdded){
        this.noteId = noteId;
        this.title = title;
        this.body = body;
        this.dateAdded = dateAdded;
    }

    public String getNoteId() {
        return noteId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getDateAdded() {
        return dateAdded;
    }

}
