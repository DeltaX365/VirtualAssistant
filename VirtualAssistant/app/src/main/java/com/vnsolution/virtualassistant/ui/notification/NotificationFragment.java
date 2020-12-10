package com.vnsolution.virtualassistant.ui.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.di.Injectable;

import javax.inject.Inject;

public class NotificationFragment extends Fragment implements Injectable {

    private NotificationViewModel notificationViewModel;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(NotificationViewModel.class);
        View root = inflater.inflate(R.layout.screen_notification_list, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        notificationViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
