package com.tes.theengineeringsolutions.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class InboxModel {
    private String Heading;
    private String Description;
    private @ServerTimestamp
    Date timeStamp;

    public InboxModel(String heading, String description, Date timeStamp) {
        Heading = heading;
        Description = description;
        this.timeStamp = timeStamp;
    }

    public String getHeading() {
        return Heading;
    }

    public void setHeading(String heading) {
        Heading = heading;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

}
