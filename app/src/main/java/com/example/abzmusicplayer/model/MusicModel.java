package com.example.abzmusicplayer.model;

import java.io.Serializable;

public class MusicModel implements Serializable {
    String musicName, authorName, promotion;
    int image, resourceId;

    public MusicModel() {
    }

    public MusicModel(String musicName, String authorName, String promotion, int image, int resourceId) {
        this.musicName = musicName;
        this.authorName = authorName;
        this.promotion = promotion;
        this.image = image;
        this.resourceId = resourceId;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
}
