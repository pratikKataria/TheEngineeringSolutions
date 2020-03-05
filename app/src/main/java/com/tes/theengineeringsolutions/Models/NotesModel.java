package com.tes.theengineeringsolutions.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class NotesModel {
    private String fileName;
    private String fileUri;
    @ServerTimestamp
    private Date created;

    public NotesModel() {

    }

    public NotesModel(String fileName, String fileUri, Date created) {
        this.fileName = fileName;
        this.fileUri = fileUri;
        this.created = created;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
