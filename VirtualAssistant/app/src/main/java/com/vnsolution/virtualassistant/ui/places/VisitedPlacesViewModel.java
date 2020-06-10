package com.vnsolution.virtualassistant.ui.places;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtualassistant.db.entity.PlaceHistoryEntity;

import java.util.List;

import javax.inject.Inject;

public class VisitedPlacesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    @Inject
    public PlaceHistoryDao placeHistoryDao;

    @Inject
    public PlaceDao placeDao;

    public VisitedPlacesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is visited places fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<PlaceHistoryEntity>> getVisitedPlaces() {
        return placeHistoryDao.getAll();
    }
}