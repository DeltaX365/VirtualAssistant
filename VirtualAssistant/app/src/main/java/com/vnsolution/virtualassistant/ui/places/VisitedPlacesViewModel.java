package com.vnsolution.virtualassistant.ui.places;

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
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class VisitedPlacesViewModel extends ViewModel {
    private PlaceDao placeDao;

    @Inject
    public VisitedPlacesViewModel(PlaceDao placeDao) {
        this.placeDao = placeDao;
    }

    public LiveData<List<Place>> getVisitedPlaces() {
        LiveData<List<PlaceWithHistoryDetailEntity>> placesLiveData = placeDao.getPlaceWithHistories();
        return Transformations.map(placesLiveData, placeEntities -> {
            List<Place> places = new ArrayList<>();
            for (PlaceWithHistoryDetailEntity placeEntity: placeEntities) {
                Place place = toPlace(placeEntity);
                places.add(place);
            }
            return places;
        });
    }

    public Place toPlace(PlaceWithHistoryDetailEntity placeEntity) {
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

    public PlaceHistory toPlaceHistory(PlaceHistoryEntity placeHistoryEntity) {
        PlaceHistory placeHistory = new PlaceHistory();
        placeHistory.id = placeHistoryEntity.id;
        placeHistory.placeId = placeHistoryEntity.placeId;
        placeHistory.enterTime = placeHistoryEntity.enterTime;
        placeHistory.existTime =  placeHistoryEntity.existTime;
        return placeHistory;
    }
}