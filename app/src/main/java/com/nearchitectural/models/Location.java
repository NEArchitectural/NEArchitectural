package com.nearchitectural.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Location {
    private final String id;
    private String name;
    private String locationType;
    private String summary;
    private String firstParagraph;
    private String secondParagraph;
    private String thirdParagraph;
    private double latitude;
    private double longitude;
    private boolean isWheelChairAccessible;
    private boolean isChildFriendly;
    private boolean hasCheapEntry;
    private boolean hasFreeEntry;
    private String thumbnailURL;
    private ArrayList<String> imageURLs;
    private long likes;


    public Location(String id, String name, String placeType, String summary, String firstParagraph,
                    String secondParagraph, String thirdParagraph, LatLng coords,
                    boolean wheelChairAccessible, boolean childFriendly,
                    boolean cheapEntry, boolean freeEntry, String thumbnailAddress, ArrayList<String> imageURLs, long likes) {
        this.id = id;
        this.name = name;
        this.locationType = placeType;
        this.summary = summary;
        this.firstParagraph = firstParagraph;
        this.secondParagraph = secondParagraph;
        this.thirdParagraph = thirdParagraph;
        this.latitude = coords.latitude;
        this.longitude = coords.longitude;
        this.isWheelChairAccessible = wheelChairAccessible;
        this.isChildFriendly = childFriendly;
        this.hasCheapEntry = cheapEntry;
        this.hasFreeEntry = freeEntry;
        this.thumbnailURL = thumbnailAddress;
        this.imageURLs = imageURLs;
        this.likes = likes;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setLocationType(String placeType) {
        this.locationType = placeType;
    }

    private void setSummary(String info) {
        this.summary = info;
    }

    public String getName() {
        return name;
    }

    public String getLocationType() {
        return locationType;
    }

    public String getSummary() {
        return summary;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isWheelChairAccessible() {
        return isWheelChairAccessible;
    }

    public boolean isChildFriendly() {
        return isChildFriendly;
    }

    public boolean hasCheapEntry() {
        return hasCheapEntry;
    }

    public boolean hasFreeEntry() {
        return hasFreeEntry;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getFirstParagraph() {
        return firstParagraph;
    }

    public void setFirstParagraph(String firstParagraph) {
        this.firstParagraph = firstParagraph;
    }

    public String getSecondParagraph() {
        return secondParagraph;
    }

    public void setSecondParagraph(String secondParagraph) {
        this.secondParagraph = secondParagraph;
    }

    public String getThirdParagraph() {
        return thirdParagraph;
    }

    public void setThirdParagraph(String thirdParagraph) {
        this.thirdParagraph = thirdParagraph;
    }

    public ArrayList<String> getImageURLs() {
        return imageURLs;
    }

    public void setImageURLs(ArrayList<String> imageURLs) {
        this.imageURLs = imageURLs;
    }

    public long getLikes() {
        return likes;
    }

    public void incrementLikes() {
        this.likes++;
    }

    public void decrementLikes(){
        this.likes--;
    }
}
