package com.vnsolution.virtualassistant.ui.travel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.vnsolution.virtualassistant.api.GoogleApi;
import com.vnsolution.virtualassistant.api.dto.GoogleMapNearByDto;
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.entity.PlaceEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceHistoryEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceSummary;
import com.vnsolution.virtualassistant.db.entity.PlaceWithHistoryDetailEntity;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceHistory;
import com.vnsolution.virtualassistant.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import retrofit2.Response;

public class TravelViewModel extends ViewModel {
    private PlaceDao placeDao;
    private GoogleApi googleService;

    private Integer index = 0; //filter all visited places
    private MutableLiveData<List<GoogleMapNearByDto.NearBy>> nearByPlacesLiveData;

    @Inject
    public TravelViewModel(PlaceDao placeDao, GoogleApi googleService) {
        this.placeDao = placeDao;
        this.googleService = googleService;
    }

    public LiveData<List<Place>> getRecentlyVisitedPlaces() {
        LiveData<List<PlaceWithHistoryDetailEntity>> placesLiveData = placeDao.getRecentlyVisitedPlaces();
        return Transformations.map(placesLiveData, placeEntities -> transformFromPlaceEntity(placeEntities));
    }

    public LiveData<List<Place>> getMostVisitedPlaces() {
        LiveData<List<PlaceWithHistoryDetailEntity>> placesLiveData = placeDao.getMostVisitedPlaces();
        return Transformations.map(placesLiveData, placeEntities -> transformFromPlaceEntity(placeEntities));
    }

    public LiveData<PlaceSummary> getPlaceSummary() {
        return placeDao.getPlaceSummary();
    }

    public LiveData<List<Place>> getPlaces() {
        LiveData<List<PlaceWithHistoryDetailEntity>> placesLiveData = placeDao.getPlaceWithHistories();
        return Transformations.map(placesLiveData, placeEntities -> transformFromPlaceEntity(placeEntities));
    }

    public LiveData<Place> getPlaceById(String placeId) {
        LiveData<PlaceWithHistoryDetailEntity> placeLiveData = placeDao.getPlaceWithHistoriesByIdASync(placeId);
        return Transformations.map(placeLiveData, placeEntity -> toPlace(placeEntity));
    }

    public LiveData<List<GoogleMapNearByDto.NearBy>> searchNearBy(Place place) {
        nearByPlacesLiveData = new MutableLiveData<>();
        startSearchingNearBy(place);
        return nearByPlacesLiveData;
    }

    private void startSearchingNearBy(Place place) {
        List<GoogleMapNearByDto.NearBy> nearByList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<GoogleMapNearByDto.NearBy>> nearByListFuture = executor.submit(new Callable<List<GoogleMapNearByDto.NearBy>>() {
            @Override
            public List<GoogleMapNearByDto.NearBy> call() throws Exception {
                String location = place.latitude + "," + place.longitude;
                Response<GoogleMapNearByDto> searchNearByLocationResponse = googleService.searchNearByLocation(location, "distance").execute();
                if (searchNearByLocationResponse.isSuccessful()) {
                    GoogleMapNearByDto nearByPlace = searchNearByLocationResponse.body();
                    return nearByPlace.results;
                }
                return new ArrayList<>();
            }
        });
        try {
            nearByList = nearByListFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();

        nearByPlacesLiveData.setValue(nearByList);
    }

    private List<Place> transformFromPlaceEntity(List<PlaceWithHistoryDetailEntity> placeEntities) {
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
        place.photoReference = placeEntity.place.photoReference;
        place.confirmed = placeEntity.place.confirmed;
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
}