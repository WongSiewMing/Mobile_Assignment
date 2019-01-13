package com.example.ywh.locality.Model;

import java.util.Date;
import java.util.List;

public class Assistance {

    private String requester;
    private boolean isSelf;
    private String emergenceType;
    private double latitude;
    private double longitude;
    private String description;
    private long requestTime;

    public Assistance() {
    }

    public Assistance(String requester, boolean isSelf, String emergenceType, double latitude, double longitude, long requestTime) {
        this.requester = requester;
        this.isSelf = isSelf;
        this.emergenceType = emergenceType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.requestTime = requestTime;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public String getEmergenceType() {
        return emergenceType;
    }

    public void setEmergenceType(String emergenceType) {
        this.emergenceType = emergenceType;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

}
