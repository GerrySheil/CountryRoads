package com.example.gerry.fypv001;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.List;

import static com.example.gerry.fypv001.MainActivity.REQUEST_LOCATION;

public class StartNavActivity extends AppCompatActivity {
    EditText place;
    TextView showLat;
    TextView showLong;
    Button go;
    GeoPoint thisDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_nav);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        place = (EditText)findViewById(R.id.editText);
        go = (Button)findViewById(R.id.button2);
        //String address = place.getText().toString();
        go.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String address = place.getText().toString();
                thisDestination = getLocationFromAddress(address);
                myGeoPoint.setData(thisDestination);
                Intent i = new Intent(StartNavActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        /*destination = getLocationFromAddress(address);
        String lat = Double.toString(destination.getLatitude());
        String longitude = Double.toString(destination.getLatitude());
        showLat.setText(lat);
        showLong.setText(longitude);*/



    }

    public GeoPoint getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
                    (double) (location.getLongitude() * 1E6));

            //return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
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
