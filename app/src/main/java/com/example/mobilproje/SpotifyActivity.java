package com.example.mobilproje;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpotifyActivity extends AppCompatActivity implements TrackAdapter.OnTrackClickListener {
    private SpotifyApi spotifyApi;
    private TextView selectedTrackText;
    private EditText songInput;
    private RecyclerView tracksRecyclerView;
    private TrackAdapter trackAdapter;
    private List<Track> trackList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);

        // Initialize UI elements
        selectedTrackText = findViewById(R.id.selectedTrackText);
        songInput = findViewById(R.id.songInput);
        Button actionButton = findViewById(R.id.actionButton);
        tracksRecyclerView = findViewById(R.id.tracksRecyclerView);

        // Set up RecyclerView
        trackAdapter = new TrackAdapter(trackList, this);
        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tracksRecyclerView.setAdapter(trackAdapter);

        // Set up Spotify API with Client ID and Client Secret
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(getString(R.string.spotify_client_id))
                .setClientSecret(getString(R.string.spotify_client_secret))
                .build();

        // Set up button to search for a song
        actionButton.setOnClickListener(v -> {
            String query = songInput.getText().toString().trim();
            if (!query.isEmpty()) {
                searchSong(query);
            } else {
                Toast.makeText(this, "Please enter a song name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchSong(String query) {
        new Thread(() -> {
            try {
                // Authenticate using Client Credentials flow
                ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
                ClientCredentials clientCredentials = clientCredentialsRequest.execute();
                spotifyApi.setAccessToken(clientCredentials.getAccessToken());

                // Search for tracks with limit of 10 results
                SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(query)
                        .limit(10)
                        .build();
                Paging<Track> trackPaging = searchTracksRequest.execute();

                // Update the track list
                runOnUiThread(() -> {
                    trackList.clear();
                    if (trackPaging.getItems().length > 0) {
                        for (Track track : trackPaging.getItems()) {
                            trackList.add(track);
                        }
                        trackAdapter.notifyDataSetChanged();
                        selectedTrackText.setText("Click on a track to select it");
                    } else {
                        selectedTrackText.setText("No tracks found for: " + query);
                    }
                });

            } catch (IOException | SpotifyWebApiException | ParseException e) {
                // Handle errors on the main thread
                runOnUiThread(() -> {
                    selectedTrackText.setText("Error: " + e.getMessage());
                    Toast.makeText(this, "Failed to search: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    @Override
    public void onTrackClick(Track track) {
        // Handle track selection
        String trackName = track.getName();
        String artistName = track.getArtists()[0].getName();
        String albumName = track.getAlbum().getName();
        String releaseDate = track.getAlbum().getReleaseDate();

        String selectedTrackInfo = "Selected Track:\n" +
                "Title: " + trackName + "\n" +
                "Artist: " + artistName + "\n" +
                "Album: " + albumName + "\n" +
                "Released: " + releaseDate;

        selectedTrackText.setText(selectedTrackInfo);

        // Seçilen müzikle ne yapacaksanız burada yapılacak.
        Toast.makeText(this, "Selected: " + trackName, Toast.LENGTH_SHORT).show();
    }
}