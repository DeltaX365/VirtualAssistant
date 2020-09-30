package com.vnsolution.virtualassistant.service.stop_tracking;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.vnsolution.virtualassistant.VirtualAssistantApp;
import com.vnsolution.virtualassistant.api.GoogleApi;
import com.vnsolution.virtualassistant.api.dto.GoogleMapPlaceSearchDto;
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.entity.PlaceEntity;
import com.vnsolution.virtualassistant.model.StopPoint;

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

public class StopTrackingWorker extends Worker {
    private static boolean isProcessing;
    private Context context;

    @Inject
    public PlaceDao placeDao;

    @Inject
    public GoogleApi googleService;

    public StopTrackingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        ((VirtualAssistantApp) (getApplicationContext())).appComponent.inject(this);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        doSync();
        return Result.success();
    }

    private void doSync() {
        if (isProcessing) return;
        try {
            isProcessing = true;
            List<PlaceEntity> unsyncedPlaces = placeDao.getUnsyncedPlaces();
            for (PlaceEntity placeEntity : unsyncedPlaces) {
                if (placeEntity.ggPlaceId == null || placeEntity.ggPlaceId.isEmpty()
                        || placeEntity.placeName == null || placeEntity.placeName.isEmpty()) {
                    StopPoint stopPoint = new StopPoint(placeEntity.longitude, placeEntity.latitude, new Date(), new Date());
                    GoogleMapPlaceSearchDto.PlaceSearch placeSearch = searchPlaceByAddress(stopPoint);
                    if (placeSearch != null) {
                        String placeId = placeEntity.id;
                        placeEntity = convertToPlaceEntityFrom(placeSearch);
                        placeEntity.id = placeId;
                    }
                }
                placeEntity.synced = 1;
                placeDao.update(placeEntity);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(exception);
        }
        finally {
            isProcessing = false;
        }
    }

    /// Duplicate methods - Begin of StopTrackingService - Consider to refactor later
    /// Move them to StopTrackingManager??? or StopTrackingUtil, StopTrackingHelper
    private GoogleMapPlaceSearchDto.PlaceSearch searchPlaceByAddress(StopPoint stopPoint) {
        Address address = getAddressFromLocation(stopPoint);
        if (address != null && address.getAddressLine(0) != null && address.getAddressLine(0) != "") {
            String search = "point_of_interest, " + address.getAddressLine(0);
            GoogleMapPlaceSearchDto.PlaceSearch candidate = searchPlaceByText(search);
            return candidate;
        }
        return null;
    }

    private PlaceEntity convertToPlaceEntityFrom(GoogleMapPlaceSearchDto.PlaceSearch candidate) {
        PlaceEntity placeEntity = new PlaceEntity();
        placeEntity.id = UUID.randomUUID().toString();
        placeEntity.ggPlaceId = candidate.placeId;
        placeEntity.placeName = candidate.name;
        placeEntity.formattedAddress = candidate.formattedAddress;
        double latitude = (candidate.geometry != null && candidate.geometry.location != null)? candidate.geometry.location.lat : 0;
        double longitude = (candidate.geometry != null && candidate.geometry.location != null)? candidate.geometry.location.lng : 0;
        latitude = Math.round(latitude * Math.pow(10, 7)) / Math.pow(10, 7);
        longitude = Math.round(longitude * Math.pow(10, 7)) / Math.pow(10, 7);
        placeEntity.latitude = latitude;
        placeEntity.longitude = longitude;
        placeEntity.plusCodeCompound = (candidate.plusCode != null)? candidate.plusCode.compoundCode : "";
        placeEntity.plusCodeGlobal = (candidate.plusCode != null)? candidate.plusCode.globalCode : "";
        placeEntity.types = join(", ", candidate.types);
        placeEntity.icon = candidate.icon;
        placeEntity.rating = candidate.rating;
        placeEntity.photoReference = (candidate.photos != null && candidate.photos.size() > 0)? candidate.photos.get(0).photoReference : "";

        if (candidate.formattedAddress != null && !candidate.formattedAddress.isEmpty()) {
            String[] splittedAddress = candidate.formattedAddress.split(",");
            if (splittedAddress.length == 5) {
                placeEntity.route = splittedAddress[0];
                placeEntity.administrativeAreaLevel3 = splittedAddress[1];
                placeEntity.administrativeAreaLevel2 = splittedAddress[2];
                placeEntity.administrativeAreaLevel1 = splittedAddress[3];
                placeEntity.country = splittedAddress[4];
            }
        }

        placeEntity.synced = 1;
        placeEntity.createdDate = new Date();
        return placeEntity;
    }

    private Address getAddressFromLocation(StopPoint stopPoint) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(stopPoint.latitude, stopPoint.longitude,1);
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

    private GoogleMapPlaceSearchDto.PlaceSearch searchPlaceByText(String search) {
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
    /// Duplicate methods - End
}
