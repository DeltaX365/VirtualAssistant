package com.vnsolution.virtualassistant.model;

import java.util.Date;
import java.util.UUID;

public class StopPoint {
    public String id;
    public double longitude;
    public double latitude;
    public Date enter;
    public Date exist;
    public String placeId;
    public boolean isCheckedIn;

    public StopPoint(double longitude, double latitude, Date enter, Date exist) {
        this.id = UUID.randomUUID().toString();
        this.longitude = longitude;
        this.latitude = latitude;
        this.enter = enter;
        this.exist = exist;
        this.placeId = "";
        this.isCheckedIn = false;
    }
}
