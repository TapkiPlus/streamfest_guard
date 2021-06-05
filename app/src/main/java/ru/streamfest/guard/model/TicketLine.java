package ru.streamfest.guard.model;

import com.google.gson.annotations.SerializedName;

public class TicketLine {

    @SerializedName("status")
    int status;

    @SerializedName("details")
    TicketDetails details;

    public int getStatus() {
        return status;
    }

    public TicketDetails getDetails() {
        return details;
    }
}
