package com.vnsolution.virtual_assistant.model;

import java.util.Date;
import java.util.UUID;

public class StopPoint {
    public String id;
    public double longitude;
    public double latitude;
    public Date enter;
    public Date exist;
    public Boolean isSynced;

    public StopPoint(double longitude, double latitude, Date enter, Date exist) {
        this.id = UUID.randomUUID().toString();
        this.longitude = longitude;
        this.latitude = latitude;
        this.enter = enter;
        this.exist = exist;
        this.isSynced = false;
    }
}
