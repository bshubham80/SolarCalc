package com.practice.solarcalculator.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.solarcalculator.activity.MapsActivity;
import com.practice.solarcalculator.R;
import com.practice.solarcalculator.adapter.RecentLocationAdapter;
import com.practice.solarcalculator.db.DatabaseHandler;
import com.practice.solarcalculator.db.model.RecentLocation;

import java.util.List;

public class RecentLocationDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.activity_recent_location_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DatabaseHandler handler = new DatabaseHandler(view.getContext());
        List<RecentLocation> locations = handler.getAllLocations();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_recent_location);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        RecentLocationAdapter adapter =
                new RecentLocationAdapter(locations, (MapsActivity) getActivity());
        recyclerView.setAdapter(adapter);
    }
}
