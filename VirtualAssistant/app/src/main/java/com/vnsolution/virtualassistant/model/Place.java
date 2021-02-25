package com.vnsolution.virtualassistant.model;

import java.util.Date;
import java.util.List;

public class Place {
    public String id;
    public String icon;
    public String placeName;
    public String formattedAddress;
    public String types;
    public double latitude;
    public double longitude;
    public String photoReference;
    public int confirmed;
    public int favourite;
    public Date createdDate;
    public List<PlaceHistory> placeHistories;
}
