package com.example.gerry.fypv001;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadLeg;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    MapView map;
    RoadNode node;
    List<RoadNode> nodes;
    GeoPoint currentLocation;
    Location location;
    GeoPoint currentNode;
    GeoPoint nextNode;
    float nextNodeDistance;
    float currentNodeDistance;
    LocationListener locationListener;
    LocationManager locationManager;
    LocationRequest locationRequest;
    IMapController mapController;
    static public final int REQUEST_LOCATION = 1;
    DatabaseReference databaseUsers;
    FirebaseFirestore db;
    DocumentReference userRef;
    String id;
    String uniqueID;
    int count = 0;
    int idFS = 1;
    String token;


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
        token = FirebaseInstanceId.getInstance().getToken();

        //LocalBroadcastManager.getInstance(ctx).registerReceiver(
               // mMessageReceiver, new IntentFilter("GetToken"));

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        db = FirebaseFirestore.getInstance();
        uniqueID = UUID.randomUUID().toString();

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(15);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
            Log.d("Location", "Location Permissions granted");
            locationListener = new MyLocationListener();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            }
        double ulatitude = location.getLatitude();
        double ulongitude = location.getLongitude();
        GeoPoint p = new GeoPoint( (ulatitude ),  (ulongitude ));
        mapController.animateTo(p);
        mapController.setCenter(p);

        MyLocationNewOverlay myLocation = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);
        myLocation.enableMyLocation();
        myLocation.enableFollowLocation();
        map.getOverlays().add(myLocation);
        GeoPoint dest = myGeoPoint.getData();
        double lat = dest.getLatitude()/1E6;
        double longit = dest.getLongitude()/1E6;
        GeoPoint cheat = new GeoPoint(lat, longit);

        Log.d("Destination", "Latitude: " + lat);
        Log.d("Destination", "Longitude: " + longit);

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(p);
        waypoints.add(cheat);


        RoadManager roadManager = new MapQuestRoadManager("5w5XI6GaRRuA1P2frfyH4DmDjXnAfEaW");

        Road road = roadManager.getRoad(waypoints);

        Polyline roadOverlay = roadManager.buildRoadOverlay(road);

        map.getOverlays().add(roadOverlay);

        nodes = new ArrayList<RoadNode>();

        Drawable nodeIcon = getResources().getDrawable(R.drawable.marker_node);
        for (int i = 0; i < road.mNodes.size(); i++) {
            node = road.mNodes.get(i);
            nodes.add(node);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(nodeIcon);
            nodeMarker.setTitle("Step " + i);
            map.getOverlays().add(nodeMarker);
            nodeMarker.setSnippet(node.mInstructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(this, node.mLength, node.mDuration));

        }
        currentNode = nodes.get(0).mLocation;
        nextNode = nodes.get(1).mLocation;

        double cNLatitude = currentNode.getLatitude();
        double cNLongitude = currentNode.getLongitude();
        double nNLatitude = nextNode.getLatitude();
        double nNLongitude = nextNode.getLongitude();
        nextNodeDistance = getDistanceFromNode(nNLatitude, nNLongitude, p);
        currentNodeDistance = getDistanceFromNode(cNLatitude, cNLongitude, p);


        Log.d("FCMToken", "This is a message" + token);
        addUserData(cNLatitude, cNLongitude, nNLatitude, nNLongitude, nextNodeDistance, currentNodeDistance);
        addUserDataFS(uniqueID, cNLatitude, cNLongitude, nNLatitude, nNLongitude, nextNodeDistance, currentNodeDistance);
        map.invalidate();


    }



    public void onResume(){
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            currentLocation = new GeoPoint(location);
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double nNodeLatitude = nextNode.getLatitude();
            double nNodeLongitude = nextNode.getLongitude();
            double cNodeLatitude = currentNode.getLatitude();
            double cNodeLongitude = currentNode.getLongitude();
            //if (count == 10) {
                nextNodeDistance = getDistanceFromNode(nNodeLatitude, nNodeLongitude, currentLocation);
                currentNodeDistance = getDistanceFromNode(cNodeLatitude, cNodeLongitude, currentLocation);
                Log.d("nNodeDistance", Float.toString(nextNodeDistance));
                Log.d("cNodeDistance", Float.toString(currentNodeDistance));
            updateUserData(nNodeLatitude, nNodeLongitude, cNodeLatitude, cNodeLongitude, nextNodeDistance, currentNodeDistance);
            //}
            //count++;
            /*double altitude = location.getAltitude();
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

    public void addUserData(double nNodelatitude, double nNodelongitude, double cNodelatitude, double cNodelongitude, float nextNodeDistance, float currentNodeDistance)
    {
        id = databaseUsers.push().getKey();
        UserData newUser = new UserData(id, nNodelatitude, nNodelongitude, cNodelatitude, cNodelongitude, nextNodeDistance, currentNodeDistance);

        databaseUsers.child(id).setValue(newUser);

    }

    public void addUserDataFS(String uuid, double nNodelatitude, double nNodelongitude, double cNodelatitude, double cNodelongitude, float nextNodeDistance, float currentNodeDistance)
    {
        UserDataFS newUser = new UserDataFS(nNodelatitude, nNodelongitude, cNodelatitude, cNodelongitude, nextNodeDistance, currentNodeDistance);
        db.collection("users").document(uuid).set(newUser);
        userRef = db.collection("users").document("uuid");
    }


    public void updateUserDataFS(double updatedNNodeLat, double updatedNNodeLong, double updatedCNodeLat, double updatedCNodeLong, float updatedNNodeDist, float updatedCNodeDist) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("/cNDist", updatedCNodeDist);
        userUpdates.put("/cNlatitude", updatedCNodeLat);
        userUpdates.put("/cNlongitude", updatedCNodeLong);
        userUpdates.put("/nNDist", updatedNNodeDist);
        userUpdates.put("/nNlatitude", updatedNNodeLat);
        userUpdates.put("/nNlongitude", updatedNNodeLong);
        userRef.update(userUpdates);
    }



    public void updateUserData(double updatedNNodeLat, double updatedNNodeLong, double updatedCNodeLat, double updatedCNodeLong, float updatedNNodeDist, float updatedCNodeDist)
    {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put(id + "/nextNodeDistance", updatedNNodeDist);
        userUpdates.put(id + "/nextNodeDistance", updatedNNodeDist);
        userUpdates.put(id + "/nextNodeDistance", updatedNNodeDist);
        userUpdates.put(id + "/nextNodeDistance", updatedNNodeDist);
        userUpdates.put(id + "/nextNodeDistance", updatedNNodeDist);
        userUpdates.put(id + "/currentNodeDistance", updatedCNodeDist);


        databaseUsers.updateChildren(userUpdates);


    }

    public float getDistanceFromNode(double nodeLatitude, double nodeLongitude, GeoPoint currentLocation){
        float dist = 0;
        float[] results = new float[1];
        Location.distanceBetween(nodeLatitude, nodeLongitude, currentLocation.getLatitude(), currentLocation.getLongitude(), results);
        dist = results[0];
        return dist;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            token = intent.getStringExtra("GetToken");
            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };


}
