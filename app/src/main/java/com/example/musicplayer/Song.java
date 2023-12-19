package com.example.musicplayer;

import java.io.File;
import java.util.ArrayList;

public class Song {
    private String name;
    private long lastModified;
    File song;
    ArrayList<File> songs ;

    public Song() {
    }


    public Song(String name) {
        this.name = name;
    }

    public ArrayList<File> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<File> songs) {
        this.songs = songs;
    }

    public File getSong() {
        return song;
    }

    public void setSong(File song) {
        this.song = song;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
