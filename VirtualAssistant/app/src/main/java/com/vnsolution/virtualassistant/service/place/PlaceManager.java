package com.vnsolution.virtualassistant.service.place;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.vnsolution.virtualassistant.api.GoogleApi;
import com.vnsolution.virtualassistant.api.dto.GoogleMapNearByDto;
import com.vnsolution.virtualassistant.api.dto.GoogleMapPlaceDetailDto;
import com.vnsolution.virtualassistant.api.dto.GoogleMapPlaceDto;
import com.vnsolution.virtualassistant.api.dto.GoogleMapPlaceSearchDto;
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtualassistant.db.entity.PlaceEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceHistoryEntity;
import com.vnsolution.virtualassistant.model.PlaceType;
import com.vnsolution.virtualassistant.model.StopPoint;
import com.vnsolution.virtualassistant.ui.travel.TravelEditActivity;
import com.vnsolution.virtualassistant.util.MappingUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import retrofit2.Response;

public class PlaceManager {
    @Inject
    public PlaceDao placeDao;

    @Inject
    public PlaceHistoryDao placeHistoryDao;

    @Inject
    public GoogleApi googleService;

    @Inject
    public PlaceManager() {}

    public PlaceEntity getPlaceById(String placeId) {
        return placeDao.getById(placeId);
    }

    public void sync(Context context) {
        List<PlaceEntity> unsyncedPlaces = placeDao.getUnsyncedPlaces();
        for (PlaceEntity placeEntity : unsyncedPlaces) {
            if (placeEntity.ggPlaceId == null || placeEntity.ggPlaceId.isEmpty()
                    || placeEntity.placeName == null || placeEntity.placeName.isEmpty()) {
                Address address = getAddressByLocation(context, placeEntity.latitude, placeEntity.longitude);
                GoogleMapPlaceSearchDto.PlaceSearch placeSearch = searchPlaceByAddress(address);
                if (placeSearch != null) {
                    PlaceEntity matchedPlaceEntity = placeDao.getByGGPlaceId(placeSearch.placeId);
                    if (matchedPlaceEntity == null) {
                        String placeId = placeEntity.id;
                        placeEntity = MappingUtils.convertPlaceSearchDtoToPlaceEntity(placeSearch);
                        placeEntity.id = placeId;
                    } else {
                        placeHistoryDao.transfer(placeEntity.id, matchedPlaceEntity.id);
                        placeDao.delete(placeEntity);
                        continue;
                    }
                }
            }
            placeEntity.synced = 1;
            placeDao.update(placeEntity);
        }
    }

    public PlaceEntity savePlace(Context context, PlaceEntity placeEntity) {
        PlaceEntity existingPlaceEntity = placeDao.getById(placeEntity.id);
        if (existingPlaceEntity == null) {
            existingPlaceEntity = placeDao.getByLocation(placeEntity.latitude, placeEntity.longitude);
        }
        PlaceEntity newPlaceEntity = null;
        if (existingPlaceEntity == null) {
            Address address = getAddressByLocation(context, placeEntity.latitude, placeEntity.longitude);
            newPlaceEntity = MappingUtils.convertAddressToPlaceEntity(address);
            GoogleMapPlaceSearchDto.PlaceSearch placeDetail = searchPlaceByAddress(address);
            if (placeDetail != null) {
                newPlaceEntity = MappingUtils.convertPlaceSearchDtoToPlaceEntity(placeDetail);
            }
            if (newPlaceEntity != null) {
                existingPlaceEntity = placeDao.getByName(newPlaceEntity.placeName);
                if (existingPlaceEntity == null) {
                    existingPlaceEntity = placeDao.getByGGPlaceId(newPlaceEntity.ggPlaceId);
                }
                if (existingPlaceEntity == null) {
                    existingPlaceEntity = placeDao.getByAddress(newPlaceEntity.formattedAddress);
                }
            }
        }
        if (existingPlaceEntity == null) {
            existingPlaceEntity = (newPlaceEntity != null) ? newPlaceEntity : placeEntity;
            placeDao.insert(existingPlaceEntity);
        } else {
            if (((existingPlaceEntity.ggPlaceId == null) || (existingPlaceEntity.ggPlaceId == "")
                    || (existingPlaceEntity.placeName == null) || (existingPlaceEntity.placeName == "")) && newPlaceEntity != null) {
                String placeId = existingPlaceEntity.id;
                existingPlaceEntity = newPlaceEntity;
                existingPlaceEntity.id = placeId;
                placeDao.update(existingPlaceEntity);
            }
        }
        return existingPlaceEntity;
    }

