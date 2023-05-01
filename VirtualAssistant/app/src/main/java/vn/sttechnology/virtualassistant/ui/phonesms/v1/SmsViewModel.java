package vn.sttechnology.virtualassistant.ui.phonesms.v1;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import vn.sttechnology.virtualassistant.model.PhoneSms;
import vn.sttechnology.virtualassistant.service.device.DeviceManager;

import java.util.List;

import javax.inject.Inject;

public class SmsViewModel extends ViewModel {
    @Inject
    public DeviceManager deviceManager;

    @Inject
    public SmsViewModel() {}

    public LiveData<List<PhoneSms>> getSms(Context context, boolean forceUpdate) {
        return deviceManager.getPhoneSmsListAsync(context, forceUpdate);
    }
}