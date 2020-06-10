package com.vnsolution.virtualassistant.ui.places;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtualassistant.db.entity.PlaceHistoryEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceHistoryWithDetailEntity;
import com.vnsolution.virtualassistant.model.PlaceHistory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class VisitedPlaceViewModel extends ViewModel {
    private PlaceHistoryDao placeHistoryDao;

    @Inject
    public VisitedPlaceViewModel(PlaceHistoryDao placeHistoryDao) {
        this.placeHistoryDao = placeHistoryDao;
    }

    public LiveData<List<PlaceHistory>> getVisitedPlaces() {
        LiveData<List<PlaceHistoryWithDetailEntity>> placeHistoriesLiveData = placeHistoryDao.getPlaceHistoriesWithDetail();
        return Transformations.map(placeHistoriesLiveData, placeHistoryEntities -> {
            List<PlaceHistory> placeHistories = new ArrayList<>();
            for (PlaceHistoryWithDetailEntity placeHistoryEntity: placeHistoryEntities) {
                PlaceHistory placeHistory = toPlaceHistory(placeHistoryEntity);
                placeHistories.add(placeHistory);
            }
            return placeHistories;
        });
    }

    public PlaceHistory toPlaceHistory(PlaceHistoryWithDetailEntity placeHistoryEntity) {
        PlaceHistory placeHistory = new PlaceHistory();
        placeHistory.placeId = placeHistoryEntity.placeHistory.placeId;
        placeHistory.placeName = placeHistoryEntity.place.placeName;
        placeHistory.formattedAddress = placeHistoryEntity.place.formattedAddress;
        placeHistory.latitude = placeHistoryEntity.place.latitude;
        placeHistory.longitude = placeHistoryEntity.place.longitude;
        placeHistory.enterTime = placeHistoryEntity.placeHistory.enterTime;
        placeHistory.existTime =  placeHistoryEntity.placeHistory.existTime;
        placeHistory.createdDate = placeHistoryEntity.place.createdDate;
        return placeHistory;
    }
}