    public void visitPlace(PlaceEntity placeEntity, Date enter, Date exist) {
        if (placeEntity != null) {
            PlaceHistoryEntity placeHistoryEntity = new PlaceHistoryEntity();
            placeHistoryEntity.id = UUID.randomUUID().toString();
            placeHistoryEntity.placeId = placeEntity.id;
            placeHistoryEntity.enterTime = enter;
            placeHistoryEntity.existTime = exist;
            placeHistoryDao.insert(placeHistoryEntity);
        }
    }

    public PlaceEntity confirmDetectedPlace(String placeId) {
        PlaceEntity placeEntity = placeDao.getById(placeId);
        if (placeEntity != null && placeEntity.confirmed == 0) {
            placeEntity.confirmed = 1;
            placeDao.update(placeEntity);
        }
        return placeEntity;
    }

    public PlaceEntity confirmUpdatedPlace(String placeId, GoogleMapNearByDto.NearBy nearBy) {
        PlaceEntity placeEntity = null;
        if (placeId != null && placeId != "" && nearBy != null) {
            placeEntity = placeDao.getByGGPlaceId(nearBy.placeId);
            if (placeEntity == null) {
                placeEntity = MappingUtils.convertNearByDtoToPlaceEntity(nearBy);
                placeEntity.id = placeId;
                placeEntity.confirmed = 1;
                placeEntity.updatedDate = new Date();
                placeDao.update(placeEntity);
            } else {
                placeHistoryDao.transfer(placeId, placeEntity.id);
                placeDao.delete(placeDao.getById(placeId));
            }
        }
        return placeEntity;
    }

    public PlaceEntity confirmUpdatedPlace(String placeId, GoogleMapPlaceDetailDto.PlaceDetail placeDetail) {
        PlaceEntity placeEntity = null;
        if (placeId != null && placeId != "" && placeDetail != null) {
            placeEntity = placeDao.getByGGPlaceId(placeDetail.placeId);
            if (placeEntity == null) {
                placeEntity = MappingUtils.convertPlaceDetailToPlaceEntity(placeDetail);
                placeEntity.id = placeId;
                placeEntity.confirmed = 1;
                placeEntity.updatedDate = new Date();
                placeDao.update(placeEntity);
            } else {
                placeHistoryDao.transfer(placeId, placeEntity.id);
                placeDao.delete(placeDao.getById(placeId));
            }
        }
        return placeEntity;
    }

    public GoogleMapPlaceSearchDto.PlaceSearch searchPlaceByAddress(Address address) {
        if (address != null && address.getAddressLine(0) != null && address.getAddressLine(0) != "") {
            String search = "point_of_interest, " + address.getAddressLine(0);
            GoogleMapPlaceSearchDto.PlaceSearch candidate = searchPlaceByText(search);
            return candidate;
        }
        return null;
    }

