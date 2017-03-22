package com.aqua_society.facebookphotosexporting.Modules;

import java.util.List;

/**
 * Created by MrCharif on 20/03/2017.
 */

public class FacebookPhotos {

    private String id;
    private FacebookImages images;
    public boolean selected = false;

    public String getId() {
        return id;
    }

    public FacebookPhotos setId(String id) {
        this.id = id;
        return this;
    }

    public FacebookImages getImages() {
        return images;
    }

    public FacebookPhotos setImages(FacebookImages images) {
        this.images = images;
        return this;
    }
}
