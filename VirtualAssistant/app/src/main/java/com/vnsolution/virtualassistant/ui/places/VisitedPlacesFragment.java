package com.vnsolution.virtualassistant.ui.places;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;

import java.util.ArrayList;
import java.util.List;

public class VisitedPlacesFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<String> dataSource = new ArrayList<>();
    private VisitedPlacesViewModel visitedPlacesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        visitedPlacesViewModel =
                ViewModelProviders.of(this).get(VisitedPlacesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_visited_places, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.visited_places);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        adapter = new VisitedPlacesAdapter(dataSource);
        recyclerView.setAdapter(adapter);

        visitedPlacesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        return root;
    }
}
