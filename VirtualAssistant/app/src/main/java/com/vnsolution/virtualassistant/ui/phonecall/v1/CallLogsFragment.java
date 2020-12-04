package com.vnsolution.virtualassistant.ui.phonecall.v1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.di.Injectable;
import com.vnsolution.virtualassistant.model.PhoneCall;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CallLogsFragment extends Fragment implements Injectable {
    private static final int REQUEST_CODE = 1001;
    private CallLogsRecycleView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FrameLayout progressOverlay;

    private CallLogsAdapter callLogsAdapter;
    private List<PhoneCall> callHistory = new ArrayList<>();
    private Context mContext;

    @Inject
    public CallLogsViewModel callLogsViewModel;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

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
        View root = inflater.inflate(R.layout.screen_phone_call, container, false);

        if (mContext != null) {
            recyclerView = root.findViewById(R.id.list_sms);
            progressOverlay =  root.findViewById(R.id.progress_overlay);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);

            // specify an adapter
            callLogsAdapter = new CallLogsAdapter(mContext, callHistory);
            recyclerView.setAdapter(callLogsAdapter);

            // retrieve data
            getCallLogs();
        }

        return root;
    }

    private void getCallLogs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.READ_CALL_LOG }, REQUEST_CODE);
                return;
            }
        }

        // retrieve data
        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressOverlay.setAnimation(inAnimation);
        progressOverlay.setVisibility(View.VISIBLE);

        callLogsViewModel.getCallLogs(mContext,false).observe(getViewLifecycleOwner(), new Observer<List<PhoneCall>>() {
            @Override
            public void onChanged(List<PhoneCall> phoneCalls) {
                callHistory = phoneCalls;
                callLogsAdapter.setData(phoneCalls);

                AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressOverlay.setAnimation(outAnimation);
                progressOverlay.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            getCallLogs();
        }
    }
}
