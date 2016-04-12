package com.example.bryan.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
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
import java.util.Vector;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    public GoogleMap mMap;
    BuildingData buildings;
    ArrayList <LatLng> buildingCoords;
    Boolean busButton = false;
    Buses bus;





    @Override
    /**
     * creating the main screen
     */
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //needed for internet connection for some reason
        StrictMode.setThreadPolicy(policy);




        setContentView(R.layout.activity_maps);
        //addListenerOnbikeCheckbox(); //checkbox tests


        // Obtain the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void addMarkersToMap(ArrayList<LatLng> latLngs) {
        for (LatLng latLng : latLngs) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
            marker.setVisible(true);
        }
    }

    /**
     * This serves as primary function for the interactions involving the 5 main buttons
     * @param v the View
     */

    public void onButtonClicked(View v) throws IOException{

        InputStream is = getAssets().open("buildingcoordinates.txt");
        InputStream bikesIs = getAssets().open("bikeracks");
        InputStream busesIs = getAssets().open("busRoutes.txt");
        mMap.clear();
        switch (v.getId()) {

            case R.id.directionsButton: //direction button stuff goes here
                if (busButton)
                {
                    bus.mainTimer.cancel();
                }
               addMarkersToMap(buildingCoords);
                Directions directions = new Directions(this,is);
                v.setSelected(true);
                break;


            case R.id.bikesButton:

                if (busButton)
                {
                    bus.mainTimer.cancel();
                }

                if (!v.isSelected()) {
                    v.setSelected(true);
                    Scanner scan = new Scanner(bikesIs);
                    ArrayList<LatLng> values = new ArrayList<LatLng>();
                    while (scan.hasNext()) {
                        LatLng coord = new LatLng(scan.nextDouble(),scan.nextDouble());
                        values.add(coord);
                    }
                    scan.close();
                    addMarkersToMap(values);
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
                }
                else {
                    v.setSelected(false);
                }

                break;


            case R.id.POIButton:

                if (busButton)
                {
                    bus.mainTimer.cancel();
                }

                POI poi = new POI(this,is);
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
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in CIS and move the camera
        LatLng CIS = new LatLng(34.226107, -77.871775);
        LatLng leutzHall = new LatLng(34.227180, -77.871813);
        LatLng bearHall = new LatLng(34.228493, -77.872800);
        /**
        mMap.addMarker(new MarkerOptions().position(CIS).title("CIS BUILDING Marker WOOO").snippet("Home of Bryan, Sierra, and Victoria"));
        mMap.addMarker(new MarkerOptions().position(leutzHall).title("Bear Hall"));
        mMap.addMarker(new MarkerOptions().position(bearHall).title("Leutze Hall"));

        **/
        try {
            InputStream is = getAssets().open("buildingcoordinates.txt");
            buildings = new BuildingData(is);
            buildingCoords= new ArrayList<>();
            for(String test: buildings.buildingCoordinates.keySet())
            {
                buildingCoords.add(buildings.buildingCoordinates.get(test).get(0));
            }
            addMarkersToMap(buildingCoords);
        }
        catch(IOException e){

        };
        mMap.moveCamera(CameraUpdateFactory.newLatLng(CIS));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);


    }

        }


