package com.vnsolution.virtualassistant.ui.places;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VisitedPlacesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public VisitedPlacesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is visited places fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}