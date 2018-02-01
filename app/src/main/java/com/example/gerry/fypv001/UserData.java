package com.example.gerry.fypv001;

/**
 * Created by gerry on 24/01/2018.
 */

public class UserData {

    private String userID;
    private double cNlatitude;
    private double cNlongitude;
    private double nNlatitude;
    private double nNlongitude;
    private float nNDist;
    private float cNDist;

    public UserData() {

    }

    public UserData(String userID, double ulatitude, double ulongitude, double nlatitude, double nlongitude, float nNDist, float cNDist) {
        this.userID = userID;
        this.cNlatitude = ulatitude;
        this.cNlongitude = ulongitude;
        this.nNlatitude = nlatitude;
        this.nNlongitude = nlongitude;
        this.cNDist = cNDist;
        this.nNDist = nNDist;

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getcNlatitude() {
        return cNlatitude;
    }

    public void setcNlatitude(double cNlatitude) {
        this.cNlatitude = cNlatitude;
    }

    public double getcNlongitude() {
        return cNlongitude;
    }

    public void setcNlongitude(double cNlongitude) {
        this.cNlongitude = cNlongitude;
    }

    public double getnNlatitude() {
        return nNlatitude;
    }

    public void setnNlatitude(double nNlatitude) {
        this.nNlatitude = nNlatitude;
    }

    public double getnNlongitude() {
        return nNlongitude;
    }

    public void setnNlongitude(double nNlongitude) {
        this.nNlongitude = nNlongitude;
    }

    public float getnNDist() {
        return nNDist;
    }

    public void setnNDist(float nNDist) {
        this.nNDist = nNDist;
    }

    public float getcNDist() {
        return cNDist;
    }

    public void setcNDist(float cNDist) {
        this.cNDist = cNDist;
    }
}
