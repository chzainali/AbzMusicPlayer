package com.example.abzmusicplayer.model;

import java.io.Serializable;

public class SongsModel implements Serializable {
    String songId, userId, playListId, songName;

    public SongsModel() {
    }

    public SongsModel(String songId, String userId, String playListId, String songName) {
        this.songId = songId;
        this.userId = userId;
        this.playListId = playListId;
        this.songName = songName;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlayListId() {
        return playListId;
    }

    public void setPlayListId(String playListId) {
        this.playListId = playListId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
}
