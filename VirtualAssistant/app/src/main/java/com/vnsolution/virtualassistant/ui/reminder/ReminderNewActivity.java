package com.vnsolution.virtualassistant.ui.reminder;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.contact.ContactViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class ReminderNewActivity extends BaseActivity implements HasAndroidInjector {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
    }

    private void setupView() {
        setContentView(R.layout.screen_reminder_new);
        setupActionBar();
    }
}
