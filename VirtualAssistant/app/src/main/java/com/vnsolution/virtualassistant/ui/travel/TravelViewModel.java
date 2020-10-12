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
import com.vnsolution.virtualassistant.model.StopPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private MutableLiveData<List<Place>> nearByPlacesLiveData;

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

    public void setIndex(int index) {
        this.index = index;
    }

    public LiveData<List<Place>> getPlaces() {
        LiveData<List<PlaceWithHistoryDetailEntity>> placesLiveData = placeDao.getPlaceWithHistories();
        return Transformations.map(placesLiveData, placeEntities -> {
            List<Place> places = transformFromPlaceEntity(placeEntities);
            switch (index) {
                case 1: //Top 10 recent visited places
                    Collections.sort(places, new Comparator<Place>() {
                        @Override
                        public int compare(Place o1, Place o2) {
                            return (int) (getLastVisitedTime(o2) - getLastVisitedTime(o1));
                        }
                    });
                    places = places.subList(0, Math.min(places.size(), 10));
                    break;
                case 2: //Top 10 most visited places
                    Collections.sort(places, new Comparator<Place>() {
                        @Override
                        public int compare(Place o1, Place o2) {
                            return (int) (calculateTotalVisitedTime(o2) - calculateTotalVisitedTime(o1));
                        }
                    });
                    places = places.subList(0, Math.min(places.size(), 10));
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
                    places = places.subList(0, Math.min(places.size(), 10));
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

    public LiveData<List<Place>> searchNearBy(StopPoint stopPoint) {
        if (nearByPlacesLiveData == null) {
            nearByPlacesLiveData = new MutableLiveData<>();
            searchNearByFromLocation(stopPoint);
        }
        return nearByPlacesLiveData;
    }

    private void searchNearByFromLocation(StopPoint stopPoint) {
        List<Place> placeList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<GoogleMapNearByDto.NearBy>> nearByListFuture = executor.submit(new Callable<List<GoogleMapNearByDto.NearBy>>() {
            @Override
            public List<GoogleMapNearByDto.NearBy> call() throws Exception {
                String location = stopPoint.latitude + "," + stopPoint.longitude;
                Response<GoogleMapNearByDto> searchNearByLocationResponse = googleService.searchNearByLocation(location, "distance").execute();
                if (searchNearByLocationResponse.isSuccessful()) {
                    GoogleMapNearByDto nearByPlace = searchNearByLocationResponse.body();
                    return nearByPlace.results;
                }
                return new ArrayList<>();
            }
        });
        try {
            List<GoogleMapNearByDto.NearBy> nearByList = nearByListFuture.get();
            placeList = transformFromNearByDto(nearByList);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();

        nearByPlacesLiveData.setValue(placeList);
    }

    private List<Place> transformFromNearByDto(List<GoogleMapNearByDto.NearBy> nearByList) {
        List<Place> places = new ArrayList<>();
        for (GoogleMapNearByDto.NearBy nearBy: nearByList) {
            Place place = toPlace(nearBy);
            places.add(place);
        }
        return places;
    }

    private List<Place> transformFromPlaceEntity(List<PlaceWithHistoryDetailEntity> placeEntities) {
        List<Place> places = new ArrayList<>();
        for (PlaceWithHistoryDetailEntity placeEntity: placeEntities) {
            Place place = toPlace(placeEntity);
            places.add(place);
        }
        return places;
    }

    private Place toPlace(GoogleMapNearByDto.NearBy nearBy) {
        Place place = new Place();
        place.id = UUID.randomUUID().toString();
        place.placeName = nearBy.name;
        place.formattedAddress = nearBy.vicinity;
        double latitude = (nearBy.geometry != null && nearBy.geometry.location != null)? nearBy.geometry.location.lat : 0;
        double longitude = (nearBy.geometry != null && nearBy.geometry.location != null)? nearBy.geometry.location.lng : 0;
        latitude = Math.round(latitude * Math.pow(10, 7)) / Math.pow(10, 7);
        longitude = Math.round(longitude * Math.pow(10, 7)) / Math.pow(10, 7);
        place.latitude = latitude;
        place.longitude = longitude;
        place.types = join(", ", nearBy.types);
        place.icon = nearBy.icon;
        place.createdDate = new Date();
        return place;
    }

    private String join(String delimiter, List<String> types) {
        StringBuffer sb = new StringBuffer();
        if (types != null) {
            for (String type : types) {
                sb.append(type);
                sb.append(delimiter);
            }
        }
        return sb.toString();
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

    private List<Place> getPlacesYesterday(List<Place> places) {
        List<Place> placesYesterday = new ArrayList<>();
        return placesYesterday;
    }

    private int getTotalCity(List<PlaceEntity> places) {
        List<String> cities = new ArrayList<>();
        for (PlaceEntity place : places) {
            if (!cities.contains(place.administrativeAreaLevel1)) {
                cities.add(place.administrativeAreaLevel1);
            }
        }
        return cities.size();
    }

    private int getTotalCountry(List<PlaceEntity> places) {
        List<String> countries = new ArrayList<>();
        for (PlaceEntity place : places) {
            if (!countries.contains(place.country)) {
                countries.add(place.country);
            }
        }
        return countries.size();
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
//        return place.placeHistories.size();
        long total = 0;
        for (PlaceHistory placeHistory: place.placeHistories) {
            total += (placeHistory.enterTime.getTime() - placeHistory.existTime.getTime());
        }
        return total;
    }
}