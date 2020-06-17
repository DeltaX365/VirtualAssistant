package com.vnsolution.virtualassistant.model;

import java.util.Date;
import java.util.List;

public class Place {
    public String id;
    public String placeName;
    public String formattedAddress;
    public double latitude;
    public double longitude;
    public Date createdDate;
    public List<PlaceHistory> placeHistories;
}
