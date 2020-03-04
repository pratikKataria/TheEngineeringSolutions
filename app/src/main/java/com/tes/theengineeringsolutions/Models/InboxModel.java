package com.tes.theengineeringsolutions.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class InboxModel {
    private String heading;
    private String description;
    private @ServerTimestamp
    Date created;

    public InboxModel() {

    }

    public InboxModel(String heading, String description, Date created) {
        this.heading = heading;
        this.description = description;
        this.created = created;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}
