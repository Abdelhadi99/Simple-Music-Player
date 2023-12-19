package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    RecyclerView recView;
    ArrayList<Song> songs;

    private static final int REQUEST_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recView = findViewById(R.id.recViewSongs);
        songs = new ArrayList<>();


            // Check and request permission when the activity is created
            checkAndRequestPermission();
    }

    private void checkAndRequestPermission() {
        // Check if permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // Permission already granted, perform your operation
            performOperation();
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform your operation
                performOperation();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void performOperation() {
        new Thread(() -> {
            // Check and request permission when the activity is created
          displaySong();
        }).start();

    }

    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.addAll(findSong(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }

    void displaySong() {

        new Thread(() -> {

        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());

        for (int i = 0; i < mySongs.size(); i++) {
            Song song = new Song();
            song.setName(mySongs.get(i).getName().replace(".mp3","").replace(".wav",""));
            song.setLastModified(mySongs.get(i).lastModified()); // Set last modified date
            song.setSong(mySongs.get(i));
            song.setSongs(mySongs);
            songs.add(song);

        }
        // Sort songs by last modified date in descending order (newest to oldest)
        songs.sort(new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {
                // Compare the last modified dates in descending order
                return Long.compare(song2.getLastModified(), song1.getLastModified());
            }
        });

        runOnUiThread(() -> {
        SongsAdapter adapter = new SongsAdapter(this,songs);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(adapter);



        });


        }).start();
    }

}