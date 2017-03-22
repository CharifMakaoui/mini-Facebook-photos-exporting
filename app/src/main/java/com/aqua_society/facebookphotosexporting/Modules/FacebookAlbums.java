package com.aqua_society.facebookphotosexporting.Modules;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by MrCharif on 20/03/2017.
 */

public class FacebookAlbums implements Comparable<FacebookAlbums> {

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public FacebookAlbums setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public FacebookAlbums setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public int compareTo(@NonNull FacebookAlbums o) {
        return this.name.compareTo(o.name);
    }
}
