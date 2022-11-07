package com.dam.musicplayer;

import android.net.Uri;

public class ModelSong {


    String songTitle;
    String songDuration;
    String songArtiste;
    Uri songCover;
    Uri songUri;


    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongArtiste() {
        return songArtiste;
    }

    public void setSongArtiste(String songArtiste) {
        this.songArtiste = songArtiste;
    }

    public Uri getSongCover() {
        return songCover;
    }

    public void setSongCover(Uri songCover) {
        this.songCover = songCover;
    }

    public Uri getSongUri() {
        return songUri;
    }

    public void setSongUri(Uri songUri) {
        this.songUri = songUri;
    }
}
