package com.example.gerry.fypv001;

/**
 * Created by gerry on 12/02/2018.
 */

public class UserDataFS {

    private double cNlatitude;
    private double cNlongitude;
    private double nNlatitude;
    private double nNlongitude;
    private float nNDist;
    private float cNDist;

    public UserDataFS() {

    }

    public UserDataFS(double ulatitude, double ulongitude, double nlatitude, double nlongitude, float nNDist, float cNDist) {
        this.cNlatitude = ulatitude;
        this.cNlongitude = ulongitude;
        this.nNlatitude = nlatitude;
        this.nNlongitude = nlongitude;
        this.cNDist = cNDist;
        this.nNDist = nNDist;

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


