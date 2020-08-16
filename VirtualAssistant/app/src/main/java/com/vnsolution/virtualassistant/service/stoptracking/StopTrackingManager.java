package com.vnsolution.virtualassistant.service.stoptracking;

import com.vnsolution.virtualassistant.model.Place;

public class StopTrackingManager  {
    private static StopTrackingManager instance;

    private StopTrackingManager(){}

    public static synchronized StopTrackingManager getInstance() {
        if (instance == null) {
            instance = new StopTrackingManager();
        }
        return instance;
    }

    public void getTotalVisitedPlaces() {}

    public void getTop10RecentVisitedPlaces() {}

    public void getTop10MostVisitedPlaces() {}

    public void getTop10FavouriteVisitedPlaces() {}

    public void getTop10UnvisitedPlaces() {}

    public void favouritePlace(Place place) {}

    public void deletePlace(Place place) {}

    public void categorizePlace(Place place) {}

    public void ratePlace(Place place) {}
}
