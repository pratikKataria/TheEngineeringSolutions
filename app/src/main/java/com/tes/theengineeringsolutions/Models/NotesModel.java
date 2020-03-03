package com.tes.theengineeringsolutions.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class NotesModel {
    private String f_name;
    private String f_uri;
    @ServerTimestamp
    private Date date;

    public NotesModel(String f_name, String f_uri, Date date) {
        this.f_name = f_name;
        this.f_uri = f_uri;
        this.date = date;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getF_uri() {
        return f_uri;
    }

    public void setF_uri(String f_uri) {
        this.f_uri = f_uri;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
