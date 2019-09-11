package com.practice.solarcalculator.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.solarcalculator.activity.MapsActivity;
import com.practice.solarcalculator.R;
import com.practice.solarcalculator.db.model.RecentLocation;

import java.util.List;

public class RecentLocationAdapter extends RecyclerView.Adapter<RecentLocationAdapter.ViewHolder> {

    private final List<RecentLocation> locations;
    private OnItemClickListener listener;

    public RecentLocationAdapter(List<RecentLocation> locations, MapsActivity activity) {
        this.locations = locations;
        this.listener = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_row_recent_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(locations.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public interface OnItemClickListener {

        /**
         * Called when any item with in recycler view or any item with in item
         * clicked
         *
         * @param obj data object of clicked item.
         */
        void onItemClicked(RecentLocation obj);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mExtras;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.textView_location_name);
            mExtras = itemView.findViewById(R.id.textView_extra);
        }

        void bind(final RecentLocation location, final OnItemClickListener listener) {
            mName.setText(location.getLocationName());
            mExtras.setText(location.getExtra());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener !=null)
                        listener.onItemClicked(location);
                }
            });
        }
    }
}
