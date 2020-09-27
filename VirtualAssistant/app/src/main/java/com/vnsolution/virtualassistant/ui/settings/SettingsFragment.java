package com.vnsolution.virtualassistant.ui.settings;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.StopTracking;
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtualassistant.di.Injectable;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import javax.inject.Inject;

public class SettingsFragment extends Fragment implements Injectable {
    private Context mContext;
    private String[] settings = { "Grant stop tracking permissions", "Reset factory" };

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Inject
    public PlaceHistoryDao placeHistoryDao;

    @Inject
    public PlaceDao placeDao;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.screen_settings, container, false);

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_setting, settings);
        ListView listSettings = (ListView) root.findViewById(R.id.list_settings);
        listSettings.setAdapter(adapter);
        listSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        requestPermissions();
                        break;
                    case 1:
                        runResetFactory();
                        Toast.makeText(getActivity(), "Data is reset successfully.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        return root;
    }

    private boolean requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionUtils.hasPermissions(getActivity(), StopTracking.PERMISSIONS_ACTIVITY_RECOGNITION)) {
                requestPermissions(StopTracking.PERMISSIONS_ACTIVITY_RECOGNITION, StopTracking.PERMISSIONS_REQUEST_CODE);
                return false;
            }
            Intent intentUA = new Intent(StopTracking.BROADCAST_PERMISSIONS_USER_ACTIVITY_GRANTED);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intentUA);

            if (!PermissionUtils.hasPermissions(getActivity(), StopTracking.PERMISSIONS_LOCATION)) {
                requestPermissions(StopTracking.PERMISSIONS_LOCATION, StopTracking.PERMISSIONS_REQUEST_CODE);
                return false;
            }
            Intent intentL = new Intent(StopTracking.BROADCAST_PERMISSIONS_LOCATION_GRANTED);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intentL);

            Intent intentA = new Intent(StopTracking.BROADCAST_PERMISSIONS_ALL_GRANTED);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intentA);
        }
        Toast.makeText(getActivity(), "Stop tracking permissions have been granted", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == StopTracking.PERMISSIONS_REQUEST_CODE) {
            requestPermissions();
        }
    }

    private void runResetFactory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                placeHistoryDao.reset();
                placeDao.reset();
            }
        }).start();
    }
}