    public GoogleMapPlaceSearchDto.PlaceSearch searchPlaceByText(String search) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<GoogleMapPlaceSearchDto.PlaceSearch>> placeSearchListFuture = executor.submit(new Callable<List<GoogleMapPlaceSearchDto.PlaceSearch>>() {
            @Override
            public List<GoogleMapPlaceSearchDto.PlaceSearch> call() throws Exception {
                Response<GoogleMapPlaceSearchDto> placeSearchResponse = googleService.searchPlaceByAddress(search, "textquery", "business_status,formatted_address,geometry,icon,name,photos,place_id,plus_code,types").execute();
                if (placeSearchResponse.isSuccessful()) {
                    GoogleMapPlaceSearchDto placeSearch = placeSearchResponse.body();
                    return placeSearch.candidates;
                }
                return new ArrayList<>();
            }
        });
        GoogleMapPlaceSearchDto.PlaceSearch candidate = null;
        try {
            List<GoogleMapPlaceSearchDto.PlaceSearch> candidates = placeSearchListFuture.get();
            if (candidates != null && !candidates.isEmpty()) {
                candidate = candidates.get(0);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return candidate;
    }

    public GoogleMapNearByDto.NearBy searchNearByFromLocation(String location) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<GoogleMapNearByDto.NearBy>> nearByListFuture = executor.submit(new Callable<List<GoogleMapNearByDto.NearBy>>() {
            @Override
            public List<GoogleMapNearByDto.NearBy> call() throws Exception {
                Response<GoogleMapNearByDto> searchNearByLocationResponse = googleService.searchNearByLocation(location, "distance").execute();
                if (searchNearByLocationResponse.isSuccessful()) {
                    GoogleMapNearByDto nearByPlace = searchNearByLocationResponse.body();
                    return nearByPlace.results;
                }
                return new ArrayList<>();
            }
        });
        GoogleMapNearByDto.NearBy nearBy = null;
        try {
            List<GoogleMapNearByDto.NearBy> nearByList = nearByListFuture.get();
            if (nearByList != null && !nearByList.isEmpty()) {
                nearBy = nearByList.get(0);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
//        String location = stopPoint.latitude + "," + stopPoint.longitude;
//        googleService.searchNearByLocation(location, "distance").observeForever(new Observer<ApiResponse<GoogleMapNearByDto>>() {
//            @Override
//            public void onChanged(ApiResponse<GoogleMapNearByDto> googleMapNearByDtoApiResponse) {
//                if (googleMapNearByDtoApiResponse instanceof ApiSuccessResponse) {
//                    GoogleMapNearByDto googleMapNearByDto = ((ApiSuccessResponse<GoogleMapNearByDto>)googleMapNearByDtoApiResponse).getBody();
//                }
//                if (googleMapNearByDtoApiResponse instanceof ApiErrorResponse) {
//
//                }
//                if (googleMapNearByDtoApiResponse instanceof ApiEmptyResponse) {
//
//                }
//            }
//        });
        return nearBy;
    }

    public GoogleMapPlaceDto.Place getPlaceByLocation(String location) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<GoogleMapPlaceDto.Place>> placeFuture = executor.submit(new Callable<List<GoogleMapPlaceDto.Place>>() {
            @Override
            public List<GoogleMapPlaceDto.Place> call() throws Exception {
                Response<GoogleMapPlaceDto> placeResponse = googleService.getPlaceByLocation(location).execute();
                if (placeResponse.isSuccessful()) {
                    GoogleMapPlaceDto place = placeResponse.body();
                    return place.results;
                }
                return new ArrayList<>();
            }
        });
        GoogleMapPlaceDto.Place place = null;
        try {
            List<GoogleMapPlaceDto.Place> placeList = placeFuture.get();
            if (placeList != null && !placeList.isEmpty()) {
                place = placeList.get(0);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return place;
    }

    public GoogleMapPlaceDetailDto.PlaceDetail getPlaceDetailById(String placeId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<GoogleMapPlaceDetailDto.PlaceDetail> placeDetailFuture = executor.submit(new Callable<GoogleMapPlaceDetailDto.PlaceDetail>() {
            @Override
            public GoogleMapPlaceDetailDto.PlaceDetail call() throws Exception {
                Response<GoogleMapPlaceDetailDto> placeDetailResponse = googleService.getPlaceDetail(placeId).execute();
                if (placeDetailResponse.isSuccessful()) {
                    GoogleMapPlaceDetailDto placeDetail = placeDetailResponse.body();
                    return placeDetail.result;
                }
                return null;
            }
        });
        GoogleMapPlaceDetailDto.PlaceDetail placeDetail = null;
        try {
            placeDetail = placeDetailFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return placeDetail;
    }

    public GoogleMapPlaceDetailDto.PlaceDetail getPlaceDetailByLocation(String location) {
        GoogleMapPlaceDto.Place place = getPlaceByLocation(location);
        if (place != null && place.placeId != null && place.placeId != "") {
            GoogleMapPlaceDetailDto.PlaceDetail placeDetail = getPlaceDetailById(place.placeId);
            return placeDetail;
        }
        return null;
    }

    public Address getAddressByLocation(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude,1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                return address;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(exception);
        }
        return null;
    }

    public List<PlaceType> getPlaceTypes() {
        List<PlaceType> placeTypes = new ArrayList<>();

        placeTypes.add(new PlaceType("accounting", "Accounting"));
        placeTypes.add(new PlaceType("airport", "Airport"));
        placeTypes.add(new PlaceType("amusement_park", "Amusement Park"));
        placeTypes.add(new PlaceType("aquarium", "Aquarium"));
        placeTypes.add(new PlaceType("art_gallery", "Art Gallery"));
        placeTypes.add(new PlaceType("atm", "Atm"));
        placeTypes.add(new PlaceType("bakery", "Bakery"));
        placeTypes.add(new PlaceType("bank", "Bank"));
        placeTypes.add(new PlaceType("bar", "Bar"));
        placeTypes.add(new PlaceType("beauty_salon", "Beauty Salon"));
        placeTypes.add(new PlaceType("bicycle_store", "Bicycle Store"));
        placeTypes.add(new PlaceType("book_store", "Book Store"));
        placeTypes.add(new PlaceType("bowling_alley", "Bowling Alley"));
        placeTypes.add(new PlaceType("bus_station", "Bus Station"));
        placeTypes.add(new PlaceType("cafe", "Cafe"));
        placeTypes.add(new PlaceType("campground", "Campground"));
        placeTypes.add(new PlaceType("car_dealer", "Car Dealer"));
        placeTypes.add(new PlaceType("car_rental", "Car Rental"));
        placeTypes.add(new PlaceType("car_repair", "Car Repair"));
        placeTypes.add(new PlaceType("car_wash", "Car Wash"));
        placeTypes.add(new PlaceType("casino", "Casino"));
        placeTypes.add(new PlaceType("cemetery", "Cemetery"));
        placeTypes.add(new PlaceType("church", "Church"));
        placeTypes.add(new PlaceType("city_hall", "City Hall"));
        placeTypes.add(new PlaceType("clothing_store", "Clothing Store"));
        placeTypes.add(new PlaceType("convenience_store", "Convenience Store"));
        placeTypes.add(new PlaceType("courthouse", "Courthouse"));
        placeTypes.add(new PlaceType("dentist", "Dentist"));
        placeTypes.add(new PlaceType("department_store", "Department Store"));
        placeTypes.add(new PlaceType("doctor", "Doctor"));
        placeTypes.add(new PlaceType("drugstore", "Drugstore"));
        placeTypes.add(new PlaceType("electrician", "Electrician"));
        placeTypes.add(new PlaceType("electronics_store", "Electronics Store"));
        placeTypes.add(new PlaceType("embassy", "Embassy"));
        placeTypes.add(new PlaceType("fire_station", "Fire Station"));
        placeTypes.add(new PlaceType("florist", "Florist"));
        placeTypes.add(new PlaceType("funeral_home", "Funeral Home"));
        placeTypes.add(new PlaceType("furniture_store", "Furniture Store"));
        placeTypes.add(new PlaceType("gas_station", "Gas Station"));
        placeTypes.add(new PlaceType("gym", "Gym"));
        placeTypes.add(new PlaceType("hair_care", "Hair Care"));
        placeTypes.add(new PlaceType("hardware_store", "Hardware Store"));
        placeTypes.add(new PlaceType("hindu_temple", "Hindu Temple"));
        placeTypes.add(new PlaceType("home_goods_store", "Home Goods Store"));
        placeTypes.add(new PlaceType("hospital", "Hospital"));
        placeTypes.add(new PlaceType("insurance_agency", "Insurance Agency"));
        placeTypes.add(new PlaceType("jewelry_store", "Jewelry Store"));
        placeTypes.add(new PlaceType("lawyer", "Lawyer"));
        placeTypes.add(new PlaceType("library", "Library"));
        placeTypes.add(new PlaceType("light_rail_station", "Light Rail Station"));
        placeTypes.add(new PlaceType("liquor_store", "Liquor Store"));
        placeTypes.add(new PlaceType("local_government_office", "Local Government Office"));
        placeTypes.add(new PlaceType("locksmith", "Locksmith"));
        placeTypes.add(new PlaceType("lodging", "Lodging"));
        placeTypes.add(new PlaceType("meal_delivery", "Meal Delivery"));
        placeTypes.add(new PlaceType("meal_takeaway", "Meal Takeaway"));
        placeTypes.add(new PlaceType("mosque", "Mosque"));
        placeTypes.add(new PlaceType("movie_rental", "Movie Rental"));
        placeTypes.add(new PlaceType("movie_theater", "Movie Theater"));
        placeTypes.add(new PlaceType("moving_company", "Moving Company"));
        placeTypes.add(new PlaceType("museum", "Museum"));
        placeTypes.add(new PlaceType("night_club", "Night Club"));
        placeTypes.add(new PlaceType("painter", "Painter"));
        placeTypes.add(new PlaceType("park", "Park"));
        placeTypes.add(new PlaceType("parking", "Parking"));
        placeTypes.add(new PlaceType("pet_store", "Pet Store"));
        placeTypes.add(new PlaceType("pharmacy", "Pharmacy"));
        placeTypes.add(new PlaceType("physiotherapist", "Physiotherapist"));
        placeTypes.add(new PlaceType("plumber", "Plumber"));
        placeTypes.add(new PlaceType("police", "Police"));
        placeTypes.add(new PlaceType("post_office", "Post Office"));
        placeTypes.add(new PlaceType("primary_school", "Primary School"));
        placeTypes.add(new PlaceType("real_estate_agency", "Real Estate Agency"));
        placeTypes.add(new PlaceType("restaurant", "Restaurant"));
        placeTypes.add(new PlaceType("roofing_contractor", "Roofing Contractor"));
        placeTypes.add(new PlaceType("rv_park", "Rv Park"));
        placeTypes.add(new PlaceType("school", "School"));
        placeTypes.add(new PlaceType("secondary_school", "Secondary School"));
        placeTypes.add(new PlaceType("shoe_store", "Shoe Store"));
        placeTypes.add(new PlaceType("shopping_mall", "Shopping Mall"));
        placeTypes.add(new PlaceType("spa", "Spa"));
        placeTypes.add(new PlaceType("stadium", "Stadium"));
        placeTypes.add(new PlaceType("storage", "Storage"));
        placeTypes.add(new PlaceType("store", "Store"));
        placeTypes.add(new PlaceType("subway_station", "Subway Station"));
        placeTypes.add(new PlaceType("supermarket", "Supermarket"));
        placeTypes.add(new PlaceType("synagogue", "Synagogue"));
        placeTypes.add(new PlaceType("taxi_stand", "Taxi Stand"));
        placeTypes.add(new PlaceType("tourist_attraction", "Tourist Attraction"));
        placeTypes.add(new PlaceType("train_station", "Train Station"));
        placeTypes.add(new PlaceType("transit_station", "Transit Station"));
        placeTypes.add(new PlaceType("travel_agency", "Travel Agency"));
        placeTypes.add(new PlaceType("university", "University"));
        placeTypes.add(new PlaceType("veterinary_care", "Veterinary Care"));
        placeTypes.add(new PlaceType("zoo", "Zoo"));

        return placeTypes;
    }
}
