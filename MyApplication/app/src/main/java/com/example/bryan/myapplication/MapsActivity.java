package com.example.bryan.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.view.View.OnClickListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

/**
 * Main Activity for application.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    public GoogleMap mMap;
    BuildingData buildings;
    BuildingData dining;
    ArrayList <LatLng> diningCoords;
    ArrayList <LatLng> buildingCoords;
    ArrayList <String> diningKeys;
    ArrayList<LatLng> bikeCoords;
    Boolean busButton = false;
    Buses bus;
    InputStream is;
    InputStream bikesIs;
    InputStream busesIs;
    InputStream foodIs;





    @Override
    /**
     * creating the main screen
     * @param savedInstanceState the Bundle
     */
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //needed for internet connection for some reason
        StrictMode.setThreadPolicy(policy);




        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * creates markers given an arrayList of coordinates uses building markers
     * @param latLngs the arrayList of coordinates
     */
    public void addMarkersToMap(ArrayList<LatLng> latLngs) {
        for (LatLng latLng : latLngs) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapsicon)));
            marker.setVisible(true);
        }
    }

    /**
     * creates markers given an arrayList of coordinates uses bike markers
     * @param latLngs the ArrayList of coordinates
     */
    public void addBikeMarkersToMap(ArrayList<LatLng> latLngs) {
        for (LatLng latLng : latLngs) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bikeicon)));
            marker.setVisible(true);
        }
    }

    /**
     * creates markers given an arrayList of coordinates uses dining markers
     * @param latLngs the ArrayList of coordinates
     */
    public void addFoodMarkersToMap(ArrayList<LatLng> latLngs)  {


        for (int i=0;i<latLngs.size();i++) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLngs.get(i))
                    .title(diningKeys.get(i))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.food)));
            marker.setVisible(true);
        }
    }

    /**
     * This serves as primary function for the interactions involving the 5 main buttons
     * @param v the View
     */

    public void onButtonClicked(View v) throws IOException{


        mMap.clear();
        switch (v.getId()) {

            case R.id.directionsButton: //direction button stuff goes here
                if (busButton)
                {
                    bus.mainTimer.cancel();
                }
                addMarkersToMap(buildingCoords);
                Directions directions = new Directions(this,buildings);
                v.setSelected(true);
                break;


            case R.id.bikesButton:

                if (busButton)
                {
                    bus.mainTimer.cancel();
                }

                if (!v.isSelected()) {
                    v.setSelected(true);

                    addBikeMarkersToMap(bikeCoords);
                }
                else {
                    v.setSelected(false);

                   addMarkersToMap(buildingCoords);
                }
                break;


            case R.id.busButton:

                if (busButton)
                {
                    bus.mainTimer.cancel();
                }

                addMarkersToMap(buildingCoords);
                bus = new Buses(this,busesIs);
                busButton = true;


                v.setSelected(true);

                break;


            case R.id.foodButton:

                if (busButton)
                {
                    bus.mainTimer.cancel();
                }

                if (!v.isSelected()) {
                    v.setSelected(true);
                    addFoodMarkersToMap(diningCoords);
                } else {
                    v.setSelected(false);
                    addMarkersToMap(buildingCoords);
                }

                break;


            case R.id.POIButton:

                if (busButton)
                {
                    bus.mainTimer.cancel();
                }

                POI poi = new POI(this,buildings);
                v.setSelected(true);
                break;

        }
    }





    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * @param googleMap the google map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // get current location
        try {
            mMap.setMyLocationEnabled(true);
            //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            System.out.println("current location didn't work");
            //dialogGPS(this.getContext()); // lets the user know there is a problem with the gps
        }


        // Add a marker in CIS and move the camera
        LatLng CIS = new LatLng(34.226107, -77.871775);


        try {
            is = getAssets().open("buildingcoordinates.txt");
            bikesIs = getAssets().open("bikeracks");
            busesIs = getAssets().open("busRoutes.txt");
            foodIs = getAssets().open("dininglocations.txt");

            // create a list with center coordinates of buildings and add building markers
            buildings = new BuildingData(is);
            buildingCoords= new ArrayList<>();
            for(String test: buildings.buildingCoordinates.keySet())
            {
                buildingCoords.add(buildings.buildingCoordinates.get(test).get(0));
            }
            addMarkersToMap(buildingCoords);

            // create a list with dining coordinates
            dining = new BuildingData(foodIs);
            diningCoords= new ArrayList<>();
            diningKeys = new ArrayList<String>();
            for(String test: dining.buildingCoordinates.keySet())
            {
                diningCoords.add(dining.buildingCoordinates.get(test).get(0));
                diningKeys.add(test);
            }

            // create a list with bike coordinates
            Scanner scan = new Scanner(bikesIs);
            bikeCoords = new ArrayList<LatLng>();
            while (scan.hasNext()) {
                LatLng coord = new LatLng(scan.nextDouble(),scan.nextDouble());
                bikeCoords.add(coord);
            }
            scan.close();

        }
        catch(IOException e){

        };
        mMap.moveCamera(CameraUpdateFactory.newLatLng(CIS));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);


    }
}


