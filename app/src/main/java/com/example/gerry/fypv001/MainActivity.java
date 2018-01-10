package com.example.gerry.fypv001;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    MapView map;
    MyLocationNewOverlay locationOverlay;
    GeoPoint currentLocation;
    Location location;
    LocationListener locationListener;
    LocationManager locationManager;
    LocationRequest locationRequest;
    IMapController mapController;
    static public final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        //As part of the osm API useage requirements the user agent must be set to avoid being banned from the API
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(9);
        GeoPoint startPoint = new GeoPoint(53.8550014, -9.28792569999996);
        mapController.setCenter(startPoint);
        mapController.setZoom(20);

        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);

        //startMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
        startMarker.setTitle("Start point");

        RoadManager roadManager = new MapQuestRoadManager("5w5XI6GaRRuA1P2frfyH4DmDjXnAfEaW");

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(startPoint);
        GeoPoint endPoint = new GeoPoint(53.802131, -9.514347);
        waypoints.add(endPoint);
        //Marker endMarker = new Marker(map);
        //endMarker.setPosition(endPoint);
        //endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        //map.getOverlays().add(endMarker);
        //endMarker.setTitle("End Point");

        //roadManager.addRequestOption("routeType=bicycle");

        Road road = roadManager.getRoad(waypoints);

        Polyline roadOverlay = roadManager.buildRoadOverlay(road);

        map.getOverlays().add(roadOverlay);


        Drawable nodeIcon = getResources().getDrawable(R.drawable.marker_node);
        for (int i = 0; i < road.mNodes.size(); i++) {
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(nodeIcon);
            nodeMarker.setTitle("Step " + i);
            map.getOverlays().add(nodeMarker);
            nodeMarker.setSnippet(node.mInstructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(this, node.mLength, node.mDuration));

        }
        /*GpsMyLocationProvider provider = new GpsMyLocationProvider(this);
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        locationOverlay = new MyLocationNewOverlay(provider, map);
        locationOverlay.enableFollowLocation();
        locationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                Log.d("MyTag", String.format("First location fix: %s", locationOverlay.getLastFix()));
                mapController.animateTo(locationOverlay.getMyLocation());
            }
        });

        map.getOverlayManager().add(locationOverlay);
        //initMyLocationNewOverlay();
        //Marker currentLocMarker = new Marker(map);*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
            Log.d("Location", "Location Permissions granted");
            locationListener = new MyLocationListener();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);// <-- Start Beemray here

            }
        //double latitude = location.getLatitude();
        //double longitude = location.getLongitude();
        //GeoPoint p = new GeoPoint( (latitude ),  (longitude ));
        mapController.animateTo(currentLocation);
        mapController.setCenter(currentLocation);

        MyLocationNewOverlay myLocation = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);
        myLocation.enableMyLocation();
        myLocation.enableFollowLocation();
        map.getOverlays().add(myLocation);

        map.invalidate();
    }



    public void onResume(){
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            currentLocation = new GeoPoint(location);
            /*double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double altitude = location.getAltitude();
            double accuracy = location.getAccuracy();

            GeoPoint p = new GeoPoint( (latitude ),  (longitude ));

            mapController.animateTo(p);
            mapController.setCenter(p);

            MyLocationNewOverlay mylocation = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);

            mylocation.enableMyLocation();
            mylocation.enableFollowLocation();
            map.getOverlays().add(mylocation);*/
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Location", "Permissions Granted");
            } else {
                Log.d("Location", "Permissions Denied");// Permission was denied or request was cancelled
            }
            }
    }



}
