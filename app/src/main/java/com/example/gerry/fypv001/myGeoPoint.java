package com.example.gerry.fypv001;

import org.osmdroid.util.GeoPoint;

/**
 * Created by gerry on 15/01/2018.
 */

public class myGeoPoint {
    private static GeoPoint destination;
    public static GeoPoint getData() {return destination;}
    public static void setData(GeoPoint destination) {myGeoPoint.destination = destination;}
}
