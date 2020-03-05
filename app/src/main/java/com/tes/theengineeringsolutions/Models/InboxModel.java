package com.tes.theengineeringsolutions.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class InboxModel {
    private String postId;
    private String heading;
    private String description;
    private @ServerTimestamp
    Date created;

    public InboxModel(String postId, String heading, String description, Date created) {
        this.postId = postId;
        this.heading = heading;
        this.description = description;
        this.created = created;
    }

    public InboxModel() {

    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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
