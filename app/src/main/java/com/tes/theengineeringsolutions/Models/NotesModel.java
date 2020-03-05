package com.tes.theengineeringsolutions.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class NotesModel {
    private String notesId;
    private String fileName;
    private String fileUri;
    @ServerTimestamp
    private Date created;

    public NotesModel(String notesId, String fileName, String fileUri, Date created) {
        this.notesId = notesId;
        this.fileName = fileName;
        this.fileUri = fileUri;
        this.created = created;
    }

    public NotesModel() {

    }

    public String getNotesId() {
        return notesId;
    }

    public void setNotesId(String notesId) {
        this.notesId = notesId;
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
