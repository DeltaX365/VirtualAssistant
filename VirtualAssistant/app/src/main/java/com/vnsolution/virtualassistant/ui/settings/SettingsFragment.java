package com.vnsolution.virtualassistant.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtualassistant.di.Injectable;
import com.vnsolution.virtualassistant.model.CallLog;
import com.vnsolution.virtualassistant.ui.calllogs.CallLogsAdapter;
import com.vnsolution.virtualassistant.ui.calllogs.CallLogsRecycleView;
import com.vnsolution.virtualassistant.ui.calllogs.CallLogsViewModel;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SettingsFragment extends Fragment implements Injectable {
    private Context mContext;
    private String[] settings = { "Reset Factory" };

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
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_setting, settings);
        ListView listSettings = (ListView) root.findViewById(R.id.list_settings);
        listSettings.setAdapter(adapter);
        listSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                runResetFactory();
                Toast.makeText(getActivity(), "Data is reset successfully.", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
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
