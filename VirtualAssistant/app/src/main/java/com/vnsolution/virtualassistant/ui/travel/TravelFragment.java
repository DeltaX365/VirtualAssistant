package com.vnsolution.virtualassistant.ui.travel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.di.Injectable;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TravelFragment extends Fragment implements Injectable {
    public static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView.LayoutManager layoutManager;
    private FrameLayout progressOverlay;

    private List<Place> places = new ArrayList<>();
    private TravelViewModel vm;
    private Context mContext;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Inject
    public TravelFragment() {
        super();
    }

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

    public static TravelFragment newInstance(int index) {
        TravelFragment fragment = new TravelFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = ViewModelProviders.of(this, viewModelFactory).get(TravelViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.screen_travel_dashboard, container, false);

//        if (mContext != null) {
//            recyclerView = root.findViewById(R.id.list_categorized_visited_places);
//            progressOverlay =  root.findViewById(R.id.progress_overlay);
//
//            // use this setting to improve performance if you know that changes
//            // in content do not change the layout size of the RecyclerView
//            recyclerView.setHasFixedSize(true);
//
//            // use a linear layout manager
//            layoutManager = new LinearLayoutManager(mContext);
//            recyclerView.setLayoutManager(layoutManager);
//
//            // specify an adapter
//            adapter = new TravelAdapter(mContext, places);
//            recyclerView.setAdapter(adapter);
//            recyclerView.setOnItemAction(this);
//
//            // retrieve data
//            AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
//            inAnimation.setDuration(200);
//            progressOverlay.setAnimation(inAnimation);
//            progressOverlay.setVisibility(View.VISIBLE);
//
//            vm.getVisitedPlaces().observe(getViewLifecycleOwner(), new Observer<List<Place>>() {
//                @Override
//                public void onChanged(@Nullable List<Place> places) {
//                    TravelFragment.this.places = places;
//                    adapter.setData(places);
//
//                    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
//                    outAnimation.setDuration(200);
//                    progressOverlay.setAnimation(outAnimation);
//                    progressOverlay.setVisibility(View.INVISIBLE);
//                }
//            });
//        }

        return root;
    }
}
