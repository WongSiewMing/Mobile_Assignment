package com.example.ywh.locality.Model;

public class Address {
    private String doorPlate;
    private String streetAddress;
    private String subStreetAddress;
    private String city;
    private String state;
    private String postCode;
    private Double longtitude;
    private Double latitude;

    public Address() {

    }

    public Address(String doorPlate, String streetAddress, String subStreetAddress, String city, String state, String postCode) {
        this.doorPlate = doorPlate;
        this.streetAddress = streetAddress;
        this.subStreetAddress = subStreetAddress;
        this.city = city;
        this.state = state;
        this.postCode = postCode;
    }

    public Address(String doorPlate, String streetAddress, String city, String state, String postCode) {
        this.doorPlate = doorPlate;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.postCode = postCode;
    }


    public String getDoorPlate() {
        return doorPlate;
    }

    public void setDoorPlate(String doorPlate) {
        this.doorPlate = doorPlate;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getSubStreetAddress() {
        return subStreetAddress;
    }

    public void setSubStreetAddress(String subStreetAddress) {
        this.subStreetAddress = subStreetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {

        if((streetAddress==null)&&(subStreetAddress==null))
            return doorPlate+", "+postCode+", "+city+", "+state;
        else if(subStreetAddress==null)
            return doorPlate+", "+streetAddress+", "+postCode+", "+city+", "+state;
        else if(streetAddress==null)
            return doorPlate+", "+subStreetAddress+", "+postCode+", "+city+", "+state;
        else
            return doorPlate+", "+streetAddress+", "+subStreetAddress+", "+postCode+", "+city+", "+state;
    }
}

