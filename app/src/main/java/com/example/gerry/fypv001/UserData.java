package com.example.gerry.fypv001;

/**
 * Created by gerry on 24/01/2018.
 */

public class UserData {

    private String userID;
    private double ulatitude;
    private double ulongitude;
    private double nlatitude;
    private double nlongitude;

    public UserData(){

    }

    public UserData(String userID, double ulatitude, double ulongitude, double nlatitude, double nlongitude){
        this.userID = userID;
        this.ulatitude = ulatitude;
        this.ulongitude = ulongitude;
        this.nlatitude = nlatitude;
        this.nlongitude = nlongitude;
    }

    public String getUserID() {
        return userID;
    }

    public double getULatitude() {
        return ulatitude;
    }

    public double getULongitude() {
        return ulongitude;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setULatitude(double latitude) {
        this.ulatitude = latitude;
    }

    public void setULongitude(double longitude) {
        this.ulongitude = longitude;
    }

    public double getNlatitude() {
        return nlatitude;
    }

    public double getNlongitude() {
        return nlongitude;
    }

    public void setNlatitude(double nlatitude) {
        this.nlatitude = nlatitude;
    }

    public void setNlongitude(double nlongitude) {
        this.nlongitude = nlongitude;
    }
}
