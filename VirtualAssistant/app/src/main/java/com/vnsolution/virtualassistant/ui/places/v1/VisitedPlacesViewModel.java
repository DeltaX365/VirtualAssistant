package com.vnsolution.virtualassistant.ui.places.v1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtualassistant.db.entity.PlaceHistoryEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceHistoryWithDetailEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceWithHistoryDetailEntity;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class VisitedPlacesViewModel extends ViewModel {
    private PlaceDao placeDao;
    private Integer index = 1;

    @Inject
    public VisitedPlacesViewModel(PlaceDao placeDao) {
        this.placeDao = placeDao;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public LiveData<List<Place>> getVisitedPlaces() {
        LiveData<List<PlaceWithHistoryDetailEntity>> placesLiveData = placeDao.getPlaceWithHistories();
        return Transformations.map(placesLiveData, placeEntities -> {
            List<Place> places = transform(placeEntities);
            switch (index) {
                case 1: //Top 10 recent visited places
                    Collections.sort(places, new Comparator<Place>() {
                        @Override
                        public int compare(Place o1, Place o2) {
                            return (int) (getLastVisitedTime(o2) - getLastVisitedTime(o1));
                        }
                    });
                    places = places.subList(0, 10);
                    break;
                case 2: //Top 10 most visited places
                    Collections.sort(places, new Comparator<Place>() {
                        @Override
                        public int compare(Place o1, Place o2) {
                            return (int) (calculateTotalVisitedTime(o2) - calculateTotalVisitedTime(o1));
                        }
                    });
                    places = places.subList(0, 10);
                    break;
                case 3: //Top 10 favourite visited places
                    places = new ArrayList<>();
                    break;
                case 4: //Top 10 unvisited places
                    Collections.sort(places, new Comparator<Place>() {
                        @Override
                        public int compare(Place o1, Place o2) {
                            return (int) (getLastVisitedTime(o1) - getLastVisitedTime(o2));
                        }
                    });
                    places = places.subList(0, 10);
                    break;
                case 5: //All visited places
                    Collections.sort(places, new Comparator<Place>() {
                        @Override
                        public int compare(Place o1, Place o2) {
                            return (int) (getLastVisitedTime(o2) - getLastVisitedTime(o1));
                        }
                    });
                    break;
            }
            return places;
        });
    }

    private List<Place> transform(List<PlaceWithHistoryDetailEntity> placeEntities) {
        List<Place> places = new ArrayList<>();
        for (PlaceWithHistoryDetailEntity placeEntity: placeEntities) {
            Place place = toPlace(placeEntity);
            places.add(place);
        }
        return places;
    }

    private Place toPlace(PlaceWithHistoryDetailEntity placeEntity) {
        Place place = new Place();
        place.id = placeEntity.place.id;
        place.icon = placeEntity.place.icon;
        place.placeName = placeEntity.place.placeName;
        place.formattedAddress = placeEntity.place.formattedAddress;
        place.types = placeEntity.place.types;
        place.latitude = placeEntity.place.latitude;
        place.longitude = placeEntity.place.longitude;
        place.createdDate = placeEntity.place.createdDate;
        place.placeHistories = new ArrayList<>();
        for (PlaceHistoryEntity placeHistoryEntity: placeEntity.placeHistories) {
            PlaceHistory placeHistory = toPlaceHistory(placeHistoryEntity);
            place.placeHistories.add(placeHistory);
        }
        return place;
    }

    private PlaceHistory toPlaceHistory(PlaceHistoryEntity placeHistoryEntity) {
        PlaceHistory placeHistory = new PlaceHistory();
        placeHistory.id = placeHistoryEntity.id;
        placeHistory.placeId = placeHistoryEntity.placeId;
        placeHistory.enterTime = placeHistoryEntity.enterTime;
        placeHistory.existTime =  placeHistoryEntity.existTime;
        return placeHistory;
    }

    private long getLastVisitedTime(Place place) {
        long lastVisitedTime = 0;
        if (place != null && place.placeHistories != null && !place.placeHistories.isEmpty()) {
            int lastIndex = place.placeHistories.size() - 1;
            lastVisitedTime = place.placeHistories.get(lastIndex).enterTime.getTime();
        }
        return lastVisitedTime;
    }

    private long calculateTotalVisitedTime(Place place) {
//        long total = 0;
//        for (PlaceHistory placeHistory: place.placeHistories) {
//            total += (placeHistory.enterTime.getTime() - placeHistory.existTime.getTime());
//        }
//        return total;
        return place.placeHistories.size();
    }
}