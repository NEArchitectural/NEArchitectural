package com.nearchitectural;

public class Place {
    private final long id;
    private String name;
    private String placeType;
    private String placeInfo;
    // We also will need some image for the thumbnail

    public Place(long id) {
        this.id = id;
    }

    public Place(long id,String name, String placeType, String placeInfo) {
        this.id = id;
        this.name = name;
        this.placeType = placeType;
        this.placeInfo = placeInfo;
    }

    public Place(long id,String name, String placeType) {
        this.id = id;
        this.name = name;
        this.placeType = placeType;
    }


    private void setName(String name) {
        this.name = name;
    }

    private void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    private void setPlaceInfo(String info) {
        this.placeInfo = info;
    }

    public String getName() {
        return name;
    }

    public String getPlaceType() {
        return placeType;
    }

    public String getPlaceInfo() {
        return placeInfo;
    }

    public long getId() {
        return id;
    }
}
