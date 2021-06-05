package ru.streamfest.guard.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class TicketDetails {
    @SerializedName("order_id")
    String orderId;

    @SerializedName("order_amount")
    int orderAmount;

    @SerializedName("order_email")
    String orderEmail;

    @SerializedName("order_phone")
    String orderPhone;

    @SerializedName("days_qty")
    int daysQty;

    @SerializedName("streamer")
    String streamer;

    @SerializedName("checkin_last")
    String checkinLast;

    @SerializedName("checkin_count")
    int checkinCount;

    public String getOrderId() {
        return orderId;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public String getOrderEmail() {
        return orderEmail;
    }

    public String getOrderPhone() {
        return orderPhone;
    }

    public int getDaysQty() {
        return daysQty;
    }

    public String getStreamer() {
        return streamer;
    }

    public String getCheckinLast() {
        return checkinLast;
    }

    public int getCheckinCount() {
        return checkinCount;
    }

}
