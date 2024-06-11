package com.example.abzmusicplayer.model;

import android.util.SparseIntArray;

import java.io.Serializable;
import java.util.ArrayList;

public class PlaylistModel implements Serializable {
    String playListId, userId, playListName;

    public PlaylistModel() {
    }

    public PlaylistModel(String playListId, String userId, String playListName) {
        this.playListId = playListId;
        this.userId = userId;
        this.playListName = playListName;
    }

    public String getPlayListId() {
        return playListId;
    }

    public void setPlayListId(String playListId) {
        this.playListId = playListId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlayListName() {
        return playListName;
    }

    public void setPlayListName(String playListName) {
        this.playListName = playListName;
    }
}
