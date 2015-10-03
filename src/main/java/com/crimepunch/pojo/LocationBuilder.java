package com.crimepunch.pojo;

/**
 * Created by manish on 3/10/15.
 */
public class LocationBuilder {
    Double latitude;
    Double longitude;

    private LocationBuilder() {
    }

    public static LocationBuilder aLocation() {
        return new LocationBuilder();
    }

    public LocationBuilder latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public LocationBuilder longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public LocationBuilder but() {
        return aLocation().latitude(latitude).longitude(longitude);
    }

    public Location build() {
        Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
}
