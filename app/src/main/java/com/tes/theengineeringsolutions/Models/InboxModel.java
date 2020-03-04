package com.tes.theengineeringsolutions.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class InboxModel {
    private String heading;
    private String description;
    private @ServerTimestamp
    Date timeStamp;

    public InboxModel(String heading, String description, Date timeStamp) {
        this.heading = heading;
        this.description = description;
        this.timeStamp = timeStamp;
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

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

}
