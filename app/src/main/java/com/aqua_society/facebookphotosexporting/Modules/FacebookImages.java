package com.aqua_society.facebookphotosexporting.Modules;

/**
 * Created by MrCharif on 20/03/2017.
 */

public class FacebookImages {
    private int height;
    private int width;
    private String source;

    public int getHeight() {
        return height;
    }

    public FacebookImages setHeight(int height) {
        this.height = height;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public FacebookImages setWidth(int width) {
        this.width = width;
        return this;
    }

    public String getSource() {
        return source;
    }

    public FacebookImages setSource(String source) {
        this.source = source;
        return this;
    }
}
