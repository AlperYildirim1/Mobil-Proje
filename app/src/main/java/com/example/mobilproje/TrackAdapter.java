package com.example.mobilproje;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
    private List<Track> tracks;
    private OnTrackClickListener listener;

    public interface OnTrackClickListener {
        void onTrackClick(Track track);
    }

    public TrackAdapter(List<Track> tracks, OnTrackClickListener listener) {
        this.tracks = tracks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.track_item, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.trackNameText.setText(track.getName());

        // Get artist name
        String artistName = track.getArtists().length > 0 ?
                track.getArtists()[0].getName() : "Unknown Artist";
        holder.artistNameText.setText(artistName);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrackClick(track);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView trackNameText;
        TextView artistNameText;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            trackNameText = itemView.findViewById(R.id.trackNameText);
            artistNameText = itemView.findViewById(R.id.artistNameText);
        }
    }
}